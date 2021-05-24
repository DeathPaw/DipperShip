package ru.ramazanov.DipperShip.simulator;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import ru.ramazanov.DipperShip.models.TimeTable;

@RestController
public class ThirdServiceController {


    private final ThirdService thirdService;
    private static int counter = 0;

    @Autowired
    public ThirdServiceController(ThirdService thirdService) {
        this.thirdService = thirdService;
    }

    @GetMapping("/solveTimeTable")
    public ResponseEntity<TimeTable> solveTimeTable() {
        RestTemplate restTemplate = new RestTemplate();//

        String resourceUrl = "http://localhost:8080/newTimeTable";//
        ResponseEntity<TimeTable> response = restTemplate.getForEntity(resourceUrl, TimeTable.class);

        TimeTable timeTable = response.getBody();
        thirdService.solveTimeTable(timeTable);

        resourceUrl = "http://localhost:8080/timeTable/timeTable" + counter;
        ++counter;
        restTemplate.postForEntity(resourceUrl, timeTable, TimeTable.class);

        return ResponseEntity.ok(timeTable);
    }

}
