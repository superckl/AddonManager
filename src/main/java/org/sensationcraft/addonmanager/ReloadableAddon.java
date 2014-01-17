package org.sensationcraft.addonmanager;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import org.bukkit.Bukkit;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.scheduler.BukkitTask;
import org.sensationcraft.addonmanager.addon.dependencies.DependencyManager;
import org.sensationcraft.addonmanager.addon.dependencies.DependencyStatus;
import org.sensationcraft.addonmanager.commands.AddonCommand;
import org.sensationcraft.addonmanager.events.AddonDisableEvent;
import org.sensationcraft.addonmanager.events.AddonEnableEvent;
import org.sensationcraft.addonmanager.exceptions.InvalidAddonException;
import org.sensationcraft.addonmanager.exceptions.UnknownAddonException;
import org.sensationcraft.addonmanager.storage.ExtendPersistance;
import org.sensationcraft.addonmanager.storage.Persistant;

public class ReloadableAddon extends AbstractReloadable
{

	private final String name;

	private final Set<Listener> listeners = new HashSet<Listener>();
	private final Set<AddonCommand> commands = new HashSet<AddonCommand>();;
	private final Set<BukkitTask> tasks = Collections.synchronizedSet(new HashSet<BukkitTask>());

	ReloadableAddon(final String name)
	{
		this.name = name;
	}

	@Override
	public DependencyStatus preLoad(final AddonManagerPlugin plugin) throws UnknownAddonException {
		final File file = new File(plugin.getDataFolder(), String.format("addons/%s.jar", this.name));
		if(!file.exists())
			throw new UnknownAddonException(this.name);
		JarFile jf = null;
		final List<String> classList = new ArrayList<String>();
		try
		{
			jf = new JarFile(file);
			final Enumeration<JarEntry> entries = jf.entries();
			if(entries.hasMoreElements())
				for(JarEntry entry = entries.nextElement(); entries.hasMoreElements(); entry = entries.nextElement())
					if(entry.getName().endsWith(".class"))
					{
						String clazz = entry.getName().substring(0, entry.getName().length() - 6);
						clazz = clazz.replace('/', '.');
						classList.add(clazz);
					}
		}
		catch(final IOException ex)
		{
			// Thou shall not pass
			throw new UnknownAddonException("Failed to read the jar file: "+ex.getMessage());
		}
		finally
		{
			if(jf != null)
            try
			{
					jf.close();
			}catch(final IOException ex){}
		}

		URL[] urls;
		try
		{
			urls = new URL[]{file.toURI().toURL()};
		}
		catch(final MalformedURLException ex)
		{
			return DependencyStatus.NONE;
			// Swallow it
		}
		final ClassLoader cloader = new java.net.URLClassLoader(urls, AddonManager.parentLoader);
		try {
			for(final String clazz : classList)
			{
				final Class<?> c = cloader.loadClass(clazz);
				final AddonData data = c.getAnnotation(AddonData.class);
				if(data == null)
					continue;
				if(this.addon != null)
					throw new InvalidAddonException("Addon "+this.name+" has more than one main class.");
				if(data.name().isEmpty())
					throw new InvalidAddonException("Addon name should be defined!");

				if(!Addon.class.isAssignableFrom(c))
					throw new InvalidAddonException("Addon "+data.name()+" has an inalid Addon class.");
				this.addonClass = c.asSubclass(Addon.class);
			}
			if(this.addonClass == null)
				throw new InvalidAddonException("Addon "+this.name+" has no main class.");
			this.dependencyManager = DependencyManager.evaluate(plugin, this, this.addonClass, this.name);
			return this.dependencyManager.getDependencyStatus(false);
		} catch (final ClassNotFoundException e) {
			e.printStackTrace();
		} catch (final InvalidAddonException e) {
			e.printStackTrace();
		}
		return DependencyStatus.NONE;
	}

	/**
	 * Loads an addon from the disk.
	 * @param reload Whether or not this was cause by a reload, used by @Persistant
	 * @param hardReload Whether or not to reset a plugins persistant data
	 */
	@Override
	public Addon load(final AddonManagerPlugin plugin, final boolean reload, final boolean hardReload) throws UnknownAddonException, InvalidAddonException
	{
		/*final File file = new File(plugin.getDataFolder(), String.format("addons/%s.jar", this.name));
		if(!file.exists())
			throw new UnknownAddonException(this.name);

		JarFile jf = null;
		final List<String> classList = new ArrayList<String>();
		try
		{
			jf = new JarFile(file);
			final Enumeration<JarEntry> entries = jf.entries();
			if(entries.hasMoreElements())
				for(JarEntry entry = entries.nextElement(); entries.hasMoreElements(); entry = entries.nextElement())
					if(entry.getName().endsWith(".class"))
					{
						String clazz = entry.getName().substring(0, entry.getName().length() - 6);
						clazz = clazz.replace('/', '.');
						classList.add(clazz);
					}
		}
		catch(final IOException ex)
		{
			// Thou shall not pass
			throw new UnknownAddonException("Failed to read the jar file: "+ex.getMessage());
		}
		finally
		{
			if(jf != null)
				try
			{
					jf.close();
			}catch(final IOException ex){}
		}

		URL[] urls;
		try
		{
			urls = new URL[]{file.toURI().toURL()};
		}
		catch(final MalformedURLException ex)
		{
			return null;
			// Swallow it
		}
		Addon a = null;
		AddonDescriptionFile desc = null;
		final ClassLoader cloader = new java.net.URLClassLoader(urls, AddonManager.parentLoader);*/
		Addon a = null;
		AddonDescriptionFile desc = null;
		try
		{
			if(this.dependencyManager.getDependencyStatus(false) == DependencyStatus.NONE)
				throw new IllegalStateException("Load was called for addon "+this.name+" but hard dependencies are not sastified.");
			if(this.addonClass == null)
				throw new IllegalStateException("load was called for addon "+this.name+" but no addon classes were found.");
			plugin.getAddonManager().getDependingAddons().remove(this);
			final AddonData data = this.addonClass.getAnnotation(AddonData.class);
			desc = new AddonDescriptionFile(data);
			a = this.addonClass.getConstructor(AddonManagerPlugin.class, AddonDescriptionFile.class).newInstance(plugin, desc);

			//Look for StorageRestore and restore where possible
			final Set<Class<?>> classes = new HashSet<Class<?>>();
			classes.add(this.addonClass);
			if(this.addonClass.isAnnotationPresent(ExtendPersistance.class))
				classes.addAll(Arrays.asList(this.addonClass.getAnnotation(ExtendPersistance.class).classes()));
			for(final Class<?> check:classes)
				for(final Field field:check.getDeclaredFields())
				{
					final boolean initialFlag = field.isAccessible();
					field.setAccessible(true);
					if(field.isAnnotationPresent(Persistant.class))
					{
						final Persistant annot = field.getAnnotation(Persistant.class);
						Object obj = a.getData(Object.class, annot.key());
						if((obj == null) || (annot.reloadOnly() && !reload) || hardReload)
						{
							obj = annot.instantiationType().newInstance();
							a.setData(annot.key(), obj);
						}
						field.set(a, obj);
					}
					field.setAccessible(initialFlag);
				}
			if(!reload)
				this.addon = a;
			plugin.getAddonManager().getAddons().put(this.addon.getName(), this);
			//break; NOTE: Only one should load?
		}
		catch(final InvocationTargetException ex)
		{
			throw new InvalidAddonException(String.format("Invalid addon found: %s", ex.getCause().getMessage()), ex);
		}
		catch(final Throwable ex)
		{
			throw new InvalidAddonException(String.format("Invalid addon '%s' found: %s", desc != null ? desc.getName() : "unknown", ex.getMessage()), ex);
		}
		return a;
	}

	@Override
	public void validate(final Addon addon) throws InvalidAddonException
	{
		if((addon instanceof Listener) == false)
			throw new InvalidAddonException(String.format("Addon is not a listener"));
	}

	@Override
	public void load(final Addon addon)
	{
		if(this.addon == null)
			this.addon = addon;
	}

	@Override
	public void unload()
	{
		if(this.addon != null)
		{
			this.disable();
			this.addon = null;
		}
	}

	@Override
	public void enable(final AddonManagerPlugin plugin) throws IllegalStateException
	{
		if(this.addon == null)
			throw new IllegalStateException("Addon not loaded");
		plugin.getLogger().info("Enabling Addon "+this.addon.getName());
		try{
			this.addon.onEnable();
			this.isEnabled = true;
			Bukkit.getPluginManager().callEvent(new AddonEnableEvent(this.addon));
		}catch(final Exception e){
			AddonManagerPlugin.getInstance().getLogger().severe("Error while enabling addon "+this.getAddon().getName());
			e.printStackTrace();
		}
	}

	@Override
	public boolean isEnabled()
	{
		return this.isEnabled;
	}

	@Override
	public void disable()
	{
		if(this.addon != null)
		{
			this.isEnabled = false;
			for(final Listener listener:this.listeners)
				HandlerList.unregisterAll(listener);
			this.listeners.clear();
			final AddonManagerPlugin plugin = AddonManagerPlugin.getInstance();
			for(final AddonCommand command:this.commands)
				plugin.unregisterCommand(this, command);
			this.commands.clear();
			synchronized(this.tasks){
				for(final BukkitTask task:this.tasks)
					task.cancel();
				this.tasks.clear();
			}
			try{
				this.addon.onDisable();
			}catch(final Exception e){
				AddonManagerPlugin.getInstance().getLogger().severe("Error while disabling addon "+this.getAddon().getName());
				e.printStackTrace();
			}
			Bukkit.getPluginManager().callEvent(new AddonDisableEvent(this.addon));
		}
	}

	void addCommand(final AddonCommand command){
		this.commands.add(command);
	}

	void addListener(final Listener listener){
		this.listeners.add(listener);
	}

	void removeCommand(final AddonCommand command){
		this.commands.remove(command);
	}

	void removeListener(final Listener listener){
		this.commands.remove(listener);
	}

	/**
	 * Internal synchronization
	 */
	void addTask(final BukkitTask task){
		synchronized(this.tasks){
			if(this.isEnabled)
				this.tasks.add(task);
			else
				task.cancel();
		}
	}

	/**
	 * Internal synchronization
	 */
	void removeTask(final BukkitTask task){
		synchronized(this.tasks) {
			this.tasks.remove(task);
		}
	}

	protected String getFileName() {
		return this.name;
	}
}
