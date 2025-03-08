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
import coil3.compose.AsyncImage
import mc.project.weatherapp.api.WeatherResponse
import android.Manifest

// Activity
class MainActivity : ComponentActivity() {
    private lateinit var fusedLocationClient: com.google.android.gms.location.FusedLocationProviderClient
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        fusedLocationClient = com.google.android.gms.location.LocationServices.getFusedLocationProviderClient(this)
        // In MainActivity
        setContent {
            val viewModel: WeatherViewModel = viewModel()
            WeatherApp(viewModel, fusedLocationClient, this@MainActivity)
        }

    }
}

@Composable
fun WeatherApp(
    viewModel: WeatherViewModel,
    fusedLocationClient : com.google.android.gms.location.FusedLocationProviderClient,
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
                1 -> AirQualityScreen()
            }
        }
    }
}
@Composable
fun AirQualityScreen() {
    TODO("Not yet implemented")
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WeatherTopBar(viewModel: WeatherViewModel) {
    var showSearch by remember { mutableStateOf(false) }
    var city by remember { mutableStateOf("") }

    TopAppBar(
        title = { Text("Weather App") },
        actions = {
            if (showSearch) {
                TextField(
                    value = city,
                    onValueChange = { city = it },
                    placeholder = { Text("Enter city") },
                    modifier = Modifier.padding(8.dp)
                )
                Button(onClick = { viewModel.fetchWeather(city); showSearch = false }) {
                    Text("Go")
                }
            } else {
                IconButton(onClick = { showSearch = true }) {
                    Icon(Icons.Default.Search, contentDescription = "Search")
                }
            }
        }
    )
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
    }
}

@Composable
fun WeatherScreen(weather: androidx.compose.runtime.State<NetworkResponse<WeatherResponse>?>) {
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
            contentDescription = "Condition icon"
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
                    WeatherKeyVal("Humidity",data.current.humidity)
                    WeatherKeyVal("Wind Speed",data.current.wind_kph+" km/h")
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceAround
                ) {
                    WeatherKeyVal("UV",data.current.uv)
                    WeatherKeyVal("Participation",data.current.precip_mm+" mm")
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
