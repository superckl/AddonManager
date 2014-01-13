package org.sensationcraft.addonmanager;

import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitTask;
import org.sensationcraft.addonmanager.commands.AddonCommand;
import org.sensationcraft.addonmanager.exceptions.InvalidAddonException;
import org.sensationcraft.addonmanager.exceptions.UnknownAddonException;
import org.sensationcraft.addonmanager.storage.ExtendPersistance;
import org.sensationcraft.addonmanager.storage.Persistant;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;



public class ReloadableAddon extends AbstractReloadable
{

	private final String name;

	private Set<Listener> listeners = new HashSet<Listener>();
	private Set<AddonCommand> commands = new HashSet<AddonCommand>();;
	private Set<BukkitTask> tasks = Collections.synchronizedSet(new HashSet<BukkitTask>());

	ReloadableAddon(final String name)
	{
		this.name = name;
	}

	/**
	 * Loads an addon from the disk.
	 * @param reload Whether or not this was cause by a reload, used by @Persistant
	 * @param hardReload Whether or not to reset a plugins persistant data
	 */
	@Override
	public Addon load(final AddonManagerPlugin plugin, final boolean reload, final boolean hardReload) throws UnknownAddonException, InvalidAddonException
	{
		final File file = new File(plugin.getDataFolder(), String.format("addons/%s.jar", this.name));
		if(!file.exists())
			throw new UnknownAddonException(this.name);
        
        JarFile jf = null;
        List<String> classList = new ArrayList<String>();
        try
        {
            jf = new JarFile(file);
            Enumeration<JarEntry> entries = jf.entries();
            if(entries.hasMoreElements())
            for(JarEntry entry = entries.nextElement(); entries.hasMoreElements(); entry = entries.nextElement())
            {
                if(entry.getName().endsWith(".class"))
                {
                    String clazz = entry.getName().substring(0, entry.getName().length() - 6);
                    clazz = clazz.replace('/', '.');
                    classList.add(clazz);
                }
            }
        }
        catch(IOException ex)
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
                }catch(IOException ex){}
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
		final ClassLoader cloader = new java.net.URLClassLoader(urls, AddonManager.parentLoader);
		try
		{
            for(String clazz : classList)
            {
                final Class<?> c = Class.forName(clazz, true, cloader);
                AddonData data = c.getAnnotation(AddonData.class);
                if(data == null)
                    continue;
                if(data.name().isEmpty())
                    throw new InvalidAddonException("Addon name should be defined!");
                
                // Check for double addons
                
                final Class<? extends Addon> addonClass = c.asSubclass(Addon.class);
                desc = new AddonDescriptionFile(data);
                a = addonClass.getConstructor(AddonManagerPlugin.class, AddonDescriptionFile.class).newInstance(plugin, desc);

                //Look for StorageRestore and restore where possible
                final Set<Class<?>> classes = new HashSet<Class<?>>();
                classes.add(addonClass);
                if(addonClass.isAnnotationPresent(ExtendPersistance.class))
                    classes.addAll(Arrays.asList(addonClass.getAnnotation(ExtendPersistance.class).classes()));
                for(final Class<?> check:classes)
                {
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
                }
                if(!reload)
                    this.addon = a;
                break;
            }
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
	public void enable(final Plugin plugin) throws IllegalStateException
	{
		if(this.addon == null)
			throw new IllegalStateException("Addon not loaded");
		try{
			this.addon.onEnable();
			this.isEnabled = true;
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
				for(BukkitTask task:this.tasks)
					task.cancel();
				this.tasks.clear();
			}
			try{
				this.addon.onDisable();
			}catch(final Exception e){
				AddonManagerPlugin.getInstance().getLogger().severe("Error while disabling addon "+this.getAddon().getName());
				e.printStackTrace();
			}
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
	void addTask(BukkitTask task){
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
	void removeTask(BukkitTask task){
		synchronized(this.tasks) {
			this.tasks.remove(task);
		}
	}
}
