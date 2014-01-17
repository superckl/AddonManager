package org.sensationcraft.addonmanager.events;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.sensationcraft.addonmanager.users.AddonUser;

public class MoneyChangeEvent extends Event{

	private double to;
	private final double from;
	private final AddonUser user;
	private final static HandlerList handlers = new HandlerList();

	public MoneyChangeEvent(final AddonUser user, final double to, final double from){
		this.user = user;
		this.to = to;
		this.from = from;
	}

	@Override
	public HandlerList getHandlers() {
		return MoneyChangeEvent.handlers;
	}
	
	public static HandlerList getHandlerList(){
		return handlers;
	}

	public double getTo() {
		return this.to;
	}

	public void setTo(final double to) {
		this.to = to;
	}

	public double getFrom() {
		return this.from;
	}

	public AddonUser getUser() {
		return this.user;
	}

}
