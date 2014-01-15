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

	private ReloadableAddon addon;
	
	private Map<String, Boolean> softAddons = new HashMap<String, Boolean>();
	private Map<String, Boolean> hardAddons = new HashMap<String, Boolean>();
	private Map<String, Boolean> softPlugins = new HashMap<String, Boolean>();
	private Map<String, Boolean> hardPlugins = new HashMap<String, Boolean>();
	
	private DependencyManager(){}
	
	public void pluginLoaded(Plugin plugin){
		//TODO
	}
	
	public void addonLoaded(Addon addon){
		//TODO
	}
	
	public DependencyStatus getDependencyStatus(){
		boolean hardSastisfied = true;
		for(boolean value:this.hardAddons.values())
			if(!value){
				hardSastisfied = false;
				break;
			}
		if(hardSastisfied)
			for(boolean value:this.hardPlugins.values())
				if(!value){
					hardSastisfied = false;
					break;
				}
		if(!hardSastisfied)
			return DependencyStatus.NONE;
		boolean softSastisfied = true;
		for(boolean value:this.softAddons.values())
			if(!value){
				softSastisfied = false;
				break;
			}
		if(softSastisfied)
			for(boolean value:this.softPlugins.values())
				if(!value){
					softSastisfied = false;
					break;
				}
		if(softSastisfied)
			return DependencyStatus.BOTH_RESOLVED;
		else
			return DependencyStatus.HARD_RESOLVED;
	}
	
	public static DependencyManager evaluate(AddonManagerPlugin plugin, ReloadableAddon addon, Set<Class<? extends Addon>> addonClasses, String name){
		try {
			DependencyManager manager = new DependencyManager();
			manager.addon = addon;
			for(Class<? extends Addon> clazz:addonClasses)
				for(Field field:clazz.getDeclaredFields()){
					field.setAccessible(true);
					if(!Map.class.isAssignableFrom(field.getType())){
						continue;
					}
					if(field.isAnnotationPresent(AddonDepends.class)){
						Map<String, DependencyType> depends = (Map<String, DependencyType>) field.get(null);
						for(Entry<String, DependencyType> entry:depends.entrySet()){
							boolean resolved = plugin.getAddon(entry.getKey()) != null; //TODO check more?
							if(entry.getValue() == DependencyType.HARD){
								manager.hardAddons.put(entry.getKey(), resolved);
							}else{
								manager.softAddons.put(entry.getKey(), resolved);
							}
						}
					}else if(field.isAnnotationPresent(PluginDepends.class)){
						Map<String, DependencyType> depends = (Map<String, DependencyType>) field.get(null);
						for(Entry<String, DependencyType> entry:depends.entrySet()){
							boolean resolved = plugin.getAddon(entry.getKey()) != null; //TODO check more?
							if(entry.getValue() == DependencyType.HARD){
								manager.hardPlugins.put(entry.getKey(), resolved);
							}else{
								manager.softPlugins.put(entry.getKey(), resolved);
							}
						}
					}
				}
			return manager;
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null; //TODO
	}
	
}
