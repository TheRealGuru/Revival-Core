package gg.revival.core.ranks;

import com.google.common.collect.Lists;
import lombok.Getter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class RankManager
{

    /**
     * Contains every rank enabled on this server instance
     */
    @Getter List<Rank> ranks = new ArrayList<>();

    /**
     * Returns a Rank object if the player has the proper permissions
     * @param player The player
     * @return Rank object
     */
    public Rank getRank(Player player)
    {
        List<Rank> reversed = Lists.reverse(ranks);

        for(Rank rank : reversed)
        {
            if(rank.hasRank(player))
                return rank;
        }

        return null;
    }

}
