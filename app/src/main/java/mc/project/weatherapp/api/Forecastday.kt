package mc.project.weatherapp.api

data class ForecastDay(
    val date: String,
    val hour: List<Hour>,
    val astro: Astro
)
