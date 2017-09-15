package gg.revival.core.essentials.cont;

import gg.revival.core.Revival;
import gg.revival.core.essentials.ECommand;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class EHelpOpCommand extends ECommand {

    public EHelpOpCommand() {
        super(
                "helpop",
                "/helpop <question>",
                "Ask the staff team a question",
                null,
                1,
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

        StringBuilder questionBuilder = new StringBuilder();

        for (String arg : args)
            questionBuilder.append(arg).append(" ");

        String question = questionBuilder.toString().trim();

        Revival.getTicketManager().createTicket(player.getUniqueId(), null, question);
        player.sendMessage(ChatColor.GREEN + "Ticket sent successfully. All online staff have been notified.");
    }

}
