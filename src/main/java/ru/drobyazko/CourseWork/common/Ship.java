package ru.drobyazko.CourseWork.common;

import java.util.Random;

public class Ship {

    private final int weightBound = 10;

    private String name;
    private CargoType cargoType;
    private ShipType shipType;
    private int nominalWeight;
    private int workingWeight;

    public Ship() {
        Random random = new Random();
        String name = null;
        String[] adjective = { "Dusty ", "Brave ", "Untouchable ", "Genius ", "Funky ", "Crazy "};
        String[] noun = { "bear", "rain", "hunter", "searcher", "diver", "beginner", "looker", "seeker"};

        this.name = adjective[random.nextInt(adjective.length)]+noun[random.nextInt(noun.length)];

        switch(random.nextInt(3)) {
            case 0:
                this.shipType = ShipType.SMALL;
                this.nominalWeight = random.nextInt(2) + 2;
                this.workingWeight = this.nominalWeight;
                break;
            case 1:
                this.shipType = ShipType.NORMAL;
                this.nominalWeight = random.nextInt(2) + 6;
                this.workingWeight = this.nominalWeight;
                break;
            case 2:
                this.shipType = ShipType.BIG;
                this.nominalWeight = random.nextInt(2) + 10;
                this.workingWeight = this.nominalWeight;
                break;
        }
        switch(random.nextInt(3)) {
            case 0:
                this.cargoType = CargoType.BULK;
                break;
            case 1:
                this.cargoType = CargoType.LIQUID;
                break;
            case 2:
                this.cargoType = CargoType.CONTAINER;
                break;
        }
    }

    public String getName() {
        return name;
    }

    public CargoType getCargoType() {
        return cargoType;
    }

    public int getNominalWeight() {
        return nominalWeight;
    }

    public synchronized int getWorkingWeight() {
        return workingWeight;
    }

    public synchronized void setWorkingWeight(int workingWeight) {
        this.workingWeight = workingWeight;
    }

    @Override
    public String toString() {
        return "Корабль " + "\"" + name + "\"" + " Тип: " +shipType + " Вид груза: " + cargoType + " Вес равен: " + nominalWeight + " ";
    }

}
