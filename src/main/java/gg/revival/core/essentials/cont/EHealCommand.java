package gg.revival.core.essentials.cont;

import gg.revival.core.Revival;
import gg.revival.core.essentials.ECommand;
import gg.revival.core.tools.Permissions;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class EHealCommand extends ECommand {

    public EHealCommand(Revival revival) {
        super(
                revival,
                "heal",
                "/heal [player]",
                "Heal a player",
                Permissions.ADMIN_TOOLS,
                0,
                1,
                false
        );
    }

    @Override
    public void onCommand(CommandSender sender, String args[]) {
        if(!validate(sender, args)) return;

        if(args.length == 0) {
            if(!(sender instanceof Player)) {
                sender.sendMessage(ChatColor.RED + "You can't heal the console!");
                return;
            }

            Player player = (Player)sender;

            player.setHealth(20.0);
            player.setFoodLevel(20);
            player.setSaturation(20);
            player.setExhaustion(0);
            player.setNoDamageTicks(0);
            player.setFireTicks(0);
            player.setFallDistance(0);

            player.sendMessage(ChatColor.GREEN + "You have been healed");

            return;
        }

        String namedPlayer = args[0];
        String healer = "Console";

        if(sender instanceof Player) {
            Player player = (Player)sender;
            healer = player.getName();
        }

        if(Bukkit.getPlayer(namedPlayer) == null || !Bukkit.getPlayer(namedPlayer).isOnline()) {
            sender.sendMessage(getRevival().getMsgTools().getMessage("errors.player-not-found"));
            return;
        }

        Player player = Bukkit.getPlayer(namedPlayer);

        player.setHealth(20.0);
        player.setFoodLevel(20);
        player.setSaturation(20);
        player.setExhaustion(0);
        player.setNoDamageTicks(0);
        player.setFireTicks(0);
        player.setFallDistance(0);

        player.sendMessage(ChatColor.GREEN + "You have been healed by " + healer);
        sender.sendMessage(ChatColor.GREEN + "You have healed " + player.getName());
    }

}
