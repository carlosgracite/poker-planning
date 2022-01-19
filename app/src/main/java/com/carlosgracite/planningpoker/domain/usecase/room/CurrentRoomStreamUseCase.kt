package com.carlosgracite.planningpoker.domain.usecase.room

import com.carlosgracite.planningpoker.repository.SettingsStore
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class CurrentRoomStreamUseCase @Inject constructor(
    private val settingsStore: SettingsStore
) {

    fun execute(): Flow<String?> {
        return settingsStore.getCurrentRoomId()
    }
}