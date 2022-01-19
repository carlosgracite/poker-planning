package com.carlosgracite.planningpoker.domain.usecase.profile

import com.carlosgracite.planningpoker.entity.User
import com.carlosgracite.planningpoker.repository.SettingsStore
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetUserProfileUseCase @Inject constructor(
    private val settingsStore: SettingsStore
) {

    fun execute(): Flow<User?> {
        return settingsStore.getUser()
    }
}