package org.sensationcraft.addonmanager.events;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.sensationcraft.addonmanager.Addon;

/**
 * Called when an Addon is disabled
 * Note: this is called AFTER onDisable is called for the Addon, regardless of the addon throwing an error
 */
public class AddonDisableEvent extends Event{

	private final Addon addon;
	private static final HandlerList handlers = new HandlerList();

	public AddonDisableEvent(final Addon addon){
		this.addon = addon;
	}

	public Addon getAddon() {
		return this.addon;
	}

	@Override
	public HandlerList getHandlers() {
		return AddonDisableEvent.handlers;
	}

	public static HandlerList getHandlerList(){
		return AddonDisableEvent.handlers;
	}

}
