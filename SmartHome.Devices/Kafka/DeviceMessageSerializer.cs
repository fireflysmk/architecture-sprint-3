using System.Text.Json;
using System.Text.Json.Serialization;
using Confluent.Kafka;
using SmartHome.Devices.Models;

namespace SmartHome.Devices.Kafka;

public class DeviceMessageSerializer : ISerializer<DeviceMessage>
{
    public byte[] Serialize(DeviceMessage data, SerializationContext context)
    {
        var options = new JsonSerializerOptions();
        options.Converters.Add(new JsonStringEnumConverter());
        return JsonSerializer.SerializeToUtf8Bytes(data, options);
    }
}