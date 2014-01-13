package org.sensationcraft.addonmanager.storage;

import java.util.HashMap;
import java.util.Map;

public class Storage
{
	Map<String, Object> stor = new HashMap<String, Object>();

	/**
	 * Retrieves an object from persistant storage
	 * @param clazz Expected class
	 * @param key Key of the value requested
	 * @return Value of type T
	 * @throws ClassCastException if the value is not of the type clazz specified
	 * Check with hasKey before getting or you might get null!
	 * @see {@link #hasKey(Class, String)}
	 */
	public <T> T get(final Class<T> clazz, final String key) throws ClassCastException
	{
		return clazz.cast(this.stor.get(key));
	}

	/**
	 * Checks if the given key exists in persistant storage
	 * @param clazz Expected class
	 * @param key Key of the value requested
	 * @return Whether or not the object was found
	 * @throws ClassCastException if the value is not of the type clazz specified
	 */
	public <T> boolean hasKey(final Class<T> clazz, final String key) throws ClassCastException
	{
		if(this.stor.containsKey(key))
			return clazz.isInstance(this.stor.get(key));
		return true;
	}

	/**
	 * Stores an Object in persistant storage
	 * @param key The key to store the Object under
	 * @param val The object to store
	 */
	public void set(final String key, final Object val)
	{
		this.stor.put(key, val);
	}
}
