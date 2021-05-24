package ru.ramazanov.DipperShip.models;

import ru.ramazanov.DipperShip.simulator.CraneRunnable;
import ru.ramazanov.DipperShip.sheduleGenerator.ShipSlot;

public interface ShipService {
    int tickAmount = 43200;
    ShipSlot requestShip(CraneRunnable craneRunnable);
    void barrierAwait();
}
