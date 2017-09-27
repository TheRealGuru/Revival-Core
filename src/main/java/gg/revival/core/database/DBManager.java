package gg.revival.core.database;

import com.mongodb.client.MongoCollection;
import gg.revival.core.Revival;
import gg.revival.driver.MongoAPI;
import lombok.Getter;
import lombok.Setter;
import org.bson.Document;
import org.bukkit.scheduler.BukkitRunnable;

public class DBManager {

    @Getter private Revival revival;

    public DBManager(Revival revival) {
        this.revival = revival;
    }

    @Getter @Setter MongoCollection<Document> accounts;
    @Getter @Setter MongoCollection<Document> punishments;
    @Getter @Setter MongoCollection<Document> tickets;

    /**
     * Connects to the MongoDB instance, every plugin that needs to do so waits for this method to be ran
     */
    public void establishConnection() {
        if(!revival.getCfg().DB_ENABLED)
            return;

        if(MongoAPI.isConnected())
            return;

        new BukkitRunnable()
        {
            public void run()
            {
                if(revival.getCfg().DB_CREDS) {
                    MongoAPI.connect(
                            revival.getCfg().DB_HOST,
                            revival.getCfg().DB_PORT,
                            revival.getCfg().DB_USERNAME,
                            revival.getCfg().DB_PASSWORD,
                            revival.getCfg().DB_DATABASE
                    );
                }

                else {
                    MongoAPI.connect(
                            revival.getCfg().DB_HOST,
                            revival.getCfg().DB_PORT,
                            null, null, null
                    );
                }

                if(MongoAPI.isConnected()) {
                    accounts = MongoAPI.getCollection(revival.getCfg().DB_DATABASE, "accounts");
                    punishments = MongoAPI.getCollection(revival.getCfg().DB_DATABASE, "punishments");
                    tickets = MongoAPI.getCollection(revival.getCfg().DB_DATABASE, "tickets");
                }
            }
        }.runTaskAsynchronously(Revival.getCore());
    }

}
