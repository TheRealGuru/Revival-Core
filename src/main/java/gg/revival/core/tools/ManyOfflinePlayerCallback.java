package gg.revival.core.tools;

import java.util.Map;
import java.util.UUID;

public interface ManyOfflinePlayerCallback {

    void onQueryDone(Map<UUID, String> result);

}
