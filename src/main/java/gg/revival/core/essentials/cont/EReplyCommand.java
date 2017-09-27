package gg.revival.core.essentials.cont;

import gg.revival.core.Revival;
import gg.revival.core.essentials.ECommand;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.UUID;

public class EReplyCommand extends ECommand {

    public EReplyCommand(Revival revival) {
        super(
                revival,
                "reply",
                "/reply <message>",
                "Reply to the last player who messaged you",
                null,
                1,
                Integer.MAX_VALUE,
                true
        );
    }

    @Override
    public void onCommand(CommandSender sender, String args[]) {
        if(!validate(sender, args)) return;

        Player player = (Player)sender;
        UUID repliedUUID = getRevival().getMessageManager().getRecentMessager(player.getUniqueId());

        if(repliedUUID == null || Bukkit.getPlayer(repliedUUID) == null) {
            if(repliedUUID != null)
                getRevival().getMessageManager().getRecentMessagers().remove(player.getUniqueId());

            player.sendMessage(getRevival().getMsgTools().getMessage("errors.player-not-found"));
            return;
        }

        Player repliedPlayer = Bukkit.getPlayer(repliedUUID);

        StringBuilder messageBuilder = new StringBuilder();

        for(int i = 0; i < args.length; i++)
            messageBuilder.append(args[i] + " ");

        String message = ChatColor.stripColor(messageBuilder.toString().trim());

        getRevival().getMessageManager().sendMessage(player, repliedPlayer, message);
    }

}
