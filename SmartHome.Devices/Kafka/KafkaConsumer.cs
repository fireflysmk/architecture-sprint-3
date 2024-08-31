using Confluent.Kafka;

namespace SmartHome.Devices.Kafka;

public class KafkaConsumer : IHostedService
{
    private readonly ILogger<KafkaConsumer> _logger;
    private readonly IConsumer<Ignore, string> _consumer;
    private readonly ConsumerConfig _config;
    private readonly string? _topic;

    public KafkaConsumer(IConfiguration configuration, ILogger<KafkaConsumer> logger)
    {
        _logger = logger;
        _config = new ConsumerConfig
        {
            BootstrapServers = configuration["Kafka:BootstrapServers"],
            GroupId = configuration["Kafka:GroupId"],
            AutoOffsetReset = AutoOffsetReset.Earliest,
            AllowAutoCreateTopics = true,
        };
        _topic = configuration["Kafka:Topic"];
        _consumer = new ConsumerBuilder<Ignore, string>(_config).Build();;
    }

    public Task StartAsync(CancellationToken cancellationToken)
    {
        _consumer.Subscribe(_topic);
        
        try
        {
            while (!cancellationToken.IsCancellationRequested)
            {
                try
                {
                    var consumeResult = _consumer.Consume(cancellationToken);
                    _logger.LogInformation($"Received message: {consumeResult.Message.Value}");
                    // Process the message here
                }
                catch (ConsumeException e)
                {
                    _logger.LogError($"Error occurred: {e.Error.Reason}");
                }
            }
        }
        catch (OperationCanceledException)
        {
            // This is expected when the cancellation token is canceled
        }
        finally
        {
            _consumer.Close();
        }
        
        return Task.CompletedTask;
    }

    public Task StopAsync(CancellationToken cancellationToken)
    {
        _consumer.Close();
        return Task.CompletedTask;
    }
}