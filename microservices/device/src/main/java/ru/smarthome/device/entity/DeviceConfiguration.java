package ru.smarthome.device.entity;

import com.fasterxml.jackson.databind.JsonNode;
import io.hypersistence.utils.hibernate.type.json.JsonNodeBinaryType;
import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.Type;

import java.util.Map;
import java.util.UUID;

@Data
@Entity
@Table(name = "device_configuration")
public class DeviceConfiguration {
    @Id
    @GeneratedValue
    private UUID id;
    private String type;

    @Type(JsonNodeBinaryType.class)
    @Column(name = "parameter_value", columnDefinition = "jsonb")
    private JsonNode parameters;

}
