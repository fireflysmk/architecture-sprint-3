package ru.yandex.practicum.smarthome.entity;

import lombok.Data;

import java.util.Date;

@Data
public class Telemetry {
    public Integer id;
    public Integer device_id;
    public Integer temperature;
    public Date date;
}