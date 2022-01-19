package com.carlosgracite.planningpoker.entity

import com.squareup.moshi.JsonClass

const val MIN_USERNAME_CHARACTERS = 3

@JsonClass(generateAdapter = true)
data class User(
    val id: String,
    val name: String
)