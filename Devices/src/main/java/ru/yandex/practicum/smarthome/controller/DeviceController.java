package ru.yandex.practicum.smarthome.controller;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.smarthome.entity.Device;
import ru.yandex.practicum.smarthome.service.DeviceService;

@RestController
@RequestMapping("/devices")
@RequiredArgsConstructor
public class DeviceController {

    private final DeviceService deviceService;

    private static final Logger logger = LoggerFactory.getLogger(DeviceController.class);

    @GetMapping("/devices/{device_id}/telemetry/latest")
    public ResponseEntity<Device> get_telemetry(Device device) {
        logger.info("get_telemetry id {}", device.id);
        return ResponseEntity.ok(deviceService.get_telemetry(device));
    }

    @GetMapping("/devices/{device_id}")
    public Device get_deviceinfo(Device device) {

        return device;
    }

    @GetMapping("/devices/{device_id}/status")
    public Device get_status(Device device) {

        return device;
    }

    @PutMapping("/devices/{device_id}/status")
    public Device set_status(@RequestBody Device device) {

        return device;
    }

    @PostMapping("/devices/{device_id}/commands")
    public Device set_telemetry(@RequestBody Device device) {

        return device;
    }

    @GetMapping("/devices/{device_id}/telemetry")
    public Device get_historytelemetry(@RequestBody Device device) {

        return device;
    }
}
