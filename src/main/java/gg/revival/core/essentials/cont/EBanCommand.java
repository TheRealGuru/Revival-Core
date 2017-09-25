package gg.revival.core.essentials.cont;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import gg.revival.core.Revival;
import gg.revival.core.essentials.ECommand;
import gg.revival.core.punishments.PunishType;
import gg.revival.core.punishments.Punishment;
import gg.revival.core.tools.Logger;
import gg.revival.core.tools.MsgUtils;
import gg.revival.core.tools.Permissions;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.UUID;

public class EBanCommand extends ECommand {

    public EBanCommand() {
        super(
                "ban",
                "/ban <player> [reason]",
                "Ban a player",
                Permissions.PUNISHMENT_BAN,
                1,
                Integer.MAX_VALUE,
                false
        );
    }

    @Override
    public void onCommand(CommandSender sender, String args[]) {
        if(!validate(sender, args)) return;

        UUID punisherResult = null;
        String namedPlayer = args[0];
        String namedPunisher = "Console";
        String reasonResult = "Reason not given";

        if(sender instanceof Player) {
            Player player = (Player)sender;
            namedPunisher = player.getName();
            punisherResult = player.getUniqueId();
        }

        if(args.length > 1) {
            StringBuilder reasonBuilder = new StringBuilder();

            for(int i = 1; i < args.length; i++)
                reasonBuilder.append(args[i] + " ");

            reasonResult = reasonBuilder.toString().trim();
        }

        final UUID punisher = punisherResult;
        final String reason = reasonResult;
        final String punisherName = namedPunisher;

        Revival.getPlayerTools().getOfflinePlayer(namedPlayer, (uuid, username) -> {
            if(uuid == null) {
                sender.sendMessage(MsgUtils.getMessage("errors.player-not-found"));
                return;
            }

            Revival.getAccountManager().getAccount(uuid, false, result -> {
                int address = 0;

                if(result.getRegisteredAddresses() != null && !result.getRegisteredAddresses().isEmpty())
                    address = result.getRegisteredAddresses().get(0);

                Punishment punishment = new Punishment(UUID.randomUUID(), uuid, address, punisher, reason, PunishType.BAN, System.currentTimeMillis(), -1L);

                result.getPunishments().add(punishment);

                if(Bukkit.getPlayer(uuid) != null) {
                    Bukkit.getPlayer(uuid).kickPlayer(MsgUtils.getBanMessage(punishment));
                }

                else {
                    Revival.getAccountManager().saveAccount(result, false, Bukkit.getPlayer(uuid) == null);

                    ByteArrayDataOutput output = ByteStreams.newDataOutput();
                    output.writeUTF("Forward");
                    output.writeUTF("ALL");
                    output.writeUTF("Punishment");

                    ByteArrayOutputStream messageBytes = new ByteArrayOutputStream();
                    DataOutputStream messageOutput = new DataOutputStream(messageBytes);

                    try {
                        messageOutput.writeUTF(namedPlayer);
                        messageOutput.writeShort(namedPlayer.getBytes().length);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    output.writeShort(messageBytes.toByteArray().length);
                    output.write(messageBytes.toByteArray());
                }


                Revival.getPlayerTools().sendPermissionMessage(MsgUtils.getMessage("punish-notifications.player-banned")
                        .replace("%player%", username)
                        .replace("%banner%", punisherName), Permissions.PUNISHMENT_VIEW);

                Logger.log(username + " has been banned by " + punisherName + " for " + punishment.getReason());
            });
        });
    }

}
