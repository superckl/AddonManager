package common.good.addonmanager;

import java.lang.reflect.Field;
import java.util.Map;

import org.bukkit.command.Command;
import org.bukkit.command.CommandMap;
import org.bukkit.plugin.java.JavaPlugin;

import common.good.addonmanager.storage.Storage;


public class AddonManagerPlugin extends JavaPlugin{

	private static AddonManagerPlugin instance;
	private final Storage data = new Storage();
	private AddonManager manager;
	private CommandMap bukkitCommandMap;

	@Override
	public void onEnable(){
		AddonManagerPlugin.instance = this;
		this.getLogger().info("Registering listeners...");
		this.getLogger().info("Loading addons...");
		this.manager = new AddonManager(this);
		this.getServer().getPluginCommand("addons").setExecutor(this.manager);
	}

	@Override
	public void onDisable(){
		this.getLogger().info("Unloading addons...");
		this.manager.destroy();
	}

	public static AddonManagerPlugin getInstance(){
		return AddonManagerPlugin.instance;
	}

	public Storage getStorage(){
		return this.data;
	}
	
	/**
	 * Registers a command with bukkit. If you have already used this method, and this plugin has not been reloaded, you can pass null for the CraftSerevr class.
	 * Make sure you take into the account the possibility of your addon being unloaded.
	 * @param command The command to register.
	 * @param fallbackPrefix A prefix that bukkit will use if your command's name is already taken. Pass null for no prefix.
	 * @param craftServer The class that represents CraftServer. You must provide it to ensure version compatibility of AddonManager. ProtocolLib has a version safe method for this.
	 * @return Whether or not the registration was succesful.
	 * @throws SecurityException 
	 * @throws NoSuchFieldException 
	 * @throws IllegalAccessException 
	 * @throws IllegalArgumentException 
	 */
	public boolean registerCommand(Command command, String fallbackPrefix, Class<?> craftServer) throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException{
		//if(!craftServer.isAssignableFrom(this.getServer().getClass()))
		//	return false;
		if(this.bukkitCommandMap == null){
			final Field f = this.getServer().getClass().getDeclaredField("commandMap");
			f.setAccessible(true);
			this.bukkitCommandMap = (CommandMap) f.get(this.getServer());
		}
		if()
		return this.bukkitCommandMap.register(fallbackPrefix == null ? "":fallbackPrefix, command);
	}
	
	public Map<String, AbstractReloadable> getLoadedAddons(){
		return this.manager.getAddons();
	}

	public AbstractReloadable getAddon(final String name){
		for(final AbstractReloadable addon:this.manager.getAddons().values())
			if(addon.getAddon().getName().equals(name))
				return addon;
		return null;
	}
}
