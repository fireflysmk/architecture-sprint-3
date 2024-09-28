package ru.yandex.practicum.smarthome.entity;

import jakarta.persistence.*;
import lombok.Data;

@Data
public class Device {
    public Integer id;
    public Integer type_id;
    public Integer house_id;
    public Integer serial_number;
    public boolean status;
}