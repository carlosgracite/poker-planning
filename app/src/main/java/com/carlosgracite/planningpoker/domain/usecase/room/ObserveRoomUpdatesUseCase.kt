package com.carlosgracite.planningpoker.domain.usecase.room

import com.carlosgracite.planningpoker.api.websocket.PokerPlanningSocketService
import com.carlosgracite.planningpoker.entity.Room
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import timber.log.Timber
import javax.inject.Inject

class ObserveRoomUpdatesUseCase @Inject constructor(
    private val pokerPlanningSocketService: PokerPlanningSocketService
) {

    fun execute(): Flow<Room> {
        return pokerPlanningSocketService.observeRoom()
            .onEach { Timber.d("Room update received: $it") }
            .map { it.room }
    }

}