package ru.yandex.practicum.smarthome.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.smarthome.dto.HeatingSystemDto;
import ru.yandex.practicum.smarthome.service.HeatingSystemCommandService;
import ru.yandex.practicum.smarthome.service.HeatingSystemQueryService;

@RestController
@RequestMapping("/api/heating")
@RequiredArgsConstructor
public class HeatingSystemController {

    private final HeatingSystemCommandService heatingSystemCommandService;
    private final HeatingSystemQueryService heatingSystemQueryService;

    private static final Logger logger = LoggerFactory.getLogger(HeatingSystemController.class);

    /**
     * Get heating system by id
     *
     * @param id - id of heating system
     * @return heating system
     */
    @Operation(summary = "Get heating system by id", description = "Get heating system by id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful operation",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = HeatingSystemDto.class))),
            @ApiResponse(responseCode = "404", description = "Heating system not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping("/{id}")
    public ResponseEntity<HeatingSystemDto> getHeatingSystem(@PathVariable("id") Long id) {
        logger.info("Fetching heating system with id {}", id);
        return ResponseEntity.ok(heatingSystemQueryService.getHeatingSystem(id));
    }

    /**
     * Update heating system by id and heating system data.
     *
     * @param id - id of heating system to update
     * @param heatingSystemDto - heating system data to update
     * @return heating system dto
     */
    @Operation(summary = "Update heating system by id and heating system data", description = "Update heating system by id and heating system data")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful operation",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = HeatingSystemDto.class))),
            @ApiResponse(responseCode = "404", description = "Heating system not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PutMapping("/{id}")
    public ResponseEntity<HeatingSystemDto> updateHeatingSystem(@PathVariable("id") Long id,
                                                                @RequestBody HeatingSystemDto heatingSystemDto) {
        logger.info("Updating heating system with id {}", id);
        return ResponseEntity.ok(heatingSystemCommandService.updateHeatingSystem(id, heatingSystemDto));
    }

    /**
     * Turn on heating system by id
     * @param id - id of heating system to turn on
     * @return heating system dto
     */
    @Operation(summary = "Turn on heating system by id", description = "Turn on heating system by id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful operation",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = HeatingSystemDto.class))),
            @ApiResponse(responseCode = "404", description = "Heating system not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PostMapping("/{id}/turn-on")
    public ResponseEntity<Void> turnOn(@PathVariable("id") Long id) {
        logger.info("Turning on heating system with id {}", id);
        heatingSystemCommandService.turnOn(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * Turn off heating system by id
     * @param id - id of heating system to turn off
     * @return heating system dto
     */
    @Operation(summary = "Turn off heating system by id", description = "Turn off heating system by id")
    @ApiResponses(value = { @ApiResponse(responseCode = "200", description = "Successful operation",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = HeatingSystemDto.class))),
            @ApiResponse(responseCode = "404", description = "Heating system not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PostMapping("/{id}/turn-off")
    public ResponseEntity<Void> turnOff(@PathVariable("id") Long id) {
        logger.info("Turning off heating system with id {}", id);
        heatingSystemCommandService.turnOff(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * Set target temperature for heating system by id
     * @param id - id of heating system to set target temperature for
     * @param temperature - target temperature to set
     * @return target temperature for heating system by id
     */
    @Operation(summary = "Set target temperature for heating system by id", description = "Set target temperature for heating system by id")
    @ApiResponses(value = { @ApiResponse(responseCode = "200", description = "Successful operation",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = HeatingSystemDto.class))),
            @ApiResponse(responseCode = "404", description = "Heating system not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PostMapping("/{id}/set-temperature")
    public ResponseEntity<Void> setTargetTemperature(@PathVariable("id") Long id, @RequestParam double temperature) {
        logger.info("Setting target temperature to {} for heating system with id {}", temperature, id);
        heatingSystemCommandService.setTargetTemperature(id, temperature);
        // TODO: Implement automatic temperature maintenance logic in the service layer
        return ResponseEntity.noContent().build();
    }

    /**
     * Get current temperature for heating system by id
     * @param id - id of heating system to get current temperature for
     * @return current temperature for heating system by id
     */
    @Operation(summary = "Get current temperature for heating system by id", description = "Get current temperature for heating system by id")
    @ApiResponses(value = { @ApiResponse(responseCode = "200", description = "Successful operation",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = HeatingSystemDto.class))),
            @ApiResponse(responseCode = "404", description = "Heating system not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping("/{id}/current-temperature")
    public ResponseEntity<Double> getCurrentTemperature(@PathVariable("id") Long id) {
        logger.info("Fetching current temperature for heating system with id {}", id);
        return ResponseEntity.ok(heatingSystemQueryService.getCurrentTemperature(id));
    }

}
