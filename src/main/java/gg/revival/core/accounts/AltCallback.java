package gg.revival.core.accounts;

import java.util.Set;
import java.util.UUID;

public interface AltCallback {

    void onQueryDone(Set<UUID> accounts);

}
