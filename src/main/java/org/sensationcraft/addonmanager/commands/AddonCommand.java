package org.sensationcraft.addonmanager.commands;

import java.util.List;

import org.bukkit.command.Command;

public abstract class AddonCommand extends Command{

	protected AddonCommand(String name) {
		super(name);
	}
	
	protected AddonCommand(String name, String description,
			String usageMessage, List<String> aliases) {
		super(name, description, usageMessage, aliases);
	}

	public abstract String getFallbackPrefix();
}
