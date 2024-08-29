package ru.smarthome.telemetry.dto;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.Data;
import ru.smarthome.telemetry.entity.TelemetryData;

import java.util.Map;
import java.util.UUID;

@Data
public class TelemetryDTO {
    private String timestamp;
    private UUID deviceId;
    private JsonNode data;

    public static TelemetryData toEntity(TelemetryDTO dto) {
        if (dto == null) {
            return null;
        }

        TelemetryData entity = new TelemetryData();
        entity.setTimestamp(dto.getTimestamp());
        entity.setDeviceId(dto.getDeviceId());
        entity.setData(dto.getData());

        return entity;
    }

    public static TelemetryDTO fromEntity(TelemetryData telemetryData) {
        TelemetryDTO telemetryDTO = new TelemetryDTO();
        telemetryDTO.setTimestamp(telemetryData.getTimestamp());
        telemetryDTO.setDeviceId(telemetryData.getDeviceId()); // Assuming UUID as String in MongoDB
        telemetryDTO.setData(telemetryData.getData());
        return telemetryDTO;
    }
}
