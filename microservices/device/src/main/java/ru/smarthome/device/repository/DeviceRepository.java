package ru.smarthome.device.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.smarthome.device.entity.Device;

import java.util.List;
import java.util.UUID;

public interface DeviceRepository extends JpaRepository<Device, UUID> {
    List<Device> findByUserId(UUID userId);
}
