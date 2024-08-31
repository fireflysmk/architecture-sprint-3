package ru.yandex.practicum.smarthome.service;

import ru.yandex.practicum.smarthome.dto.HeatingSystemDto;

public interface HeatingSystemQueryService {
    HeatingSystemDto getHeatingSystem(Long id);
    Double getCurrentTemperature(Long id);
}
