using System.Text.Json;
using System.Text.Json.Serialization;
using Confluent.Kafka;
using SmartHome.Devices.Models;

namespace SmartHome.Devices.Kafka;

public class DeviceMessageDeserializer : IDeserializer<DeviceMessage>
{
    public DeviceMessage Deserialize(ReadOnlySpan<byte> data, bool isNull, SerializationContext context)
    {
        var options = new JsonSerializerOptions
        {
            Converters = { new JsonStringEnumConverter() }
        };
        return JsonSerializer.Deserialize<DeviceMessage>(data, options) ?? new DeviceMessage();
    }
}