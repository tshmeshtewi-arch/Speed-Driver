package com.turboclone.app.ui.screens.home

import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker
import org.osmdroid.views.overlay.Polyline

/**
 * خريطة حقيقية مبنية على OpenStreetMap (osmdroid).
 * تدعم: تحديد نقطة بالضغط، عرض علامات (ماركرز)، ورسم خط المسار.
 */
@Composable
fun OsmMapView(
    modifier: Modifier = Modifier,
    centerLat: Double,
    centerLng: Double,
    zoom: Double = 15.0,
    fromPoint: GeoPoint? = null,
    toPoint: GeoPoint? = null,
    driverPoints: List<GeoPoint> = emptyList(),
    onMapClick: (GeoPoint) -> Unit = {}
) {
    val context = LocalContext.current
    val mapView = remember {
        MapView(context).apply {
            setTileSource(TileSourceFactory.MAPNIK)
            setMultiTouchControls(true)
            controller.setZoom(zoom)
            controller.setCenter(GeoPoint(centerLat, centerLng))
        }
    }

    AndroidView(
        modifier = modifier,
        factory = {
            mapView.overlays.add(
                object : org.osmdroid.views.overlay.Overlay() {
                    override fun onSingleTapConfirmed(e: android.view.MotionEvent?, mv: MapView?): Boolean {
                        e ?: return false
                        val geoPoint = mv?.projection?.fromPixels(e.x.toInt(), e.y.toInt()) as? GeoPoint
                        geoPoint?.let { onMapClick(it) }
                        return true
                    }
                }
            )
            mapView
        },
        update = { mv ->
            // إزالة الماركرز والخطوط القديمة مع الإبقاء على overlay اللمس الأول
            mv.overlays.removeAll { it is Marker || it is Polyline }

            fromPoint?.let {
                val marker = Marker(mv)
                marker.position = it
                marker.title = "نقطة الانطلاق"
                mv.overlays.add(marker)
            }
            toPoint?.let {
                val marker = Marker(mv)
                marker.position = it
                marker.title = "نقطة الوصول"
                mv.overlays.add(marker)
            }
            if (fromPoint != null && toPoint != null) {
                val line = Polyline()
                line.setPoints(listOf(fromPoint, toPoint))
                mv.overlays.add(line)
            }
            driverPoints.forEach { dp ->
                val marker = Marker(mv)
                marker.position = dp
                marker.title = "سائق قريب"
                mv.overlays.add(marker)
            }
            mv.invalidate()
        }
    )

    DisposableEffect(Unit) {
        onDispose { mapView.onDetach() }
    }
}
