package ru.ramazanov.DipperShip.dataDispatcher;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import ru.ramazanov.DipperShip.sheduleGenerator.Ship;
import ru.ramazanov.DipperShip.sheduleGenerator.ShipSlot;
import ru.ramazanov.DipperShip.models.TimeTable;

import java.util.Scanner;

@RestController
public class SecondServiceController {

    private final SecondService secondService;

    @Autowired
    public SecondServiceController(SecondService secondService) {
        this.secondService = secondService;
    }

    @GetMapping("/newTimeTable")
    public ResponseEntity<TimeTable> getNewTimeTable() {
        RestTemplate restTemplate = new RestTemplate();
        String resourceUrl = "http://localhost:8080/generate";
        ResponseEntity<TimeTable> response = restTemplate.getForEntity(resourceUrl, TimeTable.class);

        Scanner scanner = new Scanner(System.in);
        System.out.println("Напишите \"y\" чтобы добавить корабли, или любую другую клавишу если добавлять не нужно");
        String temp = scanner.next();
        if(temp.equals("y")) {
            System.out.println("Сколько судов вы хотите добавить?");
            int shipAmount = scanner.nextInt();
            for(int i = 0; i < shipAmount; ++i) {
                Ship newShip = new Ship();
                ShipSlot newShipSlot = new ShipSlot(newShip);
                System.out.println("Время прибытия и отправки?");
                int arrivalTime = scanner.nextInt();
                int dispatchTime = scanner.nextInt();
                newShipSlot.setArrivalTime(arrivalTime);
                newShipSlot.setDispatchTime(dispatchTime);
                response.getBody().addShipSlot(newShipSlot);
            }
        }

        return response;
    }

    @GetMapping("/timeTable/{fileName}")
    public ResponseEntity<TimeTable> getTimeTableByFileName(@PathVariable String fileName) {
        TimeTable timeTable = secondService.getJsonFile(fileName);
        secondService.printTimeTable(timeTable);
        return ResponseEntity.ok(timeTable);
    }

    @PostMapping("/timeTable/{fileName}")
    public void saveTimeTable(@PathVariable String fileName, @RequestBody TimeTable timeTable) {
        secondService.saveJsonFile(fileName, timeTable);
        secondService.printTimeTable(timeTable);
    }

}
