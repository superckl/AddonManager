package org.sensationcraft.addonmanager.events;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.sensationcraft.addonmanager.Addon;

/**
 * Can be used by Addons to communicate
 * @param <K> The format the message is in.
 */
public class AddonMessageEvent<K> extends Event{

	private final Addon addon;
	private K message; //Let them choose! :P
	private final static HandlerList handlers = new HandlerList();

	public AddonMessageEvent(final Addon addon, final K message){
		this.addon = addon;
		this.message = message;
	}

	public void setMessage(final K message){
		this.message = message;
	}

	public K getMessage(){
		return this.message;
	}

	public Addon getAddon(){
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
