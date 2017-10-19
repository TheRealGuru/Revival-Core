package gg.revival.core.essentials.cont;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import gg.revival.core.Revival;
import gg.revival.core.essentials.ECommand;
import gg.revival.core.tools.Permissions;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class EBroadcastCommand extends ECommand {

    public EBroadcastCommand(Revival revival) {
        super(
                revival,
                "broadcast",
                "/broadcast [-r] [-n] <message>",
                "Send out a broadcast to all players",
                Permissions.ADMIN_TOOLS,
                1,
                Integer.MAX_VALUE,
                false
        );
    }

    @Override
    public void onCommand(CommandSender sender, String args[]) {
        if(!validate(sender, args)) return;

        StringBuilder broadcastBuilder = new StringBuilder();

        boolean raw = false;
        boolean network = false;

        for(String arg : args) {
            if(arg.equals("-r"))
                raw = true;

            if(arg.equals("-n"))
                network = true;
        }

        if(raw) {
            for(int i = 1; i < args.length; i++) {
                if(args[i].equals("-r") || args[i].equals("-n")) continue;
                broadcastBuilder.append(args[i] + " ");
            }

            Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('&', broadcastBuilder.toString().trim()));

            if(network) {
                ByteArrayDataOutput output = ByteStreams.newDataOutput();
                output.writeUTF("Forward");
                output.writeUTF("ALL");
                output.writeUTF("Broadcast");

                ByteArrayOutputStream messageBytes = new ByteArrayOutputStream();
                DataOutputStream messageOutput = new DataOutputStream(messageBytes);

                try {
                    messageOutput.writeUTF("%raw% " + broadcastBuilder.toString().trim());
                    messageOutput.writeShort(("%raw% " + broadcastBuilder.toString().trim()).getBytes().length);
                } catch (IOException e) {
                    e.printStackTrace();
                }

                output.writeShort(messageBytes.toByteArray().length);
                output.write(messageBytes.toByteArray());

                getRevival().getLog().log("Deployed network-wide broadcast");
            }
        }

        else {
            for (String arg : args) {
                if (arg.equals("-r") || arg.equals("-n")) continue;
                broadcastBuilder.append(arg + " ");
            }

            Bukkit.broadcastMessage(getRevival().getCfg().BROADCASTS_PREFIX + ChatColor.stripColor(broadcastBuilder.toString().trim()));

            if(network) {
                ByteArrayDataOutput output = ByteStreams.newDataOutput();
                output.writeUTF("Forward");
                output.writeUTF("ALL");
                output.writeUTF("Broadcast");

                ByteArrayOutputStream messageBytes = new ByteArrayOutputStream();
                DataOutputStream messageOutput = new DataOutputStream(messageBytes);

                try {
                    messageOutput.writeUTF(broadcastBuilder.toString().trim());
                    messageOutput.writeShort((broadcastBuilder.toString().trim()).getBytes().length);
                } catch (IOException e) {
                    e.printStackTrace();
                }

                output.writeShort(messageBytes.toByteArray().length);
                output.write(messageBytes.toByteArray());

                getRevival().getLog().log("Deployed network-wide broadcast");
            }
        }
    }

}
