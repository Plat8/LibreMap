package com.example.libresample

import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.http.HttpStatusCode


class RemoteDataSource {

  private val httpClient = HttpClient()

  private val serverAddress = "http://localhost:8080"

  val urls: Map<String,String> =  mapOf(
    "Sample" to "${serverAddress}/api/data1",
    "Points" to "${serverAddress}/api/data2",
    "Route" to "${serverAddress}/api/data3"
  )

  suspend fun fetchGeoJson(url: String): Result<String> {
    return makeRequest<String>(url)
  }

  private suspend inline fun <reified T> makeRequest(url: String): Result<T> {
    return try {
      val response = httpClient.get<T>(url)
      Result.success(response)
    } catch (e: Exception) {
      val statusCode = (e as? io.ktor.client.features.ResponseException)?.response?.status
        ?: HttpStatusCode.InternalServerError
      Result.failure(e)
    }
  }


}