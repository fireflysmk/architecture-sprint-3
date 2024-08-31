using System.ComponentModel.DataAnnotations;

namespace SmartHome.Devices.Dto;

/// <summary>
/// Add House Dto
/// </summary>
public class AddHouseDto
{
    public string Name { get; set; }
    public string? Address { get; set; }
    public string? City { get; set; }
    public string? State { get; set; }
    public string? ZipCode { get; set; }
    public string? Country { get; set; }
}