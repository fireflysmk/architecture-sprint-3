using System.Text.Json.Serialization;

namespace SmartHome.Devices.Kafka;

public class DeviceMessage
{
    public long DeviceId {get; set;}
    
    [JsonConverter(typeof(JsonStringEnumConverter))]
    public CommandType CommandType {get; set; }
    
    public string? Payload { get; set; }
}