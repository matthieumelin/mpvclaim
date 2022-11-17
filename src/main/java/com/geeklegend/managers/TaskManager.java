package com.geeklegend.managers;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitScheduler;

import com.geeklegend.MPVClaim;
import com.geeklegend.claim.tasks.ClaimTask;
import com.geeklegend.claim.tasks.Task;

public class TaskManager implements IManager {
	private final MPVClaim plugin = MPVClaim.getPlugin();
	private final BukkitScheduler scheduler = Bukkit.getScheduler();
	private final Map<String, Task> tasks = new HashMap<>();

	@Override
	public void register() {
		tasks.put("claims", new ClaimTask());
		tasks.forEach((key, value) -> {
			start(key);
		});
	}

	public void start(final String id) {
		final Task task = getTask(id);

		if (task == null) {
			return;
		}

		task.runTaskTimerAsynchronously(plugin, 20, 20);
	}

	public void stop(final String id) {
		final Task task = getTask(id);

		if (task == null) {
			return;
		}

		scheduler.cancelTask(task.getTaskId());

		tasks.remove(id);
	}

	public void stopAll() {
		scheduler.cancelTasks(plugin);
	}

	public Task getTask(final String id) {
		return tasks.get(id);
	}
}
