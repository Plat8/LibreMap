package com.example.libresample

import android.os.Bundle
import android.view.LayoutInflater
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.example.libresample.databinding.ActivityMainBinding
import com.mapbox.mapboxsdk.Mapbox
import com.mapbox.mapboxsdk.camera.CameraPosition
import com.mapbox.mapboxsdk.geometry.LatLng

class MainActivity : AppCompatActivity() {

  private val viewModel: MainViewModel by viewModels()
  private lateinit var binding: ActivityMainBinding

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)

    // Init MapLibre
    Mapbox.getInstance(this);

    // Init layout view
    val inflater = LayoutInflater.from(this)

    binding = ActivityMainBinding.inflate(inflater)
    setContentView(binding.root)

    // Init the MapView
    binding.mapView.getMapAsync { map ->
      map.setStyle("https://demotiles.maplibre.org/style.json")
      map.cameraPosition = CameraPosition.Builder().target(LatLng(0.0, 0.0)).zoom(1.0).build()
    }
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
    binding.mapView.onDestroy()
  }

  override fun onSaveInstanceState(outState: Bundle) {
    super.onSaveInstanceState(outState)
    binding.mapView.onSaveInstanceState(outState)
  }

}