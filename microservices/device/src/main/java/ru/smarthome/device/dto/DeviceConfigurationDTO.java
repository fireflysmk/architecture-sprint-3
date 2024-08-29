package ru.smarthome.device.dto;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.Data;
import ru.smarthome.device.entity.DeviceConfiguration;

@Data
public class DeviceConfigurationDTO {
    private String type;
    private JsonNode parameters;

    // Маппинг из сущности DeviceConfiguration в DTO
    public static DeviceConfigurationDTO fromEntity(DeviceConfiguration configuration) {
        DeviceConfigurationDTO dto = new DeviceConfigurationDTO();
        dto.setType(configuration.getType());
        dto.setParameters(configuration.getParameters()); // Копирование параметров

        return dto;
    }

    // Маппинг из DTO в сущность DeviceConfiguration
    public DeviceConfiguration toEntity() {
        DeviceConfiguration configuration = new DeviceConfiguration();
        configuration.setType(this.type);
        configuration.setParameters(this.parameters); // Копирование параметров

        return configuration;
    }
}
