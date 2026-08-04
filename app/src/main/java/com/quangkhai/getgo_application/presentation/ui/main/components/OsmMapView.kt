package com.quangkhai.getgo_application.presentation.ui.main.components

import android.graphics.Canvas
import android.graphics.Paint
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import com.quangkhai.getgo_application.R
import com.quangkhai.getgo_application.domain.model.Location
import com.quangkhai.getgo_application.domain.usecase.map.haversineMeters
import com.quangkhai.getgo_application.presentation.ui.shared.favoriteStarDrawable
import com.quangkhai.getgo_application.presentation.ui.shared.quaternaryCircleDotDrawable
import com.quangkhai.getgo_application.ui.theme.GetGoTheme
import org.osmdroid.config.Configuration
import org.osmdroid.events.MapEventsReceiver
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.BoundingBox
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.CustomZoomButtonsController
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.MapEventsOverlay
import org.osmdroid.views.overlay.Marker
import org.osmdroid.views.Projection
import org.osmdroid.views.overlay.Overlay

@Composable
fun OsmMapView(
    pickedLocation: Location?,
    recenterTarget: Pair<Double, Double>?,
    recenterKey: Int,
    recenterZoom: Double = 16.0,
    recenterInstant: Boolean = false,
    onRecentered: () -> Unit = {},
    onMapTap: (lat: Double, long: Double) -> Unit,
    onMapLongPress: (lat: Double, long: Double) -> Unit = { _, _ -> },
    onPinMoved: (lat: Double, long: Double) -> Unit,
    onDeletePin: () -> Unit,
    showPickedInfoWindow: Boolean = false,
    recenterOnPick: Boolean = true,
    // --- circle discovery ---
    discoverPlaces: List<Location> = emptyList(),
    discoverCenter: Offset? = null,     // circle center in px (map top-left origin)
    discoverRadiusPx: Float = 0f,       // circle radius in px
    discoverTrigger: Int = 0,
    overrideSearchCenter: Pair<Double, Double>? = null,
    onDiscoverArea: (lat: Double, long: Double, radiusMeters: Int) -> Unit = { _, _, _ -> },
    onDiscoveredTap: (Location) -> Unit = {},
    savedPlaces: List<Location> = emptyList(),
    onSavedTap: (Location) -> Unit = {},
    favoritePlaces: List<Location> = emptyList(),
    onFavoriteTap: (Location) -> Unit = {},
    fairSpot: Location? = null,
    fairSpotLines: List<Location> = emptyList(),
    fitPoints: List<Pair<Double, Double>> = emptyList(),
    fitKey: Int = 0,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    // read the newest lambdas / places inside the long-lived tap receiver
    val latestOnMapTap = rememberUpdatedState(onMapTap)
    val latestOnMapLongPress = rememberUpdatedState(onMapLongPress)
    val latestDiscoverPlaces = rememberUpdatedState(discoverPlaces)
    val latestOnDiscoveredTap = rememberUpdatedState(onDiscoveredTap)
    val tapSlopPx = context.resources.displayMetrics.density * 40f

    val mapView = remember {
        Configuration.getInstance().userAgentValue = context.packageName
        MapView(context).apply {
            setTileSource(TileSourceFactory.MAPNIK)
            setTilesScaledToDpi(true)
            setTilesScaleFactor(1.0f)
            setMultiTouchControls(true)
            zoomController.setVisibility(CustomZoomButtonsController.Visibility.SHOW_AND_FADEOUT)
            controller.setZoom(15.0)
            controller.setCenter(GeoPoint(10.7769, 106.7009))
        }
    }

    // Tap receiver: dots are tiny, so a tap doesn't have to land exactly on a dot.
    // Pick the nearest discovered place within ~40dp; otherwise treat it as a map tap.
    DisposableEffect(Unit) {
        val events = MapEventsOverlay(object : MapEventsReceiver {
            override fun singleTapConfirmedHelper(point: GeoPoint): Boolean {
                val places = latestDiscoverPlaces.value
                if (places.isNotEmpty()) {
                    val projection = mapView.projection
                    val tap = projection.toPixels(point, null)
                    var nearest: Location? = null
                    var nearestDist = Float.MAX_VALUE
                    places.forEach { place ->
                        val pixel = projection.toPixels(GeoPoint(place.lat, place.long), null)
                        val dist = kotlin.math.hypot((pixel.x - tap.x).toFloat(), (pixel.y - tap.y).toFloat())
                        if (dist < nearestDist) {
                            nearestDist = dist
                            nearest = place
                        }
                    }
                    val chosen = nearest
                    if (chosen != null && nearestDist <= tapSlopPx) {
                        latestOnDiscoveredTap.value(chosen)
                        return true
                    }
                }
                latestOnMapTap.value(point.latitude, point.longitude)
                return true
            }
            override fun longPressHelper(point: GeoPoint): Boolean {
                latestOnMapLongPress.value(point.latitude, point.longitude)
                return true
            }
        })
        mapView.overlays.add(0, events)
        onDispose { mapView.overlays.remove(events) }
    }

    val pinMarker = remember {
        Marker(mapView).apply {
            isDraggable = true
            setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
            icon = ContextCompat.getDrawable(context, R.drawable.ic_map_pin)
            infoWindow = PinWindowTooltip(mapView) { onDeletePin() }
            setOnMarkerClickListener { marker, _ ->
                if (marker.isInfoWindowShown) marker.closeInfoWindow()
                else marker.showInfoWindow()
                true
            }
            setOnMarkerDragListener(object : Marker.OnMarkerDragListener {
                override fun onMarkerDrag(marker: Marker) {}
                override fun onMarkerDragStart(marker: Marker) {}
                override fun onMarkerDragEnd(marker: Marker) {
                    onPinMoved(marker.position.latitude, marker.position.longitude)
                }
            })
        }
    }

    // markers for the current discovery results
    val resultMarkers = remember { mutableListOf<Marker>() }

    // markers for the toggled saved places (favorites / friends / my locations)
    val savedMarkers = remember { mutableListOf<Marker>() }

    val savedDot = quaternaryCircleDotDrawable(context, GetGoTheme.colors.quaternary.toArgb())

    // markers for the toggled favorite places (star icon)
    val favoriteMarkers = remember { mutableListOf<Marker>() }
    val favoriteStar = favoriteStarDrawable(context, GetGoTheme.colors.quaternary.toArgb())

    // fair-spot overlays: spider lines, distance labels, equidistance rings
    val fairOverlays = remember { mutableListOf<Overlay>() }
    val fairLineColor = MaterialTheme.colorScheme.tertiary.toArgb()
    val fairRingColor = GetGoTheme.colors.outlineElements.toArgb()

    LaunchedEffect(pickedLocation) {
        val place = pickedLocation
        if (place != null) {
            val point = GeoPoint(place.lat, place.long)
            pinMarker.position = point
            pinMarker.title = if (showPickedInfoWindow) place.address.ifBlank { place.name } else place.name
            if (!mapView.overlays.contains(pinMarker)) mapView.overlays.add(pinMarker)
            if (showPickedInfoWindow) pinMarker.showInfoWindow() else pinMarker.closeInfoWindow()
            if (recenterOnPick) mapView.controller.animateTo(point)
            mapView.invalidate()
        } else {
            pinMarker.closeInfoWindow()
            mapView.overlays.remove(pinMarker)
            mapView.invalidate()
        }
    }

    // zoom/pan so all given points (chosen spot + members) fit on screen
    LaunchedEffect(fitKey) {
        if (fitKey == 0 || fitPoints.size < 2) return@LaunchedEffect
        val box = BoundingBox.fromGeoPoints(fitPoints.map { GeoPoint(it.first, it.second) })
        mapView.zoomToBoundingBox(box, true, 140)
    }

    LaunchedEffect(recenterKey) {
        if (recenterKey == 0) return@LaunchedEffect
        recenterTarget?.let { (latitude, longitude) ->
            if (recenterInstant) mapView.controller.setCenter(GeoPoint(latitude, longitude))
            else mapView.controller.animateTo(GeoPoint(latitude, longitude))
            mapView.controller.setZoom(recenterZoom)
            onRecentered()
        }
    }

    // rebuild result markers whenever the discovered list changes
    LaunchedEffect(discoverPlaces) {
        resultMarkers.forEach { mapView.overlays.remove(it) }
        resultMarkers.clear()

        val dot = ContextCompat.getDrawable(context, R.drawable.ic_place_dot)
        discoverPlaces.forEach { place ->
            val marker = Marker(mapView).apply {
                position = GeoPoint(place.lat, place.long)
                setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_CENTER)
                icon = dot
                title = place.name
                setOnMarkerClickListener { _, _ ->
                    onDiscoveredTap(place)
                    true
                }
            }
            resultMarkers.add(marker)
            mapView.overlays.add(marker)
        }
        mapView.invalidate()
    }

    // rebuild saved-place markers whenever the toggled list changes
    LaunchedEffect(savedPlaces) {
        savedMarkers.forEach { mapView.overlays.remove(it) }
        savedMarkers.clear()

        savedPlaces.forEach { place ->
            val marker = Marker(mapView).apply {
                position = GeoPoint(place.lat, place.long)
                setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_CENTER)
                icon = savedDot
                title = place.name
                setOnMarkerClickListener { _, _ ->
                    onSavedTap(place)
                    true
                }
            }
            savedMarkers.add(marker)
            mapView.overlays.add(marker)
        }
        mapView.invalidate()
    }

    // rebuild favorite (star) markers whenever the favorites list changes
    LaunchedEffect(favoritePlaces) {
        favoriteMarkers.forEach { mapView.overlays.remove(it) }
        favoriteMarkers.clear()

        favoritePlaces.forEach { place ->
            val marker = Marker(mapView).apply {
                position = GeoPoint(place.lat, place.long)
                setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_CENTER)
                icon = favoriteStar
                title = place.name
                setOnMarkerClickListener { _, _ ->
                    onFavoriteTap(place)
                    true
                }
            }
            favoriteMarkers.add(marker)
            mapView.overlays.add(marker)
        }
        mapView.invalidate()
    }

    // draw fair-spot spider lines + distance labels + equidistance rings around the chosen
    // spot. A single draw-only overlay, so it never consumes taps (the dots stay tappable).
    LaunchedEffect(fairSpot, fairSpotLines) {
        fairOverlays.forEach { mapView.overlays.remove(it) }
        fairOverlays.clear()

        val spot = fairSpot
        if (spot != null && fairSpotLines.isNotEmpty()) {
            val spotPoint = GeoPoint(spot.lat, spot.long)
            val maxDist = fairSpotLines.maxOf { haversineMeters(spot.lat, spot.long, it.lat, it.long) }
            val members = fairSpotLines.map { member ->
                val distKm = haversineMeters(spot.lat, spot.long, member.lat, member.long) / 1000.0
                GeoPoint(member.lat, member.long) to String.format(java.util.Locale.US, "%.1f km", distKm)
            }
            val overlay = FairOverlay(spotPoint, members, maxDist, fairLineColor, fairRingColor)
            fairOverlays.add(overlay)
            mapView.overlays.add(overlay)
        }
        mapView.invalidate()
    }

    // "Search": convert the circle's screen position (center + radius px) to a
    // geo center + radius in meters, using the live map projection.
    LaunchedEffect(discoverTrigger) {
        if (discoverTrigger == 0) return@LaunchedEffect
        val center = discoverCenter ?: return@LaunchedEffect
        if (mapView.width == 0 || mapView.height == 0) return@LaunchedEffect

        val projection = mapView.projection
        val geoCenter = projection.fromPixels(center.x.toInt(), center.y.toInt()) as GeoPoint
        val geoEdge = projection.fromPixels((center.x + discoverRadiusPx).toInt(), center.y.toInt()) as GeoPoint
        val radiusMeters = geoCenter.distanceToAsDouble(geoEdge).toInt()

        // On the first fair search the projection may not have settled after the instant
        // recenter+zoom, so trust the known fair centre instead of the pixel-derived one.
        val searchLat = overrideSearchCenter?.first ?: geoCenter.latitude
        val searchLong = overrideSearchCenter?.second ?: geoCenter.longitude
        onDiscoverArea(searchLat, searchLong, radiusMeters)
    }

    DisposableEffect(Unit) {
        mapView.onResume()
        onDispose { mapView.onDetach() }
    }

    AndroidView(factory = { mapView }, modifier = modifier)
}

// Draw-only overlay: spider lines from the chosen spot to each member, a distance label
// on each, and three equidistance rings. Overrides only draw(), so it never receives or
// consumes taps - the fair-spot dots underneath stay tappable.
private class FairOverlay(
    private val spot: GeoPoint,
    private val members: List<Pair<GeoPoint, String>>,
    private val maxRingMeters: Double,
    lineColor: Int,
    ringColor: Int
) : Overlay() {
    private val linePaint = Paint().apply {
        color = lineColor
        strokeWidth = 5f
        isAntiAlias = true
        style = Paint.Style.STROKE
    }
    private val ringPaint = Paint().apply {
        color = ringColor
        strokeWidth = 2f
        isAntiAlias = true
        style = Paint.Style.STROKE
    }
    private val textPaint = Paint().apply {
        color = lineColor
        textSize = 30f
        isAntiAlias = true
        textAlign = Paint.Align.CENTER
    }

    override fun draw(canvas: Canvas, projection: Projection) {
        val center = projection.toPixels(spot, null)
        val cx = center.x.toFloat()
        val cy = center.y.toFloat()

        for (i in 1..3) {
            val edge = spot.destinationPoint(maxRingMeters * i / 3.0, 0.0)
            val edgePx = projection.toPixels(edge, null)
            val radius = kotlin.math.hypot((edgePx.x - center.x).toFloat(), (edgePx.y - center.y).toFloat())
            canvas.drawCircle(cx, cy, radius, ringPaint)
        }

        members.forEach { (memberPoint, label) ->
            val member = projection.toPixels(memberPoint, null)
            val mx = member.x.toFloat()
            val my = member.y.toFloat()
            canvas.drawLine(cx, cy, mx, my, linePaint)
            canvas.drawText(label, (cx + mx) / 2f, (cy + my) / 2f, textPaint)
        }
    }
}
