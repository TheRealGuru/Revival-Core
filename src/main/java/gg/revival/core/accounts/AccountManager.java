package gg.revival.core.accounts;

import com.google.common.collect.ImmutableList;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import gg.revival.core.Revival;
import gg.revival.core.punishments.PunishType;
import gg.revival.core.punishments.Punishment;
import gg.revival.core.tools.IPTools;
import gg.revival.driver.MongoAPI;
import lombok.Getter;
import org.bson.Document;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.logging.Level;

public class AccountManager {

    @Getter private Revival revival;

    public AccountManager(Revival revival) {
        this.revival = revival;
    }

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
        ImmutableList<Account> cache = ImmutableList.copyOf(accounts);

        for(Account account : cache) {
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
    public void getAccount(UUID uuid, boolean unsafe, final AccountCallback callback) {
        if(!revival.getCfg().DB_ENABLED) {
            List<UUID> blockedPlayers = new ArrayList<>();
            List<Punishment> punishments = new ArrayList<>();
            List<Integer> newAddressList = new ArrayList<>();

            if(Bukkit.getPlayer(uuid) != null && Bukkit.getPlayer(uuid).isOnline())
                newAddressList.add(IPTools.ipStringToInteger(Bukkit.getPlayer(uuid).getAddress().getAddress().getHostAddress()));

            Account account = new Account(uuid, newAddressList, 0, false, false, blockedPlayers, punishments, System.currentTimeMillis());
            accounts.add(account);

            callback.onQueryDone(account);

            return;
        }

        if(getAccount(uuid) != null) {
            callback.onQueryDone(getAccount(uuid));
            return;
        }

        if(unsafe) {
            if(revival.getDatabaseManager().getAccounts() == null)
                revival.getDatabaseManager().setAccounts(MongoAPI.getCollection(revival.getCfg().DB_DATABASE, "accounts"));

            if(revival.getDatabaseManager().getPunishments() == null)
                revival.getDatabaseManager().setPunishments(MongoAPI.getCollection(revival.getCfg().DB_DATABASE, "punishments"));

            MongoCollection<Document> accountCollection = revival.getDatabaseManager().getAccounts();
            MongoCollection<Document> punishmentCollection = revival.getDatabaseManager().getPunishments();

            if(accountCollection == null || punishmentCollection == null) return;

            FindIterable<Document> accountQuery = null;

            try {
                accountQuery = MongoAPI.getQueryByFilter(accountCollection, "uuid", uuid.toString());
            } catch (LinkageError err) {
                getAccount(uuid, unsafe, callback);
                return;
            }

            Document document = accountQuery.first();
            Account account = null;

            if(document != null) {
                int xp = document.getInteger("xp");
                boolean hideGlobalChat = document.getBoolean("hideGlobalChat");
                boolean hideMessages = document.getBoolean("hideMessages");
                List<String> blockedPlayersIds = (List<String>)document.get("blockedPlayers");
                List<String> punishmentIds = (List<String>)document.get("punishments");
                List<Integer> registeredAddresses = (List<Integer>)document.get("registeredAddresses");
                List<UUID> blockedPlayers = new ArrayList<>();
                List<Punishment> punishments = new ArrayList<>();

                if(punishmentIds != null && !punishmentIds.isEmpty()) {
                    if(revival.getDatabaseManager().getPunishments() == null)
                        revival.getDatabaseManager().setPunishments(MongoAPI.getCollection(revival.getCfg().DB_DATABASE, "punishments"));

                    for(String punishmentId : punishmentIds) {
                        FindIterable<Document> punishmentQuery = punishmentCollection.find(Filters.eq("uuid", punishmentId));
                        Document punishmentDoc = punishmentQuery.first();

                        if(punishmentDoc != null) {
                            UUID punishedPlayer = UUID.fromString(punishmentDoc.getString("punishedPlayer"));
                            int punishedAddress = punishmentDoc.getInteger("punishedAddress");
                            String reason = punishmentDoc.getString("reason");
                            long createTime = punishmentDoc.getLong("created");
                            long expireTime = punishmentDoc.getLong("expires");
                            UUID punisher = null;
                            PunishType type = null;

                            if(punishmentDoc.get("punisher") != null)
                                punisher = UUID.fromString(punishmentDoc.getString("punisher"));

                            if(punishmentDoc.getString("type") != null) {
                                for(PunishType types : PunishType.values()) {
                                    if(punishmentDoc.getString("type").equalsIgnoreCase(types.toString()))
                                        type = types;
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

                if(blockedPlayersIds != null && !blockedPlayersIds.isEmpty()) {
                    for(String blockedPlayerId : blockedPlayersIds)
                        blockedPlayers.add(UUID.fromString(blockedPlayerId));
                }

                account = new Account(uuid, registeredAddresses, xp, hideGlobalChat, hideMessages, blockedPlayers, punishments, System.currentTimeMillis());
            }

            else {
                List<UUID> blockedPlayers = new ArrayList<>(); List<Punishment> punishments = new ArrayList<>(); List<Integer> registeredAddresses = new ArrayList<>();
                account = new Account(uuid, registeredAddresses, 0, false, false, blockedPlayers, punishments, System.currentTimeMillis());
            }

            if(getAccount(uuid) == null)
                accounts.add(account);

            callback.onQueryDone(account);
        }

        else {
            new BukkitRunnable() {
                public void run() {
                    if(revival.getDatabaseManager().getAccounts() == null)
                        revival.getDatabaseManager().setAccounts(MongoAPI.getCollection(revival.getCfg().DB_DATABASE, "accounts"));

                    if(revival.getDatabaseManager().getPunishments() == null)
                        revival.getDatabaseManager().setPunishments(MongoAPI.getCollection(revival.getCfg().DB_DATABASE, "punishments"));

                    MongoCollection<Document> accountCollection = revival.getDatabaseManager().getAccounts();
                    MongoCollection<Document> punishmentCollection = revival.getDatabaseManager().getPunishments();

                    if(accountCollection == null || punishmentCollection == null) return;

                    FindIterable<Document> accountQuery = null;

                    try {
                        accountQuery = MongoAPI.getQueryByFilter(accountCollection, "uuid", uuid.toString());
                    } catch (LinkageError err) {
                        getAccount(uuid, unsafe, callback);
                        return;
                    }

                    Document document = accountQuery.first();
                    Account account = null;

                    if(document != null) {
                        int xp = document.getInteger("xp");
                        boolean hideGlobalChat = document.getBoolean("hideGlobalChat");
                        boolean hideMessages = document.getBoolean("hideMessages");
                        List<String> blockedPlayersIds = (List<String>)document.get("blockedPlayers");
                        List<String> punishmentIds = (List<String>)document.get("punishments");
                        List<Integer> registeredAddresses = (List<Integer>)document.get("registeredAddresses");
                        List<UUID> blockedPlayers = new ArrayList<>();
                        List<Punishment> punishments = new ArrayList<>();

                        if(punishmentIds != null && !punishmentIds.isEmpty()) {
                            if(revival.getDatabaseManager().getPunishments() == null)
                                revival.getDatabaseManager().setPunishments(MongoAPI.getCollection(revival.getCfg().DB_DATABASE, "punishments"));

                            for(String punishmentId : punishmentIds) {
                                FindIterable<Document> punishmentQuery = punishmentCollection.find(Filters.eq("uuid", punishmentId));
                                Document punishmentDoc = punishmentQuery.first();

                                if(punishmentDoc != null) {
                                    UUID punishedPlayer = UUID.fromString(punishmentDoc.getString("punishedPlayer"));
                                    int punishedAddress = punishmentDoc.getInteger("punishedAddress");
                                    String reason = punishmentDoc.getString("reason");
                                    long createTime = punishmentDoc.getLong("created");
                                    long expireTime = punishmentDoc.getLong("expires");
                                    UUID punisher = null;
                                    PunishType type = null;

                                    if(punishmentDoc.get("punisher") != null)
                                        punisher = UUID.fromString(punishmentDoc.getString("punisher"));

                                    if(punishmentDoc.getString("type") != null) {
                                        for(PunishType types : PunishType.values()) {
                                            if(punishmentDoc.getString("type").equalsIgnoreCase(types.toString()))
                                                type = types;
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

                        if(blockedPlayersIds != null && !blockedPlayersIds.isEmpty()) {
                            for(String blockedPlayerId : blockedPlayersIds)
                                blockedPlayers.add(UUID.fromString(blockedPlayerId));
                        }

                        account = new Account(uuid, registeredAddresses, xp, hideGlobalChat, hideMessages, blockedPlayers, punishments, System.currentTimeMillis());
                    }

                    else {
                        List<UUID> blockedPlayers = new ArrayList<>(); List<Punishment> punishments = new ArrayList<>(); List<Integer> registeredAddresses = new ArrayList<>();
                        account = new Account(uuid, registeredAddresses, 0, false, false, blockedPlayers, punishments, System.currentTimeMillis());
                    }

                    if(getAccount(uuid) == null)
                        accounts.add(account);

                    final Account result = account;

                    new BukkitRunnable() {
                        public void run() {
                            callback.onQueryDone(result);
                        }
                    }.runTask(Revival.getCore());
                }
            }.runTaskAsynchronously(Revival.getCore());
        }
    }

    /**
     * Save account to database
     * @param account The user Account object
     * @param unsafe Perform async or block the thread
     */
    public void saveAccount(Account account, boolean unsafe, boolean unload) {
        if(!revival.getCfg().DB_ENABLED)
            return;

        if(unload)
            accounts.remove(account);

        if(unsafe) {
            Runnable saveTask = () -> {
                if (revival.getDatabaseManager().getAccounts() == null)
                    revival.getDatabaseManager().setAccounts(MongoAPI.getCollection(revival.getCfg().DB_DATABASE, "accounts"));

                if (revival.getDatabaseManager().getPunishments() == null)
                    revival.getDatabaseManager().setPunishments(MongoAPI.getCollection(revival.getCfg().DB_DATABASE, "punishments"));

                MongoCollection<Document> accountCollection = revival.getDatabaseManager().getAccounts();
                MongoCollection<Document> punishmentCollection = revival.getDatabaseManager().getPunishments();
                FindIterable<Document> accountQuery = accountCollection.find(Filters.eq("uuid", account.getUuid().toString()));
                Document accountDoc = accountQuery.first();

                List<String> punishmentIds = new ArrayList<>();
                List<String> blockedPlayerIds = new ArrayList<>();

                List<Punishment> punishmentCache = new CopyOnWriteArrayList<>(account.getPunishments());
                List<UUID> blockedPlayerCache = new CopyOnWriteArrayList<>(account.getBlockedPlayers());

                if (!account.getPunishments().isEmpty()) {
                    for (Punishment punishment : punishmentCache)
                        punishmentIds.add(punishment.getUuid().toString());
                }

                if (!account.getBlockedPlayers().isEmpty()) {
                    for (UUID blockedPlayer : blockedPlayerCache)
                        blockedPlayerIds.add(blockedPlayer.toString());
                }

                Document newAccountDoc = new Document("uuid", account.getUuid().toString())
                        .append("registeredAddresses", account.getRegisteredAddresses())
                        .append("xp", account.getXp())
                        .append("hideGlobalChat", account.isHideGlobalChat())
                        .append("hideMessages", account.isHideMessages())
                        .append("blockedPlayers", blockedPlayerIds)
                        .append("punishments", punishmentIds)
                        .append("lastSeen", account.getLastSeen());

                if (!account.getPunishments().isEmpty()) {
                    for (Punishment punishment : punishmentCache) {
                        FindIterable<Document> punishmentQuery = punishmentCollection.find(Filters.eq("uuid", punishment.getUuid().toString()));
                        Document punishmentDoc = punishmentQuery.first();

                        Document newPunishmentDoc = new Document("uuid", punishment.getUuid().toString())
                                .append("punishedPlayer", punishment.getPunishedPlayers().toString())
                                .append("punishedAddress", punishment.getPunishedAddress())
                                .append("reason", punishment.getReason())
                                .append("type", punishment.getType().toString())
                                .append("created", punishment.getCreateDate())
                                .append("expires", punishment.getExpireDate());

                        if (punishment.getPunisher() != null)
                            newPunishmentDoc.append("punisher", punishment.getPunisher().toString());
                        else
                            newPunishmentDoc.append("punisher", null);

                        if (punishmentDoc != null)
                            punishmentCollection.replaceOne(punishmentDoc, newPunishmentDoc);
                        else
                            punishmentCollection.insertOne(newPunishmentDoc);
                    }
                }

                if (accountDoc != null)
                    accountCollection.replaceOne(accountDoc, newAccountDoc);
                else
                    accountCollection.insertOne(newAccountDoc);
            };

            revival.getProcessor().getSingleThreadExecutor().submit(saveTask);
        }

        else {
            new BukkitRunnable() {
                public void run() {
                    if(revival.getDatabaseManager().getAccounts() == null)
                        revival.getDatabaseManager().setAccounts(MongoAPI.getCollection(revival.getCfg().DB_DATABASE, "accounts"));

                    MongoCollection<Document> accountCollection = revival.getDatabaseManager().getAccounts();
                    MongoCollection<Document> punishmentCollection = revival.getDatabaseManager().getPunishments();
                    FindIterable<Document> query = accountCollection.find(Filters.eq("uuid", account.getUuid().toString()));
                    Document document = query.first();

                    List<String> punishmentIds = new ArrayList<>();
                    List<String> blockedPlayerIds = new ArrayList<>();

                    List<Punishment> punishmentCache = new CopyOnWriteArrayList<>(account.getPunishments());
                    List<UUID> blockedPlayerCache = new CopyOnWriteArrayList<>(account.getBlockedPlayers());

                    if(!account.getPunishments().isEmpty()) {
                        for(Punishment punishment : punishmentCache)
                            punishmentIds.add(punishment.getUuid().toString());
                    }

                    if(!account.getBlockedPlayers().isEmpty()) {
                        for(UUID blockedPlayer : blockedPlayerCache)
                            blockedPlayerIds.add(blockedPlayer.toString());
                    }

                    Document newAccountDoc = new Document("uuid", account.getUuid().toString())
                            .append("registeredAddresses", account.getRegisteredAddresses())
                            .append("xp", account.getXp())
                            .append("hideGlobalChat", account.isHideGlobalChat())
                            .append("hideMessages", account.isHideMessages())
                            .append("blockedPlayers", blockedPlayerIds)
                            .append("punishments", punishmentIds)
                            .append("lastSeen", account.getLastSeen());

                    if(!account.getPunishments().isEmpty()) {
                        for(Punishment punishment : punishmentCache) {
                            FindIterable<Document> punishmentQuery = punishmentCollection.find(Filters.eq("uuid", punishment.getUuid().toString()));
                            Document punishmentDoc = punishmentQuery.first();

                            Document newPunishmentDoc = new Document("uuid", punishment.getUuid().toString())
                                    .append("punishedPlayer", punishment.getPunishedPlayers().toString())
                                    .append("punishedAddress", punishment.getPunishedAddress())
                                    .append("reason", punishment.getReason())
                                    .append("type", punishment.getType().toString())
                                    .append("created", punishment.getCreateDate())
                                    .append("expires", punishment.getExpireDate());

                            if(punishment.getPunisher() != null)
                                newPunishmentDoc.append("punisher", punishment.getPunisher().toString());
                            else
                                newPunishmentDoc.append("punisher", null);

                            if(punishmentDoc != null)
                                punishmentCollection.replaceOne(punishmentDoc, newPunishmentDoc);
                            else
                                punishmentCollection.insertOne(newPunishmentDoc);
                        }
                    }

                    if(document != null)
                        accountCollection.replaceOne(document, newAccountDoc);

                    else
                        accountCollection.insertOne(newAccountDoc);
                }
            }.runTaskAsynchronously(Revival.getCore());
        }
    }

    /**
     * Adds XP to the given UUID's account
     * @param uuid
     * @param amount
     */
    public void addXP(UUID uuid, Integer amount) {
        Account account = getAccount(uuid);

        if(account == null) {
            revival.getLog().log(Level.WARNING, "Could not find account for '" + uuid.toString() + "'");
            return;
        }

        account.setXp(account.getXp() + amount);

        if(Bukkit.getPlayer(uuid) != null)
            Bukkit.getPlayer(uuid).sendMessage(ChatColor.GREEN + "+" + amount + "XP");
    }

    /**
     * Spends XP from the given UUID's account
     * @param uuid
     * @param amount
     */
    public void spendXP(UUID uuid, Integer amount) {
        Account account = getAccount(uuid);

        if(account == null) {
            revival.getLog().log(Level.SEVERE, "Could not subtract XP from '" + uuid.toString() + "'");
            return;
        }

        account.setXp(account.getXp() - amount);

        if(Bukkit.getPlayer(uuid) != null)
            Bukkit.getPlayer(uuid).sendMessage(ChatColor.RED + "-" + amount + "XP");
    }

    /**
     * Sets given players XP to the given amount
     * @param uuid
     * @param amount
     */
    public void setXP(UUID uuid, Integer amount) {
        Account account = getAccount(uuid);
        int oldXp = account.getXp();

        if(account == null) {
            revival.getLog().log(Level.WARNING, "Could not set XP for '" + uuid.toString() + "'");
            return;
        }

        account.setXp(amount);

        if(Bukkit.getPlayer(uuid) != null)
            Bukkit.getPlayer(uuid).sendMessage(ChatColor.YELLOW + "Old XP" + ChatColor.WHITE + ": " + oldXp + ChatColor.YELLOW + " New XP" + ChatColor.WHITE + account.getXp());
    }
}
