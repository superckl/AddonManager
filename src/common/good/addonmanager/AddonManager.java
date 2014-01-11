package common.good.addonmanager;

import java.io.File;
import java.io.FileFilter;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;


import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import common.good.addonmanager.exceptions.InvalidAddonException;
import common.good.addonmanager.exceptions.UnknownAddonException;



/**
 *
 * @author DarkSeraphim
 */
public class AddonManager implements CommandExecutor
{

	private final AddonManagerPlugin plugin;

	private final static Logger log;

	Map<String, AbstractReloadable> addons = new HashMap<String, AbstractReloadable>();

	protected static ClassLoader parentLoader;

	static
	{
		log = Logger.getLogger("AddonManager");
	}

	public AddonManager(final AddonManagerPlugin plugin)
	{
		this.plugin = plugin;
		final File pluginFolder = plugin.getDataFolder();
		if(!pluginFolder.exists())
			pluginFolder.mkdirs();
		final File lFolder = new File(pluginFolder, "listeners");
		if(!lFolder.exists() || !lFolder.isDirectory())
			lFolder.mkdirs();
		ClassLoader cl = null;
		try
		{
			final Method m = JavaPlugin.class.getDeclaredMethod("getClassLoader", new Class[0]);
			if(!m.isAccessible())
				m.setAccessible(true);
			cl = (ClassLoader) m.invoke(plugin, new Object[0]);
		}
		catch(final Exception ex)
		{
			AddonManager.log.log(Level.SEVERE, "Failed to obtain the parent ClassLoader");
			ex.printStackTrace();
			Bukkit.shutdown();
			// Break it off, likely it wouldn't work
		}
		AddonManager.parentLoader = cl;

		final Set<String> ex = new HashSet<String>(this.plugin.getConfig().getStringList("excludes"));

		this.loadAll(ex);
	}

	@Override
	public boolean onCommand(final CommandSender sender, final Command cmnd, final String label, final String[] args)
	{
		if(sender instanceof Player)
		{
			sender.sendMessage(ChatColor.RED+"Cockblocked. Stay away from the addons!");
			return true;
		}
		if(args.length < 1)
		{
			sender.sendMessage("Addon Manager v0.1");
			return true;
		}
		if(args[0].equals("load"))
		{
			if(args.length < 2)
				sender.sendMessage(ChatColor.RED+"Please specify the addon you want to load");
			else
			{
				final ReloadableAddon rl = new ReloadableAddon(args[1]);
				try
				{
					rl.load(this.plugin, false);
					// Maybe call an onEnable or smth

					this.addons.put(args[1], rl);

					sender.sendMessage(ChatColor.GREEN+"Loaded the addon.");
				}
				catch(final UnknownAddonException ex)
				{
					sender.sendMessage(ChatColor.RED+"Unknown addon.");
				}
				catch(final InvalidAddonException ex)
				{
					sender.sendMessage(ChatColor.RED+"Failed to load the addon: ");
					ex.printStackTrace();
				}
			}
		}
		else if(args[0].equalsIgnoreCase("enable"))
		{
			if(args.length < 2)
				sender.sendMessage(ChatColor.RED+"Please specify the addon you want to load");
			else if(!this.addons.containsKey(args[1]))
				sender.sendMessage(ChatColor.RED+"Addon not found.");
			else
			{
				final AbstractReloadable ar = this.addons.get(args[1]);
				ar.enable(this.plugin);
				sender.sendMessage(ChatColor.GREEN+"Addon enabled.");
			}
		}
		else if(args[0].equalsIgnoreCase("disable"))
		{
			if(args.length < 2)
				sender.sendMessage(ChatColor.RED+"Please specify the addon you want to load");
			else if(!this.addons.containsKey(args[1]))
				sender.sendMessage(ChatColor.RED+"Addon not found.");
			else
			{
				final AbstractReloadable ar = this.addons.get(args[1]);
				ar.disable();
				sender.sendMessage(ChatColor.DARK_RED+"Addon disabled.");
			}
		}
		else if(args[0].equalsIgnoreCase("unload"))
		{
			if(args.length < 2)
				sender.sendMessage(ChatColor.RED+"Please specify the addon you want to load");
			else if(!this.addons.containsKey(args[1]))
				sender.sendMessage(ChatColor.RED+"Addon not found.");
			else
			{
				final AbstractReloadable ar = this.addons.remove(args[1]);
				ar.unload();
				sender.sendMessage(ChatColor.DARK_RED+"Addon unloaded.");
			}
		}
		else if(args[0].equalsIgnoreCase("reload"))
		{
			if(args.length < 2)
				sender.sendMessage(ChatColor.RED+"Please specify the addon you want to reload");
			else if(!this.addons.containsKey(args[1]))
				sender.sendMessage(ChatColor.RED+"Addon not found.");
			else
			{
				final AbstractReloadable ar = this.addons.get(args[1]);
				Addon a;
				a = ar.reload(sender, this.plugin);
				if(a == null)
				{
					sender.sendMessage(ChatColor.RED+"Failed to reload the addon!");
					return true;
				}
				sender.sendMessage(ChatColor.GREEN+"Addon reloaded.");
			}
		}
		else if(args[0].equalsIgnoreCase("list"))
		{
			final StringBuilder list = new StringBuilder();
			for(final Map.Entry<String, AbstractReloadable> e : this.addons.entrySet())
			{
				list.append(e.getValue().isEnabled() ? ChatColor.GREEN : ChatColor.RED);
				list.append(e.getKey());
				list.append(ChatColor.RESET).append(", ");
			}
			if(list.length() > 2)
				list.delete(list.length()-2, list.length());
			sender.sendMessage(String.format("Addons: %s", list.toString()));
		}
		return true;
	}

	public final void loadAll(final Set<String> excludes)
	{
		final File lisDir = new File(this.plugin.getDataFolder(), "listeners");
		final File[] files = lisDir.listFiles(new FileFilter()
		{
			@Override
			public boolean accept(final File file)
			{
				return file.getName().endsWith(".jar");
			}
		});
		for(final File file : files)
		{
			String name = file.getName();
			name = name.substring(0, name.length() - 4);
			if(excludes.contains(name))
				continue;
			final ReloadableAddon rl = new ReloadableAddon(name);
			try
			{
				rl.load(this.plugin, false);
				// Maybe call an onEnable or smth

				this.addons.put(name, rl);

				rl.enable(this.plugin);

				AddonManager.log.log(Level.INFO, ChatColor.GREEN+"Loaded addon {0}.", name);
			}
			catch(final UnknownAddonException ex)
			{
				AddonManager.log.log(Level.WARNING, "{0}Unknown addon.", ChatColor.RED);
			}
			catch(final InvalidAddonException ex)
			{
				AddonManager.log.log(Level.SEVERE, "{0}Failed to load the addon: ", ChatColor.RED);
				ex.printStackTrace();
			}
		}
	}

	public void destroy()
	{
		for(AbstractReloadable addon:this.addons.values())
			addon.unload();
	}
	
	public Map<String, AbstractReloadable> getAddons(){
		return this.addons;
	}

}
