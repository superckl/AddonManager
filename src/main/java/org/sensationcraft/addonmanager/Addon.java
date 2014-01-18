package org.sensationcraft.addonmanager;

import java.util.logging.Logger;

public abstract class Addon
{

	private final AddonManagerPlugin scg;

	private final AddonDescriptionFile desc;
    
    private final Logger logger;

	public Addon(final AddonManagerPlugin scg, final AddonDescriptionFile desc)
	{
		this.scg = scg;
		this.desc = desc;
        this.logger = Logger.getLogger("AddonManager:"+desc.getName());
	}

	/**
	 * Called when an addon is enabled
	 */
	public void onEnable()
	{

	}

	/**
	 * Called when an addon is enabled
	 */
	public void onDisable()
	{

	}

	/**
	 * @return The instance of AddonManagerPlugin being used
	 */
	public AddonManagerPlugin getPlugin()
	{
		return this.scg;
	}

	/**
	 * @return The name of this addon
	 */
	public String getName()
	{
		return this.desc.getName();
	}

	/**
	 * Stores an Object in persistant storage
	 * @param key The key to store the Object under
	 * @param value The object to store
	 */
	public void setData(final String key, final Object value)
	{
		this.getPlugin().getStorage().set(key, value);
	}

	/**
	 * Checks if the given key exists in persistant storage
	 * @param clazz Expected class
	 * @param key Key of the value requested
	 * @return Whether or not the object was found
	 */
	public <T> boolean hasData(final Class<T> clazz, final String key)
	{
		return this.getPlugin().getStorage().hasKey(clazz, key);
	}

	/**
	 * Retrieves an object from persistant storage
	 * @param clazz Expected class
	 * @param key Key of the value requested
	 * @return Value of type T
	 * Check with hasKey before getting or you might get null!
	 * @see {@link #hasData(Class, String)}
	 */
	public <T> T getData(final Class<T> clazz, final String key)
	{
		return this.getPlugin().getStorage().get(clazz, key);
	}
    
	/**
	 * @return The logger assigned to this addon
	 */
    public Logger getLogger()
    {
        return this.logger;
    }
}
