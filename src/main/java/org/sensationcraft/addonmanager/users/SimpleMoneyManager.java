package org.sensationcraft.addonmanager.users;

import org.bukkit.Bukkit;
import org.sensationcraft.addonmanager.events.MoneyChangeEvent;

public class SimpleMoneyManager extends MoneyManager{

	private double money;

	public SimpleMoneyManager(final AddonUser user){
		super(user);
		//TODO init eco info
	}

	@Override
	public double getMoney() {
		return this.money;
	}

	@Override
	public double setMoney(final double money) {
		final MoneyChangeEvent e = new MoneyChangeEvent(this.user, money, this.money);
		Bukkit.getPluginManager().callEvent(e);
		this.money = e.getTo();
		return this.money;
	}

	@Override
	public double subtract(final double money) {
		final MoneyChangeEvent e = new MoneyChangeEvent(this.user, this.money-money, this.money);
		Bukkit.getPluginManager().callEvent(e);
		this.money = e.getTo();
		return this.money;
	}

	@Override
	public double add(final double money) {
		final MoneyChangeEvent e = new MoneyChangeEvent(this.user, this.money+money, this.money);
		Bukkit.getPluginManager().callEvent(e);
		this.money = e.getTo();
		return this.money;
	}

}
