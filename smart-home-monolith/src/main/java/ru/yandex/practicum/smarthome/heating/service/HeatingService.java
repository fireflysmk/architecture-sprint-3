package ru.yandex.practicum.smarthome.heating.service;

import ru.yandex.practicum.smarthome.heating.dto.HeatingDto;

public interface HeatingService {
    HeatingDto getHeatingSystem(Long id);
    HeatingDto updateHeatingSystem(Long id, HeatingDto heatingDto);
    void turnOn(Long id);
    void turnOff(Long id);
    void setTargetTemperature(Long id, double temperature);
    Double getCurrentTemperature(Long id);
}