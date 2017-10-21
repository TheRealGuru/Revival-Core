package gg.revival.core.accounts;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
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
import org.bukkit.Sound;
import org.bukkit.entity.Player;
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
    public void getAccount(UUID uuid, boolean unsafe, boolean cache, final AccountCallback callback) {
        if(!revival.getCfg().DB_ENABLED) {
            List<Integer> newAddressList = Lists.newArrayList();

            if(Bukkit.getPlayer(uuid) != null && Bukkit.getPlayer(uuid).isOnline())
                newAddressList.add(IPTools.ipStringToInteger(Bukkit.getPlayer(uuid).getAddress().getAddress().getHostAddress()));

            Account account = new Account(uuid, newAddressList, 0, false, false, Lists.newArrayList(), Lists.newArrayList(), Lists.newArrayList(), System.currentTimeMillis());
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
                getAccount(uuid, unsafe, cache, callback);
                return;
            }

            Document document = accountQuery.first();
            Account account;

            if(document != null) {
                int xp = 0;
                long lastSeen = System.currentTimeMillis();
                boolean hideGlobalChat = false, hideMessages = false;
                List<String> blockedPlayerIds = Lists.newArrayList(), punishmentIds = Lists.newArrayList(), notes = Lists.newArrayList();
                List<Integer> addresses = Lists.newArrayList();
                List<UUID> blockedPlayers = Lists.newArrayList();
                List<Punishment> punishments = Lists.newArrayList();

                if(document.get("xp") != null)
                    xp = document.getInteger("xp");

                if(document.get("hideGlobalChat") != null)
                    hideGlobalChat = document.getBoolean("hideGlobalChat");

                if(document.get("hideMessages") != null)
                    hideMessages = document.getBoolean("hideMessages");

                if(document.get("blockedPlayers") != null)
                    blockedPlayerIds = (List<String>) document.get("blockedPlayers");

                if(document.get("punishments") != null)
                    punishmentIds = (List<String>) document.get("punishments");

                if(document.get("addresses") != null)
                    addresses = (List<Integer>) document.get("addresses");

                if(document.get("notes") != null)
                    notes = (List<String>) document.get("notes");

                if(document.get("lastSeen") != null)
                    lastSeen = document.getLong("lastSeen");

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

                if(blockedPlayerIds != null && !blockedPlayerIds.isEmpty()) {
                    for(String blockedPlayerId : blockedPlayerIds)
                        blockedPlayers.add(UUID.fromString(blockedPlayerId));
                }

                account = new Account(uuid, addresses, xp, hideGlobalChat, hideMessages, blockedPlayers, punishments, notes, lastSeen);
            }

            else {
                account = new Account(uuid, Lists.newArrayList(), 0, false, false, Lists.newArrayList(), Lists.newArrayList(), Lists.newArrayList(), System.currentTimeMillis());
            }

            if(getAccount(uuid) == null && cache)
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

                    FindIterable<Document> accountQuery;

                    try {
                        accountQuery = MongoAPI.getQueryByFilter(accountCollection, "uuid", uuid.toString());
                    } catch (LinkageError err) {
                        getAccount(uuid, unsafe, cache, callback);
                        return;
                    }

                    Document document = accountQuery.first();
                    Account account;

                    if(document != null) {
                        int xp = 0;
                        long lastSeen = System.currentTimeMillis();
                        boolean hideGlobalChat = false, hideMessages = false;
                        List<String> blockedPlayerIds = Lists.newArrayList(), punishmentIds = Lists.newArrayList(), notes = Lists.newArrayList();
                        List<Integer> addresses = Lists.newArrayList();
                        List<UUID> blockedPlayers = Lists.newArrayList();
                        List<Punishment> punishments = Lists.newArrayList();

                        if(document.get("xp") != null)
                            xp = document.getInteger("xp");

                        if(document.get("hideGlobalChat") != null)
                            hideGlobalChat = document.getBoolean("hideGlobalChat");

                        if(document.get("hideMessages") != null)
                            hideMessages = document.getBoolean("hideMessages");

                        if(document.get("blockedPlayers") != null)
                            blockedPlayerIds = (List<String>) document.get("blockedPlayers");

                        if(document.get("punishments") != null)
                            punishmentIds = (List<String>) document.get("punishments");

                        if(document.get("addresses") != null)
                            addresses = (List<Integer>) document.get("addresses");

                        if(document.get("notes") != null)
                            notes = (List<String>) document.get("notes");

                        if(document.get("lastSeen") != null)
                            lastSeen = document.getLong("lastSeen");

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

                        if(blockedPlayerIds != null && !blockedPlayerIds.isEmpty()) {
                            for(String blockedPlayerId : blockedPlayerIds)
                                blockedPlayers.add(UUID.fromString(blockedPlayerId));
                        }

                        account = new Account(uuid, addresses, xp, hideGlobalChat, hideMessages, blockedPlayers, punishments, notes, lastSeen);
                    }

                    else {
                        account = new Account(uuid, Lists.newArrayList(), 0, false, false, Lists.newArrayList(), Lists.newArrayList(), Lists.newArrayList(), System.currentTimeMillis());
                    }

                    if(getAccount(uuid) == null && cache)
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
                        .append("addresses", account.getRegisteredAddresses())
                        .append("xp", account.getXp())
                        .append("hideGlobalChat", account.isHideGlobalChat())
                        .append("hideMessages", account.isHideMessages())
                        .append("blockedPlayers", blockedPlayerIds)
                        .append("punishments", punishmentIds)
                        .append("notes", account.getNotes())
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
                            .append("addresses", account.getRegisteredAddresses())
                            .append("xp", account.getXp())
                            .append("hideGlobalChat", account.isHideGlobalChat())
                            .append("hideMessages", account.isHideMessages())
                            .append("blockedPlayers", blockedPlayerIds)
                            .append("punishments", punishmentIds)
                            .append("notes", account.getNotes())
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
    public void addXP(UUID uuid, String reason, Integer amount) {
        Account account = getAccount(uuid);

        if(account == null) {
            revival.getLog().log(Level.WARNING, "Could not find account for '" + uuid.toString() + "'");
            return;
        }

        account.setXp(account.getXp() + amount);

        new BukkitRunnable() {
            public void run() {
                if(Bukkit.getPlayer(uuid) != null) {
                    Player player = Bukkit.getPlayer(uuid);
                    player.sendMessage(ChatColor.GREEN + "+" + amount + "XP - " + reason);
                    player.playSound(player.getLocation(), Sound.LEVEL_UP, 1.0f, 1.0f);
                }
            }
        }.runTaskLater(revival, 60L);
    }

    /**
     * Spends XP from the given UUID's account
     * @param uuid
     * @param amount
     */
    public void spendXP(UUID uuid, String reason, Integer amount) {
        Account account = getAccount(uuid);

        if(account == null) {
            revival.getLog().log(Level.SEVERE, "Could not subtract XP from '" + uuid.toString() + "'");
            return;
        }

        account.setXp(account.getXp() - amount);

        if(Bukkit.getPlayer(uuid) != null)
            Bukkit.getPlayer(uuid).sendMessage(ChatColor.RED + "-" + amount + "XP - " + reason);
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
