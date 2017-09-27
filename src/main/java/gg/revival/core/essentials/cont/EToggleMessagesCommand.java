package gg.revival.core.essentials.cont;

import gg.revival.core.Revival;
import gg.revival.core.accounts.Account;
import gg.revival.core.essentials.ECommand;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class EToggleMessagesCommand extends ECommand {

    public EToggleMessagesCommand(Revival revival) {
        super(
                revival,
                "togglemessages",
                "/togglemessages",
                "Enable/Disable private messages",
                null,
                0,
                0,
                true
        );
    }

    @Override
    public void onCommand(CommandSender sender, String args[]) {
        if(!validate(sender, args)) return;

        Player player = (Player)sender;
        Account account = getRevival().getAccountManager().getAccount(player.getUniqueId());

        if(account.isHideMessages()) {
            account.setHideMessages(false);
            player.sendMessage(ChatColor.GREEN + "You can now see private messages");
        } else {
            account.setHideMessages(true);
            player.sendMessage(ChatColor.GREEN + "You can no longer see private messages");
        }
    }

}
