package ru.drobyazko.CourseWork.common;

public class Crane {

    private CargoType cargoType;
    private int craneEfficiency;
    private int craneUnloadSpeed = 1;

    public Crane(CargoType cargoType, int craneEfficiency) {
        this.cargoType = cargoType;
        this.craneEfficiency = craneEfficiency;
    }

    public CargoType getCargoType() {
        return cargoType;
    }

    public int getCraneEfficiency() {
        return craneEfficiency;
    }

    public int getCraneUnloadSpeed() {
        return craneUnloadSpeed;
    }

}
