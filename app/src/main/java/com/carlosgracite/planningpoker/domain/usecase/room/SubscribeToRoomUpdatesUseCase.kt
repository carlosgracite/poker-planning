package com.carlosgracite.planningpoker.domain.usecase.room

import com.carlosgracite.planningpoker.api.websocket.PokerPlanningSocketService
import com.carlosgracite.planningpoker.api.websocket.StartSessionMessage
import com.carlosgracite.planningpoker.repository.SettingsStore
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.first
import javax.inject.Inject

class SubscribeToRoomUpdatesUseCase @Inject constructor(
    private val pokerPlanningSocketService: PokerPlanningSocketService,
    private val settingsStore: SettingsStore
) {

    suspend fun execute() {
        val user = settingsStore.getUser().first()
        val roomId = settingsStore.getCurrentRoomId().first()

        if (user != null && roomId != null) {
            pokerPlanningSocketService.startSession(
                StartSessionMessage(
                    user = user,
                    roomId = roomId,
                )
            )
        }
    }
}