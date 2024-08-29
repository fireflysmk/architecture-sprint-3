package ru.smarthome.device.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.util.UUID;

@Data
@Entity
@Table(name = "devices")
public class Device {
    @Id
    @GeneratedValue
    private UUID id;
    private UUID userId;
    private String name;
    private String type;
    private String serialNumber;
    private String modelName;
    private String manufacturer;

    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "configuration_id")
    private DeviceConfiguration configuration;
}
