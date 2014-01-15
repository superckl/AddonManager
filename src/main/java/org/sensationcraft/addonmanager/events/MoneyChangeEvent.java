package org.sensationcraft.addonmanager.events;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.sensationcraft.addonmanager.users.AddonUser;

public class MoneyChangeEvent extends Event{

	private double to;
	private final double from;
	private final AddonUser user;

	public MoneyChangeEvent(final AddonUser user, final double to, final double from){
		this.user = user;
		this.to = to;
		this.from = from;
	}

	@Override
	public HandlerList getHandlers() {
		return new HandlerList();
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
