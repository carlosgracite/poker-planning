package com.carlosgracite.planningpoker.ui

data class AppState(
    val screen: Screen = Screen.SPLASH
)

enum class Screen {
    SPLASH, AUTHENTICATION, ROOM
}