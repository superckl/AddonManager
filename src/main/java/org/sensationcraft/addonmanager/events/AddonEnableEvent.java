package org.sensationcraft.addonmanager.events;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.sensationcraft.addonmanager.Addon;

/**
 * Called when an Addon is enabled
 * Note: this is called AFTER onEnable is called for the Addon, but only if the Addon did not throw an error
 */
public class AddonEnableEvent extends Event{

	private final Addon addon;
	private final static HandlerList handlers = new HandlerList();

	public AddonEnableEvent(final Addon addon){
		this.addon = addon;
	}

	public Addon getAddon() {
		return this.addon;
	}

	@Override
	public HandlerList getHandlers() {
		return AddonEnableEvent.handlers;
	}

	public static HandlerList getHandlerList(){
		return AddonEnableEvent.handlers;
	}

}
