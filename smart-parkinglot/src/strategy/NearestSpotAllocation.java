package strategy;

import entity.ParkingFloor;
import entity.ParkingSpot;
import enums.VehicleType;

import java.util.List;

public class NearestSpotAllocation implements AllocationStrategy {

    @Override
    public ParkingSpot allocate(VehicleType vehicleType, List<ParkingFloor> floors) {
        for (ParkingFloor floor : floors) {
            ParkingSpot spot = floor.getAvailableSpot(vehicleType);
            if (spot != null) {
                return spot;
            }
        }
        return null;
    }
}