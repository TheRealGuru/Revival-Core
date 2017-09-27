package gg.revival.core.essentials.cont;

import gg.revival.core.Revival;
import gg.revival.core.essentials.ECommand;
import gg.revival.core.tools.Permissions;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class ENameCommand extends ECommand {

    public ENameCommand(Revival revival) {
        super(
                revival,
                "name",
                "/name <name>",
                "Name an item",
                Permissions.ADMIN_TOOLS,
                1,
                Integer.MAX_VALUE,
                true
        );
    }

    @Override
    public void onCommand(CommandSender sender, String args[]) {
        if(!validate(sender, args)) return;

        Player player = (Player)sender;

        ItemStack item = player.getItemInHand();

        if(item == null || item.getType().equals(Material.AIR)) {
            player.sendMessage(ChatColor.RED + "Not holding an item");
            return;
        }

        StringBuilder nameBuilder = new StringBuilder();

        for(int i = 0; i < args.length; i++)
            nameBuilder.append(args[i] + " ");

        String name = ChatColor.translateAlternateColorCodes('&', nameBuilder.toString().trim());

        player.setItemInHand(getRevival().getItemTools().applyName(name, item));

        player.sendMessage(ChatColor.GREEN + "Applied name: " + ChatColor.RESET + name);
    }

}
