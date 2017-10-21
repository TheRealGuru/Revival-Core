package gg.revival.core.accounts;

import gg.revival.core.punishments.Punishment;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.UUID;

public class Account {

    @Getter UUID uuid;
    @Getter @Setter List<Integer> registeredAddresses;
    @Getter @Setter int xp;
    @Getter @Setter boolean hideGlobalChat;
    @Getter @Setter boolean hideMessages;
    @Getter List<UUID> blockedPlayers;
    @Getter List<Punishment> punishments;
    @Getter List<String> notes;
    @Getter @Setter long lastSeen;

    public Account(UUID uuid, List<Integer> registeredAddresses, int xp, boolean hideGlobalChat, boolean hideMessages, List<UUID> blockedPlayers,
                   List<Punishment> punishments, List<String> notes, long lastSeen) {

        this.uuid = uuid;
        this.registeredAddresses = registeredAddresses;
        this.xp = xp;
        this.hideGlobalChat = hideGlobalChat;
        this.hideMessages = hideMessages;
        this.blockedPlayers = blockedPlayers;
        this.punishments = punishments;
        this.notes = notes;
        this.lastSeen = lastSeen;

    }

}
