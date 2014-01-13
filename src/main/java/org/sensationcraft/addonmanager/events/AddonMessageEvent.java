package org.sensationcraft.addonmanager.events;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.sensationcraft.addonmanager.AbstractReloadable;

public class AddonMessageEvent<K> extends Event{

	private final AbstractReloadable addon;
	private K message; //Let them choose! :P
	
	public AddonMessageEvent(final AbstractReloadable addon, K message){
		this.addon = addon;
		this.message = message;
	}
	
	public void setMessage(K message){
		this.message = message;
	}
	
	public K getMessage(){
		return this.message;
	}

	public AbstractReloadable getAddon(){
		return this.addon;
	}

	@Override
	public HandlerList getHandlers() {
		return new HandlerList();
	}

}
