package entity;

import exceptions.NoActiveTicketFoundException;
import exceptions.ParkingLotFullException;
import strategy.AllocationStrategy;
import strategy.FeeStrategy;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import enums.SpotType;

public class ParkingLot {
    private String name;
    private List<ParkingFloor> floors;
    private Map<Long, Ticket> activeTickets;
    private AllocationStrategy allocationStrategy;
    private FeeStrategy feeStrategy;

    public ParkingLot(String name, AllocationStrategy allocationStrategy, FeeStrategy feeStrategy) {
        this.name = name;
        this.allocationStrategy = allocationStrategy;
        this.feeStrategy = feeStrategy;
        this.floors = new ArrayList<>();
        this.activeTickets = new HashMap<>();
    }

    public void addFloor(ParkingFloor floor) {
        floors.add(floor);
    }

    public Ticket parkVehicle(Vehicle vehicle) throws ParkingLotFullException {
        synchronized (this) {
            ParkingSpot spot = allocationStrategy.allocate(vehicle.getVehicleType(), floors);
            if (spot == null) {
                throw new ParkingLotFullException("No available spot for vehicle type: "
                        + vehicle.getVehicleType());
            }
            spot.assignSpot();
            Ticket ticket = new Ticket(vehicle, spot);
            activeTickets.put(vehicle.getVehicleId(), ticket);
            return ticket;
        }
    }

    public double exitVehicle(Vehicle vehicle) throws NoActiveTicketFoundException {
        synchronized (this) {
            Ticket ticket = activeTickets.get(vehicle.getVehicleId());
            if (ticket == null) {
                throw new NoActiveTicketFoundException("No active ticket found for vehicle: "
                        + vehicle.getLicensePlate());
            }
            ticket.closeTicket();
            double fee = feeStrategy.calculate(ticket);
            activeTickets.remove(vehicle.getVehicleId());
            return fee;
        }
    }

    public void displayAvailability() {
        System.out.println("--- " + name + " ---");
        for (ParkingFloor floor : floors) {
            System.out.println(floor.getFloorName() + ":");
            for (Map.Entry<SpotType, List<ParkingSpot>> entry : floor.getSpots().entrySet()) {
                long available = entry.getValue().stream()
                        .filter(ParkingSpot::isAvailable)
                        .count();
                System.out.println("  " + entry.getKey() + " → " + available + " available");
            }
        }
    }


    

    // getters
    public String getName() { return name; }
    public List<ParkingFloor> getFloors() { return floors; }
}