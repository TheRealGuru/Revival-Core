package gg.revival.core.network;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteStreams;
import gg.revival.core.Revival;
import gg.revival.core.punishments.PunishType;
import gg.revival.core.punishments.Punishment;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.plugin.messaging.PluginMessageListener;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class NetworkListener implements PluginMessageListener {

    @Getter private Revival revival;

    public NetworkListener(Revival revival) {
        this.revival = revival;
    }

    @Override
    public void onPluginMessageReceived(String channel, Player player, byte[] bytes) {
        if(!channel.equals("BungeeCord")) return;

        ByteArrayDataInput input = ByteStreams.newDataInput(bytes);
        String subchannel = input.readUTF();

        if(subchannel.equals("Punishment")) {
            short len = input.readShort();
            byte[] messageBytes = new byte[len];

            input.readFully(messageBytes);

            DataInputStream messageInput = new DataInputStream(new ByteArrayInputStream(messageBytes));

            try {
                String receivedData = messageInput.readUTF();

                if(Bukkit.getPlayer(receivedData) == null) return;

                Player foundPlayer = Bukkit.getPlayer(receivedData);

                revival.getAccountManager().getAccount(foundPlayer.getUniqueId(), false, false, result -> {
                    if(result.getPunishments() == null || result.getPunishments().isEmpty()) return;

                    for(Punishment punishment : result.getPunishments()) {
                        if(punishment.isExpired()) continue;

                        if(punishment.getType().equals(PunishType.BAN)) {
                            foundPlayer.kickPlayer(revival.getMsgTools().getBanMessage(punishment));
                            continue;
                        }

                        if(punishment.getType().equals(PunishType.MUTE)) {
                            if(revival.getPunishments().getActiveMute(foundPlayer.getUniqueId()) == null) {
                                if(punishment.isForever()) {
                                    foundPlayer.sendMessage(revival.getMsgTools().getMessage("muted.forever")
                                            .replace("%reason%", punishment.getReason()));
                                } else {
                                    Date date = new Date(punishment.getExpireDate());
                                    SimpleDateFormat formatter = new SimpleDateFormat("M-d-yyyy '@' hh:mm:ss a z");

                                    foundPlayer.sendMessage(revival.getMsgTools().getMessage("muted.temp")
                                            .replace("%reason%", punishment.getReason()).replace("%time%", formatter.format(date)));
                                }
                            }

                            revival.getPunishments().getActiveMutes().put(foundPlayer.getUniqueId(), punishment);
                        }
                    }
                });
            } catch (IOException e) {
                e.printStackTrace();
            }

            return;
        }

        if(subchannel.equals("Broadcast")) {
            short len = input.readShort();
            byte[] messageBytes = new byte[len];

            input.readFully(messageBytes);

            DataInputStream messageInput = new DataInputStream(new ByteArrayInputStream(messageBytes));

            try {
                String receivedData = messageInput.readUTF();

                if(receivedData.startsWith("%raw% "))
                    Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('&', receivedData.replace("%raw% ", "")));
                else
                    Bukkit.broadcastMessage(revival.getCfg().BROADCASTS_PREFIX + receivedData);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}
