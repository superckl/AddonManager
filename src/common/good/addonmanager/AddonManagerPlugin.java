package common.good.addonmanager;

import java.util.Map;

import org.bukkit.plugin.java.JavaPlugin;

import common.good.addonmanager.storage.Storage;


public class AddonManagerPlugin extends JavaPlugin{

	private static AddonManagerPlugin instance;
	private final Storage data = new Storage();
	private AddonManager manager;

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
