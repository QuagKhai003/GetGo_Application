package com.quangkhai.getgo_application.presentation.ui.main

import android.Manifest
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.foundation.text.input.setTextAndPlaceCursorAtEnd
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.quangkhai.getgo_application.presentation.ui.main.components.BottomRightButtons
import com.quangkhai.getgo_application.presentation.ui.main.components.DiscoverButton
import com.quangkhai.getgo_application.presentation.ui.main.components.DiscoverCircle
import com.quangkhai.getgo_application.presentation.ui.main.components.DiscoverOverlayHost
import com.quangkhai.getgo_application.presentation.ui.main.components.FairSpotCategoryDialog
import com.quangkhai.getgo_application.presentation.ui.main.components.FairSpotModal
import com.quangkhai.getgo_application.presentation.ui.main.components.FairMemberBox
import com.quangkhai.getgo_application.presentation.ui.main.components.FairSpotSheet
import com.quangkhai.getgo_application.presentation.ui.main.components.FindASpotButton
import com.quangkhai.getgo_application.presentation.ui.main.components.FriendListPill
import com.quangkhai.getgo_application.presentation.ui.main.components.MagicCirclePill
import com.quangkhai.getgo_application.presentation.ui.main.components.WeatherHistoryDialog
import com.quangkhai.getgo_application.presentation.ui.main.components.WeatherPill
import com.quangkhai.getgo_application.presentation.ui.main.components.MapSearchBar
import com.quangkhai.getgo_application.presentation.ui.main.components.SearchResults
import com.quangkhai.getgo_application.presentation.ui.main.components.OsmMapView
import com.quangkhai.getgo_application.presentation.ui.main.components.SearchThisAreaButton
import com.quangkhai.getgo_application.presentation.ui.main.components.BottomLeftButtons
import com.quangkhai.getgo_application.presentation.ui.main.components.PlaceDetailSheet
import com.quangkhai.getgo_application.data.local.getCurrentLatLong
import com.quangkhai.getgo_application.data.local.hasLocationPermission
import com.quangkhai.getgo_application.domain.model.BillGroup
import com.quangkhai.getgo_application.domain.model.Location
import com.quangkhai.getgo_application.presentation.ui.split.AddBillDialog
import com.quangkhai.getgo_application.presentation.ui.split.GroupPickerDialog
import com.quangkhai.getgo_application.domain.usecase.map.CircleDiscoverUseCase
import com.quangkhai.getgo_application.domain.usecase.map.fairCenter
import com.quangkhai.getgo_application.domain.usecase.map.haversineMeters
import com.quangkhai.getgo_application.presentation.viewmodel.CircleDiscoverViewModel
import com.quangkhai.getgo_application.presentation.viewmodel.MapViewModel
import com.quangkhai.getgo_application.presentation.viewmodel.UserViewModel
import com.quangkhai.getgo_application.presentation.viewmodel.WeatherViewModel
import kotlin.math.roundToInt
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

// I have adapted Claude Opus 4.8 Generated Code for only transforming my UI from Figma into Kotlin Compose
// This code is not orginally generated.
// It is rewritten with semantic name conventions and reused pre-existing codes
@Composable
fun MainScreen(
    onHome: () -> Unit = {},
    mapViewModel: MapViewModel = viewModel(),
    circleDiscoverViewModel: CircleDiscoverViewModel = viewModel(),
    userViewModel: UserViewModel = viewModel(),
    weatherViewModel: WeatherViewModel = viewModel()
) {
    val searchResults by mapViewModel.searchResults.collectAsState()
    val pickedLocation by mapViewModel.pickedLocation.collectAsState()
    val route by mapViewModel.routePath.collectAsState()
    val discoveredPlaces by circleDiscoverViewModel.discoveredPlaces.collectAsState()
    val discovering by circleDiscoverViewModel.discovering.collectAsState()
    val weather by weatherViewModel.weather.collectAsState()
    val weatherHistory by weatherViewModel.history.collectAsState()
    val currentUser by userViewModel.currentUser.collectAsState()

    val searchState = rememberTextFieldState()
    var isMenuOpen by remember { mutableStateOf(false) }
    var isSheetExpanded by remember { mutableStateOf(false) }
    var favoritesActive by remember { mutableStateOf(false) }
    var myLocationActive by remember { mutableStateOf(false) }
    val checkedFriendIds = remember { mutableStateListOf<String>() }
    var showFairSpotModal by remember { mutableStateOf(false) }
    var showCategoryDialog by remember { mutableStateOf(false) }
    var fairSpotActive by remember { mutableStateOf(false) }
    // true = user positions the search circle themselves; false = auto fair-centre
    var pickOwnArea by remember { mutableStateOf(false) }
    var chosenSpot by remember { mutableStateOf<Location?>(null) }
    var userLocation by remember { mutableStateOf<Pair<Double, Double>?>(null) }
    var fairTerms by remember { mutableStateOf("") }
    var fairSearchCenter by remember { mutableStateOf<Pair<Double, Double>?>(null) }
    var circleVisible by remember { mutableStateOf(true) }
    var recenterZoom by remember { mutableStateOf(16.0) }
    var recenterInstant by remember { mutableStateOf(false) }
    var pendingFairSearch by remember { mutableStateOf(false) }
    var fitPoints by remember { mutableStateOf<List<Pair<Double, Double>>>(emptyList()) }
    var fitKey by remember { mutableIntStateOf(0) }
    // "add bill from map": the place to bill, and the group chosen to add it to
    var billPlace by remember { mutableStateOf<Location?>(null) }
    var billGroup by remember { mutableStateOf<BillGroup?>(null) }
    var showWeatherHistory by remember { mutableStateOf(false) }
    // forces the first fair search onto the exact fair centre (projection not settled yet)
    var searchCenterOverride by remember { mutableStateOf<Pair<Double, Double>?>(null) }

    // --- circle discovery state ---
    var discoverMode by remember { mutableStateOf(false) }
    var discoverTrigger by remember { mutableIntStateOf(0) }
    var discoverTerm by remember { mutableStateOf("") }
    var containerSize by remember { mutableStateOf(IntSize.Zero) }
    var circleCenter by remember { mutableStateOf<Offset?>(null) }
    var isDraggingCircle by remember { mutableStateOf(false) }
    val selectedTerms = remember { mutableStateListOf<String>() }

    val density = LocalDensity.current
    val radiusPx = with(density) { (120.dp).toPx() }
    val circleDiameter = (120.dp) * 2

    val context = LocalContext.current
    val focusManager = LocalFocusManager.current
    val scope = rememberCoroutineScope()
    var recenterTarget by remember { mutableStateOf<Pair<Double, Double>?>(null) }
    var recenterTrigger by remember { mutableIntStateOf(0) }

    // run discovery for the current circle + checked categories, then dismiss the overlay
    fun runDiscover() {
        if (selectedTerms.isEmpty()) return
        discoverTerm = selectedTerms.joinToString("|")
        discoverTrigger++
        discoverMode = false     // dismiss circle + tooltip, keep result markers
    }

    // Claude Opus 4.8 Generated Code for removing stuck focus on the search upon initial tap
    fun dismiss(action: () -> Unit): () -> Unit = { focusManager.clearFocus(); action() }

    fun enterDiscover() {
        discoverMode = true
        selectedTerms.clear()
        circleCenter = Offset(containerSize.width / 2f, containerSize.height / 2f)
        isDraggingCircle = false
        circleDiscoverViewModel.clearDiscovered()
    }

    fun exitDiscover() {
        discoverMode = false
        circleDiscoverViewModel.clearDiscovered()
    }

    fun locateUser(recenter: Boolean) {
        scope.launch {
            val currentLocation = getCurrentLatLong(context)
            if (currentLocation == null) {
                if (recenter) {
                    android.widget.Toast.makeText(
                        context,
                        "Couldn't get your location. On the emulator, set one in Extended controls > Location.",
                        android.widget.Toast.LENGTH_LONG
                    ).show()
                }
                return@launch
            }
            mapViewModel.setRegion(currentLocation.first, currentLocation.second)
            if (recenter) {
                recenterInstant = false
                recenterZoom = 16.0
                recenterTarget = currentLocation
                recenterTrigger++
                mapViewModel.pinAt(currentLocation.first, currentLocation.second)
            }
        }
    }

    val locationPermissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (granted) locateUser(recenter = true)
    }

    LaunchedEffect(Unit) {
        if (hasLocationPermission(context)) {
            locateUser(recenter = false)
        }
    }

    // fetch weather from the device location on open, then every 30 minutes, in the background
    LaunchedEffect(Unit) {
        while (true) {
            if (hasLocationPermission(context)) {
                val location = getCurrentLatLong(context)
                if (location != null) weatherViewModel.load(location.first, location.second)
            }
            delay(30 * 60 * 1000L)
        }
    }

    LaunchedEffect(searchState) {
        snapshotFlow { searchState.text.toString() }
            .collect { mapViewModel.onQueryChange(it) }
    }

    // when a fair-spot search finishes, recenter the map on the circle + snap it to screen centre
    LaunchedEffect(discovering) {
        if (fairSpotActive && !discovering) {
            fairSearchCenter?.let {
                recenterTarget = it
                recenterTrigger++
                circleCenter = Offset(containerSize.width / 2f, containerSize.height / 2f)
            }
        }
    }

    val peekSheetHeight = 128.dp
    val fullSheetHeight = 300.dp
    val targetSheetHeight = when {
        fairSpotActive && chosenSpot != null -> if (isSheetExpanded) fullSheetHeight else peekSheetHeight
        pickedLocation == null -> 0.dp
        isSheetExpanded -> fullSheetHeight
        else -> peekSheetHeight
    }
    val sheetHeight by animateDpAsState(targetValue = targetSheetHeight, label = "sheetHeight")

    // circle markers: friends + my locations (skip 0,0 = no real coordinate)
    val savedPlaces = remember(currentUser, checkedFriendIds.toList(), myLocationActive) {
        val list = mutableListOf<Location>()
        val checkedFriends = (currentUser?.friends ?: emptyList()).filter { it.id != null && it.id in checkedFriendIds }
        list.addAll(checkedFriends.map { it.location })
        if (myLocationActive) list.addAll(currentUser?.myLocations ?: emptyList())
        list.filter { it.lat != 0.0 || it.long != 0.0 }
    }

    // star markers: favorites
    val favoritePlaces = remember(currentUser, favoritesActive) {
        if (favoritesActive) (currentUser?.favorites ?: emptyList()).filter { it.lat != 0.0 || it.long != 0.0 }
        else emptyList()
    }

    // trip members for the fair-spot calc: checked friends + you
    val tripMembers = remember(currentUser, checkedFriendIds.toList(), userLocation) {
        val list = (currentUser?.friends ?: emptyList())
            .filter { it.id != null && it.id in checkedFriendIds }
            .map { it.name to it.location }
            .toMutableList()
        userLocation?.let { list.add("You" to Location(id = null, name = "You", lat = it.first, long = it.second, address = "")) }
        list
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .onSizeChanged { containerSize = it }
    ) {

        Box(
            modifier = Modifier
                .fillMaxSize()
                .then(if (showFairSpotModal) Modifier.blur(16.dp) else Modifier)
        ) {

        OsmMapView(
            pickedLocation = pickedLocation,
            recenterTarget = recenterTarget,
            recenterKey = recenterTrigger,
            recenterZoom = recenterZoom,
            recenterInstant = recenterInstant,
            onRecentered = {
                if (pendingFairSearch) {
                    discoverTerm = fairTerms
                    discoverTrigger++
                    pendingFairSearch = false
                }
            },
            onMapTap = { latitude, longitude ->
                focusManager.clearFocus()
                if (!discoverMode && !fairSpotActive) mapViewModel.pinAt(latitude, longitude)
            },
            onMapLongPress = { _, _ ->
                focusManager.clearFocus()
            },
            onPinMoved = { latitude, longitude -> mapViewModel.pinAt(latitude, longitude) },
            onDeletePin = { mapViewModel.clearPicked(); mapViewModel.clearRoute() },
            recenterOnPick = !fairSpotActive,
            discoverPlaces = discoveredPlaces,
            discoverCenter = circleCenter,
            discoverRadiusPx = radiusPx,
            discoverTrigger = discoverTrigger,
            overrideSearchCenter = searchCenterOverride,
            onDiscoverArea = { latitude, longitude, radiusMeters ->
                circleDiscoverViewModel.discover(discoverTerm, latitude, longitude, radiusMeters)
                if (fairSpotActive) fairSearchCenter = latitude to longitude
                searchCenterOverride = null
            },
            onDiscoveredTap = { location ->
                focusManager.clearFocus()
                if (fairSpotActive) {
                    chosenSpot = location
                } else {
                    mapViewModel.pickDiscoveredPlace(location)
                }
            },
            savedPlaces = savedPlaces,
            onSavedTap = { location -> focusManager.clearFocus(); if (!fairSpotActive) mapViewModel.pick(location) },
            favoritePlaces = favoritePlaces,
            onFavoriteTap = { location -> focusManager.clearFocus(); if (!fairSpotActive) mapViewModel.pick(location) },
            fairSpot = chosenSpot,
            fairSpotLines = if (chosenSpot != null) tripMembers.map { it.second } else emptyList(),
            fitPoints = fitPoints,
            fitKey = fitKey,
            routePoints = route,
            modifier = Modifier.fillMaxSize()
        )

        // top area swaps: search bar + Magic Circle pill OUT, exit ✕ IN
        Box(
            modifier = Modifier
                .align(Alignment.TopCenter)
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
                            onSearch = { mapViewModel.search(searchState.text.toString()) },
                            modifier = Modifier.fillMaxWidth()
                        )
                        Spacer(Modifier.height(8.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(4.dp),
                            verticalAlignment = Alignment.Top
                        ) {
                            FriendListPill(
                                friends = currentUser?.friends ?: emptyList(),
                                checkedIds = checkedFriendIds.toSet(),
                                onToggle = { friend ->
                                    friend.id?.let {
                                        if (it in checkedFriendIds) checkedFriendIds.remove(it)
                                        else checkedFriendIds.add(it)
                                    }
                                },
                                modifier = Modifier.weight(1f)
                            )
                            MagicCirclePill(onClick = dismiss { enterDiscover() })
                            WeatherPill(
                                weather = weather,
                                onClick = {
                                    scope.launch {
                                        getCurrentLatLong(context)?.let { (lat, long) ->
                                            weatherViewModel.loadHistory(lat, long)
                                        }
                                    }
                                    showWeatherHistory = true
                                },
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }

                    if (searchResults.isNotEmpty()) {
                        SearchResults(
                            results = searchResults,
                            onPickResult = { location ->
                                mapViewModel.pick(location)
                                val filledText = if (location.address.isBlank()) location.name
                                    else "${location.name} - ${location.address}"
                                searchState.setTextAndPlaceCursorAtEnd(filledText)
                                isSheetExpanded = false
                            },
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
                DiscoverButton(active = true, onClick = dismiss { exitDiscover() })
            }
        }

        val fairCirclePos = circleCenter
        if (fairSpotActive && !discovering && fairCirclePos != null && circleVisible) {
            DiscoverCircle(
                diameter = circleDiameter,
                isDragging = isDraggingCircle,
                onDragStart = { isDraggingCircle = true },
                onDrag = { dx, dy ->
                    val cur = circleCenter
                    if (cur != null) {
                        circleCenter = Offset(
                            (cur.x + dx).coerceIn(0f, containerSize.width.toFloat()),
                            (cur.y + dy).coerceIn(0f, containerSize.height.toFloat())
                        )
                    }
                },
                onDragEnd = {
                    isDraggingCircle = false
                },
                modifier = Modifier.offset {
                    IntOffset(
                        (fairCirclePos.x - radiusPx).roundToInt(),
                        (fairCirclePos.y - radiusPx).roundToInt()
                    )
                }
            )
        }

        if (fairSpotActive && !discovering && fairCirclePos != null && circleVisible) {
            SearchThisAreaButton(
                onClick = {
                    discoverTerm = fairTerms
                    discoverTrigger++
                },
                modifier = Modifier.offset {
                    IntOffset(
                        (fairCirclePos.x - 44.dp.toPx()).roundToInt(),
                        (fairCirclePos.y + radiusPx + 10.dp.toPx()).roundToInt()
                    )
                }
            )
        }

        val center = circleCenter
        if (discoverMode && center != null) {
            DiscoverOverlayHost(
                center = center,
                radiusPx = radiusPx,
                circleDiameter = circleDiameter,
                isDragging = isDraggingCircle,
                onDragStart = { isDraggingCircle = true },
                onDrag = { dx, dy ->
                    val current = circleCenter
                    if (current != null) {
                        circleCenter = Offset(
                            (current.x + dx).coerceIn(0f, containerSize.width.toFloat()),
                            (current.y + dy).coerceIn(0f, containerSize.height.toFloat())
                        )
                    }
                },
                onDragEnd = { isDraggingCircle = false },
                containerWidth = containerSize.width,
                selectedTerms = selectedTerms.toSet(),
                onToggleTerm = { term ->
                    if (term in selectedTerms) selectedTerms.remove(term)
                    else selectedTerms.add(term)
                },
                onSearch = { runDiscover() }
            )
        }

        if (!fairSpotActive) {
            BottomLeftButtons(
                menuOpen = isMenuOpen,
                onToggleMenu = dismiss { isMenuOpen = !isMenuOpen },
                onHome = dismiss { onHome() },
                favoritesActive = favoritesActive,
                onFavoritesChange = { favoritesActive = it },
                myLocationActive = myLocationActive,
                onMyLocationChange = { myLocationActive = it },
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .navigationBarsPadding()
                    .padding(start = 14.dp)
                    .padding(bottom = sheetHeight + 12.dp)
            )

            BottomRightButtons(
                onLocate = dismiss {
                    if (hasLocationPermission(context)) {
                        locateUser(recenter = true)
                    } else {
                        locationPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
                    }
                },
                onDirections = dismiss {
                    if (route.isNotEmpty()) {
                        // a route is drawn -> the ✕ removes it
                        mapViewModel.clearRoute()
                    } else {
                        // route from the user's saved location to the pin (or chosen fair spot)
                        val dest = if (fairSpotActive) chosenSpot else pickedLocation
                        val from = currentUser?.myLocations?.firstOrNull()
                        if (dest == null) {
                            android.widget.Toast.makeText(context, "Pick a location on the map first", android.widget.Toast.LENGTH_SHORT).show()
                        } else if (from == null) {
                            android.widget.Toast.makeText(context, "Set your saved location first", android.widget.Toast.LENGTH_SHORT).show()
                        } else {
                            if (fairSpotActive) {
                                fairSpotActive = false
                                chosenSpot = null
                                circleCenter = null
                                circleDiscoverViewModel.clearDiscovered()
                            }
                            mapViewModel.fetchRoute(from.lat, from.long, dest.lat, dest.long)
                        }
                    }
                },
                directionsActive = route.isNotEmpty(),
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .navigationBarsPadding()
                    .padding(end = 14.dp)
                    .padding(bottom = sheetHeight + 12.dp)
            )
        }

        val place = pickedLocation
        if (place != null && !fairSpotActive) {
            val favorite = currentUser?.favorites?.firstOrNull { it.lat == place.lat && it.long == place.long }
            PlaceDetailSheet(
                location = place,
                expanded = isSheetExpanded,
                onExpandedChange = { isSheetExpanded = it },
                isFavorite = favorite != null,
                onSave = { userViewModel.addFavorite(place) },
                onRemove = { favorite?.id?.let { userViewModel.deleteFavorite(it) } },
                onAddBill = { billPlace = place },
                onClose = { mapViewModel.clearPicked() },
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .height(sheetHeight)
            )
        }

        if (!discoverMode && !showFairSpotModal && !fairSpotActive) {
            FindASpotButton(
                onClick = { showFairSpotModal = true },
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .navigationBarsPadding()
                    .padding(bottom = sheetHeight + 12.dp)
            )
        }

        if (fairSpotActive) {
            val exitFair = {
                fairSpotActive = false
                chosenSpot = null
                circleCenter = null
                circleDiscoverViewModel.clearDiscovered()
            }
            val spot = chosenSpot
            if (spot != null) {
                // after a spot is picked: same as the normal place sheet, plus the circle toggle
                Column(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .fillMaxWidth()
                ) {
                    FairMemberBox(
                        distances = tripMembers.map {
                            it.first to haversineMeters(spot.lat, spot.long, it.second.lat, it.second.long)
                        },
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp)
                    )
                    val favorite = currentUser?.favorites?.firstOrNull { it.lat == spot.lat && it.long == spot.long }
                    PlaceDetailSheet(
                        location = spot,
                        expanded = isSheetExpanded,
                        onExpandedChange = { isSheetExpanded = it },
                        isFavorite = favorite != null,
                        onSave = { userViewModel.addFavorite(spot) },
                        onRemove = { favorite?.id?.let { userViewModel.deleteFavorite(it) } },
                        onAddBill = { billPlace = spot },
                        onClose = exitFair,
                        circleVisible = circleVisible,
                        onToggleCircle = { circleVisible = !circleVisible },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(sheetHeight)
                    )
                }
            } else {
                FairSpotSheet(
                    places = discoveredPlaces,
                    loading = discovering,
                    circleVisible = circleVisible,
                    onToggleCircle = { circleVisible = !circleVisible },
                    onClose = exitFair,
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .fillMaxWidth()
                )
            }
        }
        }

        if (showFairSpotModal) {
            FairSpotModal(
                friendNames = (currentUser?.friends ?: emptyList())
                    .filter { it.id != null && it.id in checkedFriendIds }
                    .map { it.name },
                onFindFairSpot = {
                    val checkedFriends = (currentUser?.friends ?: emptyList())
                        .filter { it.id != null && it.id in checkedFriendIds }
                    if (checkedFriends.isEmpty()) {
                        android.widget.Toast.makeText(context, "Pick trip friends first", android.widget.Toast.LENGTH_SHORT).show()
                    } else {
                        pickOwnArea = false
                        showFairSpotModal = false
                        showCategoryDialog = true
                    }
                },
                onPickLocation = {
                    val checkedFriends = (currentUser?.friends ?: emptyList())
                        .filter { it.id != null && it.id in checkedFriendIds }
                    if (checkedFriends.isEmpty()) {
                        android.widget.Toast.makeText(context, "Pick trip friends first", android.widget.Toast.LENGTH_SHORT).show()
                    } else {
                        pickOwnArea = true
                        showFairSpotModal = false
                        showCategoryDialog = true
                    }
                },
                onDismiss = { showFairSpotModal = false }
            )
        }

        if (showCategoryDialog) {
            FairSpotCategoryDialog(
                categories = CircleDiscoverUseCase.CATEGORIES,
                onConfirm = { terms ->
                    showCategoryDialog = false
                    mapViewModel.clearPicked()
                    fairSpotActive = true
                    chosenSpot = null
                    fairTerms = terms.joinToString("|")
                    discoverTerm = fairTerms
                    circleCenter = Offset(containerSize.width / 2f, containerSize.height / 2f)
                    isDraggingCircle = false
                    circleVisible = true

                    // members for the spider lines (checked friends + you)
                    val myPoint = currentUser?.myLocations?.firstOrNull()?.let { it.lat to it.long }
                    userLocation = myPoint

                    if (!pickOwnArea) {
                        // auto fair centre: recenter there and search exactly the circle we draw
                        // (via the live projection in onDiscoverArea) so the two always match.
                        // zoom 15 makes the fixed-size circle ~1 km across
                        val friendPoints = (currentUser?.friends ?: emptyList())
                            .filter { it.id != null && it.id in checkedFriendIds }
                            .map { it.location.lat to it.location.long }
                        val points = friendPoints + listOfNotNull(myPoint)
                        val center = fairCenter(points)
                        fairSearchCenter = center
                        searchCenterOverride = center
                        recenterInstant = true
                        recenterZoom = 15.0
                        recenterTarget = center
                        recenterTrigger++
                        pendingFairSearch = true
                    }
                    // pickOwnArea: leave the circle at screen centre - user drags it + taps "Search area"
                },
                onDismiss = { showCategoryDialog = false }
            )
        }

        // "Add bill" from a place sheet: pick a group, then fill the bill (location prefilled)
        val billLoc = billPlace
        if (billLoc != null && billGroup == null) {
            GroupPickerDialog(
                groups = currentUser?.billGroups ?: emptyList(),
                onPick = { billGroup = it },
                onDismiss = { billPlace = null }
            )
        }
        val billGrp = billGroup
        if (billLoc != null && billGrp != null) {
            AddBillDialog(
                people = billGrp.people,
                location = billLoc,
                onAdd = { bill ->
                    userViewModel.updateBillGroup(
                        billGrp.copy(bills = billGrp.bills + bill.copy(id = java.util.UUID.randomUUID().toString()))
                    )
                    billPlace = null
                    billGroup = null
                },
                onDismiss = { billPlace = null; billGroup = null }
            )
        }

        if (showWeatherHistory) {
            WeatherHistoryDialog(
                weather = weather,
                history = weatherHistory,
                onDismiss = { showWeatherHistory = false }
            )
        }
    }
}
