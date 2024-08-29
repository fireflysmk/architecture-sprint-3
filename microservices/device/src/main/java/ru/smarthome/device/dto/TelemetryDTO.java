package ru.smarthome.device.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.Data;

import java.util.UUID;

@Data
public class TelemetryDTO {
    private String timestamp;
    private UUID deviceId;
    private JsonNode data;

    @JsonCreator
    public TelemetryDTO(
            @JsonProperty("timestamp") String timestamp,
            @JsonProperty("deviceId") UUID deviceId,
            @JsonProperty("data") JsonNode data
    ) {
        this.timestamp = timestamp;
        this.deviceId = deviceId;
        this.data = data;
    }
}

