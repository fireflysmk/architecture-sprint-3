using System.Data.Common;
using Microsoft.AspNetCore.Mvc.Testing;
using Microsoft.AspNetCore.TestHost;
using Microsoft.EntityFrameworkCore;
using Microsoft.Extensions.DependencyInjection;
using Microsoft.VisualStudio.TestPlatform.TestHost;
using SmartHome.Telemetry.Data;
using Testcontainers.PostgreSql;

namespace SmartHome.Telemetry.Tests;

public class TelemetryControllerTests : IAsyncLifetime
{
    private const long DeviceId = 16;
    
    private readonly WebApplicationFactory<Program> _factory;
    private readonly PostgreSqlContainer _postgresqlContainer;
    private readonly HttpClient _client;
    
    private readonly TelemetryContext _db;
    private readonly DbConnection _connection;

    public TelemetryControllerTests()
    {
        // Configure PostgreSQL container
        _postgresqlContainer = new PostgreSqlBuilder()
            .WithImage("postgres:latest")
            .WithHostname("srv-telemetry-postgresql.default")
            .WithDatabase("telemetry")
            .WithUsername("postgres")
            .WithPassword("postgres")
            .WithCleanUp(true)
            .Build();

        _postgresqlContainer.StartAsync().GetAwaiter().GetResult();

        // Setup TestServer and HttpClient
        _factory = new WebApplicationFactory<Program>()
            .WithWebHostBuilder(builder =>
            {
                builder.ConfigureTestServices(services =>
                {
                    // Remove the existing DbContext registration
                    var descriptor = services.SingleOrDefault(
                        d => d.ServiceType == typeof(DbContextOptions<TelemetryContext>));
                    if (descriptor != null)
                        services.Remove(descriptor);
                    
                    services.AddDbContext<TelemetryContext>(options =>
                        options.UseNpgsql(_postgresqlContainer.GetConnectionString()));
                });
            });

        _client = _factory.CreateClient();
        _db = _factory.Services.CreateScope().ServiceProvider.GetRequiredService<TelemetryContext>();
        _connection = _db.Database.GetDbConnection();
    }

    public async Task InitializeAsync()
    {
        await _db.Database.MigrateAsync();
        await _connection.OpenAsync();
        await SeedDatabase();
    }

    public async Task DisposeAsync()
    {
        await _connection.CloseAsync();
        await _postgresqlContainer.StopAsync();
    }
    
    private async Task SeedDatabase()
    {
        _db.TelemetryData.Add(new Models.TelemetryData
        {
            Data = "Test Telemetry",
            DeviceId = DeviceId,
            CreatedAt = DateTime.UtcNow
        });

        await _db.SaveChangesAsync();
    }

    [Fact]
    public async Task GetDevices_ReturnsOkResponse()
    {
        var response = await _client.GetAsync($"/api/telemetry/{DeviceId}");
        response.EnsureSuccessStatusCode();

        var content = await response.Content.ReadAsStringAsync();
        Assert.Contains("Test Telemetry", content);
    }
}