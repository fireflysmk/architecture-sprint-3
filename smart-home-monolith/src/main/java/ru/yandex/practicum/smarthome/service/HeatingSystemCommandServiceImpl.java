package ru.yandex.practicum.smarthome.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.smarthome.dto.HeatingSystemDto;
import ru.yandex.practicum.smarthome.entity.HeatingSystem;
import ru.yandex.practicum.smarthome.repository.HeatingSystemRepository;

@Service
@RequiredArgsConstructor
public class HeatingSystemCommandServiceImpl implements HeatingSystemCommandService {

    private final HeatingSystemRepository heatingSystemRepository;
    private final HeatingSystemService heatingSystemService;

    @Override
    public HeatingSystemDto updateHeatingSystem(Long id, HeatingSystemDto heatingSystemDto) {
        HeatingSystem existingHeatingSystem = heatingSystemRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("HeatingSystem not found"));
        existingHeatingSystem.setOn(heatingSystemDto.isOn());
        existingHeatingSystem.setTargetTemperature(heatingSystemDto.getTargetTemperature());
        HeatingSystem updatedHeatingSystem = heatingSystemRepository.save(existingHeatingSystem);
        return heatingSystemService.convertToDto(updatedHeatingSystem);
    }

    @Override
    public void turnOn(Long id) {
        HeatingSystem heatingSystem = heatingSystemRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("HeatingSystem not found"));
        heatingSystem.setOn(true);
        heatingSystemRepository.save(heatingSystem);
    }

    @Override
    public void turnOff(Long id) {
        HeatingSystem heatingSystem = heatingSystemRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("HeatingSystem not found"));
        heatingSystem.setOn(false);
        heatingSystemRepository.save(heatingSystem);
    }

    @Override
    public void setTargetTemperature(Long id, double temperature) {
        HeatingSystem heatingSystem = heatingSystemRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("HeatingSystem not found"));
        heatingSystem.setTargetTemperature(temperature);
        heatingSystemRepository.save(heatingSystem);
    }
}
