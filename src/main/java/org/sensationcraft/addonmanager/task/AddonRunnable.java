package org.sensationcraft.addonmanager.task;

import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.sensationcraft.addonmanager.Addon;
import org.sensationcraft.addonmanager.AddonManagerPlugin;

/**
 * Works as an interface between the addon and Bukkit. Currently just cancels the task on reload, will be changed so it continues to execute
 */
public abstract class AddonRunnable extends BukkitRunnable{

	private volatile boolean finished;
	private volatile Addon addon;
	private volatile BukkitTask task;

	/**
	 * ALWAYS call this when your task is finished running. Otherwise, you may have caused a memory leak. Tsk tsk.
	 */
	synchronized public void finished(){
		if(this.finished)
			return;
		AddonManagerPlugin.getInstance().unregisterTask(this.addon, this.task);
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
	synchronized public BukkitTask runTask(final Addon addon){
		this.task = super.runTask(AddonManagerPlugin.getInstance());
		this.addon = addon;
		AddonManagerPlugin.getInstance().registerTask(this.addon, this.task);
		return this.task;
	}

	/**
	 * Schedules this in the Bukkit scheduler to run after the specified number of server ticks.
	 * @param addon The addon running this Runnable
	 * @param delay How many ticks to wait before running this Runnable
	 * @return The BukkitTask that represents this Runnable
	 */
	synchronized public BukkitTask runTaskLater(final Addon addon, final long delay){
		this.task = super.runTaskLater(AddonManagerPlugin.getInstance(), delay);
		this.addon = addon;
		AddonManagerPlugin.getInstance().registerTask(this.addon, this.task);
		return this.task;
	}

	/**
	 * Schedules this in the Bukkit scheduler to repeatedly run until cancelled, starting after the specified number of server ticks.
	 * @param addon The addon running this Runnable
	 * @param delay How many ticks to wait before running this Runnable
	 * @param period How many ticks to wait between runs
	 * @return The BukkitTask that represents this Runnable
	 */
	synchronized public BukkitTask runTaskTimer(final Addon addon, final long delay, final long period){
		this.task = super.runTaskTimer(AddonManagerPlugin.getInstance(), delay, period);
		this.addon = addon;
		AddonManagerPlugin.getInstance().registerTask(this.addon, this.task);
		return this.task;
	}

	/**
	 * Asynchronous tasks should never access any API in Bukkit.
	 * @see {@link #runTask(Addon)}
	 */
	synchronized public BukkitTask runTaskAsynchronously(final Addon addon){
		this.task = super.runTaskAsynchronously(AddonManagerPlugin.getInstance());
		this.addon = addon;
		AddonManagerPlugin.getInstance().registerTask(this.addon, this.task);
		return this.task;
	}

	/**
	 * Asynchronous tasks should never access any API in Bukkit.
	 * @see {@link #runTaskLater(Addon, long)}
	 */
	synchronized public BukkitTask runTaskLaterAsynchronously(final Addon addon, final long delay){
		this.task = super.runTaskLaterAsynchronously(AddonManagerPlugin.getInstance(), delay);
		this.addon = addon;
		AddonManagerPlugin.getInstance().registerTask(this.addon, this.task);
		return this.task;
	}

	/**
	 * Asynchronous tasks should never access any API in Bukkit.
	 * @see {@link #runTaskTimer(Addon, long, long)}
	 */
	synchronized public BukkitTask runTaskTimerAsynchronously(final Addon addon, final long delay, final long period){
		this.task = super.runTaskTimerAsynchronously(AddonManagerPlugin.getInstance(), delay, period);
		this.addon = addon;
		AddonManagerPlugin.getInstance().registerTask(this.addon, this.task);
		return this.task;
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
