package org.sensationcraft.addonmanager.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.server.PluginDisableEvent;
import org.bukkit.event.server.PluginEnableEvent;
import org.sensationcraft.addonmanager.events.AddonDisableEvent;
import org.sensationcraft.addonmanager.events.AddonEnableEvent;

public class EnableDisableListener implements Listener{

	@EventHandler(priority = EventPriority.MONITOR)
	public void onPluginEnable(PluginEnableEvent e){
		//TODO if addon depends, enable addon if all depends sasitified
	}
	
	@EventHandler(priority = EventPriority.MONITOR)
	public void onPluginDisable(PluginDisableEvent e){
		//TODO If addon depends, uh-oh, disable the addon if hard depend
	}
	
	@EventHandler(priority = EventPriority.MONITOR)
	public void onAddonEnable(AddonEnableEvent e){
		//TODO
	}
	
	@EventHandler(priority = EventPriority.MONITOR)
	public void onAddonDisable(AddonDisableEvent e){
		//TODO
	}
	
}
