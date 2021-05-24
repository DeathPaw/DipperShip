package ru.ramazanov.DipperShip.sheduleGenerator;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.ramazanov.DipperShip.models.TimeTable;

@RestController
public class FirstServiceController {

    private final FirstService firstService;

    @Autowired
    public FirstServiceController(FirstService firstService) {
        this.firstService = firstService;
    }

    @GetMapping("/generate")
    public ResponseEntity<TimeTable> getTimetable() {
        return ResponseEntity.ok(firstService.generateTimeTable());
    }

}
