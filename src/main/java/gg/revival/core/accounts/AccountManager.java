package gg.revival.core.accounts;

import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import gg.revival.core.Revival;
import gg.revival.core.punishments.PunishType;
import gg.revival.core.punishments.Punishment;
import gg.revival.core.tools.Config;
import gg.revival.core.tools.Logger;
import gg.revival.driver.MongoAPI;
import lombok.Getter;
import org.bson.Document;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;

public class AccountManager
{

    /**
     * Contains a set of all cached accounts
     */
    @Getter Set<Account> accounts = new HashSet<>();

    /**
     * Returns an Account object if the UUID is cached
     * @param uuid The user UUID
     * @return Account object
     */
    public Account getAccount(UUID uuid) {
        for(Account account : accounts)
        {
            if(account.getUuid().equals(uuid))
                return account;
        }

        return null;
    }

    /**
     * Returns an Account object via callback
     * @param uuid The user UUID
     * @param callback Callback interface
     */
    public void getAccount(UUID uuid, final AccountCallback callback)
    {
        if(!Config.DB_ENABLED)
        {
            List<UUID> blockedPlayers = new ArrayList<>();
            List<Punishment> punishments = new ArrayList<>();
            Account account = new Account(uuid, 0, false, false, blockedPlayers, punishments, System.currentTimeMillis());
            accounts.add(account);

            callback.onQueryDone(account);

            return;
        }

        if(getAccount(uuid) != null)
        {
            callback.onQueryDone(getAccount(uuid));
            return;
        }

        new BukkitRunnable() {
            public void run() {
                if(Revival.getDbManager().getAccounts() == null)
                    Revival.getDbManager().setAccounts(MongoAPI.getCollection(Config.DB_DATABASE, "accounts"));

                MongoCollection<Document> accountCollection = Revival.getDbManager().getAccounts();
                MongoCollection<Document> punishmentCollection = Revival.getDbManager().getPunishments();
                FindIterable<Document> accountQuery = accountCollection.find(Filters.eq("uuid", uuid.toString()));
                Document document = accountQuery.first();

                Account account = null;

                if(document != null)
                {
                    int xp = document.getInteger("xp");
                    boolean hideGlobalChat = document.getBoolean("hideGlobalChat");
                    boolean hideMessages = document.getBoolean("hideMessages");
                    List<String> blockedPlayersIds = (List<String>)document.get("blockedPlayers");
                    List<String> punishmentIds = (List<String>)document.get("punishments");
                    List<UUID> blockedPlayers = new ArrayList<>();
                    List<Punishment> punishments = new ArrayList<>();

                    if(punishmentIds != null && !punishmentIds.isEmpty())
                    {
                        if(Revival.getDbManager().getPunishments() == null)
                            Revival.getDbManager().setPunishments(MongoAPI.getCollection(Config.DB_DATABASE, "punishments"));

                        for(String punishmentId : punishmentIds)
                        {
                            FindIterable<Document> punishmentQuery = punishmentCollection.find(Filters.eq("uuid", punishmentId));
                            Document punishmentDoc = punishmentQuery.first();

                            if(punishmentDoc != null)
                            {
                                UUID punishedPlayer = UUID.fromString(punishmentDoc.getString("punishedPlayer"));
                                int punishedAddress = punishmentDoc.getInteger("punishedAddress");
                                String reason = punishmentDoc.getString("reason");
                                long createTime = punishmentDoc.getLong("created");
                                long expireTime = punishmentDoc.getLong("expires");
                                UUID punisher = null;
                                PunishType type = null;

                                if(punishmentDoc.get("punisher") != null)
                                {
                                    punisher = UUID.fromString(punishmentDoc.getString("punisher"));
                                }

                                if(punishmentDoc.getString("type") != null)
                                {
                                    for(PunishType types : PunishType.values())
                                    {
                                        if(punishmentDoc.getString("type").equalsIgnoreCase(types.toString()))
                                        {
                                            type = types;
                                        }
                                    }
                                }

                                Punishment punishment = new Punishment(
                                        UUID.fromString(punishmentId), punishedPlayer, punishedAddress,
                                        punisher, reason, type, createTime, expireTime
                                );

                                punishments.add(punishment);
                            }
                        }
                    }

                    if(blockedPlayersIds != null && !blockedPlayersIds.isEmpty())
                    {
                        for(String blockedPlayerId : blockedPlayersIds)
                        {
                            blockedPlayers.add(UUID.fromString(blockedPlayerId));
                        }
                    }

                    account = new Account(uuid, xp, hideGlobalChat, hideMessages, blockedPlayers, punishments, System.currentTimeMillis());
                }

                else
                {
                    List<UUID> blockedPlayers = new ArrayList<>(); List<Punishment> punishments = new ArrayList<>();
                    account = new Account(uuid, 0, false, false, blockedPlayers, punishments, System.currentTimeMillis());
                }

                if(getAccount(uuid) == null)
                {
                    accounts.add(account);
                }

                final Account result = account;

                new BukkitRunnable() {
                    public void run() {
                        callback.onQueryDone(result);
                    }
                }.runTask(Revival.getCore());
            }
        }.runTaskAsynchronously(Revival.getCore());

        return;
    }

    /**
     * Save account to database
     * @param account The user Account object
     * @param unsafe Perform async or block the thread
     */
    public void saveAccount(Account account, boolean unsafe)
    {
        if(!Config.DB_ENABLED)
            return;

        if(unsafe)
        {
            if(Revival.getDbManager().getAccounts() == null)
                Revival.getDbManager().setAccounts(MongoAPI.getCollection(Config.DB_DATABASE, "accounts"));

            if(Revival.getDbManager().getPunishments() == null)
                Revival.getDbManager().setPunishments(MongoAPI.getCollection(Config.DB_DATABASE, "punishments"));

            MongoCollection<Document> accountCollection = Revival.getDbManager().getAccounts();
            MongoCollection<Document> punishmentCollection = Revival.getDbManager().getPunishments();
            FindIterable<Document> accountQuery = accountCollection.find(Filters.eq("uuid", account.getUuid().toString()));
            Document accountDoc = accountQuery.first();

            List<String> punishmentIds = new ArrayList<>();
            List<String> blockedPlayerIds = new ArrayList<>();

            if(!account.getPunishments().isEmpty())
            {
                for(Punishment punishment : account.getPunishments())
                {
                    punishmentIds.add(punishment.getUuid().toString());
                }
            }

            if(!account.getBlockedPlayers().isEmpty())
            {
                for(UUID blockedPlayer : account.getBlockedPlayers())
                {
                    blockedPlayerIds.add(blockedPlayer.toString());
                }
            }

            Document newAccountDoc = new Document("uuid", account.getUuid().toString())
                    .append("xp", account.getXp())
                    .append("hideGlobalChat", account.isHideGlobalChat())
                    .append("hideMessages", account.isHideMessages())
                    .append("blockedPlayers", blockedPlayerIds)
                    .append("punishments", punishmentIds)
                    .append("lastSeen", account.getLastSeen());

            if(!account.getPunishments().isEmpty())
            {
                for(Punishment punishment : account.getPunishments())
                {
                    FindIterable<Document> punishmentQuery = punishmentCollection.find(Filters.eq("uuid", punishment.getUuid().toString()));
                    Document punishmentDoc = punishmentQuery.first();

                    Document newPunishmentDoc = new Document("uuid", punishment.getUuid().toString())
                            .append("punishedPlayer", punishment.getPunishedPlayers().toString())
                            .append("punishedAddress", punishment.getPunishedAddresse())
                            .append("punisher", punishment.getPunisher().toString())
                            .append("reason", punishment.getReason())
                            .append("type", punishment.getType().toString())
                            .append("created", punishment.getCreateDate())
                            .append("expires", punishment.getExpireDate());

                    if(punishmentDoc != null)
                    {
                        punishmentCollection.replaceOne(punishmentDoc, newPunishmentDoc);
                    }

                    else
                    {
                        punishmentCollection.insertOne(newPunishmentDoc);
                    }
                }
            }

            if(accountDoc != null)
            {
                accountCollection.replaceOne(accountDoc, newAccountDoc);
            }

            else
            {
                accountCollection.insertOne(newAccountDoc);
            }
        }

        else
        {
            new BukkitRunnable() {
                public void run() {
                    if(Revival.getDbManager().getAccounts() == null)
                        Revival.getDbManager().setAccounts(MongoAPI.getCollection(Config.DB_DATABASE, "accounts"));

                    MongoCollection<Document> accountCollection = Revival.getDbManager().getAccounts();
                    MongoCollection<Document> punishmentCollection = Revival.getDbManager().getPunishments();
                    FindIterable<Document> query = accountCollection.find(Filters.eq("uuid", account.getUuid().toString()));
                    Document document = query.first();

                    List<String> punishmentIds = new ArrayList<>();
                    List<String> blockedPlayerIds = new ArrayList<>();

                    if(!account.getPunishments().isEmpty())
                    {
                        for(Punishment punishment : account.getPunishments())
                        {
                            punishmentIds.add(punishment.getUuid().toString());
                        }
                    }

                    if(!account.getBlockedPlayers().isEmpty())
                    {
                        for(UUID blockedPlayer : account.getBlockedPlayers())
                        {
                            blockedPlayerIds.add(blockedPlayer.toString());
                        }
                    }

                    Document newAccountDoc = new Document("uuid", account.getUuid().toString())
                            .append("xp", account.getXp())
                            .append("hideGlobalChat", account.isHideGlobalChat())
                            .append("hideMessages", account.isHideMessages())
                            .append("blockedPlayers", blockedPlayerIds)
                            .append("punishments", punishmentIds)
                            .append("lastSeen", account.getLastSeen());

                    if(!account.getPunishments().isEmpty())
                    {
                        for(Punishment punishment : account.getPunishments())
                        {
                            FindIterable<Document> punishmentQuery = punishmentCollection.find(Filters.eq("uuid", punishment.getUuid().toString()));
                            Document punishmentDoc = punishmentQuery.first();

                            Document newPunishmentDoc = new Document("uuid", punishment.getUuid().toString())
                                    .append("punishedPlayer", punishment.getPunishedPlayers().toString())
                                    .append("punishedAddress", punishment.getPunishedAddresse())
                                    .append("punisher", punishment.getPunisher().toString())
                                    .append("reason", punishment.getReason())
                                    .append("type", punishment.getType().toString())
                                    .append("created", punishment.getCreateDate())
                                    .append("expires", punishment.getExpireDate());

                            if(punishmentDoc != null)
                            {
                                punishmentCollection.replaceOne(punishmentDoc, newPunishmentDoc);
                            }

                            else
                            {
                                punishmentCollection.insertOne(newPunishmentDoc);
                            }
                        }
                    }

                    if(document != null)
                    {
                        accountCollection.replaceOne(document, newAccountDoc);
                    }

                    else
                    {
                        accountCollection.insertOne(newAccountDoc);
                    }
                }
            }.runTaskAsynchronously(Revival.getCore());
        }
    }

}
