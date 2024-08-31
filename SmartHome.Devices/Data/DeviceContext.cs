using Microsoft.EntityFrameworkCore;
using SmartHome.Devices.Models;

namespace SmartHome.Devices.Data;

public class DeviceContext : DbContext
{
    public DbSet<Device> Devices { get; set; }
    public DbSet<DeviceType> DeviceTypes { get; set; }
    public DbSet<House> Houses { get; set; }
    
    protected override void OnModelCreating(ModelBuilder modelBuilder)
    {
        if (Environment.GetEnvironmentVariable("SEED_DEVICE_DB") != "true") return;
        var seed = int.Parse(Environment.GetEnvironmentVariable("SEED_DEVICE_DATA") ?? "1");
        modelBuilder.Entity<Device>().HasData(GetDeviceData(seed));
        modelBuilder.Entity<DeviceType>().HasData(GetDeviceTypeData(seed));
        modelBuilder.Entity<House>().HasData(GetHouseData(seed));
    }

    private IEnumerable<House> GetHouseData(int seed)
    {
        var houseList = new List<House>();
        for (var i = 0; i < seed; i++)
        {
            houseList.Add(new House
            {
                Name = $"House_{i + 1}",
                Address = $"Address_{i + 1}",
                City = $"City_{i + 1}",
                Country = $"Country_{i + 1}",
                State = $"State_{i + 1}",
                ZipCode = $"ZipCode_{i + 1}",
                TimeStamp = DateTime.UtcNow
            });
        }
        
        return houseList;
    }

    private IEnumerable<DeviceType> GetDeviceTypeData(int seed)
    {
        var deviceTypeList = new List<DeviceType>();
        for (var i = 0; i < seed; i++)
        {
            deviceTypeList.Add(new DeviceType
            {
                Name = $"DeviceType_{i + 1}"
            });
        }
        
        return deviceTypeList;
    }

    private static IEnumerable<Device> GetDeviceData(int seed)
    {
        var deviceList = new List<Device>();
        for (var i = 0; i < seed; i++)
        {
            deviceList.Add(new Device
            {
                SerialNo = Guid.NewGuid(),
                DeviceTypeId = i + 1,
                HouseId = i + 1,
                CreatedAt= DateTime.UtcNow
            });
        }
        
        return deviceList;
    }
    
    public DeviceContext(DbContextOptions<DeviceContext> options) : base(options)
    {
    }
}