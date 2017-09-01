package gg.revival.core.punishments;

import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import gg.revival.core.Revival;
import gg.revival.core.tools.Config;
import gg.revival.driver.MongoAPI;
import lombok.Getter;
import org.bson.Document;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;

public class PunishmentManager
{

    /**
     * Contains active mutes running on the server
     */
    @Getter Map<UUID, Punishment> activeMutes = new HashMap<>();

    /**
     * Returns a Punishment object is the given UUID has an active mute
     * @param uuid The user UUID
     * @return Punishment Object
     */
    public Punishment getActiveMute(UUID uuid)
    {
        if(activeMutes.containsKey(uuid))
        {
            Punishment punishment = activeMutes.get(uuid);

            if(punishment.isForever() || !punishment.isExpired())
                return punishment;

            activeMutes.remove(punishment);
        }

        return null;
    }

    /**
     * Scans an IP address (in int form) and returns a callback containing a set of any punishments associated with it
     * @param ip The user IP address
     * @param callback The punishment callback set result
     */
    public void scanAddress(int ip, PunishmentCallback callback)
    {
        Set<Punishment> result = new HashSet<>();

        new BukkitRunnable()
        {
            public void run()
            {
                if(Revival.getDbManager().getPunishments() == null)
                    Revival.getDbManager().setPunishments(MongoAPI.getCollection(Config.DB_DATABASE, "punishments"));

                MongoCollection<Document> punishmentCollection = Revival.getDbManager().getPunishments();
                FindIterable<Document> query = punishmentCollection.find(Filters.eq("punishedAddress", ip));
                Iterator<Document> iterator = query.iterator();

                while(iterator.hasNext())
                {
                    Document current = iterator.next();

                    UUID punishmentId = UUID.fromString(current.getString("uuid"));
                    UUID punishedPlayer = UUID.fromString(current.getString("punishedPlayer"));
                    int punishedAddress = current.getInteger("punishedAddress");
                    String reason = current.getString("reason");
                    long createTime = current.getLong("created");
                    long expireTime = current.getLong("expires");
                    UUID punisher = null;
                    PunishType type = null;

                    if(current.getString("type") != null)
                    {
                        for(PunishType types : PunishType.values())
                        {
                            if(current.getString("type").equalsIgnoreCase(types.toString()))
                            {
                                type = types;
                            }
                        }
                    }

                    Punishment punishment = new Punishment(
                            punishmentId, punishedPlayer, punishedAddress,
                            punisher, reason, type, createTime, expireTime
                    );

                    result.add(punishment);
                }

                new BukkitRunnable()
                {
                    public void run()
                    {
                        callback.onQueryDone(result);
                    }
                }.runTask(Revival.getCore());
            }
        }.runTaskAsynchronously(Revival.getCore());
    }

}
