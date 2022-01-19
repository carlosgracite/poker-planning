package com.carlosgracite.planningpoker.domain.usecase.room

import com.carlosgracite.planningpoker.api.websocket.PokerPlanningSocketService
import com.carlosgracite.planningpoker.api.websocket.LeaveRoomMessage
import com.carlosgracite.planningpoker.repository.SettingsStore
import javax.inject.Inject

class LeaveRoomUseCase @Inject constructor(
    private val settingsStore: SettingsStore,
    private val pokerPlanningSocketService: PokerPlanningSocketService
) {

    suspend fun execute() {
        settingsStore.removeRoomId()
        pokerPlanningSocketService.leaveRoom(LeaveRoomMessage())
    }

}