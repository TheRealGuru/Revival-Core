package gg.revival.core.punishments;

import gg.revival.core.Revival;
import lombok.Getter;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import java.text.SimpleDateFormat;
import java.util.Date;

public class PunishmentListener implements Listener {

    @Getter private Revival revival;

    public PunishmentListener(Revival revival) {
        this.revival = revival;
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerChat(AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();

        if(revival.getPunishments().getActiveMute(player.getUniqueId()) != null) {
            Punishment punishment = revival.getPunishments().getActiveMute(player.getUniqueId());

            if(punishment.isForever())
                player.sendMessage(revival.getMsgTools().getMessage("muted.forever").replace("%reason%", punishment.getReason()));

            else {
                Date date = new Date(punishment.getExpireDate());
                SimpleDateFormat formatter = new SimpleDateFormat("M-d-yyyy '@' hh:mm:ss a z");

                player.sendMessage(revival.getMsgTools().getMessage("muted.temp").replace("%reason%", punishment.getReason()).replace("%time%", formatter.format(date)));
            }

            event.setCancelled(true);
        }

        if(revival.getChatFilter().isBad(event.getMessage().split(" "))) {
            player.sendMessage(revival.getMsgTools().getMessage("errors.not-allowed-to-say"));
            event.setCancelled(true);
        }
    }

}
