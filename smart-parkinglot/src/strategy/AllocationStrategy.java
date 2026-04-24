package strategy;

import entity.ParkingFloor;
import entity.ParkingSpot;
import enums.VehicleType;

import java.util.List;

public interface AllocationStrategy {
    ParkingSpot allocate(VehicleType vehicleType, List<ParkingFloor> floors);
}