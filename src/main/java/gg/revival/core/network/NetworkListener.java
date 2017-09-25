package gg.revival.core.network;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteStreams;
import gg.revival.core.Revival;
import gg.revival.core.punishments.PunishType;
import gg.revival.core.punishments.Punishment;
import gg.revival.core.tools.Config;
import gg.revival.core.tools.Logger;
import gg.revival.core.tools.MsgUtils;
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

    @Override
    public void onPluginMessageReceived(String channel, Player player, byte[] bytes) {
        if(!channel.equals("BungeeCord")) return;

        ByteArrayDataInput input = ByteStreams.newDataInput(bytes);
        String subchannel = input.readUTF();

        Logger.log("Received message from Plugin-channel '" + subchannel + "'");

        if(subchannel.equals("Punishment")) {
            short len = input.readShort();
            byte[] messageBytes = new byte[len];

            input.readFully(messageBytes);

            DataInputStream messageInput = new DataInputStream(new ByteArrayInputStream(messageBytes));

            try {
                String receivedData = messageInput.readUTF();

                if(Bukkit.getPlayer(receivedData) == null) return;

                Player foundPlayer = Bukkit.getPlayer(receivedData);

                Revival.getAccountManager().getAccount(foundPlayer.getUniqueId(), false, result -> {
                    if(result.getPunishments() == null || result.getPunishments().isEmpty()) return;

                    for(Punishment punishment : result.getPunishments()) {
                        if(punishment.isExpired()) continue;

                        if(punishment.getType().equals(PunishType.BAN)) {
                            foundPlayer.kickPlayer(MsgUtils.getBanMessage(punishment));
                            continue;
                        }

                        if(punishment.getType().equals(PunishType.MUTE)) {
                            if(Revival.getPunishments().getActiveMute(foundPlayer.getUniqueId()) == null) {
                                if(punishment.isForever()) {
                                    foundPlayer.sendMessage(MsgUtils.getMessage("muted.forever")
                                            .replace("%reason%", punishment.getReason()));
                                } else {
                                    Date date = new Date(punishment.getExpireDate());
                                    SimpleDateFormat formatter = new SimpleDateFormat("M-d-yyyy '@' hh:mm:ss a z");

                                    foundPlayer.sendMessage(MsgUtils.getMessage("muted.temp")
                                            .replace("%reason%", punishment.getReason()).replace("%time%", formatter.format(date)));
                                }
                            }

                            Revival.getPunishments().getActiveMutes().put(foundPlayer.getUniqueId(), punishment);
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
                    Bukkit.broadcastMessage(Config.BROADCASTS_PREFIX + receivedData);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}
