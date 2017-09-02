package gg.revival.core.tools;

import gg.revival.core.Revival;
import gg.revival.core.accounts.Account;
import gg.revival.core.punishments.Punishment;
import mkremins.fanciful.FancyMessage;
import org.apache.commons.lang.StringUtils;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

public class MsgUtils
{

    /**
     * Returns a ChatColor formatted string from the messages.yml file
     * @param path Message path in messages.yml
     * @return Formatted string
     */
    public static String getMessage(String path)
    {
        return ChatColor.translateAlternateColorCodes('&', Revival.getFileManager().getMessages().getString(path));
    }

    /**
     * Sends a formatted help page to the given player
     * @param player The player
     * @param topic Topic that is being sent, if null it displays the main page
     * @param content The content to be displayed
     */
    public static void sendHelpPage(Player player, String topic, List<String> content)
    {
        if(topic == null)
        {
            player.sendMessage(ChatColor.GRAY + "" + ChatColor.STRIKETHROUGH + "-------------------------");
            player.sendMessage("Displaying Help Topic: " + ChatColor.AQUA + "Main");
            player.sendMessage(ChatColor.YELLOW + "Click a topic to view more information.");
            player.sendMessage("     ");

            for(String topics : content)
            {
                new FancyMessage(" - ").then(topics).color(ChatColor.BLUE).command("/help " + topics).send(player);
            }

            player.sendMessage(ChatColor.GRAY + "" + ChatColor.STRIKETHROUGH + "-------------------------");
        }

        else
        {
            player.sendMessage(ChatColor.GRAY + "" + ChatColor.STRIKETHROUGH + "-------------------------");
            player.sendMessage("Displaying Help Topic: " + ChatColor.AQUA + topic);
            player.sendMessage("     ");

            for(String page : content)
            {
                player.sendMessage(" - " + page);
            }

            player.sendMessage(ChatColor.GRAY + "" + ChatColor.STRIKETHROUGH + "-------------------------");
        }
    }

    /**
     * Sends a list of player punishments
     * @param player The player the punishments are being sent to
     * @param username The username to lookup
     * @param account The account thats being displayed
     */
    public static void sendPunishments(Player player, String username, Account account)
    {
        player.sendMessage(ChatColor.GRAY + "" + ChatColor.STRIKETHROUGH + "-------------------------");
        player.sendMessage("Displaying all punishments for " + ChatColor.RED + username);
        player.sendMessage(ChatColor.YELLOW + "Hover over each punishment to view more information.");
        player.sendMessage("     " );

        SimpleDateFormat formatter = new SimpleDateFormat("M-d-yyyy '@' hh:mm:ss a");

        int cursor = 1;

        for(Punishment punishment : account.getPunishments())
        {
            Date date = new Date(punishment.getCreateDate());
            List<String> info = new ArrayList<>();

            info.add("Type: " + StringUtils.capitalize(punishment.getType().toString().toLowerCase()));
            info.add("Reason: " + punishment.getReason());

            if(punishment.isForever())
            {
                info.add("Expires: Never");
            }

            else
            {
                info.add("Expires: " + formatter.format(date));
            }

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
    public static String getBanMessage(Punishment punishment)
    {
        StringBuilder result = new StringBuilder();

        result.append(ChatColor.RED + "Adios, amigo!" + ChatColor.RESET + "\n");
        result.append("     " + "\n");
        result.append("You have been banned for:" + "\n");
        result.append(punishment.getReason() + "\n");
        result.append("     " + "\n");

        if(punishment.isForever())
        {
            result.append("This ban will " + ChatColor.UNDERLINE + "never" + ChatColor.RESET + " expire!" + "\n");
        }

        else
        {
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
    public static String getKickMessage(String reason)
    {
        StringBuilder result = new StringBuilder();

        result.append(ChatColor.RED + "You got the boot!" + ChatColor.RESET + "\n");
        result.append("     " + "\n");
        result.append("You were kicked for:" + "\n");
        result.append(reason);

        return result.toString();
    }

}
