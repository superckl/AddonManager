package common.good.addonmanager;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.java.JavaPlugin;

import common.good.addonmanager.storage.Storage;


public class AddonManagerPlugin extends JavaPlugin implements Listener{

	private static AddonManagerPlugin instance;
	private final Storage data = new Storage();
	private AddonManager manager;
	private final Map<String, AddonUser> addonUsers = new HashMap<String, AddonUser>();
	
	@Override
	public void onEnable(){
		instance = this;
		this.getLogger().info("Registering listeners...");
		this.getServer().getPluginManager().registerEvents(this, this);
		this.getLogger().info("Loading addons...");
		this.manager = new AddonManager(this);
		this.getServer().getPluginCommand("addons").setExecutor(this.manager);
	}
	
	@Override
	public void onDisable(){
		this.getLogger().info("Unloading addons...");
		this.manager.destroy();
	}
	
	@EventHandler(priority = EventPriority.LOWEST)
	public void onPlayerJoin(final PlayerJoinEvent e){
		this.addonUsers.put(e.getPlayer().getName(), new AddonUser(e.getPlayer()));
	}
	@EventHandler(priority = EventPriority.MONITOR)
	public void onPlayerQuit(final PlayerQuitEvent e){
		this.addonUsers.remove(e.getPlayer().getName());
	}
	
	public static AddonManagerPlugin getInstance(){
		return instance;
	}
	
	public Storage getStorage(){
		return this.data;
	}
	
	public static AddonUser getUser(final String name){
		return AddonManagerPlugin.getInstance().addonUsers.get(name);
	}
	
	public static Map<String, AddonUser> getUsers(){
		return AddonManagerPlugin.getInstance().addonUsers;
	}
	
	public Map<String, AbstractReloadable> getLoadedAddons(){
		return this.manager.getAddons();
	}
	
	public AbstractReloadable getAddon(String name){
		return this.manager.getAddons().get(name);
	}
}
