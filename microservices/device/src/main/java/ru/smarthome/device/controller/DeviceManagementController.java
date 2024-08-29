package ru.smarthome.device.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.smarthome.device.dto.DeviceConfigurationDTO;
import ru.smarthome.device.dto.DeviceDTO;
import ru.smarthome.device.dto.TelemetryDTO;
import ru.smarthome.device.service.DeviceService;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1")
public class DeviceManagementController {

    private final DeviceService deviceService;

    public DeviceManagementController(DeviceService deviceService) {
        this.deviceService = deviceService;
    }

    @PostMapping("/devices")
    public ResponseEntity<Void> addDevice(@RequestBody DeviceDTO deviceDTO) {
        deviceService.addDevice(deviceDTO);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @GetMapping("/devices/{device_id}")
    public ResponseEntity<DeviceDTO> getDeviceInfo(@PathVariable("device_id") UUID deviceId) {
        DeviceDTO device = deviceService.getDeviceInfo(deviceId);
        return ResponseEntity.ok(device);
    }

    @DeleteMapping("/devices/{device_id}")
    public ResponseEntity<Void> deleteDevice(@PathVariable("device_id") UUID deviceId) {
        deviceService.deleteDevice(deviceId);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/devices/{device_id}/configuration")
    public ResponseEntity<Void> updateDeviceConfiguration(
            @PathVariable("device_id") UUID deviceId,
            @RequestBody DeviceConfigurationDTO configurationDTO) {
        deviceService.updateDeviceConfiguration(deviceId, configurationDTO);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/devices/{device_id}/configuration")
    public ResponseEntity<DeviceConfigurationDTO> getDeviceConfiguration(@PathVariable("device_id") UUID deviceId) {
        DeviceConfigurationDTO configuration = deviceService.getDeviceConfiguration(deviceId);
        return ResponseEntity.ok(configuration);
    }

    @GetMapping("/devices/{device_id}/telemetry/latest")
    public ResponseEntity<TelemetryDTO> getLatestTelemetry(@PathVariable("device_id") UUID deviceId) {
        TelemetryDTO telemetry = deviceService.getLatestTelemetry(deviceId);
        return ResponseEntity.ok(telemetry);
    }

    @GetMapping("/devices/{device_id}/telemetry/history")
    public ResponseEntity<List<TelemetryDTO>> getTelemetryHistory(
            @PathVariable("device_id") UUID deviceId,
            @RequestParam("start_time") String startTime,
            @RequestParam("end_time") String endTime) {
        List<TelemetryDTO> history = deviceService.getTelemetryHistory(deviceId, startTime, endTime);
        return ResponseEntity.ok(history);
    }

    @GetMapping("/users/{user_id}/devices")
    public ResponseEntity<List<DeviceDTO>> getUserDevices(@PathVariable("user_id") UUID userId) {
        List<DeviceDTO> devices = deviceService.getUserDevices(userId);
        return ResponseEntity.ok(devices);
    }
}
