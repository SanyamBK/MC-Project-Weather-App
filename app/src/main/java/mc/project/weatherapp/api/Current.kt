package mc.project.weatherapp.api

data class Current(
    val temp_c: Float,
    val condition: Condition,
    val wind_kph: Float,
    val humidity: Int,
    val uv: Float,
    val precip_mm: Float,
    val air_quality: AirQualityX
)