package org.sensationcraft.addonmanager.users;

public abstract class MoneyManager {

	protected AddonUser user;
	
	public MoneyManager(AddonUser user){
		this.user = user;
	}
	
	public abstract double getMoney();
	public abstract double setMoney(double money);
	public abstract double subtract(double money);
	public abstract double add(double money);
}
