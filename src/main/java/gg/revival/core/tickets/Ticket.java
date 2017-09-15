package gg.revival.core.tickets;

import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

public class Ticket {

    @Getter UUID ticketUUID;
    @Getter UUID ticketCreator;
    @Getter UUID reportedUUID;
    @Getter String reason;
    @Getter long createDate;
    @Getter @Setter boolean closed;

    public Ticket(UUID ticketUUID, UUID ticketCreator, UUID reportedUUID, String reason, long createDate) {
        this.ticketUUID = ticketUUID;
        this.ticketCreator = ticketCreator;
        this.reportedUUID = reportedUUID;
        this.reason = reason;
        this.createDate = createDate;
        this.closed = false;
    }

}
