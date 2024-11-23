package ru.yandex.practicum.smarthome.controller.heating.controller.heating;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import ru.yandex.practicum.smarthome.heating.dto.HeatingDto;
import ru.yandex.practicum.smarthome.heating.entity.Heating;
import ru.yandex.practicum.smarthome.heating.repository.HeatingRepository;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@Testcontainers
class HeatingControllerTest {

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:13")
            .withDatabaseName("testdb")
            .withUsername("test")
            .withPassword("test");

    @DynamicPropertySource
    static void registerPgProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
    }

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private HeatingRepository heatingRepository;

    @Test
    void testGetHeatingSystem() throws Exception {
        Heating heating = new Heating();
        heating.setOn(false);
        heating.setTargetTemperature(20.0);
        heating = heatingRepository.save(heating);

        mockMvc.perform(get("/api/heating/{id}", heating.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(heating.getId()))
                .andExpect(jsonPath("$.on").value(false))
                .andExpect(jsonPath("$.targetTemperature").value(20.0));
    }

    @Test
    void testUpdateHeatingSystem() throws Exception {
        Heating heating = new Heating();
        heating.setOn(false);
        heating.setTargetTemperature(20.0);
        heating = heatingRepository.save(heating);

        HeatingDto updateDto = new HeatingDto();
        updateDto.setOn(true);
        updateDto.setTargetTemperature(22.5);

        mockMvc.perform(put("/api/heating/{id}", heating.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(heating.getId()))
                .andExpect(jsonPath("$.on").value(true))
                .andExpect(jsonPath("$.targetTemperature").value(22.5));
    }

    @Test
    void testTurnOn() throws Exception {
        Heating heating = new Heating();
        heating.setOn(false);
        heating.setTargetTemperature(20.0);
        heating = heatingRepository.save(heating);

        mockMvc.perform(post("/api/heating/{id}/turn-on", heating.getId()))
                .andExpect(status().isNoContent());

        Heating updatedHeating = heatingRepository.findById(heating.getId())
                .orElseThrow(() -> new RuntimeException("HeatingSystem not found"));
        assertTrue(updatedHeating.isOn());
    }

    @Test
    void testTurnOff() throws Exception {
        Heating heating = new Heating();
        heating.setOn(true);
        heating.setTargetTemperature(20.0);
        heating = heatingRepository.save(heating);

        mockMvc.perform(post("/api/heating/{id}/turn-off", heating.getId()))
                .andExpect(status().isNoContent());

        Heating updatedHeating = heatingRepository.findById(heating.getId())
                .orElseThrow(() -> new RuntimeException("HeatingSystem not found"));
        assertFalse(updatedHeating.isOn());
    }

    @Test
    void testSetTargetTemperature() throws Exception {
        Heating heating = new Heating();
        heating.setOn(true);
        heating.setTargetTemperature(20.0);
        heating = heatingRepository.save(heating);

        mockMvc.perform(post("/api/heating/{id}/set-temperature", heating.getId())
                .param("temperature", "23.5"))
                .andExpect(status().isNoContent());

        Heating updatedHeating = heatingRepository.findById(heating.getId())
                .orElseThrow(() -> new RuntimeException("HeatingSystem not found"));
        assertEquals(23.5, updatedHeating.getTargetTemperature(), 0.01);
    }

    @Test
    void testGetCurrentTemperature() throws Exception {
        Heating heating = new Heating();
        heating.setOn(true);
        heating.setTargetTemperature(20.0);
        heating.setCurrentTemperature(19.5);
        heating = heatingRepository.save(heating);

        mockMvc.perform(get("/api/heating/{id}/current-temperature", heating.getId()))
                .andExpect(status().isOk())
                .andExpect(content().string("19.5"));
    }
}