FROM mcr.microsoft.com/dotnet/aspnet:8.0 AS base

LABEL org.opencontainers.image.source=https://github.com/synaxintelligence/smart-home

WORKDIR /app
EXPOSE 8080

FROM mcr.microsoft.com/dotnet/sdk:8.0 AS build

ARG BUILD_CONFIGURATION=Release
#ARG PACKAGE_SOURCE=http://nexus.local:8081/repository/nuget-snx-all/index.json

WORKDIR /src
COPY *.Devices.csproj .
RUN dotnet restore *.csproj # --source $PACKAGE_SOURCE
COPY . .
RUN dotnet build *.csproj -c $BUILD_CONFIGURATION -o /app/build

FROM build AS publish
ARG BUILD_CONFIGURATION=Release
RUN dotnet publish *.csproj -c $BUILD_CONFIGURATION -o /app/publish /p:UseAppHost=false

FROM base AS final
WORKDIR /app
COPY --from=publish /app/publish .
ENTRYPOINT ["dotnet", "SmartHome.Devices.dll"]
