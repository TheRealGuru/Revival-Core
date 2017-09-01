package gg.revival.core.essentials.cont;

import gg.revival.core.Revival;
import gg.revival.core.essentials.ECommand;
import gg.revival.core.punishments.PunishType;
import gg.revival.core.punishments.Punishment;
import gg.revival.core.tools.Permissions;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.UUID;

public class EBanCommand extends ECommand
{

    public EBanCommand()
    {
        super(
                "ban",
                null,
                "/ban <player> <reason>",
                "Ban a player",
                Permissions.PUNISHMENT_BAN,
                1,
                2,
                false
        );
    }

    @Override
    public void onCommand(CommandSender sender, String args[])
    {
        if(!validate(sender, args)) return;

        UUID punisherResult = null;
        String namedPlayer = args[0];
        String reasonResult = "Reason not given";

        if(sender instanceof Player)
        {
            Player player = (Player)sender;
            punisherResult = player.getUniqueId();
        }

        if(args.length == 2)
        {
            StringBuilder reasonBuilder = new StringBuilder();

            for(int i = 1; i < args.length; i++)
            {
                reasonBuilder.append(args[i]);
            }

            reasonResult = reasonBuilder.toString();
        }

        final UUID punisher = punisherResult;
        final String reason = reasonResult;

        Revival.getPlayerTools().getOfflinePlayer(namedPlayer, (uuid, username) -> {
            if(uuid == null)
            {
                // TODO: Send player not found
                return;
            }

            Revival.getAccountManager().getAccount(uuid, result -> {
                int address = 0;

                if(result.getRegisteredAddresses() != null && !result.getRegisteredAddresses().isEmpty())
                {
                    address = result.getRegisteredAddresses().get(0);
                }

                Punishment punishment = new Punishment(UUID.randomUUID(), uuid, address, punisher, reason, PunishType.BAN, System.currentTimeMillis(), -1L);

                result.getPunishments().add(punishment);

                Revival.getAccountManager().saveAccount(result, false, Bukkit.getPlayer(uuid) == null);

                // TODO: Broadcast punishment
            });
        });
    }

}
