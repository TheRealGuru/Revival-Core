package gg.revival.core.tools;

import gg.revival.core.Revival;
import gg.revival.core.punishments.Punishment;
import org.bukkit.ChatColor;

import java.text.SimpleDateFormat;
import java.util.Date;
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
            result.append("This ban will " + ChatColor.UNDERLINE + "never" + ChatColor.RESET + " expire!");
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

}
