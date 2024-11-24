package ru.yandex.practicum.smarthome.heating.controller;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.smarthome.heating.dto.HeatingDto;
import ru.yandex.practicum.smarthome.heating.service.HeatingService;

@RestController
@RequestMapping("/api/heating")
@RequiredArgsConstructor
public class HeatingController {

    private final HeatingService heatingService;

    private static final Logger logger = LoggerFactory.getLogger(HeatingController.class);

    @GetMapping("/{id}")
    public ResponseEntity<HeatingDto> getHeatingSystem(@PathVariable("id") Long id) {
        logger.info("Fetching heating system with id {}", id);
        return ResponseEntity.ok(heatingService.getHeatingSystem(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<HeatingDto> updateHeatingSystem(@PathVariable("id") Long id,
                                                          @RequestBody HeatingDto heatingDto) {
        logger.info("Updating heating system with id {}", id);
        return ResponseEntity.ok(heatingService.updateHeatingSystem(id, heatingDto));
    }

    @PostMapping("/{id}/turn-on")
    public ResponseEntity<Void> turnOn(@PathVariable("id") Long id) {
        logger.info("Turning on heating system with id {}", id);
        heatingService.turnOn(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/turn-off")
    public ResponseEntity<Void> turnOff(@PathVariable("id") Long id) {
        logger.info("Turning off heating system with id {}", id);
        heatingService.turnOff(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/set-temperature")
    public ResponseEntity<Void> setTargetTemperature(@PathVariable("id") Long id, @RequestParam double temperature) {
        logger.info("Setting target temperature to {} for heating system with id {}", temperature, id);
        heatingService.setTargetTemperature(id, temperature);
        // TODO: Implement automatic temperature maintenance logic in the service layer
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}/current-temperature")
    public ResponseEntity<Double> getCurrentTemperature(@PathVariable("id") Long id) {
        logger.info("Fetching current temperature for heating system with id {}", id);
        return ResponseEntity.ok(heatingService.getCurrentTemperature(id));
    }

}
