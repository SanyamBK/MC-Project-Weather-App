package mc.project.weatherapp.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LightColorScheme = lightColorScheme(
    primary = Color(0xFF0288D1),
    onPrimary = Color(0xFFFFFFFF),
    primaryContainer = Color(0xFFB3E5FC),
    onPrimaryContainer = Color(0xFF01579B),
    surface = Color(0xFFFFFFFF),
    onSurface = Color(0xFF000000),
    onSurfaceVariant = Color(0xFF616161),
    secondary = Color(0xFF4CAF50),
    onSecondary = Color(0xFFFFFFFF),
    background = Color(0xFFF5F5F5),
    onBackground = Color(0xFF000000)
)

private val DarkColorScheme = darkColorScheme(
    primary = Color(0xFF4FC3F7),
    onPrimary = Color(0xFF000000),
    primaryContainer = Color(0xFF000000), // Black for header/footer
    onPrimaryContainer = Color(0xFFFFFFFF), // White for text/icons
    surface = Color(0xFF212121),
    onSurface = Color(0xFFFFFFFF),
    onSurfaceVariant = Color(0xFFB0BEC5),
    secondary = Color(0xFF81C784),
    onSecondary = Color(0xFF000000),
    background = Color(0xFF121212),
    onBackground = Color(0xFFFFFFFF)
)

@Composable
fun WeatherTheme(
    darkTheme: Boolean = false,
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) {
        DarkColorScheme
    } else {
        LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}