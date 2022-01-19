package com.carlosgracite.planningpoker.api.websocket

import com.tinder.scarlet.State
import com.tinder.scarlet.ws.Receive
import com.tinder.scarlet.ws.Send
import kotlinx.coroutines.flow.Flow

interface PokerPlanningSocketService {

    @Receive
    fun observeState(): Flow<State>

    @Send
    fun startSession(message: StartSessionMessage)

    @Send
    fun leaveRoom(message: LeaveRoomMessage)

    @Send
    fun vote(message: VoteMessage)

    @Send
    fun revealResults(message: RevealResultsMessage)

    @Send
    fun startVoting(startVotingMessage: StartVotingMessage)

    @Receive
    fun observeRoom(): Flow<RoomUpdateMessage>

}