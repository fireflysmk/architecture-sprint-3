using Microsoft.AspNetCore.Mvc;

namespace SmartHome.Telemetry.Controllers;

/// <summary>
/// Health controller
/// </summary>
[ApiExplorerSettings(IgnoreApi = true)]
[ApiController]
[Route("api/[controller]")]
public class HealthController : ControllerBase
{
    /// <summary>
    /// Health check
    /// </summary>
    /// <returns></returns>
    [HttpGet]
    public IActionResult Index()
    {
        return Ok("Healthy");
    }
}