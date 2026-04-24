import entity.*;
import enums.*;
import exceptions.*;
import strategy.*;


public static void main(String[] args) {

    // setup
    ParkingLot lot = new ParkingLot("Central Parking",
            new NearestSpotAllocation(),
            new HourlyFeeCalculator());

    ParkingFloor floor1 = new ParkingFloor("Floor 1");
    floor1.addSpot(new ParkingSpot("F1-C01", SpotType.CAR));
    floor1.addSpot(new ParkingSpot("F1-C02", SpotType.CAR));
    lot.addFloor(floor1);
    lot.displayAvailability();
    // create 5 cars competing for 2 spots
    List<Vehicle> vehicles = new ArrayList<>();
    for (int i = 1; i <= 5; i++) {
        vehicles.add(new Vehicle("TN01AB000" + i, VehicleType.CAR));
    }

    // fire all threads simultaneously
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

    // wait for all threads to finish
    for (Thread t : threads) {
        try {
            t.join();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    // now this runs after all threads are done
    lot.displayAvailability();
}