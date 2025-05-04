package mc.project.weatherapp

import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.app.ActivityCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.media3.common.util.UnstableApi
import coil.compose.AsyncImage
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import mc.project.weatherapp.api.*
import mc.project.weatherapp.ui.theme.WeatherTheme
import android.net.Uri
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.webkit.WebSettingsCompat
import androidx.webkit.WebViewFeature

class MainActivity : ComponentActivity() {
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        setContent {
            val viewModel: WeatherViewModel = viewModel()
            val isDarkMode by viewModel.darkModeEnabled.observeAsState(false)
            WeatherTheme(
                darkTheme = isDarkMode,
                dynamicColor = true
            ) {
                WeatherApp(viewModel, fusedLocationClient, this@MainActivity)
            }
        }
    }
}

@androidx.annotation.OptIn(UnstableApi::class)
@Composable
fun WeatherApp(
    viewModel: WeatherViewModel,
    fusedLocationClient: FusedLocationProviderClient,
    activity: ComponentActivity
) {
    var selectedTab by remember { mutableStateOf(0) }
    val weather = viewModel.weatherState.observeAsState()
    var currentLocation by remember { mutableStateOf<Location?>(null) }

    val locationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { isGranted ->
            if (isGranted) {
                if (ActivityCompat.checkSelfPermission(
                        activity,
                        android.Manifest.permission.ACCESS_FINE_LOCATION
                    ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                        activity,
                        android.Manifest.permission.ACCESS_COARSE_LOCATION
                    ) != PackageManager.PERMISSION_GRANTED
                ) {
                    return@rememberLauncherForActivityResult
                }
                fusedLocationClient.lastLocation
                    .addOnSuccessListener { location: Location? ->
                        location?.let {
                            currentLocation = it
                        }
                    }
            }
        }
    )

    LaunchedEffect(key1 = true) {
        locationPermissionLauncher.launch(android.Manifest.permission.ACCESS_FINE_LOCATION)
    }

    LaunchedEffect(key1 = currentLocation) {
        currentLocation?.let {
            viewModel.fetchWeather("${it.latitude},${it.longitude}")
        }
    }

    Scaffold(
        topBar = { WeatherTopBar(viewModel) },
        bottomBar = {
            BottomNavigationBar(
                selectedTab = selectedTab,
                onTabSelected = { selectedTab = it }
            )
        }
    ) { paddingValues ->
        Column(modifier = Modifier.padding(paddingValues)) {
            when (selectedTab) {
                0 -> WeatherScreen(weather)
                1 -> {
                    if (weather.value is NetworkResponse.Success) {
                        ForecastScreen((weather.value as NetworkResponse.Success<WeatherResponse>).data.forecast)
                    } else {
                        Column(
                            modifier = Modifier.fillMaxSize(),
                            verticalArrangement = Arrangement.Center,
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "Forecast data not available",
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
                2 -> {
                    if (weather.value is NetworkResponse.Success) {
                        val apiCondition = (weather.value as NetworkResponse.Success<WeatherResponse>)
                            .data.current.condition.text.lowercase()
                        val generalCondition = when {
                            apiCondition.contains("rain") -> "Rain"
                            apiCondition.contains("sunny") || apiCondition.contains("clear") -> "Sunny"
                            apiCondition.contains("cloud") -> "Cloudy"
                            apiCondition.contains("snow") || apiCondition.contains("cold") -> "Winter"
                            apiCondition.contains("storm") || apiCondition.contains("thunder") -> "Storm"
                            else -> "General"
                        }
                        MusicSuggestionsScreen(generalCondition)
                    }
                }
                3 -> {
                    val city = viewModel.currentCity
                    if (city != null) {
                        WeatherNewsScreen(city, viewModel.darkModeEnabled.observeAsState().value ?: false)
                    } else {
                        Column(
                            modifier = Modifier.fillMaxSize(),
                            verticalArrangement = Arrangement.Center,
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "Location data not available",
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun WeatherNewsScreen(city: String, isDarkMode: Boolean) {
    val context = LocalContext.current
    val query = "weather news ${city.lowercase()}"
    val url = "https://www.google.com/search?q=${Uri.encode(query)}&tbm=nws"

    AndroidView(
        factory = {
            WebView(context).apply {
                webViewClient = WebViewClient()
                settings.javaScriptEnabled = true

                // Dark mode configuration
                when {
                    WebViewFeature.isFeatureSupported(WebViewFeature.ALGORITHMIC_DARKENING) -> {
                        WebSettingsCompat.setAlgorithmicDarkeningAllowed(settings, isDarkMode)
                    }
                    WebViewFeature.isFeatureSupported(WebViewFeature.FORCE_DARK) -> {
                        WebSettingsCompat.setForceDark(
                            settings,
                            if (isDarkMode) WebSettingsCompat.FORCE_DARK_ON
                            else WebSettingsCompat.FORCE_DARK_OFF
                        )
                    }
                }

                // Preferred dark strategy
                if (WebViewFeature.isFeatureSupported(WebViewFeature.FORCE_DARK_STRATEGY)) {
                    WebSettingsCompat.setForceDarkStrategy(
                        settings,
                        WebSettingsCompat.DARK_STRATEGY_WEB_THEME_DARKENING_ONLY
                    )
                }

                loadUrl(url)
            }
        },
        modifier = Modifier.fillMaxSize(),
        update = { webView ->
            // Update dark mode without reloading
            when {
                WebViewFeature.isFeatureSupported(WebViewFeature.ALGORITHMIC_DARKENING) -> {
                    WebSettingsCompat.setAlgorithmicDarkeningAllowed(webView.settings, isDarkMode)
                }
                WebViewFeature.isFeatureSupported(WebViewFeature.FORCE_DARK) -> {
                    WebSettingsCompat.setForceDark(
                        webView.settings,
                        if (isDarkMode) WebSettingsCompat.FORCE_DARK_ON
                        else WebSettingsCompat.FORCE_DARK_OFF
                    )
                }
            }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WeatherTopBar(viewModel: WeatherViewModel) {
    var showSearch by remember { mutableStateOf(false) }
    var city by remember { mutableStateOf("") }
    var showMenu by remember { mutableStateOf(false) }
    val darkModeEnabled by viewModel.darkModeEnabled.observeAsState(false)
    val cachedCities by viewModel.cachedCities.observeAsState(setOf())
    var filteredCities by remember { mutableStateOf(cachedCities.toList()) }

    Column {
        TopAppBar(
            title = { Text("Weather", style = MaterialTheme.typography.titleLarge) },
            navigationIcon = {
                IconButton(onClick = { showMenu = true }) {
                    Icon(Icons.Default.Settings, contentDescription = "Settings")
                    DropdownMenu(
                        expanded = showMenu,
                        onDismissRequest = { showMenu = false }
                    ) {
                        DropdownMenuItem(
                            text = {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Text("Dark Mode")
                                    Spacer(Modifier.width(8.dp))
                                    Switch(
                                        checked = darkModeEnabled,
                                        onCheckedChange = { viewModel.toggleDarkMode(it) }
                                    )
                                }
                            },
                            onClick = { viewModel.toggleDarkMode(!darkModeEnabled) }
                        )
                    }
                }
            },
            actions = {
                if (!showSearch) {
                    IconButton(onClick = {
                        viewModel.loadCachedCities()
                        filteredCities = cachedCities.toList()
                        showSearch = true
                    }) {
                        Icon(Icons.Default.Search, contentDescription = "Search")
                    }
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer,
                titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                navigationIconContentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                actionIconContentColor = MaterialTheme.colorScheme.onPrimaryContainer
            )
        )

        if (showSearch) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp)
                    .background(MaterialTheme.colorScheme.surface)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TextField(
                        value = city,
                        onValueChange = {
                            city = it
                            filteredCities = cachedCities.filter { c ->
                                c.contains(city, ignoreCase = true)
                            }
                        },
                        placeholder = { Text("Enter city") },
                        modifier = Modifier
                            .weight(1f)
                            .padding(end = 8.dp),
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = MaterialTheme.colorScheme.surface,
                            unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                            focusedTextColor = MaterialTheme.colorScheme.onSurface,
                            unfocusedTextColor = MaterialTheme.colorScheme.onSurface
                        )
                    )
                    Button(
                        onClick = {
                            viewModel.fetchWeather(city)
                            showSearch = false
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary
                        )
                    ) {
                        Text("Go")
                    }
                }
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp)
                ) {
                    items(filteredCities.toList()) { cachedCity ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    city = cachedCity
                                    viewModel.fetchWeather(cachedCity)
                                    showSearch = false
                                }
                                .padding(horizontal = 16.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = cachedCity,
                                modifier = Modifier.weight(1f),
                                style = MaterialTheme.typography.bodyLarge
                            )
                            IconButton(
                                onClick = {
                                    viewModel.removeCachedCity(cachedCity)
                                    filteredCities = cachedCities.filter { it.contains(city, true) }
                                },
                                modifier = Modifier.size(16.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Close,
                                    contentDescription = "Remove",
                                    modifier = Modifier.size(16.dp),
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun BottomNavigationBar(selectedTab: Int, onTabSelected: (Int) -> Unit) {
    NavigationBar(
        containerColor = MaterialTheme.colorScheme.primaryContainer,
        contentColor = MaterialTheme.colorScheme.onPrimaryContainer
    ) {
        NavigationBarItem(
            icon = { Icon(Icons.Default.LocationOn, contentDescription = "Weather") },
            label = { Text("Weather") },
            selected = selectedTab == 0,
            onClick = { onTabSelected(0) },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = MaterialTheme.colorScheme.primary,
                selectedTextColor = MaterialTheme.colorScheme.primary,
                unselectedIconColor = MaterialTheme.colorScheme.onPrimaryContainer,
                unselectedTextColor = MaterialTheme.colorScheme.onPrimaryContainer
            )
        )
        NavigationBarItem(
            icon = { Icon(Icons.Default.DateRange, contentDescription = "Forecast") },
            label = { Text("Forecast") },
            selected = selectedTab == 1,
            onClick = { onTabSelected(1) },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = MaterialTheme.colorScheme.primary,
                selectedTextColor = MaterialTheme.colorScheme.primary,
                unselectedIconColor = MaterialTheme.colorScheme.onPrimaryContainer,
                unselectedTextColor = MaterialTheme.colorScheme.onPrimaryContainer
            )
        )
        NavigationBarItem(
            icon = {
                Icon(
                    imageVector = Icons.Filled.MusicNote,
                    contentDescription = "Music"
                )
            },
            label = { Text("Music") },
            selected = selectedTab == 2,
            onClick = { onTabSelected(2) },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = MaterialTheme.colorScheme.primary,
                selectedTextColor = MaterialTheme.colorScheme.primary,
                unselectedIconColor = MaterialTheme.colorScheme.onPrimaryContainer,
                unselectedTextColor = MaterialTheme.colorScheme.onPrimaryContainer
            )
        )
        NavigationBarItem(
            icon = { Icon(Icons.Default.Notifications, contentDescription = "News") },
            label = { Text("News") },
            selected = selectedTab == 3,
            onClick = { onTabSelected(3) },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = MaterialTheme.colorScheme.primary,
                selectedTextColor = MaterialTheme.colorScheme.primary,
                unselectedIconColor = MaterialTheme.colorScheme.onPrimaryContainer,
                unselectedTextColor = MaterialTheme.colorScheme.onPrimaryContainer
            )
        )
    }
}

@Composable
fun ErrorScreen(
    message: String = "Something went wrong.",
    onRetry: (() -> Unit)? = null
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = Icons.Default.Place,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.size(96.dp)
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = message,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            style = MaterialTheme.typography.titleMedium,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(16.dp))
        if (onRetry != null) {
            Button(onClick = onRetry) {
                Text("Retry")
            }
        }
    }
}

@Composable
fun WeatherScreen(weather: State<NetworkResponse<WeatherResponse>?>) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        item {
            when (val result = weather.value) {
                is NetworkResponse.Loading -> {
                    CircularProgressIndicator()
                }
                is NetworkResponse.Success -> {
                    WeatherCard(result.data)
                    Spacer(modifier = Modifier.height(16.dp))
                    AirQualitySummary(result.data.current.air_quality)
                    Spacer(modifier = Modifier.height(16.dp))
                    DrivingDifficulty(result.data.current)
                }
                is NetworkResponse.Error -> {
                    ErrorScreen(message = "Error: ${result.message}")
                }
                null -> {
                    ErrorScreen(message = "No data available")
                }
            }
        }
    }
}

@Composable
fun WeatherCard(data: WeatherResponse) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .shadow(8.dp, RoundedCornerShape(16.dp)),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Start,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.LocationOn,
                    contentDescription = "Location icon",
                    modifier = Modifier.size(32.dp),
                    tint = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "${data.location.name}, ${data.location.country}",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
            Spacer(modifier = Modifier.height(16.dp))
            AsyncImage(
                modifier = Modifier.size(120.dp),
                model = "https:${data.current.condition.icon}".replace("64x64", "128x128"),
                contentDescription = "Condition icon",
                contentScale = ContentScale.Fit
            )
            Text(
                text = "${data.current.temp_c}°C",
                fontSize = 48.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
            Text(
                text = data.current.condition.text,
                fontSize = 20.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(16.dp))
            WeatherInfoGrid(data)
        }
    }
}

@Composable
fun WeatherInfoGrid(data: WeatherResponse) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            WeatherKeyVal(
                icon = Icons.Default.WaterDrop,
                key = "Humidity",
                value = "${data.current.humidity}%"
            )
            WeatherKeyVal(
                icon = Icons.Default.Air,
                key = "Wind",
                value = "${data.current.wind_kph} km/h"
            )
        }
        Spacer(modifier = Modifier.height(8.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            WeatherKeyVal(
                icon = Icons.Default.LightMode,
                key = "UV Index",
                value = data.current.uv.toString()
            )
            WeatherKeyVal(
                icon = Icons.Default.Water,
                key = "Precipitation",
                value = "${data.current.precip_mm} mm"
            )
        }
        Spacer(modifier = Modifier.height(8.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            WeatherKeyVal(
                icon = Icons.Default.WbSunny,
                key = "Sunrise",
                value = data.forecast.forecastday[0].astro.sunrise
            )
            WeatherKeyVal(
                icon = Icons.Default.NightsStay,
                key = "Sunset",
                value = data.forecast.forecastday[0].astro.sunset
            )
        }
    }
}

@Composable
fun WeatherKeyVal(icon: ImageVector, key: String, value: String) {
    Row(
        modifier = Modifier.padding(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = key,
            modifier = Modifier.size(24.dp),
            tint = MaterialTheme.colorScheme.onSurface
        )
        Spacer(modifier = Modifier.width(8.dp))
        Column {
            Text(
                text = value,
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = key,
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
fun AirQualitySummary(airQuality: AirQualityX) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .shadow(8.dp, RoundedCornerShape(16.dp)),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.CloudQueue,
                    contentDescription = "Air Quality",
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Air Quality Index",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "US EPA Index: ${airQuality.us_epa_index}",
                fontSize = 16.sp,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = "PM2.5: ${airQuality.pm2_5} µg/m³",
                fontSize = 16.sp,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}

@Composable
fun DrivingDifficulty(current: Current) {
    val difficulty = when {
        current.condition.text.lowercase().contains("rain") -> "Moderate (Rain)"
        current.condition.text.lowercase().contains("snow") -> "High (Snow)"
        current.condition.text.lowercase().contains("storm") -> "Severe (Storm)"
        current.wind_kph > 50 -> "Moderate (High Winds)"
        else -> "Low (Clear)"
    }
    val difficultyColor = when {
        difficulty.contains("Severe") -> Color(0xFFFF0000)
        difficulty.contains("Moderate") -> Color(0xFFFFA500)
        else -> Color(0xFF4CAF50)
    }
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .shadow(8.dp, RoundedCornerShape(16.dp)),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.DirectionsCar,
                    contentDescription = "Driving Difficulty",
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Driving Difficulty",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = difficulty,
                fontSize = 16.sp,
                color = difficultyColor
            )
        }
    }
}

@Composable
fun ForecastScreen(forecast: Forecast) {
    var selectedDayIndex by remember { mutableStateOf(0) }
    val forecastDays = forecast.forecastday
    val selectedDay = forecastDays.getOrNull(selectedDayIndex)

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        selectedDay?.let { day ->
            LazyRow(modifier = Modifier.fillMaxWidth()) {
                items(day.hour) { hour ->
                    HourlyWeatherCard(hour)
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
            LazyRow(modifier = Modifier.fillMaxWidth()) {
                itemsIndexed(forecastDays) { index, forecastDay ->
                    DayCard(
                        date = forecastDay.date,
                        isSelected = index == selectedDayIndex,
                        onClick = { selectedDayIndex = index }
                    )
                }
            }
        } ?: run {
            Text(
                text = "Forecast data not available",
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )
        }
    }
}

@Composable
fun HourlyWeatherCard(hour: Hour) {
    Column(
        modifier = Modifier
            .padding(8.dp)
            .background(
                color = MaterialTheme.colorScheme.surfaceVariant,
                shape = RoundedCornerShape(8.dp)
            )
            .padding(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = hour.time.substringAfter(" "),
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        AsyncImage(
            modifier = Modifier.size(48.dp),
            model = "https:${hour.condition.icon}".replace("64x64", "128x128"),
            contentDescription = "Weather condition",
            contentScale = ContentScale.Fit
        )
        Text(
            text = "${hour.temp_c}°C",
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
fun DayCard(date: String, isSelected: Boolean, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .padding(4.dp)
            .background(
                if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant,
                RoundedCornerShape(8.dp)
            )
            .clickable { onClick() }
            .padding(8.dp)
    ) {
        Text(
            text = date,
            color = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}