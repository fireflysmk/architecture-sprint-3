package ru.smarthome.device.kafka.input;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import ru.smarthome.device.dto.TelemetryDTO;
import ru.smarthome.device.service.DeviceService;

import java.util.UUID;

@Component
public class TelemetryKafkaListener {

    private final DeviceService deviceService;

    public TelemetryKafkaListener(DeviceService deviceService) {
        this.deviceService = deviceService;
    }

    @KafkaListener(topics = "${kafka.topic.telemetry}", groupId = "telemetry-group")
    public void listen(TelemetryDTO telemetryData) {
        UUID deviceId = telemetryData.getDeviceId();
        deviceService.sendTelemetryData(deviceId, telemetryData);
    }
}