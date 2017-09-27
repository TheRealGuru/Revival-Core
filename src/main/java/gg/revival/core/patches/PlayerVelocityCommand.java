package gg.revival.core.patches;

import gg.revival.core.Revival;
import gg.revival.core.tools.Permissions;
import lombok.Getter;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class PlayerVelocityCommand implements CommandExecutor {

    @Getter private Revival revival;

    public PlayerVelocityCommand(Revival revival) {
        this.revival = revival;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String commandLabel, String[] args) {
        if(!command.getName().equalsIgnoreCase("playervelocity")) return false;

        if(sender instanceof Player) {
            Player player = (Player)sender;

            if(!player.hasPermission(Permissions.PLAYERVELOCITY_EDIT)) {
                player.sendMessage(ChatColor.RED + "You do not have permission to use this command");
                return false;
            }
        }

        if(args.length == 0) {
            sender.sendMessage(ChatColor.YELLOW + "Horizontal Multiplier is currently set to" + ChatColor.WHITE + ": " + revival.getCfg().PLAYER_VELOCITY_H);
            sender.sendMessage(ChatColor.YELLOW + "Vertical Multiplier is currently set to" + ChatColor.WHITE + ": " + revival.getCfg().PLAYER_VELOCITY_V);

            return false;
        }

        if(args.length == 1) {
            sender.sendMessage(ChatColor.RED + "/playervelocity <horizontal> <vertical>");
            return false;
        }

        if(args.length == 2) {
            double newHorizontal = 1.0, newVetical = 1.0;

            try {
                newHorizontal = Double.valueOf(args[0]);
                newVetical = Double.valueOf(args[1]);
            } catch (NumberFormatException e) {
                sender.sendMessage(ChatColor.RED + "Invalid multiplier values");
                return false;
            }

            revival.getCfg().PLAYER_VELOCITY_H = newHorizontal;
            revival.getCfg().PLAYER_VELOCITY_V = newVetical;

            revival.getFileManager().getConfig().set("patches.player-velocity.h", newHorizontal);
            revival.getFileManager().getConfig().set("patches.player-velocity.v", newVetical);
            revival.getFileManager().saveConfig();

            sender.sendMessage(ChatColor.GREEN + "Updated player velocity values");
            return false;
        }

        sender.sendMessage(ChatColor.RED + "/playervelocity <horizontal> <vertical>");
        return false;
    }
}
