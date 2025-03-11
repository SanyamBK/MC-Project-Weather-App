package mc.project.weatherapp.api

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface WeatherApiService {
    @GET("/v1/forecast.json") // Change from current.json to forecast.json
    suspend fun getWeather(
        @Query("q") city: String,
        @Query("key") apiKey: String,
        @Query("days") days: Int = 7, // Change type to Int
        @Query("aqi") aqi: String = "yes",
        @Query("alerts") alerts: String = "yes"
    ): Response<WeatherResponse>
}
