package org.sensationcraft.addonmanager.users;

import java.math.BigDecimal;

import org.bukkit.Bukkit;
import org.sensationcraft.addonmanager.events.MoneyChangeEvent;

public class SimpleMoneyManager {

	private final AddonUser user;
	private BigDecimal money;

	public SimpleMoneyManager(final AddonUser user){
		this.user = user;
		//TODO init eco info
	}

	public BigDecimal add(final double money){
		final MoneyChangeEvent e = new MoneyChangeEvent(this.user, this.money.add(new BigDecimal(money)), this.money);
		Bukkit.getPluginManager().callEvent(e);
		this.money = e.getTo();
		return this.money;
	}

	public BigDecimal subtract(final double money){
		final MoneyChangeEvent e = new MoneyChangeEvent(this.user, this.money.subtract(new BigDecimal(money)), this.money);
		Bukkit.getPluginManager().callEvent(e);
		this.money = e.getTo();
		return this.money;
	}

	public BigDecimal getMoney() {
		return this.money;
	}

	public void setMoney(final BigDecimal money) {
		final MoneyChangeEvent e = new MoneyChangeEvent(this.user, money, this.money);
		Bukkit.getPluginManager().callEvent(e);
		this.money = e.getTo();
	}

}
