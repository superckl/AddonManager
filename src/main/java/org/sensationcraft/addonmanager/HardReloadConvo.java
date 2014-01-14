package org.sensationcraft.addonmanager;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.conversations.BooleanPrompt;
import org.bukkit.conversations.ConversationContext;
import org.bukkit.conversations.Prompt;

public class HardReloadConvo extends BooleanPrompt
{
	private final AddonManagerPlugin pl;
	private final AbstractReloadable addon;
	private final CommandSender sender;

	public HardReloadConvo(final AddonManagerPlugin pl, final AbstractReloadable addon, final CommandSender sender)
	{
		this.pl = pl;
		this.addon = addon;
		this.sender = sender;
	}

	@Override
	protected Prompt acceptValidatedInput(final ConversationContext cc, final String in)
	{
		return this.acceptValidatedInput(cc, in.equalsIgnoreCase("yes") || in.equalsIgnoreCase("y"));
	}

	@Override
	protected Prompt acceptValidatedInput(final ConversationContext cc, final boolean bln)
	{
		if(bln){
			Addon a;
			a = this.addon.reload(this.sender, this.pl, true);
			if(a == null)
			{
				cc.getForWhom().sendRawMessage(ChatColor.RED+"Failed to reload the addon!");
				return Prompt.END_OF_CONVERSATION;
			}
			cc.getForWhom().sendRawMessage(ChatColor.GREEN+"Addon reloaded.");
		}
		return Prompt.END_OF_CONVERSATION;
	}

	@Override
	public String getPromptText(final ConversationContext cc)
	{
		return "Are you sure you want to hard reload "+this.addon.getAddon().getName()+"? It will lose ALL of it's persistant data. [y/n]";
	}

	@Override
	protected boolean isInputValid(final ConversationContext context, final String input)
	{
		return true;
	}
}
