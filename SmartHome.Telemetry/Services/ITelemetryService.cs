using SmartHome.Telemetry.Models;

namespace SmartHome.Telemetry.Services;

/// <summary>
/// Telemetry service interface
/// </summary>
public interface ITelemetryService
{
    /// <summary>
    /// Get the latest telemetry data for a device
    /// </summary>
    /// <param name="deviceId"></param>
    /// <returns></returns>
    Task<TelemetryData> GetLatestTelemetryAsync(long deviceId);
    
    /// <summary>
    /// Get the telemetry history for a device
    /// </summary>
    /// <param name="deviceId"></param>
    /// <param name="start"></param>
    /// <param name="end"></param>
    /// <returns></returns>
    Task<List<TelemetryData>> GetTelemetryHistoryAsync(long deviceId, DateTime? start, DateTime? end);

    /// <summary>
    /// Add a telemetry data to the database
    /// </summary>
    /// <param name="telemetryData"></param>
    /// <returns></returns>
    Task AddTelemetryDataAsync(TelemetryData telemetryData);
}