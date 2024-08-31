package ru.yandex.practicum.smarthome.service;

import ru.yandex.practicum.smarthome.broker.DeviceMessage;
import ru.yandex.practicum.smarthome.dto.HeatingSystemDto;

public interface ProcedureService {

    void execute(String id, DeviceMessage message);
}
