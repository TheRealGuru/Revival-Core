package gg.revival.core.chat;

import gg.revival.core.Revival;
import gg.revival.core.tools.Config;
import gg.revival.core.tools.MsgUtils;
import gg.revival.core.tools.Permissions;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

public class ChatListener implements Listener
{

    /**
     * Handles all chat filter/cooldown/freeze event listening
     * @param event
     */
    @EventHandler
    public void onPlayerChat(AsyncPlayerChatEvent event)
    {
        Player player = event.getPlayer();

        if(Revival.getChatFilter().hasCooldown(player.getUniqueId()) && !player.hasPermission(Permissions.CHAT_BYPASS_COOLDOWN))
        {
            player.sendMessage(MsgUtils.getMessage("errors.chat-cooldown"));
            event.setCancelled(true);
        }

        if(Config.CHAT_FILTER_ENABLED && Revival.getChatFilter().isBad(event.getMessage().split(" ")) && !player.hasPermission(Permissions.CHAT_BYPASS_FILTER))
        {
            player.sendMessage(MsgUtils.getMessage("errors.not-allowed-to-say"));
            event.setCancelled(true);
        }

        if(Config.CHAT_FILTER_ENABLED && Config.CHAT_FILTER_INTERVAL > 0 && !player.hasPermission(Permissions.CHAT_BYPASS_COOLDOWN))
        {
            Revival.getChatFilter().applyCooldown(player.getUniqueId(), Config.CHAT_FILTER_INTERVAL);
        }
    }

}
