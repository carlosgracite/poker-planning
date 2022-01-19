package com.carlosgracite.planningpoker.api.websocket

import com.carlosgracite.planningpoker.entity.PokerCard
import com.carlosgracite.planningpoker.entity.Room
import com.carlosgracite.planningpoker.entity.User
import com.squareup.moshi.JsonClass

const val TYPE_ROOM_UPDATE_MESSAGE = "room-update"

interface WebSocketMessage {
    val type: String
}

@JsonClass(generateAdapter = true)
data class StartSessionMessage(
    override val type: String = "start-session",
    val user: User,
    val roomId: String
): WebSocketMessage

@JsonClass(generateAdapter = true)
data class LeaveRoomMessage(
    override val type: String = "leave-room"
): WebSocketMessage

@JsonClass(generateAdapter = true)
data class VoteMessage(
    override val type: String = "vote",
    val card: PokerCard
): WebSocketMessage

@JsonClass(generateAdapter = true)
data class RevealResultsMessage(
    override val type: String = "reveal-results"
): WebSocketMessage

@JsonClass(generateAdapter = true)
data class StartVotingMessage(
    override val type: String = "start-voting"
): WebSocketMessage

@JsonClass(generateAdapter = true)
data class RoomUpdateMessage(
    override val type: String = TYPE_ROOM_UPDATE_MESSAGE,
    val room: Room
): WebSocketMessage