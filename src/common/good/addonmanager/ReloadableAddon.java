package common.good.addonmanager;

import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map.Entry;
import java.util.Set;

import org.bukkit.command.Command;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;

import common.good.addonmanager.exceptions.InvalidAddonException;
import common.good.addonmanager.exceptions.UnknownAddonException;
import common.good.addonmanager.storage.ExtendPersistance;
import common.good.addonmanager.storage.Persistant;



public class ReloadableAddon extends AbstractReloadable
{

	private final String name;
	
	private Set<Listener> listeners;
	private HashMap<Command, String> commands;

	ReloadableAddon(final String name)
	{
		this.name = name;
	}

	/**
	 * Loads an addon from the disk.
	 * @param reload Whether or not this was cause by a reload, used by @Persistant
	 */
	@Override
	public Addon load(final AddonManagerPlugin plugin, final boolean reload) throws UnknownAddonException, InvalidAddonException
	{
		final File file = new File(plugin.getDataFolder(), String.format("listeners/%s.jar", this.name));
		if(!file.exists())
			throw new UnknownAddonException(this.name);
		URL[] urls = new URL[0];
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
		final ClassLoader cloader = new java.net.URLClassLoader(urls, AddonManager.parentLoader);
		final AddonDescriptionFile desc = new AddonDescriptionFile(file);
		final String mainClass = desc.getMainClass();
		try
		{
			final Class<?> c = Class.forName(mainClass, true, cloader);
			final Class<? extends Addon> addonClass = c.asSubclass(Addon.class);
			a = addonClass.getConstructor(AddonManagerPlugin.class, AddonDescriptionFile.class).newInstance(plugin, desc);
			//if(a instanceof Listener == false)
			//	throw new InvalidAddonException(String.format("Addon is not a listener"));

			//Look for StorageRestore and restore where possible
			final Set<Class<?>> classes = new HashSet<Class<?>>();
			classes.add(addonClass);
			if(addonClass.isAnnotationPresent(ExtendPersistance.class))
				classes.addAll(Arrays.asList(addonClass.getAnnotation(ExtendPersistance.class).classes()));
			for(final Class<?> check:classes)
				for(final Field field:check.getDeclaredFields()){
					final boolean initialFlag = field.isAccessible();
					field.setAccessible(true);
					if(field.isAnnotationPresent(Persistant.class)){
						final Persistant annot = field.getAnnotation(Persistant.class);
						Object obj = a.getData(Object.class, annot.key());
						if((obj == null) || (annot.reloadOnly() && !reload)){
							obj = annot.instantiationType().newInstance();
							a.setData(annot.key(), obj);
						}
						field.set(a, obj);
					}
					field.setAccessible(initialFlag);
				}
			if(!reload)
				this.addon = a;
		}
		catch(final InvocationTargetException ex)
		{
			throw new InvalidAddonException(String.format("Invalid addon found: %s", ex.getCause().getMessage()), ex);
		}
		catch(final Throwable ex)
		{
			throw new InvalidAddonException(String.format("Invalid addon '%s' found: %s", desc.getName(), ex.getMessage()), ex);
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
	public void enable(final Plugin plugin) throws IllegalStateException
	{
		if(this.addon == null)
			throw new IllegalStateException("Addon not loaded");
		try{
			this.addon.onEnable();
			this.isEnabled = true;
		}catch(Exception e){
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
			for(Listener listener:this.listeners)
				HandlerList.unregisterAll(listener);
			this.listeners.clear();
			AddonManagerPlugin plugin = AddonManagerPlugin.getInstance();
			for(Entry<Command, String> command:this.commands.entrySet())
				plugin.unregisterCommand(this, command.getKey(), command.getValue());
			try{
				this.addon.onDisable();
			}catch(Exception e){
				AddonManagerPlugin.getInstance().getLogger().severe("Error while disabling addon "+this.getAddon().getName());
				e.printStackTrace();
			}
		}
		this.isEnabled = false;
	}
	
	void addCommand(Command command, String prefix){
		this.commands.put(command, prefix);
	}
	
	void addListener(Listener listener){
		this.listeners.add(listener);
	}
	
	void removeCommand(Command command){
		this.commands.remove(command);
	}
	
	void removeListener(Listener listener){
		this.commands.remove(listener);
	}

}
