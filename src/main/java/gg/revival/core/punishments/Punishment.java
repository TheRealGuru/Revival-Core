package gg.revival.core.punishments;

import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

public class Punishment {

    @Getter UUID uuid;
    @Getter UUID punishedPlayers;
    @Getter int punishedAddress;
    @Getter UUID punisher;
    @Getter @Setter String reason;
    @Getter PunishType type;
    @Getter long createDate;
    @Getter @Setter long expireDate;

    public Punishment(UUID uuid, UUID punishedPlayer, int punishedAddress, UUID punisher, String reason, PunishType type, long createDate, long expireDate) {
        this.uuid = uuid;
        this.punishedPlayers = punishedPlayer;
        this.punishedAddress = punishedAddress;
        this.punisher = punisher;
        this.reason = reason;
        this.type = type;
        this.createDate = createDate;
        this.expireDate = expireDate;
    }

    public boolean isExpired() {
        return expireDate < System.currentTimeMillis();
    }

    public boolean isForever() {
        return expireDate == -1L;
    }

}
