# WeatherApp

WeatherApp is an Android application built with Jetpack Compose and Kotlin, designed to provide users with real-time weather information, forecasts, and weather-related features. It integrates with the WeatherAPI to fetch weather data and offers a modern, user-friendly interface with dark mode support.

## Features

### 1. **Real-Time Weather Information**
   - Displays current weather conditions for a specified city or the user's current location.
   - Shows detailed weather metrics, including:
     - Temperature (°C)
     - Weather condition (e.g., sunny, cloudy)
     - Humidity
     - Wind speed
     - UV index
     - Precipitation
     - Sunrise and sunset times
   - Visualizes weather conditions with icons fetched from the WeatherAPI.

### 2. **Location-Based Weather**
   - Automatically fetches weather data based on the user's current location using the device's GPS.
   - Requests location permissions gracefully with a permission launcher.

### 3. **City Search and Caching**
   - Allows users to search for weather by city name.
   - Caches searched city names for quick access.
   - Provides an autocomplete-like feature by displaying cached cities as suggestions during search.
   - Users can remove individual cached cities or clear the entire cache.

### 4. **5-Day Weather Forecast**
   - Displays a 5-day weather forecast with daily summaries.
   - Includes hourly weather details for the selected day, showing temperature and condition icons.

### 5. **Air Quality Information**
   - Shows air quality metrics, including the US EPA Index and PM2.5 levels, based on data from the WeatherAPI.

### 6. **Driving Difficulty Index**
   - Estimates driving difficulty based on current weather conditions (e.g., rain, snow, high winds).
   - Color-codes the difficulty level (Low, Moderate, Severe) for quick understanding.

### 7. **Music Suggestions**
   - Recommends songs based on the current weather condition (e.g., Rain, Sunny, Cloudy, Winter, Storm).
   - Displays song names, artists, and YouTube links in a clickable card format.
   - Users can play songs directly on YouTube by clicking the song card or play button.

### 8. **Weather News**
   - Embeds a WebView to display Google News search results for weather-related news in the current city.
   - Supports dark mode in the WebView for a consistent user experience.

### 9. **Dark Mode**
   - Toggles between light and dark themes via a settings menu.
   - Persists dark mode state using LiveData in the ViewModel.
   - Applies dark mode to the WebView for news browsing.

### 10. **Modern UI with Jetpack Compose**
   - Built entirely with Jetpack Compose for a declarative and responsive UI.
   - Features a tabbed navigation bar for switching between Weather, Forecast, Music, and News screens.
   - Uses Material 3 components for a consistent and visually appealing design.
   - Includes animations and elevation effects for cards and interactive elements.

## Project Structure

- **MainActivity.kt**: The main entry point of the app, responsible for setting up the UI, handling location permissions, and initializing the WeatherViewModel.
- **WeatherViewModel.kt**: Manages weather data fetching, dark mode state, and city caching using LiveData and Retrofit for API calls.
- **MusicSuggestionsScreen.kt**: Handles the music recommendation feature, displaying songs based on weather conditions.
- **API Integration**:
  - Uses Retrofit with Gson to communicate with the WeatherAPI.
  - Fetches weather data, forecasts, and air quality information.
- **UI Components**:
  - WeatherScreen: Displays current weather and related metrics.
  - ForecastScreen: Shows daily and hourly forecasts.
  - WeatherNewsScreen: Embeds a WebView for news.
  - Various composables for cards, grids, and navigation.

## Dependencies

- **Jetpack Compose**: For building the UI.
- **Retrofit**: For making API requests to WeatherAPI.
- **Gson**: For JSON parsing.
- **Coil**: For loading weather condition icons.
- **Google Play Services Location**: For accessing device location.
- **Material 3**: For UI components and theming.
- **WebView**: For displaying weather news.

## Setup Instructions

1. **Clone the Repository**:
   ```bash
   git clone <repository-url>
   ```

2. **Add API Key**:
   - Obtain an API key from [WeatherAPI](https://www.weatherapi.com).
   - Add the key to `BuildConfig.WEATHER_API_KEY` in the `build.gradle` file or as a local property.

3. **Sync Project**:
   - Open the project in Android Studio.
   - Sync the project with Gradle to download dependencies.

4. **Run the App**:
   - Connect an Android device or use an emulator.
   - Build and run the app from Android Studio.

## Usage

1. **Launch the App**:
   - The app requests location permission to fetch weather for your current location.
   - Alternatively, use the search icon in the top bar to enter a city name.

2. **Navigate Tabs**:
   - **Weather**: View current weather, air quality, and driving difficulty.
   - **Forecast**: Check the 5-day forecast and hourly details.
   - **Music**: Browse song recommendations based on weather.
   - **News**: Read weather-related news for the current city.

3. **Toggle Dark Mode**:
   - Click the settings icon in the top bar and use the switch to enable/disable dark mode.

4. **Search and Cache Cities**:
   - Enter a city name in the search bar.
   - Cached cities appear as suggestions; click to fetch weather or remove them.

## Notes

- The `MusicDatabase` class is referenced but not provided in the codebase. It is assumed to be a placeholder for a database or static list returning `SongSuggestion` objects.
- Ensure a stable internet connection for API calls and WebView content loading.
- The app requires location permissions for automatic weather updates based on the user's location.

