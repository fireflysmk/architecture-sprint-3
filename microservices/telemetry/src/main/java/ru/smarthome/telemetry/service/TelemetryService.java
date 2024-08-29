package ru.smarthome.telemetry.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.smarthome.telemetry.dto.TelemetryDTO;
import ru.smarthome.telemetry.entity.TelemetryData;
import ru.smarthome.telemetry.repository.TelemetryDataRepository;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import static ru.smarthome.telemetry.dto.TelemetryDTO.fromEntity;

@Service
public class TelemetryService {

    private final TelemetryDataRepository telemetryDataRepository;

    @Autowired
    public TelemetryService(TelemetryDataRepository telemetryDataRepository) {
        this.telemetryDataRepository = telemetryDataRepository;
    }

    public void saveTelemetry(TelemetryDTO telemetryDTO) {
        TelemetryData telemetryData = new TelemetryData(
                null,
                telemetryDTO.getTimestamp(),
                telemetryDTO.getDeviceId(),
                telemetryDTO.getData()
        );
        telemetryDataRepository.save(telemetryData);
    }

    public TelemetryDTO getLatestTelemetry(UUID deviceId) {
        TelemetryData telemetryData = telemetryDataRepository.findTopByDeviceIdOrderByTimestampDesc(deviceId);
        if (telemetryData != null) {
            return fromEntity(telemetryData);
        } else {
            throw new RuntimeException("Latest telemetry data not found for deviceId: " + deviceId);
        }
    }

    public List<TelemetryDTO> getTelemetryHistory(UUID deviceId, String startTime, String endTime) {
        List<TelemetryData> telemetryDataList = telemetryDataRepository.findByDeviceIdAndTimestampBetween(deviceId, startTime, endTime);
        return telemetryDataList.stream().map(TelemetryDTO::fromEntity).collect(Collectors.toList());
    }
}
