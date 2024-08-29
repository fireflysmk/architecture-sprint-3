package ru.smarthome.telemetry.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.smarthome.telemetry.dto.TelemetryDTO;
import ru.smarthome.telemetry.service.TelemetryService;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1")
public class TelemetryController {

    private final TelemetryService telemetryService;

    @Autowired
    public TelemetryController(TelemetryService telemetryService) {
        this.telemetryService = telemetryService;
    }

    @PostMapping("/devices/{deviceId}/telemetry")
    public ResponseEntity<Void> saveTelemetry(
            @PathVariable UUID deviceId,
            @RequestBody TelemetryDTO telemetryDTO) {

        // Проверка корректности данных, если необходимо
        telemetryDTO.setDeviceId(deviceId);
        telemetryService.saveTelemetry(telemetryDTO);

        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @GetMapping("/devices/{deviceId}/latest")
    public ResponseEntity<TelemetryDTO> getLatestTelemetry(@PathVariable UUID deviceId) {
        TelemetryDTO telemetryDTO = telemetryService.getLatestTelemetry(deviceId);
        return ResponseEntity.ok(telemetryDTO);
    }

    @GetMapping("/devices/{deviceId}/history")
    public ResponseEntity<List<TelemetryDTO>> getTelemetryHistory(
            @PathVariable UUID deviceId,
            @RequestParam("start_time") String startTime,
            @RequestParam("end_time") String endTime) {
        List<TelemetryDTO> telemetryHistory = telemetryService.getTelemetryHistory(deviceId, startTime, endTime);
        return ResponseEntity.ok(telemetryHistory);
    }
}