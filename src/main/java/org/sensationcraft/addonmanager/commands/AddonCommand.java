package org.sensationcraft.addonmanager.commands;

import java.util.List;

import org.bukkit.command.Command;

public abstract class AddonCommand extends Command{

	protected AddonCommand(final String name) {
		super(name);
	}

	protected AddonCommand(final String name, final String description,
			final String usageMessage, final List<String> aliases) {
		super(name, description, usageMessage, aliases);
	}

	public abstract String getFallbackPrefix();
}
