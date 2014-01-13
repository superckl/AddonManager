package org.sensationcraft.addonmanager.events;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.sensationcraft.addonmanager.AbstractReloadable;

public class AddonMessageEvent extends Event{

	private final AbstractReloadable addon;
	//TODO how to store message
	public AddonMessageEvent(final AbstractReloadable addon){
		this.addon = addon;
	}

	public AbstractReloadable getAddon(){
		return this.addon;
	}

	@Override
	public HandlerList getHandlers() {
		return new HandlerList();
	}

}
