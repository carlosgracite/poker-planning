package com.carlosgracite.planningpoker.ui

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.lifecycle.viewmodel.compose.viewModel
import com.carlosgracite.planningpoker.ui.authentication.Authentication
import com.carlosgracite.planningpoker.ui.room.PlanningRoom

@ExperimentalFoundationApi
@Composable
fun App() {
    val viewModel: AppViewModel = viewModel()

    val state = viewModel.uiState.collectAsState().value

    when (state.screen) {
        Screen.SPLASH -> {
            // TODO()
        }
        Screen.AUTHENTICATION -> {
            Authentication()
        }
        Screen.ROOM -> {
            PlanningRoom()
        }
    }
}