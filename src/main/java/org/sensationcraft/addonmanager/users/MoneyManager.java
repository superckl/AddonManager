package org.sensationcraft.addonmanager.users;

/**
 * Manages money for a specific user
 */
public abstract class MoneyManager {

	protected AddonUser user;

	public MoneyManager(final AddonUser user){
		this.user = user;
	}

	/**
	 * @return The user's money
	 */
	public abstract double getMoney();
	
	/**
	 * Set's a user's balance
	 * @return The user's new balance
	 */
	public abstract double setMoney(final double money);
	
	/**
	 * Subtracts from a user's balance
	 * @return The user's new balance
	 */
	public abstract double subtract(final double money);
	
	/**
	 * Adds to a user's balance
	 * @return The user's new balance
	 */
	public abstract double add(final double money);
}
