package com.carlosgracite.planningpoker.ui.authentication

sealed class AuthenticationEvent {

    data class UserNameChanged(val userName: String) : AuthenticationEvent()

    data class RoomIdChanged(val roomId: String) : AuthenticationEvent()

    object Authenticate : AuthenticationEvent()

    object DismissError : AuthenticationEvent()

}