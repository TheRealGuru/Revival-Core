package gg.revival.core.tickets;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import gg.revival.core.Revival;
import gg.revival.core.tools.Permissions;
import gg.revival.driver.MongoAPI;
import lombok.Getter;
import org.bson.Document;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Map;
import java.util.Set;
import java.util.UUID;

public class TicketManager {

    @Getter private Revival revival;

    public TicketManager(Revival revival) {
        this.revival = revival;
    }

    /**
     * Contains all open tickets
     */
    @Getter Set<Ticket> loadedTickets = Sets.newHashSet();

    /**
     * Contains a set of users who have recently filed a ticket
     */
    @Getter Map<UUID, Long> ticketCooldowns = Maps.newHashMap();

    /**
     * Returns a Ticket object based on Ticket UUID
     * @param ticketId
     * @return
     */
    Ticket getTicketByUUID(UUID ticketId) {
        ImmutableList<Ticket> cache = ImmutableList.copyOf(loadedTickets);

        for(Ticket ticket : cache)
            if(ticket.getTicketUUID().equals(ticketId)) return ticket;

        return null;
    }

    /**
     * Returns a Set of Ticket objects based on the Ticket creators UUID
     * @param creatorId
     * @return
     */
    public Set<Ticket> getTicketsByCreator(UUID creatorId) {
        ImmutableList<Ticket> cache = ImmutableList.copyOf(loadedTickets);
        Set<Ticket> result = Sets.newHashSet();

        for(Ticket ticket : cache)
            if(ticket.getTicketCreator().equals(creatorId)) result.add(ticket);

        return result;
    }

    /**
     * Returns a Set of Ticket objects based on a players involved reports
     * @param reportedId
     * @return
     */
    public Set<Ticket> getReports(UUID reportedId) {
        ImmutableList<Ticket> cache = ImmutableList.copyOf(loadedTickets);
        Set<Ticket> result = Sets.newHashSet();

        for(Ticket ticket : cache)
            if(ticket.getReportedUUID() != null && ticket.getReportedUUID().equals(reportedId)) result.add(ticket);

        return result;
    }

    /**
     * Creates and files a new ticket
     * @param creator
     * @param reported
     * @param reason
     */
    public void createTicket(UUID creator, UUID reported, String reason) {
        Ticket ticket = new Ticket(UUID.randomUUID(), creator, reported, reason, System.currentTimeMillis());
        loadedTickets.add(ticket);
        saveTicket(ticket, false);

        revival.getPlayerTools().getOfflinePlayer(creator, (creatorId, creatorUsername) -> {
            if(reported != null) {
                revival.getPlayerTools().getOfflinePlayer(reported, (reportedId, reportedUsername) -> {
                    sendNotification(ticket, creatorUsername, reportedUsername);
                    revival.getLog().log(creatorUsername + " reported " + reportedUsername + " for " + ticket.getReason());
                });
            } else {
                sendNotification(ticket, creatorUsername, null);
                revival.getLog().log(creatorUsername + " asked " + ticket.getReason());
            }
        });

        for(Player players : Bukkit.getOnlinePlayers()) {
            if(
                    players.getOpenInventory().getTopInventory() != null &&
                            players.getOpenInventory().getTopInventory().getName() != null &&
                            players.getOpenInventory().getTopInventory().getName().equals(ChatColor.BLACK + "Tickets")) {
                revival.getTicketGui().show(players, players.getOpenInventory().getTopInventory(), getLoadedTickets());
            }
        }

        ticketCooldowns.put(creator, System.currentTimeMillis() + (revival.getCfg().TICKETS_COOLDOWN * 1000L));

        new BukkitRunnable() {
            public void run() {
                ticketCooldowns.remove(creator);
            }
        }.runTaskLaterAsynchronously(Revival.getCore(), revival.getCfg().TICKETS_COOLDOWN * 20L);
    }

    /**
     * Closes and removed a ticket
     * @param ticket
     * @param closer
     */
    void closeTicket(Ticket ticket, UUID closer) {
        ticket.setClosed(true);
        loadedTickets.remove(ticket);
        saveTicket(ticket, false);

        String closerName = null;

        if(Bukkit.getPlayer(closer) != null)
            closerName = Bukkit.getPlayer(closer).getName();

        if(closerName != null)
            revival.getLog().log(closerName + " has closed a ticket");

        for(Player players : Bukkit.getOnlinePlayers()) {
            if(players.getUniqueId().equals(closer)) continue;

            if(
                    players.getOpenInventory().getTopInventory() != null &&
                    players.getOpenInventory().getTopInventory().getName() != null &&
                    players.getOpenInventory().getTopInventory().getName().equals(ChatColor.BLACK + "Tickets")) {
                revival.getTicketGui().show(players, players.getOpenInventory().getTopInventory(), getLoadedTickets());
            }
        }
    }

    private void sendNotification(Ticket ticket, String creatorName, String reportedName) {
        if(ticket.getReportedUUID() != null && reportedName != null)
            revival.getPlayerTools().sendPermissionMessage(ChatColor.WHITE + "[" + ChatColor.DARK_RED + "Report" + ChatColor.WHITE + "] " +
            creatorName + ChatColor.GRAY + " has reported " + ChatColor.WHITE + reportedName + ChatColor.GRAY + " for " + ChatColor.RESET + ticket.getReason(), Permissions.TICKETS_VIEW);
        else
            revival.getPlayerTools().sendPermissionMessage(ChatColor.WHITE + "[" + ChatColor.DARK_GREEN + "HelpOp" + ChatColor.WHITE + "] " +
            creatorName + ChatColor.GRAY + " asked " + ChatColor.WHITE + ticket.getReason(), Permissions.TICKETS_VIEW);
    }

    /**
     * Pulls updates from database and adds any new tickets that weren't previously loaded
     * @param notifyUpdates
     */
    public void pullUpdates(boolean notifyUpdates) {
        if(!MongoAPI.isConnected()) {
            if(revival.getCfg().DB_ENABLED) {
                new BukkitRunnable() {
                    public void run() {
                        pullUpdates(notifyUpdates);
                    }
                }.runTaskLater(Revival.getCore(), 20L);
            }

            return;
        }

        new BukkitRunnable() {
            public void run() {
                if(revival.getDatabaseManager().getTickets() == null)
                    revival.getDatabaseManager().setTickets(MongoAPI.getCollection(revival.getCfg().DB_DATABASE, "tickets"));

                MongoCollection<Document> collection = revival.getDatabaseManager().getTickets();
                FindIterable<Document> query = collection.find();
                int added = 0;

                for (Document document : query) {
                    if (getTicketByUUID(UUID.fromString(document.getString("uuid"))) != null) continue;
                    if (document.getBoolean("closed")) continue;

                    UUID ticketUUID = UUID.fromString(document.getString("uuid"));
                    UUID ticketCreator = UUID.fromString(document.getString("creator"));
                    UUID reportedUUID = null;

                    if (document.get("reported") != null)
                        reportedUUID = UUID.fromString(document.getString("reported"));

                    String reason = document.getString("reason");
                    long createDate = document.getLong("created");

                    Ticket ticket = new Ticket(ticketUUID, ticketCreator, reportedUUID, reason, createDate);
                    loadedTickets.add(ticket);

                    added++;
                }

                if (notifyUpdates)
                    revival.getPlayerTools().sendPermissionMessage(ChatColor.YELLOW + "Added " + added + " new tickets", Permissions.TICKETS_VIEW);

                revival.getLog().log("Loaded " + loadedTickets.size() + " Tickets");
            }
        }.runTaskAsynchronously(Revival.getCore());
    }

    /**
     * Saves ticket to Database
     * @param ticket
     * @param unsafe
     */
    public void saveTicket(Ticket ticket, boolean unsafe) {
        if(!MongoAPI.isConnected()) return;

        if(unsafe) {
            Runnable saveTask = () -> {
                if(revival.getDatabaseManager().getTickets() == null)
                    revival.getDatabaseManager().setTickets(MongoAPI.getCollection(revival.getCfg().DB_DATABASE, "tickets"));

                MongoCollection<Document> collection = revival.getDatabaseManager().getTickets();
                FindIterable<Document> query = null;

                try {
                    query = MongoAPI.getQueryByFilter(collection, "uuid", ticket.getTicketUUID().toString());
                } catch (LinkageError err) {
                    saveTicket(ticket, unsafe);
                    return;
                }

                Document document = query.first();
                Document newDoc = null;

                if(ticket.getReportedUUID() != null) {
                    newDoc = new Document("uuid", ticket.getTicketUUID().toString())
                            .append("creator", ticket.getTicketCreator().toString())
                            .append("reported", ticket.getReportedUUID().toString())
                            .append("reason", ticket.getReason())
                            .append("created", ticket.getCreateDate())
                            .append("closed", ticket.isClosed());
                } else {
                    newDoc = new Document("uuid", ticket.getTicketUUID().toString())
                            .append("creator", ticket.getTicketCreator().toString())
                            .append("reason", ticket.getReason())
                            .append("created", ticket.getCreateDate())
                            .append("closed", ticket.isClosed());
                }

                if(document != null)
                    collection.replaceOne(document, newDoc);
                else
                    collection.insertOne(newDoc);
            };

            revival.getProcessor().getSingleThreadExecutor().submit(saveTask);
        }

        else {
            new BukkitRunnable() {
                public void run() {
                    if(revival.getDatabaseManager().getTickets() == null)
                        revival.getDatabaseManager().setTickets(MongoAPI.getCollection(revival.getCfg().DB_DATABASE, "tickets"));

                    MongoCollection<Document> collection = revival.getDatabaseManager().getTickets();
                    FindIterable<Document> query = null;

                    try {
                        query = MongoAPI.getQueryByFilter(collection, "uuid", ticket.getTicketUUID().toString());
                    } catch (LinkageError err) {
                        saveTicket(ticket, unsafe);
                        return;
                    }

                    Document document = query.first();
                    Document newDoc = null;

                    if(ticket.getReportedUUID() != null) {
                        newDoc = new Document("uuid", ticket.getTicketUUID().toString())
                                .append("creator", ticket.getTicketCreator().toString())
                                .append("reported", ticket.getReportedUUID().toString())
                                .append("reason", ticket.getReason())
                                .append("created", ticket.getCreateDate())
                                .append("closed", ticket.isClosed());
                    } else {
                        newDoc = new Document("uuid", ticket.getTicketUUID().toString())
                                .append("creator", ticket.getTicketCreator().toString())
                                .append("reason", ticket.getReason())
                                .append("created", ticket.getCreateDate())
                                .append("closed", ticket.isClosed());
                    }

                    if(document != null)
                        collection.replaceOne(document, newDoc);
                    else
                        collection.insertOne(newDoc);
                }
            }.runTaskAsynchronously(Revival.getCore());
        }
    }
}
