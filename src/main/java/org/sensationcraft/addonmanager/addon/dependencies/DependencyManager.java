package org.sensationcraft.addonmanager.addon.dependencies;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.bukkit.plugin.Plugin;
import org.sensationcraft.addonmanager.Addon;
import org.sensationcraft.addonmanager.AddonManagerPlugin;
import org.sensationcraft.addonmanager.ReloadableAddon;

public class DependencyManager {

	private final Map<String, Boolean> softAddons = new HashMap<String, Boolean>();
	private final Map<String, Boolean> hardAddons = new HashMap<String, Boolean>();
	private final Map<String, Boolean> softPlugins = new HashMap<String, Boolean>();
	private final Map<String, Boolean> hardPlugins = new HashMap<String, Boolean>();

	private DependencyManager(){}

	public void pluginLoaded(final Plugin plugin){
		//TODO
	}

	public void addonLoaded(final Addon addon){
		//TODO
	}

	public DependencyStatus getDependencyStatus(){
		boolean hardSastisfied = true;
		for(final boolean value:this.hardAddons.values())
			if(!value){
				hardSastisfied = false;
				break;
			}
		if(hardSastisfied)
			for(final boolean value:this.hardPlugins.values())
				if(!value){
					hardSastisfied = false;
					break;
				}
		if(!hardSastisfied)
			return DependencyStatus.NONE;
		boolean softSastisfied = true;
		for(final boolean value:this.softAddons.values())
			if(!value){
				softSastisfied = false;
				break;
			}
		if(softSastisfied)
			for(final boolean value:this.softPlugins.values())
				if(!value){
					softSastisfied = false;
					break;
				}
		if(softSastisfied)
			return DependencyStatus.BOTH_RESOLVED;
		else
			return DependencyStatus.HARD_RESOLVED;
	}

	public static DependencyManager evaluate(final AddonManagerPlugin plugin, final ReloadableAddon addon, final Set<Class<? extends Addon>> addonClasses, final String name){
		try {
			final DependencyManager manager = new DependencyManager();
			for(final Class<? extends Addon> clazz:addonClasses)
				for(final Field field:clazz.getDeclaredFields()){
					field.setAccessible(true);
					if(!Map.class.isAssignableFrom(field.getType()))
						continue;
					if(field.isAnnotationPresent(AddonDepends.class)){
						final Map<String, DependencyType> depends = (Map<String, DependencyType>) field.get(null);
						for(final Entry<String, DependencyType> entry:depends.entrySet()){
							final boolean resolved = plugin.getAddon(entry.getKey()) != null; //TODO check more?
							if(entry.getValue() == DependencyType.HARD)
								manager.hardAddons.put(entry.getKey(), resolved);
							else
								manager.softAddons.put(entry.getKey(), resolved);
						}
					}else if(field.isAnnotationPresent(PluginDepends.class)){
						final Map<String, DependencyType> depends = (Map<String, DependencyType>) field.get(null);
						for(final Entry<String, DependencyType> entry:depends.entrySet()){
							final boolean resolved = plugin.getAddon(entry.getKey()) != null; //TODO check more?
							if(entry.getValue() == DependencyType.HARD)
								manager.hardPlugins.put(entry.getKey(), resolved);
							else
								manager.softPlugins.put(entry.getKey(), resolved);
						}
					}
				}
			return manager;
		} catch (final SecurityException e) {
			e.printStackTrace();
		} catch (final IllegalArgumentException e) {
			e.printStackTrace();
		} catch (final IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null; //TODO
	}

}
