package com.carlosgracite.planningpoker.ui.room

import com.carlosgracite.planningpoker.entity.PokerCard

sealed class PlanningRoomEvent {

    object LeaveRoom : PlanningRoomEvent()

    data class Vote(val card: PokerCard) : PlanningRoomEvent()

    object RevealResults : PlanningRoomEvent()

    object StartVoting : PlanningRoomEvent()

}