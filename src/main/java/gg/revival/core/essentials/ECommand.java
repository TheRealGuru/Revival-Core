package gg.revival.core.essentials;

import gg.revival.core.Revival;
import lombok.Getter;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ECommand {

    @Getter private Revival revival;
    @Getter String label, syntax, description, permission;
    @Getter int minArgs, maxArgs;
    @Getter boolean playerOnly;

    public ECommand(Revival revival, String label, String syntax, String description, String permission, int minArgs, int maxArgs, boolean playerOnly) {
        this.revival = revival;
        this.label = label;
        this.syntax = syntax;
        this.description = description;
        this.permission = permission;
        this.minArgs = minArgs;
        this.maxArgs = maxArgs;
        this.playerOnly = playerOnly;
    }

    /**
     * Returns true if the command + arguments are valid and the player has the proper permissions to issue this command
     * @param sender CommandSender
     * @param args Arguments
     * @return Returns true if the command is valid
     */
    public boolean validate(CommandSender sender, String args[]) {
        if(!(sender instanceof Player) && isPlayerOnly()) {
            sender.sendMessage(revival.getMsgTools().getMessage("errors.no-console"));
            return false;
        }

        if(permission != null && sender instanceof Player) {
            Player player = (Player)sender;

            if(!player.hasPermission(permission)) {
                player.sendMessage(revival.getMsgTools().getMessage("errors.no-permission"));
                return false;
            }
        }

        if(args.length < minArgs || args.length > maxArgs) {
            if(sender instanceof Player) {
                Player player = (Player)sender;
                player.sendMessage(ChatColor.RED + syntax);
            } else {
                sender.sendMessage(syntax);
            }

            return false;
        }

        return true;
    }

    public void onCommand(CommandSender sender, String args[]) {}

}
