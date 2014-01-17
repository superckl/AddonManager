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
    
    public Logger getLogger()
    {
        return this.logger;
    }
}
