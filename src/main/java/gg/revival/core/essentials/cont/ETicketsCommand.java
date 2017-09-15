package gg.revival.core.essentials.cont;

import com.google.common.collect.Sets;
import gg.revival.core.Revival;
import gg.revival.core.essentials.ECommand;
import gg.revival.core.tickets.Ticket;
import gg.revival.core.tickets.TicketGUI;
import gg.revival.core.tools.Permissions;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Set;

public class ETicketsCommand extends ECommand {

    public ETicketsCommand() {
        super(
                "tickets",
                "/tickets [player]",
                "View open tickets",
                Permissions.TICKETS_VIEW,
                0,
                1,
                true
        );
    }

    @Override
    public void onCommand(CommandSender sender, String args[]) {
        if(!validate(sender, args)) return;

        Player player = (Player)sender;

        if(args.length == 0) {
            TicketGUI.show(player, null, Revival.getTicketManager().getLoadedTickets());
            return;
        }

        if(args.length == 1) {
            String namedPlayer = args[0];

            Revival.getPlayerTools().getOfflinePlayer(namedPlayer, (uuid, username) -> {
                if(uuid == null || username == null) {
                    player.sendMessage(ChatColor.RED + "Player not found");
                    return;
                }

                Set<Ticket> reports = Revival.getTicketManager().getReports(uuid);
                Set<Ticket> created = Revival.getTicketManager().getTicketsByCreator(uuid);
                Set<Ticket> combined = Sets.newHashSet();
                combined.addAll(reports); combined.addAll(created);

                TicketGUI.show(player, null, combined);
            });
        }
    }

}
