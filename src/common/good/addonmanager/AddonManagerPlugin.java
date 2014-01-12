package common.good.addonmanager;

import java.lang.reflect.Field;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.bukkit.command.Command;
import org.bukkit.command.CommandMap;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

import common.good.addonmanager.exceptions.InvalidAddonException;
import common.good.addonmanager.storage.Storage;


public class AddonManagerPlugin extends JavaPlugin{

	private static AddonManagerPlugin instance;
	private final Storage data = new Storage();
	private AddonManager manager;
	private CommandMap bukkitCommandMap;
	private Map<String, Command> knownCommands;
	private Set<String> aliases;
	
	private final String aliasMatcher = "[%s:]*[%s]";

	@Override
	public void onEnable(){
		AddonManagerPlugin.instance = this;
		this.saveDefaultConfig();
		this.getLogger().info("Registering listeners...");
		this.getLogger().info("Loading addons...");
		this.manager = new AddonManager(this, this.getConfig().getBoolean("Use Permissions"));
		this.getServer().getPluginCommand("addons").setExecutor(this.manager);
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
	 * @return Whether or not the registration was succesful.
	 * @see {@link org.bukkit.command.CommandMap #register(String, Command)}
	 * @throws SecurityException
	 * @throws NoSuchFieldException
	 * @throws IllegalAccessException
	 * @throws IllegalArgumentException
	 * @throws InvalidAddonException 
	 */
	public boolean registerCommand(Addon addon, String fallbackPrefix, final Command command) throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException, InvalidAddonException{
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
		if(fallbackPrefix == null || fallbackPrefix.isEmpty())
			throw new IllegalArgumentException("Addon "+addon.getName()+" tried to register a command with a null or empty fallback prefix!");
		ReloadableAddon reloadable = this.getByAddon(addon);
		if(reloadable == null)
			throw new InvalidAddonException("No corresponding ReloadableAddon found for "+addon.getName());
		reloadable.addCommand(command, fallbackPrefix);
		return this.bukkitCommandMap.register(fallbackPrefix == null ? "":fallbackPrefix, command);
	}
	
	/**
	 * Removes a registered command from Bukkit
	 * @param addon The addon requesting the unregistration
	 * @param command The command to unregister
	 * @param prefix The fallback prefix used when registering the command
	 * @see {@link #registerCommand(Addon, String, Command)}
	 * @throws InvalidAddonException
	 */
	public void unregisterCommand(Addon addon, Command command, String prefix) throws InvalidAddonException{
		ReloadableAddon reloadable = this.getByAddon(addon);
		if(reloadable == null)
			throw new InvalidAddonException("No corresponding ReloadableAddon found for "+addon.getName());
		this.unregisterCommand(reloadable, command, prefix);
	}
	
	void unregisterCommand(ReloadableAddon addon, Command command, String prefix){
		this.removeFromBukkit(command, prefix);
		addon.removeCommand(command);
	}
	
	private void removeFromBukkit(Command command, String prefix){
		this.knownCommands.remove(command.getLabel());
		for(String alias:command.getAliases()){
			Iterator<String> it = this.aliases.iterator();
			while(it.hasNext()){
				if(it.next().matches(String.format(this.aliasMatcher, prefix, alias))){
					it.remove();
					continue;
				}
			}
		}
	}
	/**
	 * Registeres a Listener with Bukkit
	 * @param addon The addon requesting the registration
	 * @param listener The listener to register
	 * @throws InvalidAddonException 
	 * @see {@link org.bukkit.plugin.PluginManager #registerEvents(Listener, org.bukkit.plugin.Plugin)}
	 */
	public void registerListener(Addon addon, Listener listener) throws InvalidAddonException{
		ReloadableAddon reloadable = this.getByAddon(addon);
		if(reloadable == null){
			throw new InvalidAddonException("No corresponding ReloadableAddon found for "+addon.getName());
		}
		this.getServer().getPluginManager().registerEvents(listener, this);
		reloadable.addListener(listener);
	}
	
	/**
	 * Unregisters a listener from Bukkit
	 * @param addon The addon requesting the unregistration
	 * @param listener The listener to unregister
	 * @see {@link #registerListener(Addon, Listener)}
	 * @throws InvalidAddonException
	 */
	public void unregisterListener(Addon addon, Listener listener) throws InvalidAddonException{
		ReloadableAddon reloadable = this.getByAddon(addon);
		if(reloadable == null){
			throw new InvalidAddonException("No corresponding ReloadableAddon found for "+addon.getName());
		}
		HandlerList.unregisterAll(listener);
		reloadable.removeListener(listener);
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
		for(final AbstractReloadable addon:this.manager.getAddons().values())
			if(addon.getAddon().getName().equals(name))
				return addon.getAddon();
		return null;
	}
	
	private ReloadableAddon getByAddon(Addon addon){
		for(AbstractReloadable reloadable:this.manager.addons.values()){
			if(addon == reloadable.getAddon())
				return (ReloadableAddon) reloadable;
		}
		return null;
	}
}
