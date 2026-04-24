package entity;

import utils.IdGenerator;
import enums.VehicleType;
public class Vehicle {
    private Long id;
    private String licensePlate;
    private VehicleType type;

    public Vehicle(String licensePlate, VehicleType type) {
        this.id = IdGenerator.nextVehicleId();
        this.licensePlate = licensePlate;
        this.type = type;
    }
    
    public Long getVehicleId() {
        return id;
    }

    public String getLicensePlate() {
        return licensePlate;
    }

    public void setLicensePlate(String licensePlate) {
        this.licensePlate = licensePlate;
    }

    public VehicleType getVehicleType() {
        return type;
    }

    public void setVehicleType(VehicleType type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return "Vehicle {" +
                "id=" + id +
                ", licensePlate='" + licensePlate + '\'' +
                ", type=" + type +
                '}';
    }
}