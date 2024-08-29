package ru.smarthome.device.remote;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;
import ru.smarthome.device.dto.TelemetryDTO;

import java.util.List;
import java.util.UUID;

@FeignClient(name = "telemetry-service", url = "${telemetry.service.url}")
public interface TelemetryClient {

    @GetMapping("/api/v1/devices/{deviceId}/latest")
    TelemetryDTO getLatestTelemetry(@PathVariable("deviceId") UUID deviceId);

    @GetMapping("/api/v1/devices/{deviceId}/history")
    List<TelemetryDTO> getTelemetryHistory(
            @PathVariable("deviceId") UUID deviceId,
            @RequestParam("start_time") String startTime,
            @RequestParam("end_time") String endTime);

    @PostMapping("/api/v1/devices/{deviceId}/telemetry")
    void recordTelemetryData(
            @PathVariable("deviceId") UUID deviceId,
            @RequestBody TelemetryDTO telemetryData);
}