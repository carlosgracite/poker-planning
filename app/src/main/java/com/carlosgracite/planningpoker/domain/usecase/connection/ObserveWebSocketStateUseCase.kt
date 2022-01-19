package com.carlosgracite.planningpoker.domain.usecase.connection

import com.carlosgracite.planningpoker.api.websocket.PokerPlanningSocketService
import com.carlosgracite.planningpoker.domain.usecase.room.SubscribeToRoomUpdatesUseCase
import com.tinder.scarlet.State
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class ObserveWebSocketStateUseCase @Inject constructor(
    private val pokerPlanningSocketService: PokerPlanningSocketService,
    private val subscribeToRoomUpdatesUseCase: SubscribeToRoomUpdatesUseCase
) {

    suspend fun execute(): Flow<Result> {
        return pokerPlanningSocketService.observeState()
            .map {
                if (it is State.Connected) {
                    subscribeToRoomUpdatesUseCase.execute()
                    Result.CONNECTED
                } else {
                    Result.CONNECTING
                }
            }
    }

    enum class Result {
        CONNECTING, CONNECTED
    }

}
