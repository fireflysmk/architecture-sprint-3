using Microsoft.EntityFrameworkCore;
using SmartHome.Telemetry.Models;

namespace SmartHome.Telemetry.Data;

/// <summary>
/// Telemetry repository
/// </summary>
/// <param name="context"></param>
public class TelemetryRepository(TelemetryContext context) : ITelemetryRepository
{
    /// <summary>
    /// Get the latest telemetry for a device
    /// </summary>
    /// <param name="deviceId"></param>
    /// <returns></returns>
    public async Task<TelemetryData> GetLatestTelemetryAsync(long deviceId)
    {
        return await context.TelemetryData
            .Where(t => t.DeviceId == deviceId)
            .OrderByDescending(t => t.CreatedAt)
            .FirstOrDefaultAsync() ?? new TelemetryData();
    }

    /// <summary>
    /// Get all telemetry for a device
    /// </summary>
    /// <param name="deviceId"></param>
    /// <param name="start"></param>
    /// <param name="end"></param>
    /// <returns></returns>
    public async Task<List<TelemetryData>> GetTelemetryHistoryAsync(
        long deviceId,
        DateTime? start,
        DateTime? end)
    {
        var query = context.TelemetryData.AsQueryable();

        if (start.HasValue)
            query = query.Where(t => t.CreatedAt >= start.Value);
        
        if (end.HasValue)
            query = query.Where(t => t.CreatedAt <= end.Value);

        return await query
            .Where(t => t.DeviceId == deviceId)
            .OrderBy(t => t.CreatedAt)
            .ToListAsync();
    }
    
    /// <summary>
    /// Add telemetry data
    /// </summary>
    /// <param name="telemetryData"></param>
    public async Task AddTelemetryDataAsync(TelemetryData telemetryData)
    {
        context.TelemetryData.Add(telemetryData);
        await context.SaveChangesAsync();
    }
}