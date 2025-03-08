package mc.project.weatherapp.api

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface WeatherApiService {
    @GET("/v1/current.json")
    suspend fun getWeather(
        @Query("q") city: String,
        @Query("key") apiKey: String,
        @Query("aqi") aqi: String = "yes"
    ): Response<WeatherResponse>
}
