using System.ComponentModel.DataAnnotations;

namespace SmartHome.Devices.Dto;

/// <summary>
/// Add Device Dto
/// </summary>
public class AddDeviceDto
{
    public bool Status { get; set; }
    public int? HouseId { get; set; }
    public int? DeviceTypeId { get; set; }
}