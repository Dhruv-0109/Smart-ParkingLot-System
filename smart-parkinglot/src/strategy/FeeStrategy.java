package strategy;
import entity.Ticket;

public interface FeeStrategy {
    double calculate(Ticket ticket);
}