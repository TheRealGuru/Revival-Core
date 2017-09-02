package gg.revival.core.essentials;

import gg.revival.core.Revival;
import gg.revival.core.essentials.cont.*;
import lombok.Getter;

import java.util.HashSet;
import java.util.Set;

public class CommandManager
{

    /*
        TODO:
            - give/i
            - inv
            - vanish/unvanish
            - report/helpop/tickets
            - gamemode
     */

    /**
     * Contains every registered command in this plugin
     */
    @Getter Set<ECommand> commands = new HashSet<>();

    /**
     * Loads every command and registers them on the server
     */
    public CommandManager()
    {
        ECommandExecutor commandExecutor = new ECommandExecutor();

        EBanCommand banCommand = new EBanCommand();
        ETempbanCommand tempbanCommand = new ETempbanCommand();
        EUnbanCommand unbanCommand = new EUnbanCommand();
        EMuteCommand muteCommand = new EMuteCommand();
        ETempmuteCommand tempmuteCommand = new ETempmuteCommand();
        EUnmuteCommand unmuteCommand = new EUnmuteCommand();
        ELookupCommand lookupCommand = new ELookupCommand();
        EEnchantCommand enchantCommand = new EEnchantCommand();
        ENameCommand nameCommand = new ENameCommand();
        EHelpCommand helpCommand = new EHelpCommand();
        EBroadcastCommand broadcastCommand = new EBroadcastCommand();
        EKickCommand kickCommand = new EKickCommand();
        EHealCommand healCommand = new EHealCommand();
        EWeatherCommand weatherCommand = new EWeatherCommand();
        ETeleportCommand teleportCommand = new ETeleportCommand();
        EFreezeCommand freezeCommand = new EFreezeCommand();
        EUnfreezeCommand unfreezeCommand = new EUnfreezeCommand();
        EHubCommand hubCommand = new EHubCommand();
        EXPCommand xpCommand = new EXPCommand();
        EMessageCommand messageCommand = new EMessageCommand();
        EReplyCommand replyCommand = new EReplyCommand();
        EGiveCommand giveCommand = new EGiveCommand();

        commands.add(banCommand);
        commands.add(tempbanCommand);
        commands.add(unbanCommand);
        commands.add(muteCommand);
        commands.add(tempmuteCommand);
        commands.add(unmuteCommand);
        commands.add(lookupCommand);
        commands.add(enchantCommand);
        commands.add(nameCommand);
        commands.add(helpCommand);
        commands.add(broadcastCommand);
        commands.add(kickCommand);
        commands.add(healCommand);
        commands.add(weatherCommand);
        commands.add(teleportCommand);
        commands.add(freezeCommand);
        commands.add(unfreezeCommand);
        commands.add(hubCommand);
        commands.add(xpCommand);
        commands.add(messageCommand);
        commands.add(replyCommand);
        commands.add(giveCommand);

        for(ECommand commands : commands)
        {
            Revival.getCore().getCommand(commands.getLabel()).setExecutor(commandExecutor);
        }
    }

}
