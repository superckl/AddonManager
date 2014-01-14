package org.sensationcraft.addonmanager.events;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.sensationcraft.addonmanager.Addon;

public class AddonEnableEvent extends Event{

	private final Addon addon;

	public AddonEnableEvent(final Addon addon){
		this.addon = addon;
	}

	public Addon getAddon() {
		return this.addon;
	}

	@Override
	public HandlerList getHandlers() {
		return new HandlerList();
	}

}
