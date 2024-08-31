using SmartHome.Telemetry.Models;

namespace SmartHome.Telemetry.Data;

/// <summary>
/// Telemetry repository
/// </summary>
public interface ITelemetryRepository
{
    /// <summary>
    /// Get latest telemetry
    /// </summary>
    /// <param name="deviceId"></param>
    /// <returns></returns>
    Task<TelemetryData> GetLatestTelemetryAsync(long deviceId);
    
    /// <summary>
    /// Get telemetry history for device
    /// </summary>
    /// <param name="deviceId"></param>
    /// <param name="start"></param>
    /// <param name="end"></param>
    /// <returns></returns>
    Task<List<TelemetryData>> GetTelemetryHistoryAsync(long deviceId, DateTime? start, DateTime? end);

    /// <summary>
    /// Add telemetry data
    /// </summary>
    /// <param name="telemetryData"></param>
    /// <returns></returns>
    Task AddTelemetryDataAsync(TelemetryData telemetryData);
}