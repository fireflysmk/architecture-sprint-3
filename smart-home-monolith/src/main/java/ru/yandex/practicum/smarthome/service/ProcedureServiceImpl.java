package ru.yandex.practicum.smarthome.service;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.smarthome.broker.CommandType;
import ru.yandex.practicum.smarthome.broker.DeviceMessage;
import ru.yandex.practicum.smarthome.broker.KafkaProducer;
import ru.yandex.practicum.smarthome.controller.HeatingSystemController;

@Service
@RequiredArgsConstructor
public class ProcedureServiceImpl implements ProcedureService {

    private final HeatingSystemQueryService heatingSystemQueryService;
    private final HeatingSystemCommandService heatingSystemCommandService;
    private final KafkaProducer producer;
    private static final Logger logger = LoggerFactory.getLogger(ProcedureServiceImpl.class);

    @Override
    public void execute(String correlationId, DeviceMessage message) {
        var deviceId = message.getDeviceId();
        var result = "";

        switch (message.getCommandType()){
            case On:
                logger.info("Turning on heating system with id {}", deviceId);
                heatingSystemCommandService.turnOn(deviceId);
                result = heatingSystemQueryService.getHeatingSystem(deviceId).isOn()
                        ? "The heating system has been turned on"
                        : "FAULT";
                break;
            case Off:
                logger.info("Turning off heating system with id {}", deviceId);
                heatingSystemCommandService.turnOff(deviceId);
                result = heatingSystemQueryService.getHeatingSystem(deviceId).isOn()
                        ? "FAULT"
                        : "The heating system has been turned off";
                break;
            case SetTemperature:
                var temperature = Double.parseDouble(message.getPayload());
                logger.info("Setting target temperature to {} for heating system with id {}", temperature, deviceId);
                heatingSystemCommandService.setTargetTemperature(deviceId, temperature);
                result = "New target temperature is " + heatingSystemQueryService.getHeatingSystem(deviceId).getTargetTemperature();
                break;
            default:
                result = "Unknown command type: " + message.getCommandType();
                throw new IllegalStateException("Unexpected value: " + message.getCommandType());
        }

        producer.sendCommandMessage(correlationId, "The procedure has been executed.\n" + result);
        producer.sendTelemetryMessage(deviceId + ";The telemetry registered for the device. " + result);
    }
}
