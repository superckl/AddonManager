package org.sensationcraft.addonmanager;

public abstract class Addon
{

	private final AddonManagerPlugin scg;

	private final AddonDescriptionFile desc;

	public Addon(final AddonManagerPlugin scg, final AddonDescriptionFile desc)
	{
		this.scg = scg;
		this.desc = desc;
	}

	public void onEnable()
	{

	}

	public void onDisable()
	{

	}

	public AddonManagerPlugin getPlugin()
	{
		return this.scg;
	}

	public String getName()
	{
		return this.desc.getName();
	}

	public void setData(final String key, final Object value)
	{
		this.getPlugin().getStorage().set(key, value);
	}

	public <T> boolean hasData(final Class<T> clazz, final String key)
	{
		return this.getPlugin().getStorage().hasKey(clazz, key);
	}

	public <T> T getData(final Class<T> clazz, final String key)
	{
		return this.getPlugin().getStorage().get(clazz, key);
	}
}
