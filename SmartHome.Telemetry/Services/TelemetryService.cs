using SmartHome.Telemetry.Data;
using SmartHome.Telemetry.Models;

namespace SmartHome.Telemetry.Services;

/// <summary>
/// Telemetry Service
/// </summary>
/// <param name="telemetryRepository"></param>
public class TelemetryService(ITelemetryRepository telemetryRepository) : ITelemetryService
{
    /// <summary>
    /// Get latest telemetry for a device
    /// </summary>
    /// <param name="deviceId"></param>
    /// <returns></returns>
    public async Task<TelemetryData> GetLatestTelemetryAsync(long deviceId)
    {
        return await telemetryRepository.GetLatestTelemetryAsync(deviceId);
    }

    /// <summary>
    /// Get telemetry history for a device
    /// </summary>
    /// <param name="deviceId"></param>
    /// <param name="start"></param>
    /// <param name="end"></param>
    /// <returns></returns>
    public async Task<List<TelemetryData>> GetTelemetryHistoryAsync(long deviceId, DateTime? start, DateTime? end)
    {
        return await telemetryRepository.GetTelemetryHistoryAsync(deviceId, start, end);
    }
    
    /// <summary>
    /// Add telemetry data
    /// </summary>
    /// <param name="telemetryData"></param>
    public async Task AddTelemetryDataAsync(TelemetryData telemetryData)
    {
        await telemetryRepository.AddTelemetryDataAsync(telemetryData);
    }
}