using System.ComponentModel.DataAnnotations;
using System.ComponentModel.DataAnnotations.Schema;

namespace SmartHome.Telemetry.Models;

/// <summary>
/// Telemetry Data
/// </summary>
[Table("TelemetryData")]
public class TelemetryData
{
    [Key]
    [DatabaseGenerated(DatabaseGeneratedOption.Identity)]
    public int Id { get; set; }
    public long DeviceId { get; set; }
    public string Data { get; set; }
    public DateTime CreatedAt { get; set; }
}