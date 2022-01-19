package com.carlosgracite.planningpoker

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.lifecycle.lifecycleScope
import com.carlosgracite.planningpoker.ui.App
import com.carlosgracite.planningpoker.ui.authentication.Authentication
import com.carlosgracite.planningpoker.ui.room.PlanningRoom
import com.carlosgracite.planningpoker.ui.theme.PlanningPokerTheme
import com.google.accompanist.insets.ProvideWindowInsets
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

@ExperimentalFoundationApi
@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            PlanningPokerTheme {
                ProvideWindowInsets(windowInsetsAnimationsEnabled = true, consumeWindowInsets = false) {
                    Surface(color = MaterialTheme.colors.background) {
                        App()
                    }
                }
            }
        }
    }
}