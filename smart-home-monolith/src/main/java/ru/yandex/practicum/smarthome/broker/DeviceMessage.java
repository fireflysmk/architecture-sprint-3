package ru.yandex.practicum.smarthome.broker;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class DeviceMessage {

    @JsonProperty("DeviceId")
    private long deviceId;

    @JsonProperty("CommandType")
    private CommandType commandType;

    @JsonProperty("Payload")
    private String payload;
}
