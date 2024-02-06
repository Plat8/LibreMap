package com.example.libresample

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class MainState(
  val geoJson: String = "",
  val menuList: List<String> = listOf(),
  val selectedItem: Int = 0
)

sealed class MainSideEffect() {
  data class ShowToast(val message: String) : MainSideEffect()
}

class MainViewModel(
  private val dataSource: RemoteDataSource = RemoteDataSource()
) : ViewModel() {

  private val _uiState = MutableStateFlow(MainState())
  val uiState = _uiState.asStateFlow()

  private val _sideEffect = MutableSharedFlow<MainSideEffect>()
  val sideEffect: Flow<MainSideEffect> = _sideEffect

  init {
    _uiState.update { it.copy(menuList = dataSource.urls.map { it.key }) }
    selectItem(0)
  }

  fun selectItem(index: Int) {
    viewModelScope.launch {
      val key = dataSource.urls.keys.elementAt(index)
      _sideEffect.emit(MainSideEffect.ShowToast("Fetch ${key} from server"))
      val result = dataSource.fetchGeoJson(dataSource.urls.get(key) ?: "")
      result.onSuccess { geoJson ->
        _uiState.update { it.copy(geoJson = geoJson, selectedItem = index) }
      }.onFailure { error ->
        error.printStackTrace()
        _sideEffect.emit(MainSideEffect.ShowToast(error.message ?: "Error occured!"))
      }
    }
  }

}