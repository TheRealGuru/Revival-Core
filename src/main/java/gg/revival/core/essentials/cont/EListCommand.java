package gg.revival.core.essentials.cont;

import com.google.common.base.Joiner;
import com.google.common.collect.Lists;
import gg.revival.core.Revival;
import gg.revival.core.essentials.ECommand;
import gg.revival.core.ranks.Rank;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EListCommand extends ECommand
{

    public EListCommand()
    {
        super(
                "list",
                "/list",
                "View all online players",
                null,
                0,
                0,
                false
        );
    }

    @Override
    public void onCommand(CommandSender sender, String args[])
    {
        if(!validate(sender, args)) return;

        if(Bukkit.getOnlinePlayers().size() > 150)
        {
            sender.sendMessage("     ");
            sender.sendMessage("There is " + ChatColor.AQUA + Bukkit.getOnlinePlayers().size() + ChatColor.WHITE + " players online");
            sender.sendMessage("     ");

            return;
        }

        List<Rank> ranksReversed = new ArrayList<>(Revival.getRankManager().getRanks());
        Rank defaultRank = new Rank("Default", ChatColor.WHITE + "", null);
        List<Rank> ranks = Lists.reverse(ranksReversed);
        ranks.add(defaultRank);

        StringBuilder rankList = new StringBuilder();

        for(Rank rank : ranks)
        {
            rankList.append(ChatColor.translateAlternateColorCodes('&', rank.getTag()) + rank.getName() + " ");
        }

        sender.sendMessage(rankList.toString());
        sender.sendMessage("     ");

        Map<String, Rank> playerList = new HashMap<>();

        for(Player players : Bukkit.getOnlinePlayers())
        {
            if(Revival.getRankManager().getRank(players) != null)
            {
                playerList.put(players.getName(), Revival.getRankManager().getRank(players));
            }

            else
            {
                playerList.put(players.getName(), defaultRank);
            }
        }

        for(Rank rank : ranks)
        {
            List<String> usernames = new ArrayList<>();

            for(String players : playerList.keySet())
            {
                if(rank.equals(playerList.get(players)))
                {
                    usernames.add(players);
                }
            }

            if(usernames.isEmpty()) continue;

            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', rank.getTag() + rank.getName() + ChatColor.RESET + ": " + Joiner.on(", ").join(usernames)));
            sender.sendMessage("     ");
        }

        sender.sendMessage("There is " + ChatColor.AQUA + Bukkit.getOnlinePlayers().size() + ChatColor.WHITE + " players online");
        sender.sendMessage("     ");
    }

}
