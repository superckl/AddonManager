package org.sensationcraft.addonmanager.addon.dependencies;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.bukkit.plugin.Plugin;
import org.sensationcraft.addonmanager.Addon;
import org.sensationcraft.addonmanager.AddonManagerPlugin;
import org.sensationcraft.addonmanager.ReloadableAddon;
import org.sensationcraft.addonmanager.exceptions.InvalidAddonException;
import org.sensationcraft.addonmanager.exceptions.UnknownAddonException;

public class DependencyManager {
	
	private final ReloadableAddon addon;

	private final Map<String, Boolean> softAddons = new HashMap<String, Boolean>();
	private final Map<String, Boolean> hardAddons = new HashMap<String, Boolean>();
	private final Map<String, Boolean> softPlugins = new HashMap<String, Boolean>();
	private final Map<String, Boolean> hardPlugins = new HashMap<String, Boolean>();
	
	private DependencyStatus currrentStatus;

	private DependencyManager(final ReloadableAddon addon){
		this.addon = addon;
	}
	
	public Set<String> getRemainingHardDepends(){
		Set<String> depends = new HashSet<String>();
		for(Entry<String, Boolean> entry:this.hardAddons.entrySet())
			if(entry.getValue().booleanValue() == false)
				depends.add(entry.getKey());
		for(Entry<String, Boolean> entry:this.hardPlugins.entrySet())
			if(entry.getValue().booleanValue() == false)
				depends.add(entry.getKey());
		return depends;
	}

	public void pluginLoaded(final Plugin plugin){
		try {
			Boolean sastisfied = this.hardPlugins.get(plugin.getName());
			if(sastisfied != null){
				if(sastisfied.booleanValue() == true){
					//TODO wha? It's already loaded?
				}
				this.hardPlugins.put(plugin.getName(), true);
				//New dependency detected, reassess status
				DependencyStatus status = this.currrentStatus;
				if(this.getDependencyStatus(true) == DependencyStatus.HARD_RESOLVED && status == DependencyStatus.NONE && !AddonManagerPlugin.getInstance().isInStartup())
					this.addon.load(AddonManagerPlugin.getInstance(), false);
				else if(this.currrentStatus == DependencyStatus.BOTH_RESOLVED && status == DependencyStatus.NONE)
					//The addon must have been unloaded with another one it depends on
					this.addon.load(AddonManagerPlugin.getInstance(), false);
				return;
			}
			sastisfied = this.softPlugins.get(plugin.getName());
			if(sastisfied != null){
				if(sastisfied.booleanValue() == true){
					//TODO wha? It's already loaded?
				}
				this.softPlugins.put(plugin.getName(), true);
				DependencyStatus status = this.currrentStatus;
				if(this.getDependencyStatus(false) == DependencyStatus.BOTH_RESOLVED && status == DependencyStatus.HARD_RESOLVED && AddonManagerPlugin.getInstance().isInStartup())
					this.addon.load(AddonManagerPlugin.getInstance(), false);
			}
		} catch (UnknownAddonException e) {
			e.printStackTrace();
		} catch (InvalidAddonException e) {
			e.printStackTrace();
		}
	}

	public void addonLoaded(final Addon addon){
		try {
			Boolean sastisfied = this.hardAddons.get(addon.getName());
			if(sastisfied != null){
				if(sastisfied.booleanValue() == true){
					//TODO wha? It's already loaded?
				}
				this.hardAddons.put(addon.getName(), true);
				//New dependency detected, reassess status
				DependencyStatus status = this.currrentStatus;
				if(this.getDependencyStatus(true) == DependencyStatus.HARD_RESOLVED && status == DependencyStatus.NONE && !AddonManagerPlugin.getInstance().isInStartup())
					this.addon.load(AddonManagerPlugin.getInstance(), false);
				else if(this.currrentStatus == DependencyStatus.BOTH_RESOLVED && status == DependencyStatus.NONE)
					//The addon must have been unloaded with another one it depends on
					this.addon.load(AddonManagerPlugin.getInstance(), false);
				return;
			}
			sastisfied = this.softAddons.get(addon.getName());
			if(sastisfied != null){
				if(sastisfied.booleanValue() == true){
					//TODO wha? It's already loaded?
				}
				this.softAddons.put(addon.getName(), true);
				DependencyStatus status = this.currrentStatus;
				if(this.getDependencyStatus(false) == DependencyStatus.BOTH_RESOLVED && status == DependencyStatus.HARD_RESOLVED && AddonManagerPlugin.getInstance().isInStartup())
					this.addon.load(AddonManagerPlugin.getInstance(), false);
			}
		} catch (UnknownAddonException e) {
			e.printStackTrace();
		} catch (InvalidAddonException e) {
			e.printStackTrace();
		}
	}
	
	public void pluginUnloaded(final Plugin plugin){
		Boolean sastisfied = this.hardPlugins.get(plugin.getName());
		if(sastisfied != null){
			if(sastisfied.booleanValue() == false){
				//Wha?
			}
			this.hardPlugins.put(plugin.getName(), false);
			DependencyStatus status = this.currrentStatus;
			if(this.getDependencyStatus(true) == DependencyStatus.NONE && status == DependencyStatus.HARD_RESOLVED || status == DependencyStatus.BOTH_RESOLVED)
				//Darn, gotta unload it.
				this.addon.unload();
			return;
		}
		sastisfied = this.softPlugins.get(plugin.getName());
		if(sastisfied != null){
			if(sastisfied == false){
				//Wha?
			}
			this.softPlugins.put(plugin.getName(), false);
		}
	}
	
	public void addonUnloaded(final Addon addon){
		Boolean sastisfied = this.hardAddons.get(addon.getName());
		if(sastisfied != null){
			if(sastisfied.booleanValue() == false){
				//Wha?
			}
			this.hardAddons.put(addon.getName(), false);
			DependencyStatus status = this.currrentStatus;
			if(this.getDependencyStatus(true) == DependencyStatus.NONE && status == DependencyStatus.HARD_RESOLVED || status == DependencyStatus.BOTH_RESOLVED)
				//Darn, gotta unload it.
				this.addon.unload();
			return;
		}
		sastisfied = this.softAddons.get(addon.getName());
		if(sastisfied != null){
			if(sastisfied == false){
				//Wha?
			}
			this.softAddons.put(addon.getName(), false);
		}
	}

	public DependencyStatus getDependencyStatus(boolean hardOnly){
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
		if(!hardSastisfied){
			this.currrentStatus = DependencyStatus.NONE;
			return DependencyStatus.NONE;
		}else if(hardSastisfied && hardOnly){
			if(this.currrentStatus == DependencyStatus.NONE)
				this.currrentStatus = DependencyStatus.HARD_RESOLVED;
			return DependencyStatus.HARD_RESOLVED;
		}
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
		if(softSastisfied){
			this.currrentStatus = DependencyStatus.BOTH_RESOLVED;
			return DependencyStatus.BOTH_RESOLVED;
		}else{
			this.currrentStatus = DependencyStatus.HARD_RESOLVED;
			return DependencyStatus.HARD_RESOLVED;
		}
	}
	
	public DependencyStatus getCurrentStatus(){
		return this.currrentStatus;
	}

	public static DependencyManager evaluate(final AddonManagerPlugin plugin, final ReloadableAddon addon, final Class<? extends Addon> addonClass, final String name){
		try {
			final DependencyManager manager = new DependencyManager(addon);
				for(final Field field:addonClass.getDeclaredFields()){
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
			e.printStackTrace();
		}
		return null; //TODO
	}

}
