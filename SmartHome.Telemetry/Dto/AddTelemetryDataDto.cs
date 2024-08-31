using System.ComponentModel.DataAnnotations;

namespace SmartHome.Telemetry.Dto;

/// <summary>
/// AddTelemetryDataDto
/// </summary>
public class AddTelemetryDataDto
{
    [Required]
    public long DeviceId { get; set; }
    [Required]
    public string Data { get; set; }
}