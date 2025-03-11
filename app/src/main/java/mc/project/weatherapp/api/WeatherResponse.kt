package mc.project.weatherapp.api

data class WeatherResponse(
    val alerts: Alerts,
    val current: Current,
    val forecast: Forecast,
    val location: Location
)