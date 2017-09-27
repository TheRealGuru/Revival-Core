package gg.revival.core.tools;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.wrappers.WrappedChatComponent;
import gg.revival.core.Revival;
import lombok.Getter;
import org.apache.commons.lang.WordUtils;
import org.bukkit.ChatColor;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.lang.reflect.InvocationTargetException;

public class ServerTools {

    @Getter private Revival revival;

    public ServerTools(Revival revival) {
        this.revival = revival;
    }

    private final BlockFace[] axis = { BlockFace.NORTH, BlockFace.EAST, BlockFace.SOUTH, BlockFace.WEST };
    private final BlockFace[] radial = { BlockFace.NORTH, BlockFace.NORTH_EAST, BlockFace.EAST, BlockFace.SOUTH_EAST, BlockFace.SOUTH, BlockFace.SOUTH_WEST, BlockFace.WEST, BlockFace.NORTH_WEST };

    public BlockFace yawToFace(float yaw, boolean useSubCardinalDirections) {
        if (useSubCardinalDirections)
            return radial[Math.round(yaw / 45f) & 0x7].getOppositeFace();

        return axis[Math.round(yaw / 90f) & 0x3].getOppositeFace();
    }

    public void sendFormattedTabList(Player player, int x, int y, int z, float yaw) {
        if(!revival.getCfg().TAB_ENABLED) return;

        new BukkitRunnable() {
            public void run() {
                PacketContainer packet = ProtocolLibrary.getProtocolManager().createPacket(PacketType.Play.Server.PLAYER_LIST_HEADER_FOOTER);

                String header = revival.getCfg().TAB_HEADER + "\n     ";
                String footer = revival.getCfg().TAB_FOOTER;
                String direction = WordUtils.capitalizeFully(yawToFace(yaw, true).name().toLowerCase().replace("_", " "));

                StringBuilder status = new StringBuilder();

                status.append("     \n");

                if(revival.getCfg().TAB_DISPLAY_STATUS) {
                    status.append(ChatColor.GRAY + "Facing: " + ChatColor.LIGHT_PURPLE + direction + "\n");
                    status.append(ChatColor.GRAY + "X: " + ChatColor.LIGHT_PURPLE + x + " " + ChatColor.GRAY + "Y: " + ChatColor.LIGHT_PURPLE + y + " " + ChatColor.GRAY + "Z: " + ChatColor.LIGHT_PURPLE + z + "\n     \n");
                }

                status.append(footer);

                packet.getChatComponents()
                        .write(0, WrappedChatComponent.fromText(header))
                        .write(1, WrappedChatComponent.fromText(status.toString()));

                try {
                    ProtocolLibrary.getProtocolManager().sendServerPacket(player, packet);
                } catch (InvocationTargetException e) {
                    e.printStackTrace();
                }
            }
        }.runTaskAsynchronously(Revival.getCore());
    }

}
