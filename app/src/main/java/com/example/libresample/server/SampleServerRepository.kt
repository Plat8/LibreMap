package com.example.libresample.server

import android.content.Context
import com.example.libresample.MyApplication

class SampleServerRepository(
  private val context: Context = MyApplication.getAppContext()
    ?: throw IllegalStateException("Application context is not initialized")
) {

  fun getSampleJson(): String {
    val geoJson = context.assets.open("point-samples.geojson").use {
      it.reader().readText()
    }
    return geoJson
  }

  fun getPointsJson(): String {
    val geoJson = context.assets.open("geoPoints.geojson").use {
      it.reader().readText()
    }
    return geoJson
  }

  fun getRouteJson(): String {
    val geoJson = context.assets.open("route.geojson").use {
      it.reader().readText()
    }
    return geoJson
  }
}