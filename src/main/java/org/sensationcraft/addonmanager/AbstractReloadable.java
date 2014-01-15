package org.sensationcraft.addonmanager;

import java.util.HashSet;
import java.util.Set;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.Plugin;
import org.sensationcraft.addonmanager.addon.dependencies.DependencyManager;
import org.sensationcraft.addonmanager.addon.dependencies.DependencyStatus;
import org.sensationcraft.addonmanager.exceptions.InvalidAddonException;
import org.sensationcraft.addonmanager.exceptions.UnknownAddonException;

public abstract class AbstractReloadable
{

	protected Addon addon;

	protected volatile boolean isEnabled = false;
	
	protected DependencyManager dependencyManager;
	
	protected Set<Class<? extends Addon>> addonClasses = new HashSet<Class<? extends Addon>>();

	public Addon getAddon()
	{
		return this.addon;
	}

	public abstract Addon load(final AddonManagerPlugin plugin, final boolean reload, final boolean hardReload) throws UnknownAddonException, InvalidAddonException;
	public Addon load(final AddonManagerPlugin plugin, final boolean reload) throws UnknownAddonException, InvalidAddonException{
		return this.load(plugin, reload, false);
	}
	
	public abstract DependencyStatus preLoad(final AddonManagerPlugin plugin) throws UnknownAddonException;
	
	public abstract void load(final Addon addon);

	public abstract void unload();

	public abstract void validate(final Addon a) throws InvalidAddonException;

	public Addon reload(final CommandSender sender, final AddonManagerPlugin plugin, final boolean hard)
	{
		Addon a = null;
		try
		{
			a = this.load(plugin, true, hard);
		}
		catch(final UnknownAddonException ex)
		{
			sender.sendMessage(ChatColor.RED+"Unknown addon.");
		}
		catch(final InvalidAddonException ex)
		{
			sender.sendMessage(ChatColor.RED+"Failed to load the addon!");
			ex.printStackTrace();
		}
		if(a != null)
		{
			this.unload();
			this.load(a);
			this.enable(plugin);
		}
		return a;
	}

	public abstract void enable(final Plugin plugin) throws IllegalStateException;

	public boolean isEnabled()
	{
		return this.isEnabled;
	}

	public abstract void disable();

	public DependencyManager getDependencyManager() {
		return dependencyManager;
	}
}
