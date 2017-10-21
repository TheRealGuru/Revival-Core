package gg.revival.core.essentials.cont;

import gg.revival.core.Revival;
import gg.revival.core.accounts.Account;
import gg.revival.core.essentials.ECommand;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class EToggleGlobalCommand extends ECommand {

    public EToggleGlobalCommand(Revival revival) {
        super(
                revival,
                "toggleglobal",
                "/toggleglobal",
                "Toggle global messages",
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

        if(account == null) {
            player.sendMessage(ChatColor.RED + "Account not found");
            return;
        }

        if(account.isHideGlobalChat()) {
            account.setHideGlobalChat(false);
            player.sendMessage(ChatColor.GREEN + "You can now see global chat");
        } else {
            account.setHideGlobalChat(true);
            player.sendMessage(ChatColor.GREEN + "You can no longer see global chat");
        }
    }

}
