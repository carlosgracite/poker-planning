package com.carlosgracite.planningpoker.ui.authentication

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.carlosgracite.planningpoker.domain.usecase.profile.GetUserProfileUseCase
import com.carlosgracite.planningpoker.domain.usecase.room.JoinRoomUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthenticationViewModel @Inject constructor(
    private val getUserProfileUseCase: GetUserProfileUseCase,
    private val joinRoomUseCase: JoinRoomUseCase
) : ViewModel() {

    val uiState = MutableStateFlow(AuthenticationState())

    init {
        viewModelScope.launch {
            getUserProfileUseCase.execute().collect { user ->
                uiState.value = uiState.value.copy(username = user?.name ?: "")
            }
        }
    }

    fun handleEvent(authenticationEvent: AuthenticationEvent) {
        when (authenticationEvent) {
            AuthenticationEvent.Authenticate -> {
                authenticate()
            }
            AuthenticationEvent.DismissError -> {
                dismissError()
            }
            is AuthenticationEvent.UserNameChanged -> {
                updateUserName(authenticationEvent.userName)
            }
            is AuthenticationEvent.RoomIdChanged -> {
                updateRoomId(authenticationEvent.roomId)
            }
        }
    }

    private fun authenticate() {
        uiState.value = uiState.value.copy(
            isLoading = true
        )

        with(uiState.value) {
            viewModelScope.launch {
                val result = joinRoomUseCase.execute(username, roomId)

                when (result) {
                    is JoinRoomUseCase.Result.Success -> {
                        uiState.value = uiState.value.copy(
                            isLoading = false
                        )
                    }
                    is JoinRoomUseCase.Result.Failure -> {
                        uiState.value = uiState.value.copy(
                            isLoading = false,
                            error = result.cause,
                        )
                    }
                }
            }
        }
    }

    private fun dismissError() {
        uiState.value = uiState.value.copy(error = null)
    }

    private fun updateUserName(userName: String) {
        uiState.value = uiState.value.copy(username = userName)
    }

    private fun updateRoomId(roomId: String) {
        uiState.value = uiState.value.copy(roomId = roomId)
    }

}