package org.sensationcraft.addonmanager.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.server.PluginDisableEvent;
import org.bukkit.event.server.PluginEnableEvent;
import org.sensationcraft.addonmanager.AbstractReloadable;
import org.sensationcraft.addonmanager.AddonManagerPlugin;
import org.sensationcraft.addonmanager.ReloadableAddon;
import org.sensationcraft.addonmanager.events.AddonDisableEvent;
import org.sensationcraft.addonmanager.events.AddonEnableEvent;

public class EnableDisableListener implements Listener{

	private final AddonManagerPlugin plugin;

	public EnableDisableListener(final AddonManagerPlugin plugin){
		this.plugin = plugin;
	}

	@EventHandler(priority = EventPriority.MONITOR)
	public void onPluginEnable(final PluginEnableEvent e){
		//TODO if addon depends, enable addon if all depends sasitified
		for(final ReloadableAddon addon:this.plugin.getAddonManager().getDependingAddons())
			addon.getDependencyManager().pluginLoaded(e.getPlugin());
	}

	@EventHandler(priority = EventPriority.MONITOR)
	public void onPluginDisable(final PluginDisableEvent e){
		//TODO If addon depends, uh-oh, disable the addon if hard depend
		for(final ReloadableAddon addon:this.plugin.getAddonManager().getDependingAddons())
			addon.getDependencyManager().pluginUnloaded(e.getPlugin());
		for(final AbstractReloadable addon:this.plugin.getLoadedAddons().values())
			addon.getDependencyManager().pluginUnloaded(e.getPlugin());
	}

	@EventHandler(priority = EventPriority.MONITOR)
	public void onAddonEnable(final AddonEnableEvent e){
		//TODO
		for(final ReloadableAddon addon:this.plugin.getAddonManager().getDependingAddons())
			addon.getDependencyManager().addonLoaded(e.getAddon());
	}

	@EventHandler(priority = EventPriority.MONITOR)
	public void onAddonDisable(final AddonDisableEvent e){
		//TODO
		for(final ReloadableAddon addon:this.plugin.getAddonManager().getDependingAddons())
			addon.getDependencyManager().addonUnloaded(e.getAddon());
		for(final AbstractReloadable addon:this.plugin.getLoadedAddons().values())
			addon.getDependencyManager().addonUnloaded(e.getAddon());
	}

}
