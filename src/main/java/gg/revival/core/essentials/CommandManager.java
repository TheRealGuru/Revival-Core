package gg.revival.core.essentials;

import gg.revival.core.Revival;
import gg.revival.core.essentials.cont.EBanCommand;
import lombok.Getter;

import java.util.HashSet;
import java.util.Set;

public class CommandManager
{

    /**
     * Contains every registered command in this plugin
     */
    @Getter Set<ECommand> commands = new HashSet<>();

    /**
     * Loads every command and registers them on the server
     */
    public CommandManager()
    {
        EBanCommand banCommand = new EBanCommand();
        commands.add(banCommand);

        for(ECommand commands : commands)
        {
            Revival.getCore().getCommand(commands.getLabel()).setExecutor(new ECommandExecutor());
        }
    }

}
