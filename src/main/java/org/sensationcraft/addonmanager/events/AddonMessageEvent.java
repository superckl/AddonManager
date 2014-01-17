package org.sensationcraft.addonmanager.events;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.sensationcraft.addonmanager.AbstractReloadable;

public class AddonMessageEvent<K> extends Event{

	private final AbstractReloadable addon;
	private K message; //Let them choose! :P
	private final static HandlerList handlers = new HandlerList();

	public AddonMessageEvent(final AbstractReloadable addon, final K message){
		this.addon = addon;
		this.message = message;
	}

	public void setMessage(final K message){
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
		return AddonMessageEvent.handlers;
	}

	public static HandlerList getHandlerList(){
		return AddonMessageEvent.handlers;
	}

}
