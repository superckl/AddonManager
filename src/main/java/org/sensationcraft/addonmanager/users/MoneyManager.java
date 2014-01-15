package org.sensationcraft.addonmanager.users;

public abstract class MoneyManager {

	protected AddonUser user;

	public MoneyManager(final AddonUser user){
		this.user = user;
	}

	public abstract double getMoney();
	public abstract double setMoney(final double money);
	public abstract double subtract(final double money);
	public abstract double add(final double money);
}
