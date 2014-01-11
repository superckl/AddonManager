package common.good.addonmanager.storage;

import java.util.HashMap;
import java.util.Map;

public class Storage
{
	Map<String, Object> stor = new HashMap<String, Object>();

	/**
	 * 
	 * @param clazz Expected class
	 * @param key Key of the value requested
	 * @return Value of type T
	 * @throws IllegalStateException
	 * @throws ClassCastException if the value is not of the type clazz specified
	 * Check with hasKey before get!
	 */
	public <T> T get(final Class<T> clazz, final String key) throws IllegalStateException, ClassCastException
	{
		return clazz.cast(this.stor.get(key));
	}

	public <T> boolean hasKey(final Class<T> clazz, final String key) throws IllegalStateException
	{
		if(this.stor.containsKey(key))
			return clazz.isInstance(this.stor.get(key));
		return true;
	}

	public void set(final String key, final Object val)
	{
		this.stor.put(key, val);
	}
}
