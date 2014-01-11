package common.good.addonmanager;

import lombok.Delegate;

import org.bukkit.command.CommandSender;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.entity.Entity;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.permissions.Permissible;
import org.bukkit.permissions.ServerOperator;

/**
 * @author superckl - Have a taste of your own medicine
 */
public class AddonUser
{
	@Delegate(types = 
			{
			Player.class, Entity.class, CommandSender.class, ServerOperator.class,
            HumanEntity.class, ConfigurationSerializable.class, LivingEntity.class,
            Permissible.class
			})
	protected Player base;

	public AddonUser(final Player base) {
		this.base = base;
	}

}
