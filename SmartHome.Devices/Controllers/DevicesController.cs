using Microsoft.AspNetCore.Mvc;
using Microsoft.EntityFrameworkCore;
using SmartHome.Devices.Data;
using SmartHome.Devices.Dto;
using SmartHome.Devices.Kafka;
using SmartHome.Devices.Models;

namespace SmartHome.Devices.Controllers;

/// <summary>
/// API for managing devices in the Smart Home application.
/// </summary>
[ApiController]
[Route("api/[controller]")]
public class DevicesController(DeviceContext context, KafkaProducer producer) : ControllerBase
{
    /// <summary>
    /// Retrieves a list of all devices.
    /// </summary>
    /// <returns>A list of devices.</returns>
    /// <response code="200">Returns the list of devices</response>
    /// <response code="500">If there was an internal server error</response>
    [HttpGet]
    [ProducesResponseType(StatusCodes.Status200OK)]
    [ProducesResponseType(StatusCodes.Status500InternalServerError)]
    public async Task<ActionResult<IEnumerable<Device>>> GetDevices()
    {
        return await context.Devices.ToListAsync();
    }

    /// <summary>
    /// Retrieves a specific device by unique ID.
    /// </summary>
    /// <param name="id">The ID of the device.</param>
    /// <returns>The requested device.</returns>
    /// <response code="200">Returns the requested device</response>
    /// <response code="404">If the device was not found</response>
    /// <response code="500">If there was an internal server error</response>
    [HttpGet("{id}")]
    [ProducesResponseType(StatusCodes.Status200OK)]
    [ProducesResponseType(StatusCodes.Status404NotFound)]
    [ProducesResponseType(StatusCodes.Status500InternalServerError)]
    public async Task<ActionResult<Device>> GetDevice(Guid id)
    {
        var device = await context.Devices.FindAsync(id);

        return device == null
            ? NotFound()
            : device;
    }

    /// <summary>
    /// Updates an existing device.
    /// </summary>
    /// <param name="id">The ID of the device.</param>
    /// <param name="deviceDto"></param>
    /// <returns>The updated device.</returns>
    /// <response code="200">Returns the updated device</response>
    /// <response code="400">If the device is invalid</response>
    /// <response code="404">If the device was not found</response>
    /// <response code="500">If there was an internal server error</response>
    [HttpPut("{id}")]
    [ProducesResponseType(StatusCodes.Status200OK)]
    [ProducesResponseType(StatusCodes.Status400BadRequest)]
    [ProducesResponseType(StatusCodes.Status404NotFound)]
    [ProducesResponseType(StatusCodes.Status500InternalServerError)]
    public async Task<IActionResult> PutDevice(int id, [FromBody] AddDeviceDto deviceDto)
    {
        var device = await context.Devices.FindAsync(id);
            
        if (device == null)
            return NotFound("Device not found");
            
        device.Status = deviceDto.Status;
        device.HouseId = deviceDto.HouseId;
        device.DeviceTypeId = deviceDto.DeviceTypeId;

        try
        {
            await context.SaveChangesAsync();
        }
        catch (DbUpdateConcurrencyException)
        {
            if (!DeviceExists(id))
                return NotFound();
            throw;
        }

        return NoContent();
    }

    /// <summary>
    /// Registers a new device.
    /// </summary>
    /// <param name="deviceDto"></param>
    /// <returns>The registered device.</returns>
    /// <response code="201">Returns the newly registered device</response>
    /// <response code="400">If the device is invalid</response>
    /// <response code="500">If there was an internal server error</response>
    [HttpPost]
    [ProducesResponseType(StatusCodes.Status201Created)]
    [ProducesResponseType(StatusCodes.Status400BadRequest)]
    [ProducesResponseType(StatusCodes.Status500InternalServerError)]
    public async Task<ActionResult<Device>> PostDevice(AddDeviceDto deviceDto)
    {
        var device = new Device
        {
            SerialNo = Guid.NewGuid(),
            HouseId = deviceDto.HouseId,
            DeviceTypeId = deviceDto.DeviceTypeId,
            Status = deviceDto.Status,
            CreatedAt = DateTime.UtcNow
        };
            
        context.Devices.Add(device);
        await context.SaveChangesAsync();

        return CreatedAtAction("GetDevice", new { id = device.Id }, device);
    }

    /// <summary>
    /// Deletes a specific device by unique ID.
    /// </summary>
    /// <param name="id">The ID of the device.</param>
    /// <returns>Action result indicating success or failure.</returns>
    /// <response code="204">Indicates that the device was successfully deleted</response>
    /// <response code="404">If the device was not found</response>
    /// <response code="500">If there was an internal server error</response>
    [HttpDelete("{id}")]
    [ProducesResponseType(StatusCodes.Status204NoContent)]
    [ProducesResponseType(StatusCodes.Status404NotFound)]
    [ProducesResponseType(StatusCodes.Status500InternalServerError)]
    public async Task<IActionResult> DeleteDevice(int id)
    {
        var device = await context.Devices.FindAsync(id);
        if (device == null)
            return NotFound();

        context.Devices.Remove(device);
        await context.SaveChangesAsync();

        return NoContent();
    }
        
    /// <summary>
    /// Sets the status of a device (on/off).
    /// </summary>
    /// <param name="id">The ID of the device.</param>
    /// <param name="status">The desired status of the device (true for on, false for off).</param>
    /// <returns>An action result indicating success or failure.</returns>
    /// <response code="200">Indicates that the device status was successfully updated</response>
    /// <response code="404">If the device was not found</response>
    /// <response code="400">If the request was invalid</response>
    /// <response code="500">If there was an internal server error</response>
    [HttpPatch("{id}/status")]
    [ProducesResponseType(StatusCodes.Status200OK)]
    [ProducesResponseType(StatusCodes.Status404NotFound)]
    [ProducesResponseType(StatusCodes.Status400BadRequest)]
    [ProducesResponseType(StatusCodes.Status500InternalServerError)]
    public async Task<IActionResult> SetDeviceStatus(
        int id,
        [FromBody] bool status)
    {
        var device = await context.Devices.FindAsync(id);

        if (device == null)
            return NotFound();
            
        device.Status = status;

        try
        {
            await context.SaveChangesAsync();
        }
        catch (DbUpdateConcurrencyException)
        {
            if (!DeviceExists(id))
                return NotFound();
            throw;
        }
        return Ok(device);
    }
    
    /// <summary>
    /// Sends a command to a device.
    /// </summary>
    /// <param name="message">The message to send.</param>
    /// <returns>An action result indicating success or failure.</returns>
    /// <response code="200">Indicates that the command was successfully sent</response>
    /// <response code="400">If the request was invalid</response>
    /// <response code="404">If the device was not found</response>
    /// <response code="500">If there was an internal server error</response>
    [HttpPost("call")]
    [ActionName("call")]
    [ProducesResponseType(StatusCodes.Status200OK)]
    [ProducesResponseType(StatusCodes.Status404NotFound)]
    [ProducesResponseType(StatusCodes.Status400BadRequest)]
    [ProducesResponseType(StatusCodes.Status500InternalServerError)]
    public async Task<IActionResult> CallProcedure([FromBody] DeviceMessage message)
    {
        var result = await producer.CallAsync(message, TimeSpan.FromMilliseconds(1000));
        return Ok(result);
    }

    private bool DeviceExists(int id)
    {
        return context.Devices.Any(e => e.Id == id);
    }
}