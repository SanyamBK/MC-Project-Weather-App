package mc.project.weatherapp

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
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

    val currentCity: String?
        get() = ((weatherState.value as? NetworkResponse.Success)?.data?.location?.name + ", " +
                (weatherState.value as? NetworkResponse.Success)?.data?.location?.country)

    // Store cached city names
    private val _cachedCities = MutableLiveData<Set<String>>(setOf())
    val cachedCities: LiveData<Set<String>> = _cachedCities


    fun fetchWeather(city: String) {
        val formattedCity = city.trim().lowercase()

        viewModelScope.launch {
            try {
                val response = apiService.getWeather(formattedCity, BuildConfig.WEATHER_API_KEY)
                if (response.isSuccessful) {
                    response.body()?.let {
                        _weatherState.value = NetworkResponse.Success(it)
                        cacheCity(formattedCity) // ✅ Save the city name
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

    fun cacheCity(city: String) {
        val currentSet = _cachedCities.value?.toMutableSet() ?: mutableSetOf()
        if (currentSet.add(city)) {
            _cachedCities.value = currentSet
        }
    }


    fun loadCachedCities() {
        // Optional: used to manually refresh the observer
        _cachedCities.value = _cachedCities.value ?: mutableSetOf()
    }

    fun removeCachedCity(city: String) {
        val updatedSet = _cachedCities.value?.toMutableSet()
        updatedSet?.remove(city)
        _cachedCities.value = updatedSet ?: setOf()
    }


    fun clearCachedCities() {
        _cachedCities.value = mutableSetOf()
    }

}