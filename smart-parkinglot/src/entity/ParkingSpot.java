package entity;

import enums.SpotType;
import utils.IdGenerator;

public class ParkingSpot {
    private Long spotId;
    private String spotNumber;   // e.g. "F1-001" — floor 1, spot 1
    private SpotType spotType;
    private boolean isAvailable;

    public ParkingSpot(String spotNumber, SpotType spotType) {
        this.spotId = IdGenerator.nextSpotId(); 
        this.spotNumber = spotNumber;
        this.spotType = spotType;
        this.isAvailable = true;               // available by default
    }

    public void assignSpot() {
        this.isAvailable = false;
    }

    public void freeSpot() {
        this.isAvailable = true;
    }

    // getters
    public Long getSpotId() { return spotId; }
    public String getSpotNumber() { return spotNumber; }
    public SpotType getSpotType() { return spotType; }
    public boolean isAvailable() { return isAvailable; }
}