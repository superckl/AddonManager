package org.sensationcraft.addonmanager.events;

import java.math.BigDecimal;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.sensationcraft.addonmanager.users.AddonUser;

public class MoneyChangeEvent extends Event{

	private BigDecimal to;
	private final BigDecimal from;
	private final AddonUser user;

	public MoneyChangeEvent(final AddonUser user, final BigDecimal to, final BigDecimal from){
		this.user = user;
		this.to = to;
		this.from = from;
	}

	@Override
	public HandlerList getHandlers() {
		return new HandlerList();
	}

	public BigDecimal getTo() {
		return this.to;
	}

	public void setTo(final BigDecimal to) {
		this.to = to;
	}

	public BigDecimal getFrom() {
		return this.from;
	}

	public AddonUser getUser() {
		return this.user;
	}

}
