using Microsoft.AspNetCore.Mvc;

namespace SmartHome.Devices.Controllers;

[ApiExplorerSettings(IgnoreApi = true)]
[ApiController]
[Route("api/[controller]")]
public class HealthController : ControllerBase
{
    [HttpGet]
    public IActionResult Index()
    {
        return Ok("Healthy");
    }
}