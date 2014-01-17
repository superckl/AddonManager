package org.sensationcraft.addonmanager.events;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.sensationcraft.addonmanager.Addon;

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
		return handlers;
	}

}
