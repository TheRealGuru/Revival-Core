package gg.revival.core.essentials.cont;

import gg.revival.core.Revival;
import gg.revival.core.essentials.ECommand;
import gg.revival.core.tools.Permissions;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ENoteCommand extends ECommand {

    public ENoteCommand(Revival revival) {
        super(
                revival,
                "note",
                "/note <player> <message>",
                "Leave a note on a players profile",
                Permissions.MOD_TOOLS,
                2,
                Integer.MAX_VALUE,
                false);
    }

    @Override
    public void onCommand(CommandSender sender, String args[]) {
        if(!validate(sender, args)) return;

        String namedPlayer = args[0];
        StringBuilder note = new StringBuilder();

        getRevival().getPlayerTools().getOfflinePlayer(namedPlayer, (uuid, username) -> {
            if(uuid == null || username == null) {
                sender.sendMessage(getRevival().getMsgTools().getMessage("errors.player-not-found"));
                return;
            }

            Player notePlayer = Bukkit.getPlayer(uuid);

            getRevival().getAccountManager().getAccount(uuid, false, false, result -> {
                if(result == null) {
                    sender.sendMessage(getRevival().getMsgTools().getMessage("errors.player-not-found"));
                    return;
                }

                for(int i = 1; i < args.length; i++)
                    note.append(args[i] + " ");

                String formattedNote = note.toString().trim();

                result.getNotes().add(formattedNote);

                if(notePlayer == null || !notePlayer.isOnline())
                    getRevival().getAccountManager().saveAccount(result, false, true);

                getRevival().getPlayerTools().sendPermissionMessage(
                        ChatColor.GOLD + sender.getName() + ChatColor.YELLOW + " left a note on " + ChatColor.RED + username + ChatColor.YELLOW + "'s account", getPermission());

                getRevival().getLog().log(sender.getName() + " left a note on " + username + "'s account");
            });
        });
    }

}
