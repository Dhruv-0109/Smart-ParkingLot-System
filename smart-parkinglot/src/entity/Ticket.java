package entity;

import utils.IdGenerator;
import java.time.LocalDateTime;

public class Ticket {
    private Long ticketId;
    private Vehicle vehicle;
    private ParkingSpot spot;
    private LocalDateTime entryTime;
    private LocalDateTime exitTime;

    public Ticket(Vehicle vehicle, ParkingSpot spot) {
        this.ticketId = IdGenerator.nextTicketId();
        this.vehicle = vehicle;
        this.spot = spot;
        this.entryTime = LocalDateTime.now();
        this.exitTime = null;                  // set on exit
    }

    public void closeTicket() {
        this.exitTime = LocalDateTime.now();
    }

    // getters
    public Long getTicketId() { return ticketId; }
    public Vehicle getVehicle() { return vehicle; }
    public ParkingSpot getSpot() { return spot; }
    public LocalDateTime getEntryTime() { return entryTime; }
    public LocalDateTime getExitTime() { return exitTime; }
}