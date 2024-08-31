using Microsoft.EntityFrameworkCore;
using SmartHome.Telemetry.Models;

namespace SmartHome.Telemetry.Data;

public class TelemetryContext : DbContext
{
    public DbSet<TelemetryData> TelemetryData { get; set; }
    protected override void OnModelCreating(ModelBuilder modelBuilder)
    {
        if (Environment.GetEnvironmentVariable("SEED_TELEMETRY_DB") != "true") return;
        var seed = int.Parse(Environment.GetEnvironmentVariable("SEED_TELEMETRY_DATA") ?? "1");
        modelBuilder.Entity<TelemetryData>().HasData(GetTelemetryData(seed));
    }
    
    private static IEnumerable<TelemetryData> GetTelemetryData(int seed = 1)
    {
        var telemetryDataList = new List<TelemetryData>();
        for (var i = 0; i < seed; i++)
        {
            telemetryDataList.Add(new TelemetryData
            {
                DeviceId = i + 1,
                Data = Guid.NewGuid().ToString(),
                CreatedAt= DateTime.UtcNow
            });
        }
        
        return telemetryDataList;
    }
    
    public TelemetryContext(DbContextOptions<TelemetryContext> options) : base(options)
    {
    }
}