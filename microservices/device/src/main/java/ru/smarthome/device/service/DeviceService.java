package ru.smarthome.device.service;

import org.springframework.stereotype.Service;
import ru.smarthome.device.dto.DeviceConfigurationDTO;
import ru.smarthome.device.dto.DeviceDTO;
import ru.smarthome.device.dto.TelemetryDTO;
import ru.smarthome.device.remote.TelemetryClient;
import ru.smarthome.device.repository.DeviceRepository;

import java.util.List;
import java.util.UUID;

@Service
public class DeviceService {

    private final DeviceRepository deviceRepository;
    private final TelemetryClient telemetryClient;

    public DeviceService(DeviceRepository deviceRepository, TelemetryClient telemetryClient) {
        this.deviceRepository = deviceRepository;
        this.telemetryClient = telemetryClient;
    }

    public void addDevice(DeviceDTO deviceDTO) {
        // Логика добавления устройства в базу данных
        deviceRepository.save(deviceDTO.toEntity());
    }

    public DeviceDTO getDeviceInfo(UUID deviceId) {
        // Логика получения информации об устройстве
        return deviceRepository.findById(deviceId)
                .map(DeviceDTO::fromEntity)
                .orElseThrow(() -> new RuntimeException("Устройство не найдено"));
    }

    public void deleteDevice(UUID deviceId) {
        // Логика удаления устройства из базы данных
        if (!deviceRepository.existsById(deviceId)) {
            throw new RuntimeException("Устройство не найдено");
        }
        deviceRepository.deleteById(deviceId);
    }

    public void updateDeviceConfiguration(UUID deviceId, DeviceConfigurationDTO configurationDTO) {
        // Логика обновления конфигурации устройства
        // Получение устройства и обновление конфигурации
        DeviceDTO device = getDeviceInfo(deviceId);
        device.setConfiguration(configurationDTO);
        deviceRepository.save(device.toEntity());
    }

    public DeviceConfigurationDTO getDeviceConfiguration(UUID deviceId) {
        // Логика получения конфигурации устройства
        return getDeviceInfo(deviceId).getConfiguration();
    }

    public TelemetryDTO getLatestTelemetry(UUID deviceId) {
        // Логика получения последних данных телеметрии через Feign Client
        try {
            return telemetryClient.getLatestTelemetry(deviceId);
        } catch (Exception e) {
            throw new RuntimeException("Данные телеметрии не найдены", e);
        }
    }

    public List<TelemetryDTO> getTelemetryHistory(UUID deviceId, String startTime, String endTime) {
        // Логика получения исторических данных телеметрии через Feign Client
        try {
            return telemetryClient.getTelemetryHistory(deviceId, startTime, endTime);
        } catch (Exception e) {
            throw new RuntimeException("Не удалось получить исторические данные телеметрии", e);
        }
    }

    public List<DeviceDTO> getUserDevices(UUID userId) {
        // Логика получения всех устройств пользователя
        return deviceRepository.findByUserId(userId).stream()
                .map(DeviceDTO::fromEntity)
                .toList();
    }

    public void sendTelemetryData(UUID deviceId, TelemetryDTO telemetryData) {
        telemetryClient.recordTelemetryData(deviceId, telemetryData);
    }
}