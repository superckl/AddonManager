
package common.good.addonmanager;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;


import org.bukkit.configuration.file.YamlConfiguration;

import common.good.addonmanager.exceptions.InvalidAddonException;



/**
 *
 * @author DarkSeraphim
 */
public class AddonDescriptionFile
{
	private final YamlConfiguration yml;

	protected AddonDescriptionFile(final File file) throws IllegalArgumentException, InvalidAddonException
	{
		YamlConfiguration y = null;
		if(file == null)
			throw new IllegalArgumentException("Addon file cannot be null");
		if(!file.exists())
			throw new IllegalArgumentException("Addon file is nonexistant");
		if(!file.getName().endsWith(".jar"))
			throw new IllegalArgumentException("Addon file is not a jar");
		JarFile jf = null;
		try
		{
			jf = new JarFile(file);
			final JarEntry je = jf.getJarEntry("addon.yml");
			if(je == null)
				throw new InvalidAddonException("Invalid addon: missing addon.yml");
			final InputStream in = jf.getInputStream(je);
			if(in == null)
				throw new InvalidAddonException("Invalid addon: missing addon.yml");
			y = YamlConfiguration.loadConfiguration(in);
		}
		catch(final Exception ex)
		{
			ex.printStackTrace();
		}
		finally{
			try{
				if(jf != null)
					jf.close();
			}catch(final IOException e){
				AddonManagerPlugin.getInstance().getLogger().warning("Failed to close a JarFile resource!");
			}
		}
		if(y != null)
			this.yml = y;
		else
			this.yml = new YamlConfiguration();
	}

	public String getMainClass()
	{
		return this.yml.getString("main", "");
	}

	public String getName()
	{
		return this.yml.getString("name", "");
	}
}
