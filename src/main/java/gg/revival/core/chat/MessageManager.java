package gg.revival.core.chat;

import gg.revival.core.Revival;
import gg.revival.core.accounts.Account;
import gg.revival.core.tools.Logger;
import gg.revival.core.tools.MsgUtils;
import lombok.Getter;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class MessageManager
{

    @Getter Map<UUID, UUID> recentMessagers = new HashMap<>();

    public UUID getRecentMessager(UUID uuid)
    {
        if(recentMessagers.containsKey(uuid))
            return recentMessagers.get(uuid);

        return null;
    }

    public void sendMessage(Player sender, Player receiver, String message)
    {
        Account senderAccount = Revival.getAccountManager().getAccount(sender.getUniqueId());
        Account receiverAccount = Revival.getAccountManager().getAccount(receiver.getUniqueId());

        if(senderAccount.getBlockedPlayers().contains(receiver.getUniqueId()))
        {
            sender.sendMessage(ChatColor.RED + "You have " + receiver.getName() + " blocked");
            return;
        }

        if(senderAccount.isHideMessages())
        {
            sender.sendMessage(ChatColor.RED + "You have private messages disabled");
            return;
        }

        if(receiverAccount.getBlockedPlayers().contains(sender.getUniqueId()) || receiverAccount.isHideMessages())
        {
            sender.sendMessage(ChatColor.RED + receiver.getName() + " has private messages disabled");
            return;
        }

        recentMessagers.put(receiver.getUniqueId(), sender.getUniqueId());

        sender.sendMessage(MsgUtils.getMessage("messages.to-format")
        .replace("%receiver%", receiver.getName())
        .replace("%message%", message));

        receiver.sendMessage(MsgUtils.getMessage("messages.from-format")
        .replace("%sender%", sender.getName())
        .replace("%message%", message));

        Logger.log("[PM] " + sender.getName() + " told " + receiver.getName() + ": " + message);
    }

}
