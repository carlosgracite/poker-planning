package com.carlosgracite.planningpoker.repository

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.carlosgracite.planningpoker.entity.User
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

@Singleton
class SettingsStore @Inject constructor(
    @ApplicationContext private val context: Context
) {

    private val KEY_USER_ID = stringPreferencesKey("user_id")
    private val KEY_USER_NAME = stringPreferencesKey("user_name")
    private val KEY_CURRENT_ROOM_ID = stringPreferencesKey("current_room_id")

    suspend fun getOrCreateUser(userName: String): User {
        val user = getUser().first()?.copy(name = userName)
            ?: User(UUID.randomUUID().toString(), userName)

        context.dataStore.edit { settings ->
            settings[KEY_USER_ID] = user.id
            settings[KEY_USER_NAME] = user.name
        }

        return user
    }

    fun getUser(): Flow<User?> {
        return context.dataStore.data
            .map { settings ->
                val userId = settings[KEY_USER_ID]
                val userName = settings[KEY_USER_NAME]

                if (userId == null || userName == null) {
                    return@map null
                }

                User(userId, userName)
            }
    }

    fun getCurrentRoomId(): Flow<String?> {
        return context.dataStore.data
            .map { settings ->
                settings[KEY_CURRENT_ROOM_ID]
            }
    }

    suspend fun saveRoomId(roomId: String) {
        context.dataStore.edit { settings ->
            settings[KEY_CURRENT_ROOM_ID] = roomId
        }
    }

    suspend fun removeRoomId() {
        context.dataStore.edit { settings ->
            settings.remove(KEY_CURRENT_ROOM_ID)
        }
    }

}