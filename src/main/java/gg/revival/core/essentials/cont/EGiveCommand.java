package gg.revival.core.essentials.cont;

import gg.revival.core.Revival;
import gg.revival.core.essentials.ECommand;
import gg.revival.core.tools.MsgUtils;
import gg.revival.core.tools.Permissions;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class EGiveCommand extends ECommand
{

    public EGiveCommand()
    {
        super(
                "give",
                "/give [player] <item> [amount]",
                "Give you or another player items",
                Permissions.ADMIN_TOOLS,
                1,
                3,
                true
        );
    }

    @Override
    public void onCommand(CommandSender sender, String args[])
    {
        if(!validate(sender, args)) return;

        Player player = (Player)sender;

        if(args.length == 1)
        {
            ItemStack item = Revival.getItemTools().getItemByName(args[0]);

            if(item == null)
            {
                player.sendMessage(ChatColor.RED + "Item not found");
                return;
            }

            player.getInventory().addItem(item);
            player.sendMessage(ChatColor.YELLOW + "Given " + StringUtils.capitalize(item.getType().name().toLowerCase().replace("_", " ")));

            return;
        }

        if(args.length == 2)
        {
            if(NumberUtils.isNumber(args[1]) && Bukkit.getPlayer(args[1]) == null)
            {
                ItemStack item = Revival.getItemTools().getItemByName(args[0]);
                int amount = NumberUtils.toInt(args[1]);

                if(item == null)
                {
                    player.sendMessage(ChatColor.RED + "Item not found");
                    return;
                }

                item.setAmount(amount);

                player.getInventory().addItem(item);
                player.sendMessage(ChatColor.YELLOW + "Given " + StringUtils.capitalize(item.getType().name().toLowerCase().replace("_", " ") + " (" + amount + ")"));

                return;
            }

            else
            {
                Player givenPlayer = Bukkit.getPlayer(args[0]);
                ItemStack item = Revival.getItemTools().getItemByName(args[1]);

                if(item == null)
                {
                    player.sendMessage(ChatColor.RED + "Item not found");
                    return;
                }

                givenPlayer.getInventory().addItem(item);
                givenPlayer.sendMessage(ChatColor.YELLOW + "Given " + StringUtils.capitalize(item.getType().name().toLowerCase().replace("_", " ")));

                return;
            }
        }

        if(args.length == 3)
        {
            String namedPlayer = args[0];
            String itemName = args[1];
            String namedAmount = args[2];

            if(Bukkit.getPlayer(namedPlayer) == null)
            {
                player.sendMessage(MsgUtils.getMessage("errors.player-not-found"));
                return;
            }

            Player givenPlayer = Bukkit.getPlayer(namedPlayer);
            ItemStack item = Revival.getItemTools().getItemByName(itemName);

            if(item == null)
            {
                player.sendMessage(ChatColor.RED + "Item not found");
                return;
            }

            if(!NumberUtils.isNumber(namedAmount))
            {
                player.sendMessage(ChatColor.RED + getSyntax());
                return;
            }

            int amount = NumberUtils.toInt(namedAmount);

            item.setAmount(amount);

            givenPlayer.getInventory().addItem(item);
            givenPlayer.sendMessage(ChatColor.YELLOW + "Given " + StringUtils.capitalize(item.getType().name().toLowerCase().replace("_", " ") + " (" + amount + ")"));

            return;
        }
    }

}
