package com.carlosgracite.planningpoker

import android.app.Application
import com.tinder.scarlet.Lifecycle
import dagger.hilt.android.HiltAndroidApp
import timber.log.Timber
import javax.inject.Inject

@HiltAndroidApp
class PlanningPokerApp : Application() {

    @Inject
    lateinit var lifecycle: Lifecycle

    override fun onCreate() {
        super.onCreate()

        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }
    }

}