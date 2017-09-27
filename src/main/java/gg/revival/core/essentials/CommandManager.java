package gg.revival.core.essentials;

import gg.revival.core.Revival;
import gg.revival.core.essentials.cont.*;
import lombok.Getter;

import java.util.HashSet;
import java.util.Set;

public class CommandManager {

    @Getter private Revival revival;

    /**
     * Contains every registered command in this plugin
     */
    @Getter Set<ECommand> commands = new HashSet<>();

    /**
     * Loads every command and registers them on the server
     */
    public CommandManager(Revival revival) {
        this.revival = revival;

        ECommandExecutor commandExecutor = new ECommandExecutor(revival);

        EBanCommand banCommand = new EBanCommand(revival);
        ETempbanCommand tempbanCommand = new ETempbanCommand(revival);
        EUnbanCommand unbanCommand = new EUnbanCommand(revival);
        EMuteCommand muteCommand = new EMuteCommand(revival);
        ETempmuteCommand tempmuteCommand = new ETempmuteCommand(revival);
        EUnmuteCommand unmuteCommand = new EUnmuteCommand(revival);
        ELookupCommand lookupCommand = new ELookupCommand(revival);
        EEnchantCommand enchantCommand = new EEnchantCommand(revival);
        ENameCommand nameCommand = new ENameCommand(revival);
        EHelpCommand helpCommand = new EHelpCommand(revival);
        EBroadcastCommand broadcastCommand = new EBroadcastCommand(revival);
        EKickCommand kickCommand = new EKickCommand(revival);
        EHealCommand healCommand = new EHealCommand(revival);
        EWeatherCommand weatherCommand = new EWeatherCommand(revival);
        ETeleportCommand teleportCommand = new ETeleportCommand(revival);
        EFreezeCommand freezeCommand = new EFreezeCommand(revival);
        EUnfreezeCommand unfreezeCommand = new EUnfreezeCommand(revival);
        EHubCommand hubCommand = new EHubCommand(revival);
        EXPCommand xpCommand = new EXPCommand(revival);
        EMessageCommand messageCommand = new EMessageCommand(revival);
        EReplyCommand replyCommand = new EReplyCommand(revival);
        EGiveCommand giveCommand = new EGiveCommand(revival);
        EGamemodeCommand gamemodeCommand = new EGamemodeCommand(revival);
        EInvCommand invCommand = new EInvCommand(revival);
        EIgnoreCommand ignoreCommand = new EIgnoreCommand(revival);
        EUnignoreCommand unignoreCommand = new EUnignoreCommand(revival);
        EToggleMessagesCommand toggleMessagesCommand = new EToggleMessagesCommand(revival);
        EToggleGlobalCommand toggleGlobalCommand = new EToggleGlobalCommand(revival);
        EListCommand listCommand = new EListCommand(revival);
        EReportCommand reportCommand = new EReportCommand(revival);
        EHelpOpCommand helpopCommand = new EHelpOpCommand(revival);
        ETicketsCommand ticketsCommand = new ETicketsCommand(revival);

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
        commands.add(gamemodeCommand);
        commands.add(invCommand);
        commands.add(ignoreCommand);
        commands.add(unignoreCommand);
        commands.add(toggleMessagesCommand);
        commands.add(toggleGlobalCommand);
        commands.add(listCommand);
        commands.add(reportCommand);
        commands.add(helpopCommand);
        commands.add(ticketsCommand);

        for(ECommand commands : commands)
            Revival.getCore().getCommand(commands.getLabel()).setExecutor(commandExecutor);
    }

}
