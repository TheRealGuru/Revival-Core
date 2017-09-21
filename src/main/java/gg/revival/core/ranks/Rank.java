package gg.revival.core.ranks;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.entity.Player;

public class Rank {

    @Getter @Setter String name, tag, permission;

    public Rank(String name, String tag, String permission) {
        this.name = name;
        this.tag = tag;
        this.permission = permission;
    }

    public boolean hasRank(Player player) {
        return player.hasPermission(permission);
    }

}
