package ru.smarthome.telemetry.entity;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.UUID;

@Document(collection = "telemetry_data")
@CompoundIndex(name = "device_timestamp_idx", def = "{'deviceId': 1, 'timestamp': -1}")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TelemetryData {

    @Id
    private String id;

    private String timestamp;

    @Indexed
    private UUID deviceId;

    private JsonNode data;
}