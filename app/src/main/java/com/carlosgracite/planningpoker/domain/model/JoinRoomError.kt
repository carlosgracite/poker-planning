package com.carlosgracite.planningpoker.domain.model

sealed class JoinRoomError {
    object RoomNotFound : JoinRoomError()
    object InvalidUserName : JoinRoomError()
    object NetworkError : JoinRoomError()
}