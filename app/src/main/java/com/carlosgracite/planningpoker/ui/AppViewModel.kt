package com.carlosgracite.planningpoker.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.carlosgracite.planningpoker.domain.usecase.room.CurrentRoomStreamUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AppViewModel @Inject constructor(
    private val currentRoomStreamUseCase: CurrentRoomStreamUseCase
) : ViewModel() {

    val uiState = MutableStateFlow(AppState())

    init {
        viewModelScope.launch {
            currentRoomStreamUseCase.execute().collect {
                val screen = if (it == null) {
                    Screen.AUTHENTICATION
                } else {
                    Screen.ROOM
                }
                uiState.value = uiState.value.copy(screen = screen)
            }
        }
    }

}