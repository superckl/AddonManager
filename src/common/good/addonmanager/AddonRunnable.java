package common.good.addonmanager;

import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

public abstract class AddonRunnable extends BukkitRunnable{

	/**
	 * ALWAYS call this when your task is finished running. Otherwise, you may have caused a memory leak. Tsk tsk.
	 */
	public void finished(){
		//TODO Remove references to BukkitTask here
	}

	/**
	 * Schedules this in the Bukkit scheduler to run on next tick.
	 * @param addon The addon running this Runnable
	 * @return The BukkitTask that represents this Runnable
	 */
	public BukkitTask runTask(final AbstractReloadable addon){
		//TODO Store incase addon is disabled
		return super.runTask(AddonManagerPlugin.getInstance());
	}

	/**
	 * Schedules this to run after the specified number of server ticks.
	 * @param addon The addon running this Runnable
	 * @param delay How many ticks to wait before running this Runnable
	 * @return The BukkitTask that represents this Runnable
	 */
	public BukkitTask runTaskLater(final AbstractReloadable addon, final long delay){
		//TODO Store incase addon is disabled
		return super.runTaskLater(AddonManagerPlugin.getInstance(), delay);
	}

	/**
	 * Schedules this to repeatedly run until cancelled, starting after the specified number of server ticks.
	 * @param addon The addon running this Runnable
	 * @param delay How many ticks to wait before running this Runnable
	 * @param period How many ticks to wait between runs
	 * @return The BukkitTask that represents this Runnable
	 */
	public BukkitTask runTaskTimer(final AbstractReloadable addon, final long delay, final long period){
		//TODO Store incase addon is disabled
		return super.runTaskTimer(AddonManagerPlugin.getInstance(), delay, period);
	}

	/**
	 * Asynchronous tasks should never access any API in Bukkit.
	 * @see {@link #runTask(AbstractReloadable)}
	 */
	public BukkitTask runTaskAsynchronously(final AbstractReloadable addon){
		//TODO Store incase addon is disabled
		return super.runTaskAsynchronously(AddonManagerPlugin.getInstance());
	}

	/**
	 * Asynchronous tasks should never access any API in Bukkit.
	 * @see {@link #runTaskLater(AbstractReloadable, long)}
	 */
	public BukkitTask runTaskLaterAsynchronously(final AbstractReloadable addon, final long delay){
		//TODO Store incase addon is disabled
		return super.runTaskLaterAsynchronously(AddonManagerPlugin.getInstance(), delay);
	}

	/**
	 * Asynchronous tasks should never access any API in Bukkit.
	 * @see {@link #runTaskTimer(AbstractReloadable, long, long)}
	 */
	public BukkitTask runTaskTimerAsynchronously(final AbstractReloadable addon, final long delay, final long period){
		//TODO Store incase addon is disabled
		return super.runTaskTimerAsynchronously(AddonManagerPlugin.getInstance(), delay, period);
	}
	/**
	 * Shoo! What are you doing here?
	 */
	@Override
	public BukkitTask runTask(final Plugin plugin){
		throw new IllegalStateException("Use the methods we so thoughtfully wrote for you!");
	}
	/**
	 * Shoo! What are you doing here?
	 */
	@Override
	public BukkitTask runTaskLater(final Plugin plugin, final long delay){
		throw new IllegalStateException("Use the methods we so thoughtfully wrote for you!");
	}
	/**
	 * Shoo! What are you doing here?
	 */
	@Override
	public BukkitTask runTaskTimer(final Plugin plugin, final long delay, final long period){
		throw new IllegalStateException("Use the methods we so thoughtfully wrote for you!");
	}
	/**
	 * Shoo! What are you doing here?
	 */
	@Override
	public BukkitTask runTaskAsynchronously(final Plugin plugin){
		throw new IllegalStateException("Use the methods we so thoughtfully wrote for you!");
	}
	/**
	 * Shoo! What are you doing here?
	 */
	@Override
	public BukkitTask runTaskLaterAsynchronously(final Plugin plugin, final long delay){
		throw new IllegalStateException("Use the methods we so thoughtfully wrote for you!");
	}
	/**
	 * Shoo! What are you doing here?
	 */
	@Override
	public BukkitTask runTaskTimerAsynchronously(final Plugin plugin, final long delay, final long period){
		throw new IllegalStateException("Use the methods we so thoughtfully wrote for you!");
	}
}
