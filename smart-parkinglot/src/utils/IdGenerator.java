package utils;

public class IdGenerator {

    public static Long vehicleId = 0L;
    public static Long ticketId = 0L;
    public static Long floorId = 0L;
    public static Long spotId = 0L;

    public static synchronized Long nextVehicleId() {
        vehicleId = vehicleId + 1;
        return vehicleId;
    }

    public static synchronized Long nextTicketId() {
        ticketId = ticketId + 1;
        return ticketId;
    }

    public static synchronized Long nextFloorId() {
        floorId = floorId + 1;
        return floorId;
    }


    public static synchronized Long nextSpotId() {
        spotId = spotId + 1;
        return spotId;
    }
}