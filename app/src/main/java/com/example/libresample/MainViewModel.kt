package com.example.libresample

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class MainViewState(
  val geoJson: String = "",
  val menuList: List<String> = listOf("1", "2","3")
)

class MainViewModel(
  private val dataSource:RemoteDataSource = RemoteDataSource()
): ViewModel() {

  private val _uiState = MutableStateFlow(MainViewState())
  val uiState = _uiState.asStateFlow()
  init {

    _uiState.update { it.copy(menuList =  dataSource.urls.map { it.key }) }

    viewModelScope.launch {
      val result = dataSource.fetchGeoJson(dataSource.urls.map { it.value }[1])
      result.onSuccess { geoJson ->
        _uiState.update { it.copy(geoJson = geoJson) }
      }.onFailure { error ->
        error.printStackTrace()
      }
    }
  }

  fun selectItem()
  {

  }

}