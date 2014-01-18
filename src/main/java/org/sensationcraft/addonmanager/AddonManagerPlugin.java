package org.sensationcraft.addonmanager;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandMap;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.sensationcraft.addonmanager.addon.dependencies.DependencyStatus;
import org.sensationcraft.addonmanager.commands.AddonCommand;
import org.sensationcraft.addonmanager.exceptions.InvalidAddonException;
import org.sensationcraft.addonmanager.exceptions.UnknownAddonException;
import org.sensationcraft.addonmanager.listeners.EnableDisableListener;
import org.sensationcraft.addonmanager.storage.Storage;
import org.sensationcraft.addonmanager.users.AddonUser;


public class AddonManagerPlugin extends JavaPlugin implements Listener{

	private static AddonManagerPlugin instance;
	private final Storage data = new Storage();
	private AddonManager manager;

	private CommandMap bukkitCommandMap;
	private Map<String, Command> knownCommands;
	private Set<String> aliases;
	private final Map<String, AddonUser> users = new HashMap<String, AddonUser>();

	private final String aliasMatcher = "[%s:]*[%s]";

	private boolean inStartup;

	@Override
	public void onEnable(){
		this.inStartup = true;
		AddonManagerPlugin.instance = this;
		this.saveDefaultConfig();
		this.getLogger().info("Registering listeners...");
		this.getServer().getPluginManager().registerEvents(this, this); //User events
		this.getServer().getPluginManager().registerEvents(new EnableDisableListener(this), this);
		this.getLogger().info("Loading addons...");
		this.manager = new AddonManager(this, this.getConfig().getBoolean("Use Permissions"));
		this.manager.loadAll();
		this.getServer().getPluginCommand("addons").setExecutor(this.manager);
		new BukkitRunnable(){

			@Override
			public void run() {
				for(final ReloadableAddon addon:AddonManagerPlugin.this.manager.getDependingAddons())
					if(addon.getDependencyManager().getCurrentStatus() == DependencyStatus.HARD_RESOLVED)
						try {
							addon.load(AddonManagerPlugin.this, false);
						} catch (final UnknownAddonException e) {
							e.printStackTrace();
						} catch (final InvalidAddonException e) {
							e.printStackTrace();
						}
				for(final ReloadableAddon addon:AddonManagerPlugin.this.manager.getDependingAddons())
					AddonManagerPlugin.this.getLogger().severe(new StringBuilder("Failed to enable Addon ").append(addon.getFileName())
							.append(". All dependencies were not sastisfied. Dependencies not sastisfied: ")
							.append(StringUtils.join(addon.getDependencyManager().getRemainingHardDepends(), ", ")).toString());
			}

		}.runTask(this);
		this.inStartup = false;
	}

	@Override
	public void onDisable(){
		this.getLogger().info("Unloading addons...");
		this.manager.destroy();
	}

	/**
	 * @return The static reference to AddonManagerPlugin
	 */
	public static AddonManagerPlugin getInstance(){
		return AddonManagerPlugin.instance;
	}

	/**
	 * @return The default Storage reference
	 */
	public Storage getStorage(){
		return this.data;
	}

	/**
	 * Registers a command with bukkit. The first time this method is called after startup, reflection will be done to obtain the CommandMap used by Bukkit.
	 * @param addon The addon requesting the registration
	 * @param fallbackPrefix The prefix to append should the commands name be taken. Usually the name of the addon.
	 * @param command The command to register.
	 * @return Whether or not the registration didn't use the fallback prefix.
	 * @see {@link org.bukkit.command.CommandMap #register(String, Command)}
	 */
	public boolean registerCommand(final Addon addon, final AddonCommand command){
		try {
			if(this.bukkitCommandMap == null){
				final Field f = this.getServer().getClass().getDeclaredField("commandMap");
				f.setAccessible(true);
				this.bukkitCommandMap = (CommandMap) f.get(this.getServer());
			}
			if(this.knownCommands == null){
				final Field f = this.bukkitCommandMap.getClass().getDeclaredField("knownCommands");
				f.setAccessible(true);
				this.knownCommands = (Map<String, Command>) f.get(this.bukkitCommandMap);
			}
			if(this.aliases == null){
				final Field f = this.bukkitCommandMap.getClass().getDeclaredField("aliases");
				f.setAccessible(true);
				this.aliases = (Set<String>) f.get(this.bukkitCommandMap);
			}
			final String fallbackPrefix = command.getFallbackPrefix();
			if((fallbackPrefix == null) || fallbackPrefix.isEmpty())
				throw new IllegalArgumentException("Addon "+addon.getName()+" tried to register a command with a null or empty fallback prefix!");
			final ReloadableAddon reloadable = this.getByAddon(addon);
			if(reloadable == null)
				throw new InvalidAddonException("No corresponding ReloadableAddon found for "+addon.getName());
			reloadable.addCommand(command);
			return this.bukkitCommandMap.register(fallbackPrefix == null ? "":fallbackPrefix, command);
		} catch (final Exception e) {
			this.getLogger().severe("Failed to register command "+command.getName()+" for addon "+addon.getName());
			e.printStackTrace();
		}
		return true;
	}

	/**
	 * Removes a registered command from Bukkit
	 * @param addon The addon requesting the unregistration
	 * @param command The command to unregister
	 * @param prefix The fallback prefix used when registering the command
	 * @see {@link #registerCommand(Addon, String, Command)}
	 */
	public void unregisterCommand(final Addon addon, final AddonCommand command){
		try {
			final ReloadableAddon reloadable = this.getByAddon(addon);
			if(reloadable == null)
				throw new InvalidAddonException("No corresponding ReloadableAddon found for "+addon.getName());
			this.unregisterCommand(reloadable, command);
		} catch (final InvalidAddonException e) {
			this.getLogger().severe("Failed to unregister command "+command.getName()+" for addon "+addon.getName()+". Addon may not unload properly.");
			e.printStackTrace();
		}
	}

	void unregisterCommand(final ReloadableAddon addon, final AddonCommand command){
		this.removeFromBukkit(command, command.getFallbackPrefix());
		addon.removeCommand(command);
	}

	private void removeFromBukkit(final Command command, final String prefix){
		this.knownCommands.remove(command.getLabel());
		final Iterator<String> it = this.aliases.iterator();
		while(it.hasNext()){
			final String alias = it.next();
			for(final String cAlias:command.getAliases())
				if(alias.matches(String.format(this.aliasMatcher, prefix, cAlias)))
					it.remove();
		}
	}
	/**
	 * Registers a Listener with Bukkit
	 * @param addon The addon requesting the registration
	 * @param listener The listener to register
	 * @throws InvalidAddonException
	 * @see {@link org.bukkit.plugin.PluginManager #registerEvents(Listener, org.bukkit.plugin.Plugin)}
	 */
	public void registerListener(final Addon addon, final Listener listener){
		try {
			final ReloadableAddon reloadable = this.getByAddon(addon);
			if(reloadable == null)
				throw new InvalidAddonException("No corresponding ReloadableAddon found for "+addon.getName());
			this.getServer().getPluginManager().registerEvents(listener, this);
			reloadable.addListener(listener);
		} catch (final InvalidAddonException e) {
			this.getLogger().severe("Failed to register listener for addon "+addon.getName());
			e.printStackTrace();
		}
	}

	/**
	 * Unregisters a listener from Bukkit
	 * @param addon The addon requesting the unregistration
	 * @param listener The listener to unregister
	 * @see {@link #registerListener(Addon, Listener)}
	 * @throws InvalidAddonException
	 */
	public void unregisterListener(final Addon addon, final Listener listener){
		try {
			final ReloadableAddon reloadable = this.getByAddon(addon);
			if(reloadable == null)
				throw new InvalidAddonException("No corresponding ReloadableAddon found for "+addon.getName());
			HandlerList.unregisterAll(listener);
			reloadable.removeListener(listener);
		} catch (final InvalidAddonException e) {
			this.getLogger().severe("Failed to unregister listener for addon "+addon.getName()+". Addon may not unload properly.");
			e.printStackTrace();
		}
	}

	/**
	 * You should NOT need to call this. AddonRunnable will do it for you if you use the built in methods.
	 * @param addon
	 * @param task
	 */
	synchronized public void registerTask(final Addon addon, final BukkitTask task){
		try {
			final ReloadableAddon reloadable = this.getByAddon(addon);
			if(reloadable == null)
				throw new InvalidAddonException("No corresponding ReloadableAddon found for "+addon.getName());
			reloadable.addTask(task);
		} catch (final InvalidAddonException e) {
			this.getLogger().severe("Failed to register task for addon "+addon.getName()+".");
			e.printStackTrace();
		}
	}

	/**
	 * You should NOT need to call this. AddonRunnable will do it for you if you use the built in methods.
	 * @param addon
	 * @param task
	 */
	synchronized public void unregisterTask(final Addon addon, final BukkitTask task){
		try {
			final ReloadableAddon reloadable = this.getByAddon(addon);
			if(reloadable == null)
				throw new InvalidAddonException("No corresponding ReloadableAddon found for "+addon.getName());
			reloadable.removeTask(task);
		} catch (final InvalidAddonException e) {
			this.getLogger().severe("Failed to unregister task for addon "+addon.getName()+". Addon may not unload properly.");
			e.printStackTrace();
		}
	}

	/**
	 * @return All loaded addons with their corresponding name
	 */
	public Map<String, AbstractReloadable> getLoadedAddons(){
		return this.manager.getAddons();
	}

	/**
	 * Retrieves an Addon by it's name by looping over the set of all loaded addons and checking their description files.
	 * @param name The name of the Addon to retrieve
	 * @return The addon retrieved, may be null
	 */
	public Addon getAddon(final String name){
		final AbstractReloadable ar = this.manager.getAddons().get(name);
		if(ar == null)
			return null;
		return ar.getAddon();
		/*for(final AbstractReloadable addon:this.manager.getAddons().values())
			if(addon.getAddon().getName().equals(name))
				return addon.getAddon();
		return null;*/
	}

	synchronized private ReloadableAddon getByAddon(final Addon addon){
		for(final AbstractReloadable reloadable:this.manager.getAddons().values())
			if(addon == reloadable.getAddon())
				return (ReloadableAddon) reloadable;
		return null;
	}

	/**
	 * @return The default AddonManager being used
	 */
	public AddonManager getAddonManager(){
		return this.manager;
	}

	@EventHandler(priority = EventPriority.LOWEST)
	public void onPlayerJoin(final PlayerJoinEvent e){
		this.users.put(e.getPlayer().getName(), new AddonUser(e.getPlayer()));
	}

	/**
	 * @return Whether AddonManager is in startup or not.
	 */
	public boolean isInStartup() {
		return this.inStartup;
	}
}
