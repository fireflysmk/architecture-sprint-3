using Confluent.Kafka;
using SmartHome.Telemetry.Models;
using SmartHome.Telemetry.Services;

namespace SmartHome.Telemetry.Kafka;

/// <summary>
/// Kafka consumer service
/// </summary>
public class KafkaConsumerService : BackgroundService
{
    private readonly ILogger<KafkaConsumerService> _logger;
    private readonly IConsumer<string, string> _consumer;
    private readonly IServiceScopeFactory _serviceScopeFactory;
    private readonly string _topic;

    /// <summary>
    /// Constructor
    /// </summary>
    /// <param name="logger"></param>
    /// <param name="configuration"></param>
    /// <param name="serviceScopeFactory"></param>
    public KafkaConsumerService(
        ILogger<KafkaConsumerService> logger,
        IConfiguration configuration,
        IServiceScopeFactory serviceScopeFactory)
    {
        _logger = logger;
        _serviceScopeFactory = serviceScopeFactory;

        var consumerConfig = new ConsumerConfig
        {
            BootstrapServers = configuration["Kafka:BootstrapServers"],
            GroupId = configuration["Kafka:GroupId"],
            AutoOffsetReset = AutoOffsetReset.Earliest
        };

        _topic = configuration["Kafka:Topic"] ?? string.Empty;
        _consumer = new ConsumerBuilder<string, string>(consumerConfig).Build();
    }

    /// <summary>
    /// Start the service
    /// </summary>
    /// <param name="stoppingToken"></param>
    /// <returns></returns>
    protected override Task ExecuteAsync(CancellationToken stoppingToken)
    {
        return Task.Run(async () =>
        {
            _consumer.Subscribe(_topic);
            
            while (!stoppingToken.IsCancellationRequested)
            {
                try
                {
                    var cr = _consumer.Consume(stoppingToken);
                    _logger.LogInformation($"Consumed message '{cr.Message.Value}' at: '{cr.TopicPartitionOffset}'.");
                    await ProcessMessageAsync(cr.Message.Value);
                }
                catch (ConsumeException e)
                {
                    _logger.LogError($"Error occurred: {e.Error.Reason}");
                }
            }
        }, stoppingToken);
    }

    private async Task ProcessMessageAsync(string message)
    {
        var splitMessage = message.Split(";");
        var deviceId = splitMessage[0];
        var data = splitMessage[1];
        
        var telemetryData = new TelemetryData()
        {
            DeviceId = Int32.Parse(deviceId),
            Data = data,
            CreatedAt = DateTime.UtcNow
        };

        using var scope = _serviceScopeFactory.CreateScope();
        var telemetryService = scope.ServiceProvider.GetRequiredService<ITelemetryService>();

        await telemetryService.AddTelemetryDataAsync(telemetryData);
    }

    /// <summary>
    /// Stop the service
    /// </summary>
    /// <param name="stoppingToken"></param>
    /// <returns></returns>
    public override Task StopAsync(CancellationToken stoppingToken)
    {
        _consumer.Close();
        return base.StopAsync(stoppingToken);
    }
}