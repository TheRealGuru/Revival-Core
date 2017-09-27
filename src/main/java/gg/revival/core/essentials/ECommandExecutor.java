package gg.revival.core.essentials;

import gg.revival.core.Revival;
import lombok.Getter;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class ECommandExecutor implements CommandExecutor {

    @Getter private Revival revival;

    public ECommandExecutor(Revival revival) {
        this.revival = revival;
    }

    /**
     * Loops through every registered command to see if it matches
     * @param sender CommandSender
     * @param command Command
     * @param commandLabel String
     * @param args Arguments
     * @return NOTHIN HEHE
     */
    public boolean onCommand(CommandSender sender, Command command, String commandLabel, String args[]) {
        for(ECommand cmd : revival.getCommandManager().getCommands()) {
            if(command.getName().equalsIgnoreCase(cmd.getLabel())) {
                cmd.onCommand(sender, args);

                return true;
            }
        }

        return false;
    }

}
