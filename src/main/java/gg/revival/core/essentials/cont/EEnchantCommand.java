package gg.revival.core.essentials.cont;

import gg.revival.core.Revival;
import gg.revival.core.essentials.ECommand;
import gg.revival.core.tools.Permissions;
import org.apache.commons.lang.math.NumberUtils;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class EEnchantCommand extends ECommand
{

    public EEnchantCommand()
    {
        super(
                "enchant",
                "/enchant <enchantment> <level>",
                "Enchant an item",
                Permissions.ADMIN_TOOLS,
                2,
                2,
                true
        );
    }

    @Override
    public void onCommand(CommandSender sender, String args[])
    {
        if(!validate(sender, args)) return;

        Player player = (Player)sender;

        ItemStack item = player.getItemInHand();
        Enchantment enchantment = Revival.getItemTools().getEnchantFromString(args[0]);
        int level;

        if(item == null || item.getType().equals(Material.AIR))
        {
            player.sendMessage(ChatColor.RED + "Not holding an item");
            return;
        }

        if(enchantment == null)
        {
            player.sendMessage(ChatColor.RED + "Invalid enchantment name");
            return;
        }

        if(!NumberUtils.isNumber(args[1]))
        {
            player.sendMessage(ChatColor.RED + "Invalid enchantment level");
            return;
        }

        level = NumberUtils.toInt(args[1]);

        player.setItemInHand(Revival.getItemTools().applyEnchantment(enchantment, level, item));

        player.sendMessage(ChatColor.GREEN + "Applied enchantment: " + enchantment.getName().replace("_", " ") + ", Level: " + level);
    }

}
