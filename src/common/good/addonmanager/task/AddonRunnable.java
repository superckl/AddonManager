package common.good.addonmanager.task;

import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import common.good.addonmanager.AbstractReloadable;
import common.good.addonmanager.AddonManagerPlugin;

public abstract class AddonRunnable extends BukkitRunnable{

	private volatile boolean finished;

	/**
	 * ALWAYS call this when your task is finished running. Otherwise, you may have caused a memory leak. Tsk tsk.
	 */
	synchronized public void finished(){
		//TODO Remove references to BukkitTask here
		this.finished = true;
	}

	@Override
	synchronized public void cancel(){
		super.cancel();
		if(!this.finished)
			this.finished();
	}

	/**
	 * Schedules this in the Bukkit scheduler to run on next tick.
	 * @param addon The addon running this Runnable
	 * @return The BukkitTask that represents this Runnable
	 */
	synchronized public BukkitTask runTask(final AbstractReloadable addon){
		return super.runTask(AddonManagerPlugin.getInstance());
		//TODO Store incase addon is disabled
	}

	/**
	 * Schedules this in the Bukkit scheduler to run after the specified number of server ticks.
	 * @param addon The addon running this Runnable
	 * @param delay How many ticks to wait before running this Runnable
	 * @return The BukkitTask that represents this Runnable
	 */
	synchronized public BukkitTask runTaskLater(final AbstractReloadable addon, final long delay){
		return super.runTaskLater(AddonManagerPlugin.getInstance(), delay);
		//TODO Store incase addon is disabled
	}

	/**
	 * Schedules this in the Bukkit scheduler to repeatedly run until cancelled, starting after the specified number of server ticks.
	 * @param addon The addon running this Runnable
	 * @param delay How many ticks to wait before running this Runnable
	 * @param period How many ticks to wait between runs
	 * @return The BukkitTask that represents this Runnable
	 */
	synchronized public BukkitTask runTaskTimer(final AbstractReloadable addon, final long delay, final long period){
		return super.runTaskTimer(AddonManagerPlugin.getInstance(), delay, period);
		//TODO Store incase addon is disabled
	}

	/**
	 * Asynchronous tasks should never access any API in Bukkit.
	 * @see {@link #runTask(AbstractReloadable)}
	 */
	synchronized public BukkitTask runTaskAsynchronously(final AbstractReloadable addon){
		return super.runTaskAsynchronously(AddonManagerPlugin.getInstance());
		//TODO Store incase addon is disabled
	}

	/**
	 * Asynchronous tasks should never access any API in Bukkit.
	 * @see {@link #runTaskLater(AbstractReloadable, long)}
	 */
	synchronized public BukkitTask runTaskLaterAsynchronously(final AbstractReloadable addon, final long delay){
		return super.runTaskLaterAsynchronously(AddonManagerPlugin.getInstance(), delay);
		//TODO Store incase addon is disabled
	}

	/**
	 * Asynchronous tasks should never access any API in Bukkit.
	 * @see {@link #runTaskTimer(AbstractReloadable, long, long)}
	 */
	synchronized public BukkitTask runTaskTimerAsynchronously(final AbstractReloadable addon, final long delay, final long period){
		return super.runTaskTimerAsynchronously(AddonManagerPlugin.getInstance(), delay, period);
		//TODO Store incase addon is disabled
	}
	/**
	 * Shoo! What are you doing here?
	 */
	@Override
	synchronized public BukkitTask runTask(final Plugin plugin){
		throw new IllegalStateException("Use the methods we so thoughtfully wrote for you!");
	}
	/**
	 * Shoo! What are you doing here?
	 */
	@Override
	synchronized public BukkitTask runTaskLater(final Plugin plugin, final long delay){
		throw new IllegalStateException("Use the methods we so thoughtfully wrote for you!");
	}
	/**
	 * Shoo! What are you doing here?
	 */
	@Override
	synchronized public BukkitTask runTaskTimer(final Plugin plugin, final long delay, final long period){
		throw new IllegalStateException("Use the methods we so thoughtfully wrote for you!");
	}
	/**
	 * Shoo! What are you doing here?
	 */
	@Override
	synchronized public BukkitTask runTaskAsynchronously(final Plugin plugin){
		throw new IllegalStateException("Use the methods we so thoughtfully wrote for you!");
	}
	/**
	 * Shoo! What are you doing here?
	 */
	@Override
	synchronized public BukkitTask runTaskLaterAsynchronously(final Plugin plugin, final long delay){
		throw new IllegalStateException("Use the methods we so thoughtfully wrote for you!");
	}
	/**
	 * Shoo! What are you doing here?
	 */
	@Override
	synchronized public BukkitTask runTaskTimerAsynchronously(final Plugin plugin, final long delay, final long period){
		throw new IllegalStateException("Use the methods we so thoughtfully wrote for you!");
	}
}
