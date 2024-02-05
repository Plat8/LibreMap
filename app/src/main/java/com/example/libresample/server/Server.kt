package com.example.libresample.server

import io.ktor.application.Application
import io.ktor.application.call
import io.ktor.application.install
import io.ktor.features.ContentNegotiation
import io.ktor.gson.gson
import io.ktor.response.respond
import io.ktor.routing.get
import io.ktor.routing.routing


fun Application.module() {
  install(ContentNegotiation) {
    gson()
  }

  routing {
    get("/api/data") {
      call.respond(mapOf("message" to "Hello, world!"))
    }

    get("/api/data1") {
      val geoJson = SampleServerRepository().getPointsJson()
      call.respond(geoJson)
    }
  }
}