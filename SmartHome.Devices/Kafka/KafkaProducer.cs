using Confluent.Kafka;
using SmartHome.Devices.Models;

namespace SmartHome.Devices.Kafka;

public class KafkaProducer
{
    private readonly IProducer<string, DeviceMessage> _producer;
    private readonly IConsumer<string, string> _consumer;

    private readonly string _requestTopic;
    private readonly string _responseTopic;

    public KafkaProducer(IConfiguration configuration)
    {
        _requestTopic = configuration["Kafka:RequestTopic"] ?? string.Empty;
        _responseTopic = configuration["Kafka:ResponseTopic"] ?? string.Empty;
        
        var producerConfig = new ProducerConfig
        {
            BootstrapServers = configuration["Kafka:BootstrapServers"]
        };
        _producer = new ProducerBuilder<string, DeviceMessage>(producerConfig)
            .SetValueSerializer(new DeviceMessageSerializer())
            .Build();
        
        var consumerConfig = new ConsumerConfig
        {
            BootstrapServers = configuration["Kafka:BootstrapServers"],
            GroupId = configuration["Kafka:GroupId"],
            AutoOffsetReset = AutoOffsetReset.Earliest,
            AllowAutoCreateTopics = true
        };
        _consumer = new ConsumerBuilder<string, string>(consumerConfig).Build();
        _consumer.Subscribe(_responseTopic);
    }

    public async Task<string> CallAsync(DeviceMessage message, TimeSpan timeout)
    {
        var correlationId = Guid.NewGuid().ToString();
        
        await _producer.ProduceAsync(_requestTopic, new Message<string, DeviceMessage>
        {
            Key = correlationId,
            Value = message
        });
        
        using var cts = new CancellationTokenSource(timeout);
        try
        {
            while (!cts.IsCancellationRequested)
            {
                var consumeResult = _consumer.Consume(cts.Token);
                if (consumeResult.Message.Key == correlationId)
                {
                    return consumeResult.Message.Value;
                }
            }
        }
        catch (OperationCanceledException)
        {
            throw new TimeoutException("RPC call timed out");
        }

        throw new TimeoutException("RPC call timed out");
    }
    
    public void Dispose()
    {
        _consumer?.Dispose();
        _producer?.Dispose();
    }
}