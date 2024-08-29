package ru.smarthome.device.dto;

import lombok.Data;
import ru.smarthome.device.entity.Device;

import java.util.UUID;

@Data
public class DeviceDTO {
    private UUID id;
    private UUID userId;
    private String name;
    private String type;
    private String serialNumber;
    private String modelName;
    private String manufacturer;
    private DeviceConfigurationDTO configuration;

    // Маппинг из сущности Device в DTO
    public static DeviceDTO fromEntity(Device device) {
        DeviceDTO dto = new DeviceDTO();
        dto.setId(device.getId());
        dto.setUserId(device.getUserId());
        dto.setName(device.getName());
        dto.setType(device.getType());
        dto.setSerialNumber(device.getSerialNumber());
        dto.setModelName(device.getModelName());
        dto.setManufacturer(device.getManufacturer());

        // Маппинг конфигурации, если она существует
        if (device.getConfiguration() != null) {
            dto.setConfiguration(DeviceConfigurationDTO.fromEntity(device.getConfiguration()));
        }

        return dto;
    }

    // Маппинг из DTO в сущность Device
    public Device toEntity() {
        Device device = new Device();
        device.setUserId(this.userId);
        device.setName(this.name);
        device.setType(this.type);
        device.setSerialNumber(this.serialNumber);
        device.setModelName(this.modelName);
        device.setManufacturer(this.manufacturer);

        // Маппинг конфигурации, если она существует
        if (this.configuration != null) {
            device.setConfiguration(this.configuration.toEntity());
        }

        return device;
    }
}
