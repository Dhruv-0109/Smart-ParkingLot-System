package entity;

import enums.SpotType;
import enums.VehicleType;
import utils.IdGenerator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ParkingFloor {
    private Long floorId;
    private String floorName;
    private Map<SpotType, List<ParkingSpot>> spots;

    public ParkingFloor(String floorName) {
        this.floorId = IdGenerator.nextFloorId();
        this.floorName = floorName;
        this.spots = new HashMap<>();
        for (SpotType type : SpotType.values()) {
            spots.put(type, new ArrayList<>());
        }
    }

    public void addSpot(ParkingSpot spot) {
        spots.get(spot.getSpotType()).add(spot);
    }

    public ParkingSpot getAvailableSpot(VehicleType vehicleType) {
        SpotType spotType = SpotType.valueOf(vehicleType.name());
        List<ParkingSpot> candidates = spots.get(spotType);
        for (ParkingSpot spot : candidates) {
            if (spot.isAvailable()) {
                return spot;
            }
        }
        return null;
    }

    public void freeSpot(ParkingSpot spot) {
        spot.freeSpot();
    }

    // getters
    public Long getFloorId() { return floorId; }
    public String getFloorName() { return floorName; }
    public Map<SpotType, List<ParkingSpot>> getSpots() { return spots; }
}