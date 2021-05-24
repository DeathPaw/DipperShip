package ru.ramazanov.DipperShip.tools;

import ru.ramazanov.DipperShip.models.CargoType;
import ru.ramazanov.DipperShip.models.Crane;

import java.util.ArrayList;
import java.util.List;

public class CraneInitializer {

    public static List<Crane> createCraneList(int bulkCraneAmount, int liquidCraneAmount, int containerCraneAmount
            , int bulkCraneEfficiency, int liquidCraneEfficiency, int containerCraneEfficiency) {
        List<Crane> craneList = new ArrayList<>();

        for(int i = 0; i < bulkCraneAmount; ++i) {
            Crane newCraneBulk = new Crane(CargoType.BULK, bulkCraneEfficiency);
            craneList.add(newCraneBulk);
        }

        for(int i = 0; i < liquidCraneAmount; ++i) {
            Crane newCraneLiquid = new Crane(CargoType.LIQUID, liquidCraneEfficiency);
            craneList.add(newCraneLiquid);
        }

        for(int i = 0; i < containerCraneAmount; ++i) {
            Crane newCraneContainer = new Crane(CargoType.CONTAINER, containerCraneEfficiency);
            craneList.add(newCraneContainer);
        }

        return craneList;
    }

}
