package ru.ramazanov.DipperShip.simulator;

import org.springframework.stereotype.Component;
import org.springframework.util.SerializationUtils;
import ru.ramazanov.DipperShip.models.*;
import ru.ramazanov.DipperShip.models.Crane;
import ru.ramazanov.DipperShip.models.ShipService;
import ru.ramazanov.DipperShip.models.TimeTable;
import ru.ramazanov.DipperShip.sheduleGenerator.Ship;
import ru.ramazanov.DipperShip.sheduleGenerator.ShipSlot;
import ru.ramazanov.DipperShip.tools.CraneInitializer;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Random;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;
import java.util.stream.Collectors;

@Component
public class ThirdService implements ShipService {

    private final int bulkCraneEfficiency = 1;
    private final int liquidCraneEfficiency = 2;
    private final int containerCraneEfficiency = 4;
    private TimeTable timeTable;
    private CyclicBarrier cyclicBarrier;

    public ThirdService() {
    }

    public void solveTimeTable (TimeTable timeTable) {
        this.timeTable = timeTable;
        generateTimeOffsets();
        sortShipSlotsByArrivalTime();

        List<ShipSlot> optimalShipSlotList = new ArrayList<>();
        int optimalPenalty = -1;
        int optimalBulkCraneAmount = -1;
        int optimalLiquidCraneAmount = -1;
        int optimalContainerCraneAmount = -1;
        int maxCraneAmount = 3;

        for(int bulkCraneAmount = 1; bulkCraneAmount < maxCraneAmount; ++bulkCraneAmount) {

            for(int liquidCraneAmount = 1; liquidCraneAmount < maxCraneAmount; ++liquidCraneAmount) {

                for(int containerCraneAmount = 1; containerCraneAmount < maxCraneAmount; ++containerCraneAmount) {

                    nullifyShipSlots();

                    List<Crane> craneList = CraneInitializer.createCraneList(bulkCraneAmount, liquidCraneAmount
                            , containerCraneAmount, bulkCraneEfficiency
                            , liquidCraneEfficiency, containerCraneEfficiency);
                    List<CraneRunnable> craneRunnableList = new ArrayList<>();
                    int totalCraneAmount = bulkCraneAmount + liquidCraneAmount + containerCraneAmount;

                    cyclicBarrier = new CyclicBarrier(totalCraneAmount);

                    for(int i = 0; i < totalCraneAmount; ++i) {
                        craneRunnableList.add( new CraneRunnable(this, craneList.get(i) ) );
                    }

                    for(int i = 0; i < totalCraneAmount; ++i) {
                        craneRunnableList.get(i).getThread().start();
                    }

                    for(int i = 0; i < totalCraneAmount; ++i) {
                        try {
                            craneRunnableList.get(i).getThread().join();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }

                    int totalPenalty = 0;
                    for (ShipSlot shipSlot : timeTable.getShipSlotList()) {
                        totalPenalty += shipSlot.getPenalty();
                    }

                    totalPenalty += 30000 * totalCraneAmount;

                    if(totalPenalty < optimalPenalty || optimalPenalty == -1) {
                        optimalPenalty = totalPenalty;
                        optimalBulkCraneAmount = bulkCraneAmount;
                        optimalLiquidCraneAmount = liquidCraneAmount;
                        optimalContainerCraneAmount = containerCraneAmount;
                        optimalShipSlotList.clear();
                        restoreByOptimal(optimalBulkCraneAmount, optimalLiquidCraneAmount, optimalContainerCraneAmount);
                       // optimalShipSlotList = timeTable.getShipSlotList().stream()
                        //        .collect(Collectors.toList());

                    }

                }

            }

        }

        int maxDispatchTimeOffset = 0;
        for (ShipSlot shipSlot : optimalShipSlotList) {
            if(shipSlot.getStartTime() == -1) {
                break;
            }
            System.out.println(shipSlot);

            int dispatchTimeOffsetTemp = shipSlot.getDispatchTimeOffsetNominal();
            if(dispatchTimeOffsetTemp > maxDispatchTimeOffset) {
                maxDispatchTimeOffset = dispatchTimeOffsetTemp;
            }
        }
        timeTable.setOptimalBulkCraneAmount(optimalBulkCraneAmount);
        timeTable.setOptimalContainerCraneAmount(optimalContainerCraneAmount);
        timeTable.setOptimalLiquidCraneAmount(optimalLiquidCraneAmount);

    }

    private void nullifyShipSlots() {
        for (ShipSlot shipSlot : timeTable.getShipSlotList()) {
            shipSlot.setCranesWorkingOn(0);
            Ship ship = shipSlot.getShip();
            ship.setWorkingWeight(ship.getNominalWeight());
            shipSlot.setDispatchTimeOffset(shipSlot.getDispatchTimeOffsetNominal());
            shipSlot.setStartTime(-1);
            shipSlot.setDispatchTime(-1);
            shipSlot.setPenalty(0);
        }
    }
    private void sortShipSlotsByArrivalTime() {
        timeTable.getShipSlotList().sort(Comparator.comparing(ShipSlot::getArrivalTime));
    }


    private void generateTimeOffsets() {
        Random random = new Random();
        for (ShipSlot shipSlot : timeTable.getShipSlotList()) {
            shipSlot.setDispatchTimeOffsetNominal(random.nextInt(1441));
            shipSlot.setArrivalTimeOffset(random.nextInt(21600)-10800);
            if(shipSlot.getArrivalTimeOffset() < 0) {
                shipSlot.setArrivalTimeOffset(0);
            }
            shipSlot.setArrivalTime(shipSlot.getArrivalTime() + shipSlot.getArrivalTimeOffset());
        }
    }

    private void restoreByOptimal(int optimalBulkCraneAmount, int optimalLiquidCraneAmount
            , int optimalContainerCraneAmount) {
        List<Crane> craneList = CraneInitializer.createCraneList(optimalBulkCraneAmount, optimalLiquidCraneAmount
                , optimalContainerCraneAmount, bulkCraneEfficiency
                , liquidCraneEfficiency, containerCraneEfficiency);

        List<CraneRunnable> craneRunnableList = new ArrayList<>();

        int totalCraneAmount = optimalBulkCraneAmount + optimalLiquidCraneAmount + optimalContainerCraneAmount;

        cyclicBarrier = new CyclicBarrier(totalCraneAmount);

        for (int i = 0; i < totalCraneAmount; ++i) {
            craneRunnableList.add(new CraneRunnable(this, craneList.get(i)));
        }

        for (int i = 0; i < totalCraneAmount; ++i) {
            craneRunnableList.get(i).getThread().start();
        }

        for (int i = 0; i < totalCraneAmount; ++i) {
            try {
                craneRunnableList.get(i).getThread().join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void barrierAwait() {
        try {
            cyclicBarrier.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (BrokenBarrierException e) {
            e.printStackTrace();
        }
    }

    @Override
    public ShipSlot requestShip(CraneRunnable craneRunnable) {
        for (ShipSlot shipSlot : timeTable.getShipSlotList()) {

            if(craneRunnable.getCrane().getCargoType() == shipSlot.getShip().getCargoType()
                    && shipSlot.getShip().getWorkingWeight() > 0
                    && shipSlot.getCranesWorkingOn() != 2
                    && shipSlot.getArrivalTime() <= craneRunnable.getCurrentTick()) {

                shipSlot.setCranesWorkingOn(shipSlot.getCranesWorkingOn() + 1);
                return shipSlot;
            }

        }
        return null;
    }

}
