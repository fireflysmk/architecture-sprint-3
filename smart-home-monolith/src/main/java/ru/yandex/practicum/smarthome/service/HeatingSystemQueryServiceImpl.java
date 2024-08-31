package ru.yandex.practicum.smarthome.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.smarthome.dto.HeatingSystemDto;
import ru.yandex.practicum.smarthome.entity.HeatingSystem;
import ru.yandex.practicum.smarthome.repository.HeatingSystemRepository;

@Service
@RequiredArgsConstructor
public class HeatingSystemQueryServiceImpl implements HeatingSystemQueryService{

    private final HeatingSystemRepository heatingSystemRepository;
    private final HeatingSystemService heatingSystemService;

    @Override
    public HeatingSystemDto getHeatingSystem(Long id) {
        HeatingSystem heatingSystem = heatingSystemRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("HeatingSystem not found"));
        return heatingSystemService.convertToDto(heatingSystem);
    }

    @Override
    public Double getCurrentTemperature(Long id) {
        HeatingSystem heatingSystem = heatingSystemRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("HeatingSystem not found"));
        return heatingSystem.getCurrentTemperature();
    }
}
