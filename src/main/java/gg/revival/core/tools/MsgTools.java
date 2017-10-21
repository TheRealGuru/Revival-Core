package gg.revival.core.tools;

import gg.revival.core.Revival;
import gg.revival.core.accounts.Account;
import gg.revival.core.punishments.PunishType;
import gg.revival.core.punishments.Punishment;
import lombok.Getter;
import mkremins.fanciful.FancyMessage;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MsgTools {

    @Getter private Revival revival;

    public MsgTools(Revival revival) {
        this.revival = revival;
    }

    /**
     * Returns a ChatColor formatted string from the messages.yml file
     * @param path Message path in messages.yml
     * @return Formatted string
     */
    public String getMessage(String path) {
        return ChatColor.translateAlternateColorCodes('&', revival.getFileManager().getMessages().getString(path));
    }

    /**
     * Sends a formatted help page to the given player
     * @param player The player
     * @param topic Topic that is being sent, if null it displays the main page
     * @param content The content to be displayed
     */
    public static void sendHelpPage(Player player, String topic, List<String> content) {
        if(topic == null) {
            player.sendMessage(ChatColor.GRAY + "" + ChatColor.STRIKETHROUGH + "-------------------------");
            player.sendMessage("Displaying Help Topic: " + ChatColor.AQUA + "Main");
            player.sendMessage(ChatColor.YELLOW + "Click a topic to view more information.");
            player.sendMessage("     ");

            for(String topics : content)
                new FancyMessage(" - ").then(topics).color(ChatColor.BLUE).command("/help " + topics).send(player);

            player.sendMessage(ChatColor.GRAY + "" + ChatColor.STRIKETHROUGH + "-------------------------");
        } else {
            player.sendMessage(ChatColor.GRAY + "" + ChatColor.STRIKETHROUGH + "-------------------------");
            player.sendMessage("Displaying Help Topic: " + ChatColor.AQUA + topic);
            player.sendMessage("     ");

            for(String page : content)
                player.sendMessage(" - " + page);

            player.sendMessage(ChatColor.GRAY + "" + ChatColor.STRIKETHROUGH + "-------------------------");
        }
    }

    /**
     * Sends player lookup information to the given displayedTo player
     * @param displayedTo
     * @param username
     * @param account
     */
    public void sendLookup(Player displayedTo, String username, Account account) {
        StringBuilder response = new StringBuilder();
        boolean online = Bukkit.getPlayer(username) != null && Bukkit.getPlayer(username).isOnline();

        response.append(ChatColor.YELLOW + "" + ChatColor.STRIKETHROUGH + "-------------------------\n");
        response.append(ChatColor.YELLOW + username + ChatColor.GRAY + " [");

        if(online)
            response.append(ChatColor.GREEN + "Online");
        else
            response.append(ChatColor.RED + "Offline");

        response.append(ChatColor.GRAY + "]\n");

        if(!online)
            response.append(ChatColor.AQUA + "Last seen" + ChatColor.WHITE + ": " + revival.getTimeTools().formatIntoUptime(System.currentTimeMillis() - account.getLastSeen()) + " ago\n");

        if(!account.getPunishments().isEmpty()) {
            for(Punishment punishment : account.getPunishments()) {
                if(!punishment.getType().equals(PunishType.BAN) || punishment.isExpired()) continue;
                response.append(ChatColor.RED + "This player is currently banned\n");
                break;
            }
        }

        response.append(ChatColor.YELLOW + "" + ChatColor.STRIKETHROUGH + "-------------------------\n");

        response.append(ChatColor.GOLD + "Account Info" + ChatColor.WHITE + ":\n");
        response.append(ChatColor.YELLOW + " - " + ChatColor.GOLD + "Revival XP: " + ChatColor.WHITE + account.getXp() + "\n");
        response.append(ChatColor.YELLOW + " - " + ChatColor.GOLD + "Global Chat Hidden: " + (account.isHideGlobalChat() ? ChatColor.GREEN + "True" : ChatColor.RED + "False") + "\n");
        response.append(ChatColor.YELLOW + " - " + ChatColor.GOLD + "Private Messages Hidden: " + (account.isHideMessages() ? ChatColor.GREEN + "True" : ChatColor.RED + "False") + "\n");
        response.append(ChatColor.YELLOW + " - " + ChatColor.GOLD + "Blocked Players: " + ChatColor.WHITE + account.getBlockedPlayers().size() + "\n");
        response.append(ChatColor.YELLOW + " - " + ChatColor.GOLD + "Punishments on record: " + ChatColor.WHITE + account.getPunishments().size() + "\n");

        response.append(ChatColor.YELLOW + "" + ChatColor.STRIKETHROUGH + "-------------------------\n");

        if(!account.getNotes().isEmpty()) {
            response.append(ChatColor.GOLD + "Notes" + ChatColor.WHITE + "\n");

            for(String note : account.getNotes())
                response.append(ChatColor.YELLOW + " - " + ChatColor.WHITE + note + ChatColor.RESET + "\n");

            response.append(ChatColor.YELLOW + "" + ChatColor.STRIKETHROUGH + "-------------------------\n");
        }

        displayedTo.sendMessage(response.toString());
    }

    /**
     * Sends a list of player punishments
     * @param player The player the punishments are being sent to
     * @param username The username to lookup
     * @param account The account thats being displayed
     */
    public void sendPunishments(Player player, String username, Account account) {
        player.sendMessage(ChatColor.GRAY + "" + ChatColor.STRIKETHROUGH + "-------------------------");
        player.sendMessage("Displaying all punishments for " + ChatColor.RED + username);
        player.sendMessage(ChatColor.YELLOW + "Hover over each punishment to view more information.");
        player.sendMessage("     " );

        SimpleDateFormat formatter = new SimpleDateFormat("M-d-yyyy '@' hh:mm:ss a");

        int cursor = 1;

        for(Punishment punishment : account.getPunishments()) {
            Date date = new Date(punishment.getCreateDate());
            List<String> info = new ArrayList<>();

            info.add("Type: " + StringUtils.capitalize(punishment.getType().toString().toLowerCase()));
            info.add("Reason: " + punishment.getReason());

            if(punishment.isForever())
                info.add("Expires: Never");
            else
                info.add("Expires: " + formatter.format(date));

            new FancyMessage(cursor + ". ").then(formatter.format(date)).color(ChatColor.RED).tooltip(info).send(player);

            cursor++;
        }

        player.sendMessage(ChatColor.GRAY + "" + ChatColor.STRIKETHROUGH + "-------------------------");
    }

    /**
     * Returns a formatted ban message
     * @param punishment The players found punishment
     * @return Formatted ban message
     */
    public String getBanMessage(Punishment punishment) {
        StringBuilder result = new StringBuilder();

        result.append(ChatColor.RED + "Adios, amigo!" + ChatColor.RESET + "\n");
        result.append("     " + "\n");
        result.append("You have been banned for:" + "\n");
        result.append(punishment.getReason() + "\n");
        result.append("     " + "\n");

        if(punishment.isForever())
            result.append("This ban will " + ChatColor.UNDERLINE + "never" + ChatColor.RESET + " expire!" + "\n");

        else {
            Date date = new Date(punishment.getExpireDate());
            SimpleDateFormat formatter = new SimpleDateFormat("M-d-yyyy '@' hh:mm:ss a z");

            result.append("This ban will expire " + formatter.format(date) + "\n");
        }

        result.append("You may appeal at " + ChatColor.UNDERLINE + "http://hcfrevival.net/bans/appeal");

        return result.toString();
    }

    /**
     * Returns a formatted kick message
     * @param reason Kick reason
     * @return Formatted kick reason
     */
    public String getKickMessage(String reason) {
        StringBuilder result = new StringBuilder();

        result.append(ChatColor.RED + "You got the boot!" + ChatColor.RESET + "\n");
        result.append("     " + "\n");
        result.append("You were kicked for:" + "\n");
        result.append(reason);

        return result.toString();
    }

}
