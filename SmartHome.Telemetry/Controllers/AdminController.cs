using Microsoft.AspNetCore.Authorization;
using Microsoft.AspNetCore.Mvc;
using Microsoft.OpenApi.Writers;
using Swashbuckle.AspNetCore.Swagger;

namespace SmartHome.Telemetry.Controllers;

/// <summary>
/// API for admins.
/// </summary>
[Authorize]
[ApiController]
[ApiExplorerSettings(IgnoreApi = true)]
[Route("api/[controller]")]
public class AdminController : Controller
{
    /// <summary>
    /// Generates the OpenAPI specification.
    /// </summary>
    /// <param name="swaggerProvider">The Swagger provider.</param>
    /// <returns>The OpenAPI specification.</returns>
    [HttpGet]
    [ActionName("openapi")]
    [ProducesResponseType(StatusCodes.Status200OK)]
    [ProducesResponseType(StatusCodes.Status500InternalServerError)]
    public ActionResult GenerateOpenApiSpec([FromServices] ISwaggerProvider swaggerProvider)
    {
        try
        {
            var swagger = swaggerProvider.GetSwagger("v1");
            var stringWriter = new StringWriter();
            swagger.SerializeAsV3(new OpenApiYamlWriter(stringWriter));
            var swaggerYaml = stringWriter.ToString();

            using var fileWriter = new StreamWriter("api.yaml");
            fileWriter.Write(swaggerYaml);

            return Ok("OpenAPI spec generated successfully.");
        }
        catch (Exception ex)
        {
            return StatusCode(StatusCodes.Status500InternalServerError, $"An error occurred: {ex.Message}");
        }
    }
}