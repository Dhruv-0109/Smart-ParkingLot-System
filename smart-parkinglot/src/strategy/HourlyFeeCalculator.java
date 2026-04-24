package strategy;

import entity.Ticket;
import enums.VehicleType;
import java.util.HashMap;
import java.util.Map;
import java.time.Duration;

public class HourlyFeeCalculator implements FeeStrategy {

    private static final Map<VehicleType, Double> RATES = new HashMap<>() {{
        put(VehicleType.MOTORCYCLE, 10.0);
        put(VehicleType.CAR, 20.0);
        put(VehicleType.BUS, 50.0);
    }};

    @Override
    public double calculate(Ticket ticket) {
        Duration duration = Duration.between(ticket.getEntryTime(), ticket.getExitTime());
        //long minutes = duration.toMinutes();
        long seconds = duration.toSeconds();
        double rate = RATES.get(ticket.getVehicle().getVehicleType());
       // return (minutes / 60.0) * rate;
        return (seconds / 60.0) * rate;
    }
}