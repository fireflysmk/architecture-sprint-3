package ru.yandex.practicum.smarthome.heating.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.smarthome.heating.dto.HeatingDto;
import ru.yandex.practicum.smarthome.heating.entity.Heating;
import ru.yandex.practicum.smarthome.heating.repository.HeatingRepository;

@Service
@RequiredArgsConstructor
public class HeatingServiceImpl implements HeatingService {
    private final HeatingRepository heatingRepository;
    
    @Override
    public HeatingDto getHeatingSystem(Long id) {
        Heating heating = heatingRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("HeatingSystem not found"));
        return convertToDto(heating);
    }

    @Override
    public HeatingDto updateHeatingSystem(Long id, HeatingDto heatingDto) {
        Heating existingHeating = heatingRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("HeatingSystem not found"));
        existingHeating.setOn(heatingDto.isOn());
        existingHeating.setTargetTemperature(heatingDto.getTargetTemperature());
        Heating updatedHeating = heatingRepository.save(existingHeating);
        return convertToDto(updatedHeating);
    }

    @Override
    public void turnOn(Long id) {
        Heating heating = heatingRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("HeatingSystem not found"));
        heating.setOn(true);
        heatingRepository.save(heating);
    }

    @Override
    public void turnOff(Long id) {
        Heating heating = heatingRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("HeatingSystem not found"));
        heating.setOn(false);
        heatingRepository.save(heating);
    }

    @Override
    public void setTargetTemperature(Long id, double temperature) {
        Heating heating = heatingRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("HeatingSystem not found"));
        heating.setTargetTemperature(temperature);
        heatingRepository.save(heating);
    }

    @Override
    public Double getCurrentTemperature(Long id) {
        Heating heating = heatingRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("HeatingSystem not found"));
        return heating.getCurrentTemperature();
    }

    private HeatingDto convertToDto(Heating heating) {
        HeatingDto dto = new HeatingDto();
        dto.setId(heating.getId());
        dto.setOn(heating.isOn());
        dto.setTargetTemperature(heating.getTargetTemperature());
        return dto;
    }
}