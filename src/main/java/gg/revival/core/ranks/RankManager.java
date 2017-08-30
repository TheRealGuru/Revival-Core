package gg.revival.core.ranks;

import lombok.Getter;
import org.bukkit.entity.Player;

import java.util.HashSet;
import java.util.Set;

public class RankManager
{

    /**
     * Contains every rank enabled on this server instance
     */
    @Getter Set<Rank> ranks = new HashSet<>();

    /**
     * Returns a Rank object if the player has the proper permissions
     * @param player The player
     * @return Rank object
     */
    public Rank getRank(Player player)
    {
        for(Rank rank : ranks)
        {
            if(rank.hasRank(player))
                return rank;
        }

        return null;
    }

}
