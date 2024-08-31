using Microsoft.AspNetCore.Mvc;
using Microsoft.EntityFrameworkCore;
using SmartHome.Devices.Data;
using SmartHome.Devices.Dto;
using SmartHome.Devices.Models;

namespace SmartHome.Devices.Controllers;

/// <summary>
/// Api controller for houses in smart home application
/// </summary>
[ApiController]
[Route("api/[controller]")]
public class HousesController(DeviceContext context) : ControllerBase
{
    /// <summary>
    /// Get all houses
    /// </summary>
    /// <returns>A list of houses</returns>
    /// <response code="200">OK</response>
    /// <response code="500">Internal Server Error</response>
    [HttpGet]
    [ProducesResponseType(StatusCodes.Status200OK)]
    [ProducesResponseType(StatusCodes.Status500InternalServerError)]
    public async Task<ActionResult<IEnumerable<House>>> GetHouses()
    {
        return await context.Houses
            .Include(h => h.Devices)
            .ToListAsync();
    }

    /// <summary>
    /// Get house by identifier
    /// </summary>
    /// <param name="id">House identifier</param>
    /// <returns>The requested house</returns>
    /// <response code="200">OK</response>
    /// <response code="404">Not Found</response>
    /// <response code="500">Internal Server Error</response>
    [HttpGet("{id}")]
    [ProducesResponseType(StatusCodes.Status200OK)]
    [ProducesResponseType(StatusCodes.Status404NotFound)]
    [ProducesResponseType(StatusCodes.Status500InternalServerError)]
    public async Task<ActionResult<House>> GetHouse(int id)
    {
        var house = await context.Houses.Include(h => h.Devices).FirstOrDefaultAsync(h => h.Id == id);

        return house == null
            ? NotFound()
            : house;
    }

    /// <summary>
    /// Update house by identifier.
    /// </summary>
    /// <param name="id">House identifier</param>
    /// <param name="houseDto"></param>
    /// <returns>The updated house</returns>
    /// <response code="200">OK</response>
    /// <response code="400">Bad Request</response>
    /// <response code="404">Not Found</response>
    /// <response code="500">Internal Server Error</response>
    [HttpPut("{id}")]
    [ProducesResponseType(StatusCodes.Status200OK)]
    [ProducesResponseType(StatusCodes.Status400BadRequest)]
    [ProducesResponseType(StatusCodes.Status404NotFound)]
    [ProducesResponseType(StatusCodes.Status500InternalServerError)]
    public async Task<IActionResult> UpdateHouse(int id, [FromBody] AddHouseDto houseDto)
    {
        var house = await context.Houses.FindAsync(id);
        
        if (house == null)
            return NotFound("House not found");
        
        house.Name = houseDto.Name;
        house.Address = houseDto.Address;
        house.City = houseDto.City;
        house.State = houseDto.State;
        house.ZipCode = houseDto.ZipCode;
        house.TimeStamp = DateTime.Now;

        try
        {
            await context.SaveChangesAsync();
        }
        catch (DbUpdateConcurrencyException)
        {
            if (!HouseExists(id))
                return NotFound("House not found");
            throw;
        }

        return NoContent();
    }
    
    /// <summary>
    /// Create new house
    /// </summary>
    /// <param name="house">House to create</param>
    /// <returns>The created house</returns>
    /// <response code="201">Created</response>
    /// <response code="400">Bad Request</response>
    /// <response code="500">Internal Server Error</response>
    [HttpPost]
    [ProducesResponseType(StatusCodes.Status201Created)]
    [ProducesResponseType(StatusCodes.Status400BadRequest)]
    [ProducesResponseType(StatusCodes.Status500InternalServerError)]
    public async Task<ActionResult<House>> CreateHouse(AddHouseDto houseDto)
    {
        var house = new House
        {
            Name = houseDto.Name,
            Address = houseDto.Address,
            City = houseDto.City,
            State = houseDto.State,
            ZipCode = houseDto.ZipCode,
            TimeStamp = DateTime.Now
        };
        
        context.Houses.Add(house);
        await context.SaveChangesAsync();

        return CreatedAtAction(nameof(GetHouse), new { id = house.Id }, house);
    }

    /// <summary>
    /// Delete house by identifier
    /// </summary>
    /// <param name="id">House identifier</param>
    /// <returns>Action result indicating success or failure.</returns>
    /// <response code="204">No Content</response>
    /// <response code="404">Not Found</response>
    /// <response code="500">Internal Server Error</response>
    [HttpDelete("{id}")]
    [ProducesResponseType(StatusCodes.Status204NoContent)]
    [ProducesResponseType(StatusCodes.Status404NotFound)]
    [ProducesResponseType(StatusCodes.Status500InternalServerError)]
    public async Task<IActionResult> DeleteHouse(int id)
    {
        var house = await context.Houses.FindAsync(id);
        if (house == null)
            return NotFound();

        context.Houses.Remove(house);
        await context.SaveChangesAsync();

        return NoContent();
    }

    private bool HouseExists(int id)
    {
        return context.Houses.Any(e => e.Id == id);
    }
}