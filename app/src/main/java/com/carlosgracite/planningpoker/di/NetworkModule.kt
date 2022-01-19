package com.carlosgracite.planningpoker.di

import android.app.Application
import com.carlosgracite.planningpoker.BuildConfig
import com.carlosgracite.planningpoker.api.PlanningPokerApi
import com.carlosgracite.planningpoker.api.websocket.scarlet.CoroutinesStreamAdapterFactory
import com.carlosgracite.planningpoker.api.websocket.PokerPlanningSocketService
import com.carlosgracite.planningpoker.di.qualifier.ApiClientQualifier
import com.carlosgracite.planningpoker.di.qualifier.WebSocketClientQualifier
import com.carlosgracite.planningpoker.api.websocket.RoomUpdateMessage
import com.carlosgracite.planningpoker.api.websocket.TYPE_ROOM_UPDATE_MESSAGE
import com.carlosgracite.planningpoker.api.websocket.WebSocketMessage
import com.squareup.moshi.Moshi
import com.squareup.moshi.adapters.PolymorphicJsonAdapterFactory
import com.tinder.scarlet.Lifecycle
import com.tinder.scarlet.Scarlet
import com.tinder.scarlet.lifecycle.android.AndroidLifecycle
import com.tinder.scarlet.messageadapter.moshi.MoshiMessageAdapter
import com.tinder.scarlet.websocket.okhttp.newWebSocketFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
object NetworkModule {

    private const val PLANNING_POKER_API_URL = "https://rocky-dawn-40863.herokuapp.com/"
    private const val PLANNING_POKER_WEB_SOCKET_URL = "wss://rocky-dawn-40863.herokuapp.com/planning-poker"

    @Singleton
    @Provides
    fun providePlanningPokerApi(
        @ApiClientQualifier okHttpClient: OkHttpClient,
        moshi: Moshi
    ): PlanningPokerApi {
        return Retrofit.Builder()
            .baseUrl(PLANNING_POKER_API_URL)
            .client(okHttpClient)
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .build()
            .create(PlanningPokerApi::class.java)
    }

    @Singleton
    @Provides
    @WebSocketClientQualifier
    fun provideWebSocketHttpClient(): OkHttpClient {
        return OkHttpClient.Builder()
            .readTimeout(0, TimeUnit.MILLISECONDS)
            .apply {
                if (BuildConfig.DEBUG) {
                    addInterceptor(HttpLoggingInterceptor().also {
                        it.setLevel(HttpLoggingInterceptor.Level.BODY)
                    })
                }
            }
            .build()
    }

    @Singleton
    @Provides
    @ApiClientQualifier
    fun provideHttpClient(): OkHttpClient {
        return OkHttpClient.Builder()
            .apply {
                if (BuildConfig.DEBUG) {
                    addInterceptor(HttpLoggingInterceptor().also {
                        it.setLevel(HttpLoggingInterceptor.Level.BODY)
                    })
                }
            }
            .build()
    }

    @Singleton
    @Provides
    fun provideMoshi(): Moshi {
        return Moshi.Builder()
            .add(
                PolymorphicJsonAdapterFactory.of(WebSocketMessage::class.java, "type")
                    .withSubtype(RoomUpdateMessage::class.java, TYPE_ROOM_UPDATE_MESSAGE)
            )
            .build()
    }

    @Singleton
    @Provides
    fun provideSocketService(
        @WebSocketClientQualifier okHttpClient: OkHttpClient,
        lifecycle: Lifecycle,
        moshi: Moshi
    ): PokerPlanningSocketService {
        val scarletInstance = Scarlet.Builder()
            .lifecycle(lifecycle)
            .webSocketFactory(okHttpClient.newWebSocketFactory(PLANNING_POKER_WEB_SOCKET_URL))
            .addMessageAdapterFactory(MoshiMessageAdapter.Factory(moshi))
            .addStreamAdapterFactory(CoroutinesStreamAdapterFactory())
            .build()
        return scarletInstance.create(PokerPlanningSocketService::class.java)
    }

    @Singleton
    @Provides
    fun lifecycle(application: Application): Lifecycle {
        return AndroidLifecycle.ofApplicationForeground(application)
    }

}