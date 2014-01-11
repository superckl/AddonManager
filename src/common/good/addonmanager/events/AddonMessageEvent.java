package common.good.addonmanager.events;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import common.good.addonmanager.AbstractReloadable;

public class AddonMessageEvent extends Event{

	private AbstractReloadable addon;
	//TODO how to store message
	public AddonMessageEvent(AbstractReloadable addon){
		this.addon = addon;
	}
	
	@Override
	public HandlerList getHandlers() {
		return new HandlerList();
	}

}
