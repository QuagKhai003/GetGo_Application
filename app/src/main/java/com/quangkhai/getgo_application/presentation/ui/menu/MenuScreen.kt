package com.quangkhai.getgo_application.presentation.ui.menu

import android.Manifest
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.quangkhai.getgo_application.data.local.getCurrentLatLong
import com.quangkhai.getgo_application.data.local.hasLocationPermission
import com.quangkhai.getgo_application.presentation.viewmodel.WeatherViewModel
import com.quangkhai.getgo_application.ui.theme.GetGoTheme
import com.quangkhai.getgo_application.ui.theme.GetGo_ApplicationTheme
import kotlinx.coroutines.launch

private val GreenBackground = Color(0xFF1B7426)

// main/home screen: a grid of white cards on the green background.
// Same 14.dp padding/spacing as the map screen.
@Composable
fun MenuScreen(
    onLetsGetGo: () -> Unit,
    onFriendList: () -> Unit,
    onUserSetting: () -> Unit,
    onSplitBill: () -> Unit,
    weatherViewModel: WeatherViewModel = viewModel()
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val weather by weatherViewModel.weather.collectAsState()

    fun loadWeather() {
        scope.launch {
            val location = getCurrentLatLong(context)
            if (location != null) weatherViewModel.load(location.first, location.second)
        }
    }

    val locationPermissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (granted) loadWeather()
    }

    // ask for location the moment the menu shows (right after splash), then load weather
    LaunchedEffect(Unit) {
        if (hasLocationPermission(context)) {
            loadWeather()
        } else {
            locationPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(GreenBackground)
            .statusBarsPadding()
            .navigationBarsPadding()
            .padding(14.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            MenuCard("Let's\nGet Go", onLetsGetGo, Modifier.weight(1f), backgroundColor = GetGoTheme.colors.quaternary)
            WeatherMenuCard(
                weather = weather,
                onClick = {
                    // no navigation — tap reloads the weather
                    scope.launch {
                        getCurrentLatLong(context)?.let { (lat, long) ->
                            weatherViewModel.load(lat, long)
                        }
                    }
                },
                modifier = Modifier.weight(1f)
            )
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            MenuCard("Your\nFriend List", onFriendList, Modifier.weight(1f))
            MenuCard("User\nSetting", onUserSetting, Modifier.weight(1f))
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            MenuCard("Split\nYour Bills", onSplitBill, Modifier.weight(1f), backgroundColor = MaterialTheme.colorScheme.tertiary, textColor = MaterialTheme.colorScheme.surface)
            Spacer(Modifier.weight(1f))
        }
    }
}

@Preview(showBackground = true)
@Composable
fun MenuScreenPreview() {
    GetGo_ApplicationTheme {
        MenuScreen(
            onLetsGetGo = {},
            onFriendList = {},
            onUserSetting = {},
            onSplitBill = {}
        )
    }
}
