package ru.yandex.practicum.smarthome.service;

import ru.yandex.practicum.smarthome.entity.Device;

public interface DeviceService {
    Device get_telemetry(Device device);
}