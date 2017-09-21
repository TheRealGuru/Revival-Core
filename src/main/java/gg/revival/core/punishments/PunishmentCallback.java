package gg.revival.core.punishments;

import java.util.Set;

public interface PunishmentCallback {

    void onQueryDone(Set<Punishment> result);

}
