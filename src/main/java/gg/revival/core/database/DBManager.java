package gg.revival.core.database;

import com.mongodb.client.MongoCollection;
import gg.revival.core.Revival;
import gg.revival.core.tools.Config;
import gg.revival.driver.MongoAPI;
import lombok.Getter;
import lombok.Setter;
import org.bson.Document;
import org.bukkit.scheduler.BukkitRunnable;

public class DBManager
{

    @Getter @Setter MongoCollection<Document> accounts;
    @Getter @Setter MongoCollection<Document> punishments;
    @Getter @Setter MongoCollection<Document> tickets;

    /**
     * Connects to the MongoDB instance, every plugin that needs to do so waits for this method to be ran
     */
    public void establishConnection()
    {
        if(!Config.DB_ENABLED)
            return;

        if(MongoAPI.isConnected())
            return;

        new BukkitRunnable()
        {
            public void run()
            {
                if(Config.DB_CREDS)
                {
                    MongoAPI.connect(
                            Config.DB_HOST,
                            Config.DB_PORT,
                            Config.DB_USERNAME,
                            Config.DB_PASSWORD,
                            Config.DB_DATABASE
                    );
                }

                else
                {
                    MongoAPI.connect(
                            Config.DB_HOST,
                            Config.DB_PORT,
                            null, null, null
                    );
                }

                if(MongoAPI.isConnected()) {
                    accounts = MongoAPI.getCollection(Config.DB_DATABASE, "accounts");
                    punishments = MongoAPI.getCollection(Config.DB_DATABASE, "punishments");
                    tickets = MongoAPI.getCollection(Config.DB_DATABASE, "tickets");
                }
            }
        }.runTaskAsynchronously(Revival.getCore());
    }

}
