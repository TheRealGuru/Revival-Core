package gg.revival.core.tickets;

import com.google.common.collect.Sets;
import gg.revival.core.Revival;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

import java.text.SimpleDateFormat;
import java.util.*;

public class TicketGUI {

    @Getter private Revival revival;

    public TicketGUI(Revival revival) {
        this.revival = revival;
    }

    /**
     * Opens/Updates the TicketGUI for a specified player
     * @param displayedTo
     * @param inventory
     * @param tickets
     */
    public void show(Player displayedTo, final Inventory inventory, Set<Ticket> tickets) {
        if(tickets.size() == 0) {
            displayedTo.closeInventory();
            return;
        }

        Set<UUID> toLookup = Sets.newHashSet();

        for(Ticket ticket : tickets) {
            toLookup.add(ticket.getTicketCreator());

            if(ticket.getReportedUUID() != null)
                toLookup.add(ticket.getReportedUUID());
        }

        revival.getPlayerTools().getManyOfflinePlayersByUUID(toLookup, result -> {
            Inventory newInventory = inventory;

            if(newInventory == null)
                newInventory = Bukkit.createInventory(null, 54, ChatColor.BLACK + "Tickets");
            else
                newInventory.clear();

            for(Ticket ticket : tickets) {
                String creatorUsername = result.get(ticket.getTicketCreator());
                String reportedUsername = null;
                List<String> lore = new ArrayList<>();

                if(ticket.getReportedUUID() != null)
                    reportedUsername = result.get(ticket.getReportedUUID());

                ItemStack icon = new ItemStack(Material.SKULL_ITEM, 1, (short)3);
                SkullMeta meta = (SkullMeta)icon.getItemMeta();

                if(reportedUsername != null)
                    meta.setDisplayName(ChatColor.DARK_RED + "Reported: " + ChatColor.WHITE + reportedUsername);
                else
                    meta.setDisplayName(ChatColor.GREEN + "Question: " + ChatColor.WHITE + creatorUsername);

                lore.add(ChatColor.GOLD + "ID: " + ChatColor.YELLOW + ticket.getTicketUUID().toString());
                lore.add(ChatColor.GOLD + "Created by: " + ChatColor.YELLOW + creatorUsername);
                lore.add(ChatColor.GOLD + "Date: " + ChatColor.YELLOW + new SimpleDateFormat("M'/'d '@' hh:mm a z").format(new Date(ticket.getCreateDate())));
                lore.add(ChatColor.GOLD + "Reason: " + ChatColor.YELLOW + ticket.getReason());

                meta.setLore(lore);
                icon.setItemMeta(meta);

                newInventory.addItem(icon);
            }

            if(inventory == null || displayedTo.getOpenInventory().getTopInventory() != newInventory)
                displayedTo.openInventory(newInventory);
        });
    }

}
