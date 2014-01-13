
package common.good.addonmanager;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import org.bukkit.configuration.file.YamlConfiguration;

import common.good.addonmanager.exceptions.InvalidAddonException;
import java.util.Enumeration;



public class AddonDescriptionFile
{

    private final String name;
    private final String version;
    private final String[] authors;
    
	protected AddonDescriptionFile(AddonData data) throws IllegalArgumentException, InvalidAddonException
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
