package gg.revival.core.chat;

import gg.revival.core.Revival;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Broadcasts {

    @Getter private Revival revival;

    public Broadcasts(Revival revival) {
        this.revival = revival;
    }

    /**
     * Contains all configured broadcasts
     */
    @Getter List<String> loadedBroadcasts = new ArrayList<>();

    /**
     * Stores all broadcasts that have not been used in the current session
     */
    @Getter List<String> remainingBroadcasts = new ArrayList<>();

    /**
     * Performs a broadcast and automatically schedules another
     * @param random Prints a random broadcast instead of grabbing first result in the list
     * @param interval Delay in seconds before the next broadcast is ran
     */
    public void performBroadcast(boolean random, int interval) {
        if(remainingBroadcasts.isEmpty())
            remainingBroadcasts.addAll(loadedBroadcasts);

        int cursor = 0;

        if(random) {
            Random rand = new Random();
            cursor = Math.abs(rand.nextInt(remainingBroadcasts.size()));
        }

        String broadcast = remainingBroadcasts.get(cursor);

        Bukkit.broadcastMessage(revival.getCfg().BROADCASTS_PREFIX + broadcast);

        remainingBroadcasts.remove(broadcast);

        new BukkitRunnable() {
            public void run() {
                performBroadcast(random, interval);
            }
        }.runTaskLater(Revival.getCore(), 20L * interval);
    }

}
