package gg.revival.core.essentials.cont;

import gg.revival.core.Revival;
import gg.revival.core.essentials.ECommand;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class EReportCommand extends ECommand {

    public EReportCommand() {
        super(
                "report",
                "/report <player> <reason>",
                "Report a player",
                null,
                2,
                Integer.MAX_VALUE,
                true
        );
    }

    @Override
    public void onCommand(CommandSender sender, String[] args) {
        if(!validate(sender, args)) return;

        Player player = (Player)sender;

        if(Revival.getTicketManager().getTicketCooldowns().containsKey(player.getUniqueId())) {
            player.sendMessage(ChatColor.RED + "You can not file another ticket for " +
                    (int)(Revival.getTicketManager().getTicketCooldowns().get(player.getUniqueId()) - System.currentTimeMillis()) / 1000L +
            " seconds");
            return;
        }

        String namedPlayer = args[0];

        StringBuilder reasonBuilder = new StringBuilder();
        for(int i = 1; i < args.length; i++)
            reasonBuilder.append(args[i]).append(" ");

        final String reason = reasonBuilder.toString().trim();

        Revival.getPlayerTools().getOfflinePlayer(namedPlayer, (uuid, username) -> {
            if(uuid == null || username == null) {
                player.sendMessage(ChatColor.RED + "Player not found");
                return;
            }

            Revival.getTicketManager().createTicket(player.getUniqueId(), uuid, reason);
            player.sendMessage(ChatColor.GREEN + "Ticket sent successfully. All online staff have been notified.");
        });
    }
}
