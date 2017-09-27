package gg.revival.core.essentials.cont;

import gg.revival.core.Revival;
import gg.revival.core.essentials.ECommand;
import gg.revival.core.tools.Permissions;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ETeleportCommand extends ECommand {

    public ETeleportCommand(Revival revival) {
        super(
                revival,
                "teleport",
                "/teleport <player> [player] - /teleport [world] <x> <y> <z>",
                "Teleport to a player or location",
                Permissions.MOD_TOOLS,
                1,
                4,
                true
        );
    }

    @Override
    public void onCommand(CommandSender sender, String args[]) {
        if(!validate(sender, args)) return;

        Player player = (Player)sender;

        if(args.length == 1) {
            String namedPlayer = args[0];

            if(namedPlayer.equalsIgnoreCase("-all")) {
                for(Player players : Bukkit.getOnlinePlayers()) {
                    if(players.getUniqueId().equals(player.getUniqueId())) continue;

                    players.teleport(player);
                    players.sendMessage(ChatColor.YELLOW + "You have been teleported to " + ChatColor.AQUA + player.getName());
                }

                player.sendMessage(ChatColor.YELLOW + "You have summoned every player");

                return;
            }

            if(Bukkit.getPlayer(namedPlayer) == null || !Bukkit.getPlayer(namedPlayer).isOnline())
                player.sendMessage(getRevival().getMsgTools().getMessage("errors.player-not-found"));

            Player toPlayer = Bukkit.getPlayer(namedPlayer);

            player.teleport(toPlayer);
            player.sendMessage(ChatColor.YELLOW + "Teleported to " + ChatColor.AQUA + toPlayer.getName());

            return;
        }

        if(args.length == 2) {
            String namedFromPlayer = args[0];
            String namedToPlayer = args[1];

            if(Bukkit.getPlayer(namedFromPlayer) == null || Bukkit.getPlayer(namedToPlayer) == null)
                player.sendMessage(getRevival().getMsgTools().getMessage("errors.player-not-found"));

            Player fromPlayer = Bukkit.getPlayer(namedFromPlayer);
            Player toPlayer = Bukkit.getPlayer(namedToPlayer);

            fromPlayer.teleport(toPlayer);
            fromPlayer.sendMessage(ChatColor.YELLOW + "You have been teleported to " + ChatColor.AQUA + toPlayer.getName());

            return;
        }

        if(args.length == 3) {
            String namedX = args[0], namedY = args[1], namedZ = args[2];
            double x, y, z;

            try {
                x = Double.valueOf(namedX);
                y = Double.valueOf(namedY);
                z = Double.valueOf(namedZ);
            } catch (NumberFormatException e) {
                player.sendMessage(ChatColor.RED + "Invalid coordinates");
                return;
            }

            Location location = new Location(player.getWorld(), x, y, z);

            player.teleport(location);
            player.sendMessage(ChatColor.YELLOW + "You have teleported to 'World: " + player.getWorld().getName() + ", X: " + x + ", Y: " + y + ", Z: " + z + "'");

            return;
        }

        if(args.length == 4) {
            String namedWorld = args[0];
            String namedX = args[1], namedY = args[2], namedZ = args[3];
            double x, y, z;

            if(Bukkit.getWorld(namedWorld) == null) {
                player.sendMessage(ChatColor.RED + "Invalid world");
                return;
            }

            try {
                x = Double.valueOf(namedX);
                y = Double.valueOf(namedY);
                z = Double.valueOf(namedZ);
            } catch (NumberFormatException e) {
                player.sendMessage(ChatColor.RED + "Invalid coordinates");
                return;
            }

            Location location = new Location(Bukkit.getWorld(namedWorld), x, y, z);

            player.teleport(location);
            player.sendMessage(ChatColor.YELLOW + "You have teleported to 'World: " + Bukkit.getWorld(namedWorld).getName() + ", X: " + x + ", Y: " + y + ", Z: " + z + "'");
        }
    }

}
