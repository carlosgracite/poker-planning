package com.carlosgracite.planningpoker.ui.authentication

import com.carlosgracite.planningpoker.domain.model.JoinRoomError

data class AuthenticationState(
    val username: String = "",
    val roomId: String? = null,
    val isLoading: Boolean = false,
    val error: JoinRoomError? = null,
) {
    fun isFormValid(): Boolean {
        return username.isNotBlank()
    }

    fun isJoinMode(): Boolean {
        return !roomId.isNullOrBlank()
    }
}