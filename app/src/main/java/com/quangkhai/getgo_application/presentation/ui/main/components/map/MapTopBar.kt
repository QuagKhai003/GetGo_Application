package com.quangkhai.getgo_application.presentation.ui.main.components.map
import com.quangkhai.getgo_application.presentation.ui.main.components.magiccirlce.MagicCircleRoundButton
import com.quangkhai.getgo_application.presentation.ui.main.components.friendlist.FriendListPill
import com.quangkhai.getgo_application.presentation.ui.main.components.magiccirlce.MagicCirclePill
import com.quangkhai.getgo_application.presentation.ui.main.components.weather.WeatherPill

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.quangkhai.getgo_application.domain.model.Friend
import com.quangkhai.getgo_application.domain.model.Location
import com.quangkhai.getgo_application.domain.model.Weather

// The top area over the map: search bar + Friend-list / Magic-circle / Weather pills,
// with the search-result list. Swaps to the discover exit button while in discover mode.
@Composable
fun MapTopBar(
    searchState: TextFieldState,
    onSearch: () -> Unit,
    discoverMode: Boolean,
    friends: List<Friend>,
    checkedIds: Set<String>,
    onToggleFriend: (Friend) -> Unit,
    onEnterDiscover: () -> Unit,
    onExitDiscover: () -> Unit,
    weather: Weather?,
    onWeatherClick: () -> Unit,
    searchResults: List<Location>,
    onPickResult: (Location) -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .statusBarsPadding()
            .padding(horizontal = 14.dp, vertical = 12.dp)
    ) {
        AnimatedVisibility(
            visible = !discoverMode,
            enter = slideInVertically { -it } + fadeIn(),
            exit = slideOutVertically { -it } + fadeOut(),
            modifier = Modifier.fillMaxWidth()
        ) {
            Box(modifier = Modifier.fillMaxWidth()) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    MapSearchBar(
                        state = searchState,
                        onSearch = onSearch,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(Modifier.height(8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                        verticalAlignment = Alignment.Top
                    ) {
                        FriendListPill(
                            friends = friends,
                            checkedIds = checkedIds,
                            onToggle = onToggleFriend,
                            modifier = Modifier.weight(1f)
                        )
                        MagicCirclePill(onClick = onEnterDiscover)
                        WeatherPill(
                            weather = weather,
                            onClick = onWeatherClick,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }

                if (searchResults.isNotEmpty()) {
                    SearchResults(
                        results = searchResults,
                        onPickResult = onPickResult,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 54.dp)
                    )
                }
            }
        }

        AnimatedVisibility(
            visible = discoverMode,
            enter = slideInVertically { -it } + fadeIn(),
            exit = slideOutVertically { -it } + fadeOut(),
            modifier = Modifier.align(Alignment.TopCenter)
        ) {
            MagicCircleRoundButton(active = true, onClick = onExitDiscover)
        }
    }
}
