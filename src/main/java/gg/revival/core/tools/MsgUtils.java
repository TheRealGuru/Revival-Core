package gg.revival.core.tools;

import gg.revival.core.Revival;
import org.bukkit.ChatColor;

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

}
