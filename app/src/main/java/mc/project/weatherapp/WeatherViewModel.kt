package mc.project.weatherapp

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import mc.project.weatherapp.api.WeatherApiService
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import mc.project.weatherapp.BuildConfig
import mc.project.weatherapp.api.NetworkResponse
import mc.project.weatherapp.api.WeatherResponse



class WeatherViewModel : ViewModel() {
    private val retrofit = Retrofit.Builder()
        .baseUrl("https://api.weatherapi.com")
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    private val apiService = retrofit.create(WeatherApiService::class.java)

    private val _darkModeEnabled = MutableLiveData(false)
    val darkModeEnabled: LiveData<Boolean> = _darkModeEnabled

    fun toggleDarkMode(enabled: Boolean) {
        _darkModeEnabled.value = enabled
    }

    // Use LiveData
    private val _weatherState = MutableLiveData<NetworkResponse<WeatherResponse>>()
    val weatherState: LiveData<NetworkResponse<WeatherResponse>> = _weatherState

    fun fetchWeather(city: String) {
        viewModelScope.launch {
            try {
                val response = apiService.getWeather(city, BuildConfig.WEATHER_API_KEY)
                if (response.isSuccessful) {
                    response.body()?.let {
                        _weatherState.value = NetworkResponse.Success(it)

                    }

                } else {
                    _weatherState.value = NetworkResponse.Error("Failed to Load data")
                }
            } catch (e: Exception) {
                _weatherState.value = NetworkResponse.Error("Failed to Load data")
                e.printStackTrace()

            }
        }
    }

}