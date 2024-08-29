package ru.smarthome.telemetry.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import ru.smarthome.telemetry.entity.TelemetryData;

import java.util.List;
import java.util.UUID;

@Repository
public interface TelemetryDataRepository extends MongoRepository<TelemetryData, String> {
    List<TelemetryData> findByDeviceIdAndTimestampBetween(UUID deviceId, String startTime, String endTime);

    TelemetryData findTopByDeviceIdOrderByTimestampDesc(UUID deviceId);
}
