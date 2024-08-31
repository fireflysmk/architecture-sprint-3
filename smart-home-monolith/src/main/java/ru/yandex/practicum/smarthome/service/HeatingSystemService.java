package ru.yandex.practicum.smarthome.service;

import ru.yandex.practicum.smarthome.dto.HeatingSystemDto;
import ru.yandex.practicum.smarthome.entity.HeatingSystem;

public interface HeatingSystemService {
    HeatingSystemDto convertToDto(HeatingSystem heatingSystem);
}
