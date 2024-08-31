using System.Data.Common;
using Microsoft.AspNetCore.Mvc.Testing;
using Microsoft.AspNetCore.TestHost;
using Microsoft.Extensions.DependencyInjection;
using Microsoft.EntityFrameworkCore;
using SmartHome.Devices.Data;
using SmartHome.Devices.Models;
using Testcontainers.PostgreSql;
using Xunit;

namespace SmartHome.Devices.Tests;

public class DeviceControllerTests : IAsyncLifetime
{
    private readonly WebApplicationFactory<Program> _factory;
    private readonly PostgreSqlContainer _postgresqlContainer;
    private readonly HttpClient _client;
    
    private readonly DeviceContext _db;
    private readonly DbConnection _connection;
    
    private readonly Guid _serialNo = Guid.NewGuid();

    public DeviceControllerTests()
    {
        // Configure PostgreSQL container
        _postgresqlContainer = new PostgreSqlBuilder()
            .WithImage("postgres:latest")
            .WithHostname("srv-devices-postgresql.default")
            .WithDatabase("devices")
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
                        d => d.ServiceType == typeof(DbContextOptions<DeviceContext>));
                    if (descriptor != null)
                        services.Remove(descriptor);
                    
                    services.AddDbContext<DeviceContext>(options =>
                        options.UseNpgsql(_postgresqlContainer.GetConnectionString()));
                });
            });

        _client = _factory.CreateClient();
        _db = _factory.Services.CreateScope().ServiceProvider.GetRequiredService<DeviceContext>();
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
        _db.Devices.Add(new Device
        {
            SerialNo = _serialNo,
            Status = true,
            CreatedAt = DateTime.UtcNow
        });

        await _db.SaveChangesAsync();
    }

    [Fact]
    public async Task GetDevices_ReturnsOkResponse()
    {
        var response = await _client.GetAsync("/api/devices");
        response.EnsureSuccessStatusCode();

        var content = await response.Content.ReadAsStringAsync();
        Assert.Contains(_serialNo.ToString(), content);
    }
}