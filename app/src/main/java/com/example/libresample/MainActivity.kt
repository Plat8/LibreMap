package com.example.libresample

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import com.example.libresample.databinding.ActivityMainBinding
import com.example.libresample.server.module
import com.google.gson.JsonObject
import com.mapbox.geojson.FeatureCollection
import com.mapbox.mapboxsdk.Mapbox
import com.mapbox.mapboxsdk.camera.CameraPosition
import com.mapbox.mapboxsdk.geometry.LatLng
import com.mapbox.mapboxsdk.maps.Style
import com.mapbox.mapboxsdk.style.layers.PropertyFactory.iconAllowOverlap
import com.mapbox.mapboxsdk.style.layers.PropertyFactory.iconImage
import com.mapbox.mapboxsdk.style.layers.PropertyFactory.iconSize
import com.mapbox.mapboxsdk.style.layers.SymbolLayer
import com.mapbox.mapboxsdk.style.sources.GeoJsonSource
import io.ktor.application.Application
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import io.ktor.server.netty.NettyApplicationEngine
import kotlinx.coroutines.launch


class MainActivity : AppCompatActivity() {

  private val viewModel: MainViewModel by viewModels()
  private lateinit var binding: ActivityMainBinding
  private lateinit var server: NettyApplicationEngine


  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)

    startServer()

    lifecycleScope.launch {
      viewModel.uiState.flowWithLifecycle(lifecycle).collect { uiState ->
        render(uiState)
      }
    }


    // Init MapLibre
    Mapbox.getInstance(this);

    // Init layout view
    val inflater = LayoutInflater.from(this)

    binding = ActivityMainBinding.inflate(inflater)
    setContentView(binding.root)

    // Init the MapView
    binding.mapView.getMapAsync { map ->
      map.setStyle("https://demotiles.maplibre.org/style.json") {

        val geoJson = assets.open("point-samples.geojson").use { stream ->
          stream.reader().readText()
        }


        // Create feature object from the GeoJSON we declared.
        val parisBoundariesFeature = FeatureCollection.fromJson(geoJson)
        // Create a GeoJson Source from our feature.
        val geojsonSource = GeoJsonSource("points-source", parisBoundariesFeature)
        // Add the source to the style
        it.addSource(geojsonSource)

        val name = resources.getResourceEntryName(R.drawable.pnt_survey)
        val drawable = getDrawable(R.drawable.pnt_survey) ?: throw Exception("Drawable not found")

        if (it.getImage(name) == null)
          it.addImage(name, drawable)

        val symbolLayer = SymbolLayer("points-layer", "points-source")
        symbolLayer.withProperties(
          iconImage(name),
          iconSize(1.0f),
          iconAllowOverlap(true)
        )

        it.addLayer(symbolLayer)


        map.addOnMapClickListener { point ->
          val screenPoint = map.projection.toScreenLocation(point)
          val clickedFeatures = map.queryRenderedFeatures(screenPoint, "points-layer")

          if (clickedFeatures.isNotEmpty()) {
            // Marker clicked, show Toast with feature properties
            val properties = clickedFeatures[0].properties()
            properties?.let { it ->
              val toastMessage = buildToastMessage(it)
              Toast.makeText(this, toastMessage, Toast.LENGTH_SHORT).show()
            }
            true
          } else {
            false
          }
        }

      }
      map.cameraPosition = CameraPosition.Builder().target(LatLng(0.0, 0.0)).zoom(1.0).build()

    }

    setupToolbar();

  }

  private fun setupToolbar() {
    binding.drawerLayout.setScrimColor(Color.TRANSPARENT)
    setSupportActionBar(binding.appBarMain.toolbar)
    supportActionBar?.apply {
      setDisplayHomeAsUpEnabled(true)
      setDisplayShowHomeEnabled(true)
      setHomeAsUpIndicator(R.drawable.ic_menu)
    }

    val toolbar = binding.appBarMain.toolbar
    toolbar.setNavigationOnClickListener { binding.drawerLayout.open() }
  }


  private fun updateGeoJsonSource(style: Style, sourceId: String, updatedGeoJsonData: String) {
    style.getSourceAs<GeoJsonSource>(sourceId)
      ?.setGeoJson(FeatureCollection.fromJson(updatedGeoJsonData))
  }

  private fun render(uiState: MainViewState) {

    binding.mapView.getMapAsync { map ->
      map.style?.let {
        updateGeoJsonSource(it, "points-source", uiState.geoJson)
      }
    }

    if (binding.navView.menu.size() != uiState.menuList.size) {
      binding.navView.menu.clear()
      uiState.menuList.onEachIndexed { it, str ->
        binding.navView.menu.add(Menu.NONE, it, Menu.NONE, str)
      }

    }


  }

  private fun buildToastMessage(properties: JsonObject): String {
    // Customize the Toast message based on feature properties
    val builder = StringBuilder()
    for (key in properties.keySet()) {
      val value = properties[key]
      builder.append("$key: $value\n")
    }
    return builder.toString()
  }

  override fun onStart() {
    super.onStart()
    binding.mapView.onStart()
  }

  override fun onResume() {
    super.onResume()
    binding.mapView.onResume()
  }

  override fun onPause() {
    super.onPause()
    binding.mapView.onPause()
  }

  override fun onStop() {
    super.onStop()
    binding.mapView.onStop()
  }

  override fun onLowMemory() {
    super.onLowMemory()
    binding.mapView.onLowMemory()
  }

  override fun onDestroy() {
    super.onDestroy()
    server.stop(0, 0)
    binding.mapView.onDestroy()
  }

  override fun onSaveInstanceState(outState: Bundle) {
    super.onSaveInstanceState(outState)
    binding.mapView.onSaveInstanceState(outState)
  }

  private fun startServer() {
    server = embeddedServer(Netty, port = 8080, module = Application::module)
    server.start()
  }


}