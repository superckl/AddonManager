package org.sensationcraft.addonmanager;

import org.sensationcraft.addonmanager.exceptions.InvalidAddonException;

public class AddonDescriptionFile
{

	private final String name;
	private final String version;
	private final String[] authors;

	protected AddonDescriptionFile(final AddonData data) throws IllegalArgumentException, InvalidAddonException
	{
		this.name = data.name();
		this.version = data.version();
		this.authors = data.authors();
	}

	public String getName()
	{
		return this.name;
	}

	public String getVersion()
	{
		return this.version;
	}

	public String[] getAuthors()
	{
		return this.authors;
	}

	/*public List<String> getAddonDependencies(){
		return this.yml.getStringList("addon dependencies");
	}

	public List<String> getPluginDependencies(){
		return this.yml.getStringList("plugin dependencies");
	}*/
}
