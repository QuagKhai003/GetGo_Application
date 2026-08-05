package com.quangkhai.getgo_application.presentation.ui.main

import android.Manifest
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
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
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.quangkhai.getgo_application.presentation.ui.main.components.controls.BottomRightButtons
import com.quangkhai.getgo_application.presentation.ui.main.components.magiccirlce.MagicCircleLayer
import com.quangkhai.getgo_application.presentation.ui.main.components.fairspot.FairSpotCategoryDialog
import com.quangkhai.getgo_application.presentation.ui.main.components.fairspot.FairSpotModal
import com.quangkhai.getgo_application.presentation.ui.main.components.fairspot.FairSpotMapLayer
import com.quangkhai.getgo_application.presentation.ui.main.components.fairspot.FairSpotSheetLayer
import com.quangkhai.getgo_application.presentation.ui.main.components.fairspot.FindASpotButton
import com.quangkhai.getgo_application.presentation.ui.main.components.fact.BoredSwitchCircle
import com.quangkhai.getgo_application.presentation.ui.main.components.fact.FactModal
import com.quangkhai.getgo_application.presentation.ui.main.components.weather.WeatherHistoryDialog
import com.quangkhai.getgo_application.presentation.ui.main.components.placedetail.MapAddBillFlow
import com.quangkhai.getgo_application.presentation.ui.main.components.map.MapTopBar
import com.quangkhai.getgo_application.presentation.ui.main.components.map.OsmMapView
import com.quangkhai.getgo_application.presentation.ui.main.components.controls.BottomLeftButtons
import com.quangkhai.getgo_application.presentation.ui.main.components.placedetail.PlaceDetailSheet
import com.quangkhai.getgo_application.data.local.PrefManager
import com.quangkhai.getgo_application.data.local.getCurrentLatLong
import com.quangkhai.getgo_application.data.local.hasLocationPermission
import com.quangkhai.getgo_application.domain.model.BillGroup
import com.quangkhai.getgo_application.domain.model.Location
import com.quangkhai.getgo_application.domain.usecase.map.MagicCircleDiscoverUseCase
import com.quangkhai.getgo_application.domain.usecase.map.fairCenter
import com.quangkhai.getgo_application.presentation.viewmodel.MagicCircleDiscoverViewModel
import com.quangkhai.getgo_application.presentation.viewmodel.FactViewModel
import com.quangkhai.getgo_application.presentation.viewmodel.MapViewModel
import com.quangkhai.getgo_application.presentation.viewmodel.UserViewModel
import com.quangkhai.getgo_application.presentation.viewmodel.WeatherViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

// I have adapted Claude Opus 4.8 Generated Code for only transforming my UI from Figma into Kotlin Compose
// This code is not orginally generated.
// It is rewritten with semantic name conventions and reused pre-existing codes
@Composable
fun MainScreen(
    onHome: () -> Unit = {},
    mapViewModel: MapViewModel = viewModel(),
    magicCircleDiscoverViewModel: MagicCircleDiscoverViewModel = viewModel(),
    userViewModel: UserViewModel = viewModel(),
    weatherViewModel: WeatherViewModel = viewModel(),
    factViewModel: FactViewModel = viewModel()
) {
    val searchResults by mapViewModel.searchResults.collectAsState()      // live search results list
    val pickedLocation by mapViewModel.pickedLocation.collectAsState()    // place shown in the bottom sheet
    val route by mapViewModel.routePath.collectAsState()                  // road path points to draw
    val discoveredPlaces by magicCircleDiscoverViewModel.discoveredPlaces.collectAsState()  // POIs from the circle search
    val discovering by magicCircleDiscoverViewModel.discovering.collectAsState()            // true while a search runs
    val weather by weatherViewModel.weather.collectAsState()              // current weather for the picked place
    val weatherHistory by weatherViewModel.history.collectAsState()       // past hours for the weather dialog
    val currentUser by userViewModel.currentUser.collectAsState()         // logged-in user (friends, location, favorites)
    val fact by factViewModel.fact.collectAsState()                       // "I'm Feeling Bored" fun fact

    val searchState = rememberTextFieldState()                            // text in the search bar
    val context = LocalContext.current
    val prefs = remember { PrefManager(context) }                         // saved toggles
    var isMenuOpen by remember { mutableStateOf(false) }                  // side menu open
    var isSheetExpanded by remember { mutableStateOf(false) }             // bottom sheet peek vs full
    var favoritesActive by remember { mutableStateOf(false) }             // show favorite star markers

    // remembered across app restarts via PrefManager
    var myLocationActive by remember { mutableStateOf(prefs.getMyLocationActive()) }        // show my saved location marker
    val checkedFriendIds = remember { mutableStateListOf<String>().apply { addAll(prefs.getCheckedFriends()) } }  // friends ticked for the trip
    var showFairSpotModal by remember { mutableStateOf(false) }           // the "Calculate Fair Spot" dialog

    // "I'm Feeling Bored" mode toggle for the Find A Spot button
    var boredMode by remember { mutableStateOf(false) }
    var showCategoryDialog by remember { mutableStateOf(false) }          // the pick-categories dialog
    var fairSpotActive by remember { mutableStateOf(false) }              // fair-spot flow running

    // true = user positions the search circle themselves; false = auto fair-centre
    var pickOwnArea by remember { mutableStateOf(false) }
    var chosenSpot by remember { mutableStateOf<Location?>(null) }        // the fair spot the user tapped
    var userLocation by remember { mutableStateOf<Pair<Double, Double>?>(null) }  // your live location (for spider lines)
    var fairTerms by remember { mutableStateOf("") }                      // category filters for the fair search
    var fairSearchCenter by remember { mutableStateOf<Pair<Double, Double>?>(null) }  // geo centre the fair search ran at
    var circleVisible by remember { mutableStateOf(true) }               // show/hide the search circle
    var recenterZoom by remember { mutableStateOf(16.0) }               // zoom for the next recenter
    var recenterInstant by remember { mutableStateOf(false) }            // jump vs animate on recenter
    var pendingFairSearch by remember { mutableStateOf(false) }          // waiting for the map to recenter before the first fair search
    // "add bill from map": the place to bill, and the group chosen to add it to
    var billPlace by remember { mutableStateOf<Location?>(null) }
    var billGroup by remember { mutableStateOf<BillGroup?>(null) }
    var showWeatherHistory by remember { mutableStateOf(false) }         // the weather history dialog
    // forces the first fair search onto the exact fair centre (projection not settled yet)
    var searchCenterOverride by remember { mutableStateOf<Pair<Double, Double>?>(null) }

    // --- circle discovery state ---
    var discoverMode by remember { mutableStateOf(false) }               // magic-circle discover running
    var discoverTrigger by remember { mutableIntStateOf(0) }             // bump to re-run the search
    var discoverTerm by remember { mutableStateOf("") }                  // categories for the circle search
    var containerSize by remember { mutableStateOf(IntSize.Zero) }       // map area size in px (for circle math)
    var circleCenter by remember { mutableStateOf<Offset?>(null) }       // circle position on screen
    var isDraggingCircle by remember { mutableStateOf(false) }           // user dragging the circle
    val selectedTerms = remember { mutableStateListOf<String>() }        // chosen categories in the dialog

    // shared circle-drag handlers (used by both the fair-spot and magic-circle layers)
    val startCircleDrag = { isDraggingCircle = true }
    val endCircleDrag = { isDraggingCircle = false }
    val moveCircle: (Float, Float) -> Unit = { moveX, moveY ->
        val cur = circleCenter
        if (cur != null) {
            circleCenter = Offset(
                (cur.x + moveX).coerceIn(0f, containerSize.width.toFloat()),
                (cur.y + moveY).coerceIn(0f, containerSize.height.toFloat())
            )
        }
    }

    val density = LocalDensity.current
    val radiusPx = with(density) { (120.dp).toPx() }
    val circleDiameter = (120.dp) * 2

    val focusManager = LocalFocusManager.current
    val scope = rememberCoroutineScope()
    var recenterTarget by remember { mutableStateOf<Pair<Double, Double>?>(null) }
    var recenterTrigger by remember { mutableIntStateOf(0) }

    // run discovery for the current circle + checked categories, then dismiss the overlay
    fun runDiscover() {
        if (selectedTerms.isEmpty()) return
        discoverTerm = selectedTerms.joinToString("|")
        discoverTrigger++
        discoverMode = false
    }

    // Claude Opus 4.8 Generated Code for removing stuck focus on the search upon initial tap
    fun dismiss(action: () -> Unit): () -> Unit = { focusManager.clearFocus(); action() }

    fun enterDiscover() {
        discoverMode = true
        selectedTerms.clear()
        circleCenter = Offset(containerSize.width / 2f, containerSize.height / 2f)
        isDraggingCircle = false
        magicCircleDiscoverViewModel.clearDiscovered()
    }

    fun exitDiscover() {
        discoverMode = false
        magicCircleDiscoverViewModel.clearDiscovered()
    }

    // Adapted Claude Opus 4.8 Generated code for marking and repostioning current location
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

                // turn on the "my location" marker toggle automatically
                myLocationActive = true
                prefs.saveMyLocationActive(true)

                // save the location once as a fallback if the user has none yet.
                // updating it later is done from the profile (set location + picker).
                if (currentUser?.location == null) {
                    userViewModel.addLocation("My location", currentLocation.first, currentLocation.second, "")
                }
            }
        }
    }

    // the user's live device location; falls back to their saved location if GPS is unavailable
    suspend fun currentUserPoint(): Pair<Double, Double>? =
        getCurrentLatLong(context) ?: currentUser?.location?.let { it.lat to it.long }

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

    // in pick-a-location, the tapped place (resolved by the map view model) is the chosen spot
    LaunchedEffect(pickedLocation) {
        if (fairSpotActive && pickOwnArea) pickedLocation?.let { chosenSpot = it }
    }

    // when an AUTO fair-spot search finishes, recenter the map on the circle + snap it to screen centre.
    // skip for pick-your-own-area: the user placed the circle at their own zoom, so leave the map alone.
    LaunchedEffect(discovering) {
        if (fairSpotActive && !discovering && !pickOwnArea) {
            fairSearchCenter?.let {
                recenterTarget = it
                recenterTrigger++
                circleCenter = Offset(containerSize.width / 2f, containerSize.height / 2f)
            }
        }
    }

    // how tall the bottom detail sheet, and animates it sliding open/closed
    val targetSheetHeight = when {
        fairSpotActive -> if (isSheetExpanded && chosenSpot != null) 300.dp else 128.dp
        pickedLocation == null -> 0.dp // nothing select so the sheet disappear
        isSheetExpanded -> 300.dp
        else -> 128.dp
    }
    val sheetHeight by animateDpAsState(targetValue = targetSheetHeight, label = "sheetHeight")

    // circle markers: friends + my locations (skip 0,0 = no real coordinate)
    val savedPlaces = remember(currentUser, checkedFriendIds.toList(), myLocationActive) {
        val list = mutableListOf<Location>()
        val checkedFriends = (currentUser?.friends ?: emptyList()).filter { it.id != null && it.id in checkedFriendIds }
        list.addAll(checkedFriends.map { it.location })
        if (myLocationActive) currentUser?.location?.let { list.add(it) }
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
            pickedLocation = if (fairSpotActive) chosenSpot else pickedLocation,
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
                if (fairSpotActive && pickOwnArea) mapViewModel.pickDiscoveredPlace(Location(id = null, name = "Selected spot", lat = latitude, long = longitude, address = ""))
                else if (!fairSpotActive && !discoverMode) mapViewModel.pinAt(latitude, longitude)
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
                magicCircleDiscoverViewModel.discover(discoverTerm, latitude, longitude, radiusMeters)
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
            routePoints = route,
            modifier = Modifier.fillMaxSize()
        )

        MapTopBar(
            searchState = searchState,
            onSearch = { mapViewModel.search(searchState.text.toString()) },
            discoverMode = discoverMode,
            friends = currentUser?.friends ?: emptyList(),
            checkedIds = checkedFriendIds.toSet(),
            onToggleFriend = { friend ->
                friend.id?.let {
                    if (it in checkedFriendIds) checkedFriendIds.remove(it)
                    else checkedFriendIds.add(it)
                    prefs.saveCheckedFriends(checkedFriendIds.toSet())
                }
            },
            onEnterDiscover = dismiss { enterDiscover() },
            onExitDiscover = dismiss { exitDiscover() },
            weather = weather,
            onWeatherClick = {
                scope.launch {
                    getCurrentLatLong(context)?.let { (lat, long) ->
                        weatherViewModel.loadHistory(lat, long)
                    }
                }
                showWeatherHistory = true
            },
            searchResults = searchResults,
            onPickResult = { location ->
                mapViewModel.pick(location)
                val filledText = if (location.address.isBlank()) location.name
                    else "${location.name} - ${location.address}"
                searchState.setTextAndPlaceCursorAtEnd(filledText)
                isSheetExpanded = false
            },
            modifier = Modifier.align(Alignment.TopCenter)
        )

        FairSpotMapLayer(
            show = fairSpotActive && !pendingFairSearch && circleVisible && (pickOwnArea || !discovering),
            circlePos = circleCenter,
            circleDiameter = circleDiameter,
            radiusPx = radiusPx,
            isDragging = isDraggingCircle,
            onDragStart = startCircleDrag,
            onDrag = moveCircle,
            onDragEnd = endCircleDrag,
            onSearchArea = {
                discoverTerm = fairTerms
                discoverTrigger++
            }
        )

        MagicCircleLayer(
            discoverMode = discoverMode,
            circleCenter = circleCenter,
            radiusPx = radiusPx,
            circleDiameter = circleDiameter,
            isDragging = isDraggingCircle,
            containerWidth = containerSize.width,
            selectedTerms = selectedTerms.toSet(),
            onDragStart = startCircleDrag,
            onDrag = moveCircle,
            onDragEnd = endCircleDrag,
            onToggleTerm = { term ->
                if (term in selectedTerms) selectedTerms.remove(term)
                else selectedTerms.add(term)
            },
            onSearch = { runDiscover() }
        )

        if (!fairSpotActive) {
            BottomLeftButtons(
                menuOpen = isMenuOpen,
                onToggleMenu = dismiss { isMenuOpen = !isMenuOpen },
                onHome = dismiss { onHome() },
                favoritesActive = favoritesActive,
                onFavoritesChange = { favoritesActive = it },
                myLocationActive = myLocationActive,
                onMyLocationChange = { myLocationActive = it; prefs.saveMyLocationActive(it) },
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
                        // route from the user's live location to the pin (or chosen fair spot)
                        val dest = if (fairSpotActive) chosenSpot else pickedLocation
                        if (dest == null) {
                            android.widget.Toast.makeText(context, "Pick a location on the map first", android.widget.Toast.LENGTH_SHORT).show()
                        } else {
                            scope.launch {
                                val from = currentUserPoint()
                                if (from == null) {
                                    android.widget.Toast.makeText(context, "Couldn't get your location. Enable location to get directions.", android.widget.Toast.LENGTH_SHORT).show()
                                } else {
                                    if (fairSpotActive) {
                                        fairSpotActive = false
                                        chosenSpot = null
                                        circleCenter = null
                                        magicCircleDiscoverViewModel.clearDiscovered()
                                    }
                                    mapViewModel.fetchRoute(from.first, from.second, dest.lat, dest.long)
                                }
                            }
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
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .navigationBarsPadding()
                    .padding(bottom = sheetHeight + 12.dp)
            ) {
                BoredSwitchCircle(onToggle = { boredMode = !boredMode })
                Spacer(Modifier.height(8.dp))
                FindASpotButton(
                    boredMode = boredMode,
                    onClick = {
                        if (boredMode) {
                            scope.launch {
                                val point = currentUserPoint()
                                if (point != null) factViewModel.loadRandomFact(point.first, point.second)
                                else android.widget.Toast.makeText(context, "Enable location for nearby facts", android.widget.Toast.LENGTH_SHORT).show()
                            }
                        } else {
                            showFairSpotModal = true
                        }
                    }
                )
            }
        }

        if (fairSpotActive) {
            FairSpotSheetLayer(
                chosen = chosenSpot,
                members = tripMembers,
                places = discoveredPlaces,
                loading = discovering,
                circleVisible = circleVisible,
                expanded = isSheetExpanded,
                sheetHeight = sheetHeight,
                favorites = currentUser?.favorites ?: emptyList(),
                onExpandedChange = { isSheetExpanded = it },
                onToggleCircle = { circleVisible = !circleVisible },
                onSaveFavorite = { userViewModel.addFavorite(it) },
                onRemoveFavorite = { userViewModel.deleteFavorite(it) },
                onAddBill = { billPlace = it },
                onClose = {
                    fairSpotActive = false
                    chosenSpot = null
                    circleCenter = null
                    magicCircleDiscoverViewModel.clearDiscovered()
                },
                pickOwnArea = pickOwnArea,
                modifier = Modifier.align(Alignment.BottomCenter)
            )
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

        fact?.let { current ->
            FactModal(
                fact = current,
                onVisit = {
                    mapViewModel.pick(Location(id = null, name = current.title, lat = current.lat, long = current.long, address = current.description))
                    recenterInstant = false
                    recenterZoom = 16.0
                    recenterTarget = current.lat to current.long
                    recenterTrigger++
                    factViewModel.clear()
                },
                onDismiss = { factViewModel.clear() }
            )
        }

        if (showCategoryDialog) {
            FairSpotCategoryDialog(
                categories = MagicCircleDiscoverUseCase.CATEGORIES,
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

                    // members for the spider lines (checked friends + you) - "you" is the live location
                    scope.launch {
                        val myPoint = currentUserPoint()
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
                    }
                    // pickOwnArea: leave the circle at screen centre - user drags it + taps "Search area"
                },
                onDismiss = { showCategoryDialog = false }
            )
        }

        MapAddBillFlow(
            place = billPlace,
            group = billGroup,
            groups = currentUser?.billGroups ?: emptyList(),
            onPickGroup = { billGroup = it },
            onAddBill = { g, bill ->
                userViewModel.updateBillGroup(g.copy(bills = g.bills + bill.copy(id = java.util.UUID.randomUUID().toString())))
                billPlace = null
                billGroup = null
            },
            onDismiss = { billPlace = null; billGroup = null }
        )

        if (showWeatherHistory) {
            WeatherHistoryDialog(
                weather = weather,
                history = weatherHistory,
                onDismiss = { showWeatherHistory = false }
            )
        }
    }
}
