package mc.project.weatherapp.api

data class WeatherResponse(
    val current: Current,
    val location: Location
)