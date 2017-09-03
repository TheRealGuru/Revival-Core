package gg.revival.core.essentials.cont;

import gg.revival.core.Revival;
import gg.revival.core.accounts.Account;
import gg.revival.core.essentials.ECommand;
import gg.revival.core.tools.MsgUtils;
import gg.revival.core.tools.OfflinePlayerCallback;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class EIgnoreCommand extends ECommand
{

    public EIgnoreCommand()
    {
        super(
                "ignore",
                "/ignore <player>",
                "Ignore a player",
                null,
                1,
                1,
                true
        );
    }

    @Override
    public void onCommand(CommandSender sender, String args[])
    {
        if(!validate(sender, args)) return;

        Player player = (Player)sender;
        String namedPlayer = args[0];

        Revival.getPlayerTools().getOfflinePlayer(namedPlayer, (uuid, username) -> {
            if(uuid == null)
            {
                player.sendMessage(MsgUtils.getMessage("errors.player-not-found"));
                return;
            }

            Account account = Revival.getAccountManager().getAccount(player.getUniqueId());

            if(account.getBlockedPlayers().contains(uuid))
            {
                player.sendMessage(ChatColor.RED + "This player is already blocked");
                return;
            }

            account.getBlockedPlayers().add(uuid);

            player.sendMessage(ChatColor.GREEN + "You have blocked " + username);
        });
    }

}
