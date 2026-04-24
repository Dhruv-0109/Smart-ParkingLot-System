import entity.*;
import enums.SpotType;
import enums.VehicleType;
import exceptions.NoActiveTicketFoundException;
import exceptions.ParkingLotFullException;
import strategy.HourlyFeeCalculator;
import strategy.NearestSpotAllocation;

import java.util.ArrayList;
import java.util.List;

public class Main {

    public static void main(String[] args) {
        ParkingLot lot = setupLot();

        demoNormalFlow(lot);
        demoAllocationAcrossFloors(lot);
        demoLotFullException(lot);
        demoNoActiveTicketException(lot);
        demoConcurrency(lot);
    }

    // ── setup ──────────────────────────────────────────────────────────────

    private static ParkingLot setupLot() {
        ParkingLot lot = new ParkingLot("Central Parking",
                new NearestSpotAllocation(),
                new HourlyFeeCalculator());

        ParkingFloor floor1 = new ParkingFloor("Floor 1");
        floor1.addSpot(new ParkingSpot("F1-C01", SpotType.CAR));
        floor1.addSpot(new ParkingSpot("F1-C02", SpotType.CAR));
        floor1.addSpot(new ParkingSpot("F1-M01", SpotType.MOTORCYCLE));
        floor1.addSpot(new ParkingSpot("F1-B01", SpotType.BUS));

        ParkingFloor floor2 = new ParkingFloor("Floor 2");
        floor2.addSpot(new ParkingSpot("F2-C01", SpotType.CAR));
        floor2.addSpot(new ParkingSpot("F2-M01", SpotType.MOTORCYCLE));

        lot.addFloor(floor1);
        lot.addFloor(floor2);

        System.out.println("=== Lot initialised ===");
        lot.displayAvailability();
        return lot;
    }

    // ── normal park and exit ───────────────────────────────────────────────

    private static void demoNormalFlow(ParkingLot lot) {
        System.out.println("\n=== Normal park and exit ===");
        Vehicle car = new Vehicle("TN01AB1111", VehicleType.CAR);
        try {
            Ticket ticket = lot.parkVehicle(car);
            System.out.println("Parked: " + car.getLicensePlate()
                    + " | Spot: " + ticket.getSpot().getSpotNumber()
                    + " | Ticket: " + ticket.getTicketId());

            double fee = lot.exitVehicle(car);
            System.out.println("Exited: " + car.getLicensePlate()
                    + " | Fee: ₹" + String.format("%.2f", fee));
        } catch (ParkingLotFullException | NoActiveTicketFoundException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    // ── allocation across floors ───────────────────────────────────────────

    private static void demoAllocationAcrossFloors(ParkingLot lot) {
        System.out.println("\n=== Allocation across floors ===");
        Vehicle car1 = new Vehicle("TN01AB2222", VehicleType.CAR);
        Vehicle car2 = new Vehicle("TN01AB3333", VehicleType.CAR);
        Vehicle car3 = new Vehicle("TN01AB4444", VehicleType.CAR);
        try {
            Ticket t1 = lot.parkVehicle(car1);
            System.out.println("Parked: " + car1.getLicensePlate()
                    + " | Spot: " + t1.getSpot().getSpotNumber());

            Ticket t2 = lot.parkVehicle(car2);
            System.out.println("Parked: " + car2.getLicensePlate()
                    + " | Spot: " + t2.getSpot().getSpotNumber());

            // floor 1 car spots full — should go to floor 2
            Ticket t3 = lot.parkVehicle(car3);
            System.out.println("Parked: " + car3.getLicensePlate()
                    + " | Spot: " + t3.getSpot().getSpotNumber() + " ← moved to floor 2");

            lot.displayAvailability();

            // clean up
            lot.exitVehicle(car1);
            lot.exitVehicle(car2);
            lot.exitVehicle(car3);
        } catch (ParkingLotFullException | NoActiveTicketFoundException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    // ── lot full exception ─────────────────────────────────────────────────

    private static void demoLotFullException(ParkingLot lot) {
        System.out.println("\n=== Lot full exception ===");
        List<Vehicle> buses = List.of(
                new Vehicle("TN01BUS001", VehicleType.BUS),
                new Vehicle("TN01BUS002", VehicleType.BUS)
        );
        try {
            lot.parkVehicle(buses.get(0));
            System.out.println("Parked: " + buses.get(0).getLicensePlate());
            lot.parkVehicle(buses.get(1)); // only 1 bus spot exists
            System.out.println("Parked: " + buses.get(1).getLicensePlate());
        } catch (ParkingLotFullException e) {
            System.out.println("Caught expected exception: " + e.getMessage());
        }
        // clean up
        try { lot.exitVehicle(buses.get(0)); }
        catch (NoActiveTicketFoundException e) { System.out.println("Error: " + e.getMessage()); }
    }

    // ── no active ticket exception ─────────────────────────────────────────

    private static void demoNoActiveTicketException(ParkingLot lot) {
        System.out.println("\n=== No active ticket exception ===");
        Vehicle ghost = new Vehicle("TN01GH0000", VehicleType.CAR);
        try {
            lot.exitVehicle(ghost);
        } catch (NoActiveTicketFoundException e) {
            System.out.println("Caught expected exception: " + e.getMessage());
        }
    }

    // ── concurrency stress test ────────────────────────────────────────────

    private static void demoConcurrency(ParkingLot lot) {
        System.out.println("\n=== Concurrency stress test (5 cars, 2 spots) ===");

        List<Vehicle> vehicles = new ArrayList<>();
        for (int i = 1; i <= 5; i++) {
            vehicles.add(new Vehicle("TN01CC000" + i, VehicleType.CAR));
        }

        List<Thread> threads = new ArrayList<>();
        for (Vehicle vehicle : vehicles) {
            Thread t = new Thread(() -> {
                try {
                    Ticket ticket = lot.parkVehicle(vehicle);
                    System.out.println("Parked: " + vehicle.getLicensePlate()
                            + " | Spot: " + ticket.getSpot().getSpotNumber());
                } catch (ParkingLotFullException e) {
                    System.out.println("Rejected: " + vehicle.getLicensePlate()
                            + " | Lot full");
                }
            });
            threads.add(t);
            t.start();
        }

        for (Thread t : threads) {
            try { t.join(); }
            catch (InterruptedException e) { Thread.currentThread().interrupt(); }
        }

        System.out.println("Expected: 2 parked, 3 rejected");
        lot.displayAvailability();
    }
}