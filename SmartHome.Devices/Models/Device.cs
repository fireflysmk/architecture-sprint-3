using System.ComponentModel.DataAnnotations;
using System.ComponentModel.DataAnnotations.Schema;

namespace SmartHome.Devices.Models;

[Table("Devices")]
public class Device
{
    [Key]
    [DatabaseGenerated(DatabaseGeneratedOption.Identity)]
    public int Id { get; set; }
    public Guid SerialNo { get; set; }
    public bool Status { get; set; }
    public int? HouseId { get; set; }
    public int? DeviceTypeId { get; set; }
    public DateTime CreatedAt { get; set; }
    
    public House? House { get; set; }
    public DeviceType? DeviceType { get; set; }
}