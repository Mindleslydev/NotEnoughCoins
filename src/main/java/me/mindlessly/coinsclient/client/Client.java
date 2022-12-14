package me.mindlessly.coinsclient.client;

import java.io.IOException;
import java.util.Objects;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import me.mindlessly.coinsclient.Config;
import me.mindlessly.coinsclient.utils.ApiHandler;
import me.mindlessly.coinsclient.utils.Utils;

public class Client {
	public static ScheduledExecutorService scheduledExecutorService = Executors.newScheduledThreadPool(1);
	static int increment = 0;
	static int totalPages = 0;

	public static void checkIfUpdate() {
		scheduledExecutorService.shutdownNow();
		if (Config.enabled) {
			scheduledExecutorService = Executors.newScheduledThreadPool(1);
			scheduledExecutorService.schedule(() -> flip(), 1, TimeUnit.SECONDS);
		}
	}

	private static void flip() {
		scheduledExecutorService.scheduleAtFixedRate(() -> {
			ApiHandler.getBins();
			Utils.updatePurse();
		}, 0, 2, TimeUnit.MINUTES);

		try {
			totalPages = Objects.requireNonNull(Utils.getJson("https://api.hypixel.net/skyblock/auctions?page=" + 0))
					.getAsJsonObject().get("totalPages").getAsInt();
		} catch (Exception e) {
			e.printStackTrace();
		}

		scheduledExecutorService.scheduleAtFixedRate(() -> {
			try {
				ApiHandler.getFlips(increment);
			} catch (IOException e) {
				e.printStackTrace();
			}
			increment++;
			if (increment == totalPages) {
				increment = 0;
			}
		}, 0, 100, TimeUnit.MILLISECONDS);
	}
}
