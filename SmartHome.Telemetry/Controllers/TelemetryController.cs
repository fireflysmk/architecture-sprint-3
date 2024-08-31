using Microsoft.AspNetCore.Mvc;
using SmartHome.Telemetry.Dto;
using SmartHome.Telemetry.Models;
using SmartHome.Telemetry.Services;

namespace SmartHome.Telemetry.Controllers
{
    /// <summary>
    /// API for managing devices in the Smart Home application.
    /// </summary>
    [Route("api/[controller]")]
    [ApiController]
    public class TelemetryController(ITelemetryService telemetryService) : ControllerBase
    {
        /// <summary>
        /// Gets the latest telemetry data for a device.
        /// </summary>
        /// <param name="id">The ID of the device.</param>
        /// <returns>The latest telemetry data for the device.</returns>
        /// <response code="200">Returns the requested device</response>
        /// <response code="404">If the device was not found</response>
        /// <response code="500">If there was an internal server error</response>
        [HttpGet("{id}/latest")]
        [ProducesResponseType(StatusCodes.Status200OK)]
        [ProducesResponseType(StatusCodes.Status404NotFound)]
        [ProducesResponseType(StatusCodes.Status500InternalServerError)]
        public async Task<IActionResult> GetLatestTelemetryData(long id)
        {
            var telemetry = await telemetryService.GetLatestTelemetryAsync(id);
            return Ok(telemetry);
        }

        /// <summary>
        /// Gets all telemetry data for a device.
        /// </summary>
        /// <param name="id">The ID of the device.</param>
        /// <param name="start">The start date of the telemetry data.</param>
        /// <param name="end">The end date of the telemetry data.</param>
        /// <returns>All telemetry data for the device.</returns>
        /// <response code="200">Returns the requested device</response>
        /// <response code="404">If the device was not found</response>
        /// <response code="500">If there was an internal server error</response>
        [HttpGet("{id}")]
        [ProducesResponseType(StatusCodes.Status200OK)]
        [ProducesResponseType(StatusCodes.Status404NotFound)]
        [ProducesResponseType(StatusCodes.Status500InternalServerError)]
        public async Task<IActionResult> GetTelemetryHistory(
            long id,
            [FromQuery] DateTime? start,
            [FromQuery] DateTime? end)
        {
            var telemetryHistory =
                await telemetryService.GetTelemetryHistoryAsync(id, start, end);

            return telemetryHistory.Count == 0
                ? NotFound($"No telemetry history found for device {id} in the given time range.")
                : Ok(telemetryHistory);
        }

        /// <summary>
        /// Adds a new telemetry data for a device.
        /// </summary>
        /// <param name="telemetryDataDto">The new telemetry data.</param>
        /// <returns>The created device.</returns>
        /// <response code="200">Returns the requested device</response>
        /// <response code="500">If there was an internal server error</response>
        [HttpPost]
        [ProducesResponseType(StatusCodes.Status200OK)]
        [ProducesResponseType(StatusCodes.Status500InternalServerError)]
        public async Task<IActionResult> AddTelemetryData([FromBody] AddTelemetryDataDto telemetryDataDto)
        {
            if (!ModelState.IsValid)
                return BadRequest(ModelState);

            var telemetryData = new TelemetryData
            {
                CreatedAt = DateTime.Now,
                DeviceId = telemetryDataDto.DeviceId,
                Data = telemetryDataDto.Data
            };

            await telemetryService.AddTelemetryDataAsync(telemetryData);

            return CreatedAtAction(nameof(GetLatestTelemetryData), new { deviceId = telemetryData.DeviceId }, telemetryData);
        }
    }
}
