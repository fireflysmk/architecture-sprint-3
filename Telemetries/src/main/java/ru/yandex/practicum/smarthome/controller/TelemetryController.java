package ru.yandex.practicum.smarthome.controller;

import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.smarthome.entity.Telemetry;
import ru.yandex.practicum.smarthome.service.TelemetryService;

import java.util.Arrays;
import java.util.Properties;

@RestController
@RequestMapping("/telemetry")
@RequiredArgsConstructor
public class TelemetryController {

    private final TelemetryService telemetryService;

    private static final Logger logger = LoggerFactory.getLogger(TelemetryController.class);

    @GetMapping("/telemetry/{device_id}/telemetry/latest")
    public ResponseEntity<Telemetry> get_telemetry(Telemetry telemetry) {
        logger.info("get_telemetry id {}", telemetry.id);
        return ResponseEntity.ok(telemetryService.get_telemetry(telemetry));
    }

    @PostMapping("/telemetry/{device_id}/commands")
    public Telemetry set_telemetry(@RequestBody Telemetry telemetry) {
        return telemetry;
    }

    @GetMapping("/telemetry/{device_id}/telemetry")
    public Telemetry get_historytelemetry(Telemetry telemetry) {
        return telemetry;
    }

    public Telemetry get_temperature(Telemetry telemetry) {
        return telemetry;
    }

    public Telemetry set_temperature(Telemetry telemetry) {
        return telemetry;
    }
}
