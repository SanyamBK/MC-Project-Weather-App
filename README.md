# **Weather App Documentation**

## **1. Introduction**
The Weather App is an Android application built using **Kotlin** and **Jetpack Compose**. It provides real-time weather updates based on the user's current location and allows searching for weather details of other locations. The app fetches data from the Weather API and presents a user-friendly interface with forecasts and air quality details.

## **2. Features**
- **Current Weather Display**: Shows temperature, weather conditions, and AQI for the current location.
- **Hourly Forecast**: Provides weather updates for the next 24 hours.
- **7-Day Forecast**: Displays a detailed weather outlook for the upcoming week.
- **Location Search**: Users can search for weather details of other locations.
- **Air Quality Index (AQI)**: Displays air quality details if available.
- **Error Handling**: Displays error messages in case of network failure.
- **Dark Mode & Accessibility Features**: Supports dark mode for better accessibility.
- **Location-Based Weather Fetching**: Uses GPS to get real-time weather for the user's current location.

## **3. Project Structure**
```
Weather App/
│── app/
│   ├── src/
│   │   ├── main/
│   │   │   ├── AndroidManifest.xml
│   │   │   ├── java/mc/project/weatherapp/
│   │   │   │   ├── MainActivity.kt
│   │   │   │   ├── WeatherViewModel.kt
│   │   │   │   ├── api/
│   │   │   │   │   ├── AirQuality.kt
│   │   │   │   │   ├── AirQualityX.kt
│   │   │   │   │   ├── Alerts.kt
│   │   │   │   │   ├── Astro.kt
│   │   │   │   │   ├── Condition.kt
│   │   │   │   │   ├── Current.kt
│   │   │   │   │   ├── Day.kt
│   │   │   │   │   ├── Forecast.kt
│   │   │   │   │   ├── Forecastday.kt
│   │   │   │   │   ├── Hour.kt
│   │   │   │   │   ├── Location.kt
│   │   │   │   │   ├── NetworkResponse.kt
│   │   │   │   │   ├── WeatherApiService.kt
│   │   │   │   │   ├── WeatherResponse.kt
│   │   │   │   ├── WeatherScreen.kt (Includes Search Functionality)
│   │   │   │   ├── AirQualityScreen.kt
│   │   │   │   ├── ForecastScreen.kt
│   │   │   │   ├── WeatherIcon.kt
│   ├── build.gradle.kts
│   ├── proguard-rules.pro
│── build.gradle.kts
│── settings.gradle.kts
│── gradlew
│── gradlew.bat
│── gradle.properties
```

## **4. Main Components**
### **Activities & ViewModel**
- **MainActivity.kt**: Serves as the entry point for the application.
- **WeatherViewModel.kt**: Manages API calls and UI-related data.
- **WeatherScreen.kt**: Displays the current weather and includes the search feature.
- **ForecastScreen.kt**: Displays the hourly and weekly weather forecast.
- **AirQualityScreen.kt**: Shows air quality data.

### **API Models** (Located in `api/` folder)
- **WeatherResponse.kt**: Represents the complete API response.
- **Current.kt, Condition.kt, Forecast.kt, AirQuality.kt, etc.**: Models different parts of the response.
- **WeatherApiService.kt**: Handles API calls to fetch weather data.

### **UI Components** (Located in `ui/` folder)
- **WeatherScreen.kt**: Displays the current weather and includes search functionality.
- **ForecastScreen.kt**: Shows hourly and weekly forecasts.
- **AirQualityScreen.kt**: Displays AQI and air pollution data.
- **WeatherIcon.kt**: Loads and displays weather condition icons.

## **5. API Integration**
The app fetches weather data using WeatherAPI, making network requests through the **WeatherViewModel**. The API response is parsed into data models and displayed using Jetpack Compose UI components.

## **6. Implemented Functionalities**
### **Basic Functionalities:**
✔ Use of at least three **Activity Fragments** (WeatherScreen, AirQualityScreen, ForecastScreen)

✔ **Network Connectivity**: Fetching data from the Weather API

✔ **Caching of relevant data and use of background services**: Uses GPS to determine user’s current location. Caches user's last searched location.

✔ **Error Handling**: Displays messages in case of network failures


### **Extra Features:**
✔ **Accessibility**: Supports Dark Mode and Search Autocomplete

✔ **Native API**: GPS-based location tracking

✔ **Sensing**: Uses GPS to fetch weather data for the current location



## **8. Conclusion**
This Weather App provides a seamless experience for users to check real-time and forecasted weather conditions. The combination of **Jetpack Compose**, **Retrofit**, and **ViewModel** ensures a modern and maintainable architecture for the application.

