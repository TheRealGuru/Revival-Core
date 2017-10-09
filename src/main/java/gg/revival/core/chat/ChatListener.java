package gg.revival.core.chat;

import gg.revival.core.Revival;
import gg.revival.core.tools.Permissions;
import lombok.Getter;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

public class ChatListener implements Listener {

    @Getter private Revival revival;

    public ChatListener(Revival revival) {
        this.revival = revival;
    }

    /**
     * Handles all chat filter/cooldown/freeze event listening
     * @param event
     */
    @EventHandler
    public void onPlayerChat(AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();

        if(revival.getChatFilter().hasCooldown(player.getUniqueId()) && !player.hasPermission(Permissions.CHAT_BYPASS_COOLDOWN)) {
            player.sendMessage(revival.getMsgTools().getMessage("errors.chat-cooldown").replace("%cooldown%",
                    revival.getTimeTools().getFormattedCooldown(true, revival.getChatFilter().getCooldowns().get(player.getUniqueId()) - System.currentTimeMillis())));

            event.setCancelled(true);
        }

        if(revival.getCfg().CHAT_FILTER_ENABLED && revival.getChatFilter().isBad(event.getMessage().split(" ")) && !player.hasPermission(Permissions.CHAT_BYPASS_FILTER)) {
            player.sendMessage(revival.getMsgTools().getMessage("errors.not-allowed-to-say"));
            event.setCancelled(true);
        }

        if(revival.getCfg().CHAT_FILTER_ENABLED && revival.getCfg().CHAT_FILTER_INTERVAL > 0 && !player.hasPermission(Permissions.CHAT_BYPASS_COOLDOWN))
            revival.getChatFilter().applyCooldown(player.getUniqueId(), revival.getCfg().CHAT_FILTER_INTERVAL);
    }

}
