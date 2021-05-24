package ru.ramazanov.DipperShip.dataDispatcher;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;
import ru.ramazanov.DipperShip.sheduleGenerator.ShipSlot;
import ru.ramazanov.DipperShip.models.TimeTable;

import java.io.File;
import java.io.IOException;

import static ru.ramazanov.DipperShip.tools.TimeFormatter.formatTime;

@Component
public class SecondService {

    public void saveJsonFile(String fileName, TimeTable timeTable) {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            objectMapper.writerWithDefaultPrettyPrinter()
                    .writeValue(new File("./timetables/"+ fileName), timeTable);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public TimeTable getJsonFile(String fileName) {
        ObjectMapper objectMapper = new ObjectMapper();
        TimeTable timeTable = null;
        File file = new File("./timetables/"+ fileName);
        if(!file.exists()) {
            return null;
        }
        try {
            timeTable = objectMapper.readValue(file, TimeTable.class);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return timeTable;
    }

    public void printTimeTable(TimeTable timeTable) {
        System.out.println("Результаты:");
        int totalCraneAmount = timeTable.getOptimalBulkCraneAmount()
                + timeTable.getOptimalLiquidCraneAmount()
                + timeTable.getOptimalContainerCraneAmount();
        int optimalCost = 30000 * totalCraneAmount;
        int doneShipsNumber = 0;
        int queueTime = 0;
        int dispatchTimeOffset = 0;
        int maxDispatchTimeOffset = 0;
        for (ShipSlot shipSlot : timeTable.getShipSlotList()) {
            if(shipSlot.getStartTime() == -1) {
                continue;
            }

            ++doneShipsNumber;
            queueTime += shipSlot.getStartTime() - shipSlot.getArrivalTime();
            optimalCost += shipSlot.getPenalty();

            int dispatchTimeOffsetTemp = shipSlot.getDispatchTimeOffsetNominal();
            if(dispatchTimeOffsetTemp > maxDispatchTimeOffset) {
                maxDispatchTimeOffset = dispatchTimeOffsetTemp;
            }
            dispatchTimeOffset += dispatchTimeOffsetTemp;
        }

        System.out.println("Общая стоимость: " + optimalCost);
        System.out.println("Сумма штрафа: " + (optimalCost - totalCraneAmount * 30000));
        System.out.println("Оптимальный список кранов: Балкерных кранов: " + timeTable.getOptimalBulkCraneAmount()
                        + " Кранов для жидких грузов: " + timeTable.getOptimalLiquidCraneAmount()
                + " Контейнерных кранов: " + timeTable.getOptimalContainerCraneAmount());
        System.out.println("Судов разгружено: " + doneShipsNumber);
        System.out.println("Среднее время ожидания: " + formatTime(queueTime / doneShipsNumber));
        System.out.println("Максимальное отклонение времени отправки: " + formatTime(maxDispatchTimeOffset));
        System.out.println("Среднее смещение времени отправки: " + formatTime(dispatchTimeOffset / doneShipsNumber));
    }

}
