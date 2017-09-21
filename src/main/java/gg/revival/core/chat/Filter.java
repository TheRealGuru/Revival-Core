package gg.revival.core.chat;

import gg.revival.core.Revival;
import lombok.Getter;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;

public class Filter
{

    /**
     * Contains all users who have active chat cooldowns
     */
    @Getter Map<UUID, Long> cooldowns = new HashMap<>();

    /**
     * Contains a list of all the words we want to filter out
     */
    @Getter List<String> badWords = new ArrayList<>();

    /**
     * Applies the chat cooldown to a specific user
     * @param uuid The user UUID
     * @param interval Cooldown duration
     */
    public void applyCooldown(UUID uuid, int interval) {
        cooldowns.put(uuid, System.currentTimeMillis() + (interval * 1000L));

        new BukkitRunnable() {
            public void run() {
                cooldowns.remove(uuid);
            }
        }.runTaskLaterAsynchronously(Revival.getCore(), 20L * interval);
    }

    /**
     * Returns true if the player has an active chat cooldown
     * @param uuid The user UUID
     * @return Player has an active chat cooldown
     */
    public boolean hasCooldown(UUID uuid)
    {
        return cooldowns.containsKey(uuid);
    }

    /**
     * Returns true if the supplied array contains a word in the filter list
     * @param message The user message
     * @return Supplied array of words contains a 'bad' word
     */
    public boolean isBad(String message[]) {
        for(String word : message) {
            if(badWords.contains(word.toLowerCase()))
                return true;
        }

        return false;
    }

}
