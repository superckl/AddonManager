package common.good.addonmanager;


import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.Plugin;

import common.good.addonmanager.exceptions.InvalidAddonException;
import common.good.addonmanager.exceptions.UnknownAddonException;



/**
 *
 * @author DarkSeraphim
 */
public abstract class AbstractReloadable
{

	protected Addon addon;

	protected boolean isEnabled = false;

	protected Addon getAddon()
	{
		return this.addon;
	}

	public abstract Addon load(AddonManagerPlugin plugin, boolean reload) throws UnknownAddonException, InvalidAddonException;
	public abstract void load(Addon addon);

	public abstract void unload();

	public abstract void validate(Addon a) throws InvalidAddonException;

	public Addon reload(final CommandSender sender, final AddonManagerPlugin plugin)
	{
		Addon a = null;
		try
		{
			a = this.load(plugin, true);
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

	public abstract void enable(Plugin plugin) throws IllegalStateException;

	public boolean isEnabled()
	{
		return this.isEnabled;
	}

	public abstract void disable();
}
