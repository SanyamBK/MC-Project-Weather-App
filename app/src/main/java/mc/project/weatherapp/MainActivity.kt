package mc.project.weatherapp

import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import mc.project.weatherapp.api.NetworkResponse
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.core.app.ActivityCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.media3.common.util.Log
import android.Manifest
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Settings
import androidx.compose.runtime.State
import androidx.compose.ui.res.painterResource
import androidx.media3.common.util.UnstableApi
import coil.compose.AsyncImage
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import mc.project.weatherapp.api.AirQuality
import mc.project.weatherapp.api.Forecast
import mc.project.weatherapp.api.Hour
import android.location.Geocoder
import android.net.Uri
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Place
//import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.platform.LocalContext
import mc.project.weatherapp.api.WeatherResponse
import mc.project.weatherapp.ui.theme.WeatherTheme
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Switch
import androidx.compose.ui.res.painterResource
import androidx.compose.material3.Switch
import androidx.compose.material3.DropdownMenu
import androidx.compose.ui.viewinterop.AndroidView
import java.util.Locale
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.layout.ContentScale

// Activity
class MainActivity : ComponentActivity() {
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        // In MainActivity
//        setContent {
//            val viewModel: WeatherViewModel = viewModel()
//            WeatherApp(viewModel, fusedLocationClient, this@MainActivity)
//        }

        setContent {
            val viewModel: WeatherViewModel = viewModel()
            val isDarkMode by viewModel.darkModeEnabled.observeAsState(false)

            WeatherTheme(
                darkTheme = isDarkMode, // Use the observed dark mode state
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
    fusedLocationClient : FusedLocationProviderClient,
    activity: ComponentActivity // Accept the activity context
) {
    var selectedTab by remember { mutableStateOf(0) }
    val weather = viewModel.weatherState.observeAsState()
    var currentLocation by remember { mutableStateOf<Location?>(null) }

    // Location permission launcher
    val locationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { isGranted ->
            if (isGranted) {
                // Permission granted, get location
                if (ActivityCompat.checkSelfPermission(
                        activity, // Use the passed activity context
                        Manifest.permission.ACCESS_FINE_LOCATION
                    ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                        activity,
                        Manifest.permission.ACCESS_COARSE_LOCATION
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
            } else {
                // Permission denied
                Log.e("Location", "Permission denied")
            }
        }
    )


    // Request location permission on launch
    LaunchedEffect(key1 = true) {
        locationPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
    }

    // Fetch weather for current location if available
    LaunchedEffect(key1 = currentLocation) {
        currentLocation?.let {
            viewModel.fetchWeather(it.latitude.toString() + "," + it.longitude.toString())
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
                    // Check if weather data is available and in the Success state
                    if (weather.value is NetworkResponse.Success) {
                        AirQualityScreen((weather.value as NetworkResponse.Success<WeatherResponse>).data.current.air_quality)
                    } else {
                        // Handle the case where weather data is not available or is not in the Success state
                        Column(
                            modifier = Modifier.fillMaxSize(),
                            verticalArrangement = Arrangement.Center,
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text("AQI data not available", color = Color.Gray)
                        }
                    }
                }
                2 -> {
                    if (weather.value is NetworkResponse.Success) {
                        ForecastScreen((weather.value as NetworkResponse.Success<WeatherResponse>).data.forecast)
                    } else {
                        Column(
                            modifier = Modifier.fillMaxSize(),
                            verticalArrangement = Arrangement.Center,
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text("Forecast data not available", color = Color.Gray)
                        }
                    }
                }
                3 -> {
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
                4 -> {
                    val city = viewModel.currentCity
                    if (city != null) {
                        WeatherNewsScreen(city)
                    } else {
                        Column(
                            modifier = Modifier.fillMaxSize(),
                            verticalArrangement = Arrangement.Center,
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text("Location data not available", color = Color.Gray)
                        }
                    }
                }
            }
        }
    }
}


@Composable
fun WeatherNewsScreen(city: String) {
    val context = LocalContext.current
    val query = "weather news ${city.lowercase()}"
    val url = "https://www.google.com/search?q=${Uri.encode(query)}&tbm=nws"

    AndroidView(
        factory = {
            WebView(context).apply {
                webViewClient = WebViewClient()
                settings.javaScriptEnabled = true
                loadUrl(url)
            }
        },
        modifier = Modifier.fillMaxSize()
    )
}

@Composable
fun AirQualityScreen(airQuality: AirQuality) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "Air Quality Index", fontSize = 24.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(16.dp))
        AirQualityParameter(label = "CO (Carbon Monoxide)", value = airQuality.co.toString())
        AirQualityParameter(label = "NO₂ (Nitrogen Dioxide)", value = airQuality.no2.toString())
        AirQualityParameter(label = "O₃ (Ozone)", value = airQuality.o3.toString())
        AirQualityParameter(label = "PM10 (Particulate Matter <10µm)", value = airQuality.pm10.toString())
        AirQualityParameter(label = "PM2.5 (Particulate Matter <2.5µm)", value = airQuality.pm2_5.toString())
        AirQualityParameter(label = "SO₂ (Sulfur Dioxide)", value = airQuality.so2.toString())
        AirQualityParameter(label = "US EPA Index", value = airQuality.us_epa_index.toString())
        AirQualityParameter(label = "GB DEFRA Index", value = airQuality.gb_defra_index.toString())
    }
}

@Composable
fun AirQualityParameter(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = label, fontWeight = FontWeight.SemiBold)
        Text(text = value)
    }
}


//@OptIn(ExperimentalMaterial3Api::class)
//@Composable
//fun WeatherTopBar(viewModel: WeatherViewModel) {
//    var showSearch by remember { mutableStateOf(false) }
//    var city by remember { mutableStateOf("") }
//
//    TopAppBar(
//        title = { Text("Weather App") },
//        actions = {
//            if (showSearch) {
//                TextField(
//                    value = city,
//                    onValueChange = { city = it },
//                    placeholder = { Text("Enter city") },
//                    modifier = Modifier.padding(8.dp)
//                )
//                Button(onClick = { viewModel.fetchWeather(city); showSearch = false }) {
//                    Text("Go")
//                }
//            } else {
//                IconButton(onClick = { showSearch = true }) {
//                    Icon(Icons.Default.Search, contentDescription = "Search")
//                }
//            }
//        }
//    )
//}
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
            title = { Text("Weather") },
            navigationIcon = {
                IconButton(onClick = { showMenu = true }) {
                    Icon(Icons.Default.Settings, contentDescription = "Settings")
                    //                Icon(
                    //                    painter = painterResource(R.drawable.img), // Add your settings icon
                    //                    contentDescription = "Settings"
                    //                )
                    DropdownMenu(
                        expanded = showMenu,
                        onDismissRequest = { showMenu = false }
                    ) {
                        DropdownMenuItem(
                            text = {
                                Row(verticalAlignment = Alignment.CenterVertically) {
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
            }
        )

        if (showSearch) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp)
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
                            .padding(end = 8.dp)
                    )
                    Button(onClick = {
                        viewModel.fetchWeather(city)
                        showSearch = false
                    }) {
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
                                    tint = Color.Gray
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
    NavigationBar {
        NavigationBarItem(
            icon = { Icon(Icons.Default.LocationOn, contentDescription = "Weather") },
            label = { Text("Weather") },
            selected = selectedTab == 0,
            onClick = { onTabSelected(0) }
        )
        NavigationBarItem(
            icon = { Icon(Icons.Default.Search, contentDescription = "Air Quality") },
            label = { Text("Air Quality") },
            selected = selectedTab == 1,
            onClick = { onTabSelected(1) }
        )
        NavigationBarItem(
            icon = { Icon(Icons.Default.DateRange, contentDescription = "Forecast") },
            label = { Text("Forecast") },
            selected = selectedTab == 2,
            onClick = { onTabSelected(2) }
        )
        NavigationBarItem(
            icon = { Icon(Icons.Default.Search, "Music") }, // Replace with music icon
            label = { Text("Mausam Beats") },
            selected = selectedTab == 3,
            onClick = { onTabSelected(3) }
        )
        NavigationBarItem(
            icon = { Icon(Icons.Default.Notifications, contentDescription = "News") },
            label = { Text("News") },
            selected = selectedTab == 4,
            onClick = { onTabSelected(4) }
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
            tint = Color.Gray,
            modifier = Modifier.size(96.dp)
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = message,
            color = Color.Gray,
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
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        when (val result = weather.value) {
            is NetworkResponse.Loading -> {
                CircularProgressIndicator()
            }

            is NetworkResponse.Success -> {
                WeatherCard(result.data)
            }

            is NetworkResponse.Error -> {
                Text(text = "Error: ${result.message}", color = Color.Red)
            }

            null -> {}
        }
    }
}



@Composable
fun WeatherCard(data: WeatherResponse) {

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Start,
            verticalAlignment = Alignment.Bottom
        ) {
            Icon(
                imageVector = Icons.Default.LocationOn,
                contentDescription = "Location icon",
                modifier = Modifier.size(40.dp)
            )
            Text(text = data.location.name, fontSize = 30.sp)
            Spacer(modifier = Modifier.width(8.dp))
            Text(text = data.location.country, fontSize = 18.sp, color = Color.Gray)
        }

        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = " ${data.current.temp_c} ° c",
            fontSize = 56.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )

        AsyncImage(
            modifier = Modifier.size(160.dp),
            model = "https:${data.current.condition.icon}".replace("64x64","128x128"),
            contentDescription = "Condition icon",
        )

        Text(
            text = data.current.condition.text,
            fontSize = 20.sp,
            textAlign = TextAlign.Center,
            color = Color.Gray
        )
        Spacer(modifier = Modifier.height(16.dp))
        Card {
            Column(
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceAround
                ) {
                    WeatherKeyVal("Humidity", data.current.humidity.toString())
                    WeatherKeyVal("Wind Speed",data.current.wind_kph.toString() +" km/h")
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceAround
                ) {
                    WeatherKeyVal("UV", data.current.uv.toString())
                    WeatherKeyVal("Participation",data.current.precip_mm.toString() +" mm")
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceAround
                ) {
                    WeatherKeyVal("Local Time",data.location.localtime.split(" ")[1])
                    WeatherKeyVal("Local Date",data.location.localtime.split(" ")[0])
                }
            }
        }



    }

}

@Composable
fun WeatherKeyVal(key : String, value : String) {
    Column(
        modifier = Modifier.padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = value, fontSize = 24.sp, fontWeight = FontWeight.Bold)
        Text(text = key, fontWeight = FontWeight.SemiBold, color = Color.Gray)
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
            Text("Forecast data not available", color = Color.Gray, modifier = Modifier.align(Alignment.CenterHorizontally))
        }
    }
}

//@Composable
//fun HourlyWeatherCard(hour: Hour) {
//    Column(
//        modifier = Modifier.padding(8.dp).background(Color.LightGray, RoundedCornerShape(8.dp)).padding(8.dp),
//        horizontalAlignment = Alignment.CenterHorizontally
//    ) {
//        Text(hour.time.substringAfter(" "), fontWeight = FontWeight.Bold)
//        AsyncImage(
//            modifier = Modifier.size(160.dp),
//            model = "https:${hour.condition.icon}",
//            contentDescription = "Condition icon"
//        )
//        Text("${hour.temp_c}°C")
//
//    }
//}
//@Composable
//fun HourlyWeatherCard(hour: Hour) {
//    Column(
//        modifier = Modifier
//            .padding(8.dp)
//            .background(
//                color = MaterialTheme.colorScheme.surfaceVariant,
//                shape = RoundedCornerShape(8.dp)
//            )
//            .padding(8.dp),
//        horizontalAlignment = Alignment.CenterHorizontally
//    ) {
//        Text(
//            text = hour.time.substringAfter(" "),
//            fontWeight = FontWeight.Bold,
//            color = MaterialTheme.colorScheme.onSurfaceVariant
//        )
//        AsyncImage(
//            modifier = Modifier.size(160.dp),
//            model = "https:${hour.condition.icon}",
//            contentDescription = "Condition icon"
//        )
//        Text(
//            text = "${hour.temp_c}°C",
//            color = MaterialTheme.colorScheme.onSurfaceVariant
//        )
//    }
//}

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

        // Weather icon
        val iconRes = when {
            hour.condition.text.contains("sun", ignoreCase = true) -> R.drawable.weather_sunny
            hour.condition.text.contains("rain", ignoreCase = true) -> R.drawable.weather_pouring
            hour.condition.text.contains("cloud", ignoreCase = true) -> R.drawable.weather_cloudy
            hour.condition.text.contains("snow", ignoreCase = true) -> R.drawable.weather_snowy_heavy
            hour.condition.text.contains("thunder", ignoreCase = true) -> R.drawable.weather_dust
            else -> R.drawable.weather_default
        }

        Image(
            painter = painterResource(id = iconRes),
            contentDescription = "Weather condition",
            modifier = Modifier.size(48.dp),
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
            .background(if (isSelected) Color.Blue else Color.Gray, RoundedCornerShape(8.dp))
            .clickable { onClick() }
            .padding(8.dp)
    ) {
        Text(date, color = Color.White)
    }
}