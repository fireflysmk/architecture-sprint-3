package ru.yandex.practicum.smarthome.heating.dto;

import lombok.Data;

@Data
public class HeatingDto {
    private Long id;
    private boolean isOn;
    private double targetTemperature;
    private double currentTemperature;
}