# Smart Parking Lot System

A low-level design implementation of a smart parking lot system in Java. 

## Overview

The system manages vehicle entry and exit across a multi-floor parking lot. It automatically assigns spots based on vehicle type, tracks time, calculates fees, and handles concurrent vehicle arrivals safely.

## Project Structure

```
smart-parkinglot/src/
├── entity/
│   ├── ParkingLot.java
│   ├── ParkingFloor.java
│   ├── ParkingSpot.java
│   ├── Vehicle.java
│   └── Ticket.java
├── enums/
│   ├── VehicleType.java
│   └── SpotType.java
├── strategy/
│   ├── AllocationStrategy.java
│   ├── FeeStrategy.java
│   ├── NearestSpotAllocation.java
│   └── HourlyFeeCalculator.java
├── exceptions/
│   ├── ParkingLotFullException.java
│   └── NoActiveTicketFoundException.java
├── utils/
│   └── IdGenerator.java
└── Main.java
```

## Features

- Multi-floor parking with dedicated spots per vehicle type (Car, Motorcycle, Bus)
- Automatic spot allocation via pluggable `AllocationStrategy`
- Fee calculation via pluggable `FeeStrategy` based on exact duration
- Real-time availability display across all floors
- Thread-safe entry and exit handling using synchronized blocks
- Centralized ID generation for vehicles, tickets, floors and spots

## Design Patterns Used

- **Strategy Pattern** — `AllocationStrategy` and `FeeStrategy` are interfaces with swappable implementations. Parking lot doesn't know or care which implementation is injected.
- **Composition** — `ParkingLot` owns `ParkingFloor` objects, `ParkingFloor` owns `ParkingSpot` objects. Lifecycle is tied to the parent.
- **Dependency Inversion** — High level modules (`ParkingLot`) depend on abstractions (`AllocationStrategy`, `FeeStrategy`), not concrete implementations.

## Data Structures

| Class | Structure | Reason |
|-------|-----------|--------|
| `ParkingFloor` | `Map<SpotType, List<ParkingSpot>>` | O(1) access to spots by type, avoids scanning irrelevant spots |
| `ParkingLot` | `Map<Long, Ticket>` | O(1) ticket lookup by vehicleId on exit |
| `ParkingLot` | `List<ParkingFloor>` | Ordered — floors scanned sequentially, ground floor first |

## Parking Flow

```
Vehicle enters
     ↓
ParkingLot.parkVehicle(vehicle)
     ↓
AllocationStrategy.allocate(vehicleType, floors)
     ↓
ParkingSpot.assignSpot()
     ↓
Ticket created (spot + entryTime)
     ↓
Ticket stored in activeTickets map
```

## Exit Flow

```
Vehicle exits
     ↓
ParkingLot.exitVehicle(vehicle)
     ↓
Ticket looked up by vehicleId
     ↓
Ticket.closeTicket() — sets exitTime
     ↓
FeeStrategy.calculate(ticket)
     ↓
Spot freed, ticket removed from map
     ↓
Fee returned to caller
```

## Fee Rates

| Vehicle Type | Rate |
|-------------|------|
| Motorcycle | ₹10 / hour |
| Car | ₹20 / hour |
| Bus | ₹50 / hour |

Fees are calculated on exact minutes — no rounding up to the nearest hour.

## Concurrency

`parkVehicle()` and `exitVehicle()` use `synchronized(this)` blocks around the critical section — the check-then-act sequence of finding a spot and assigning it. This prevents two threads from assigning the same spot simultaneously.

Verified with a stress test: 5 threads competing for 2 spots simultaneously always results in exactly 2 parked and 3 rejected with no duplicate assignments.

## Running the Project

```bash
cd smart-parkinglot/src
javac Main.java
java Main
```

## Sample Output

```
--- Central Parking ---
Floor 1:
  CAR        → 2 available
  MOTORCYCLE → 1 available
  BUS        → 1 available

Parked: TN01AB0001 | Spot: F1-C01 | Ticket: 1
Parked: TN01AB0002 | Spot: F1-C02 | Ticket: 2
Rejected: TN01AB0003 | Lot full
Rejected: TN01AB0004 | Lot full
Rejected: TN01AB0005 | Lot full

--- Central Parking ---
Floor 1:
  CAR        → 0 available
  MOTORCYCLE → 1 available
  BUS        → 1 available
```
