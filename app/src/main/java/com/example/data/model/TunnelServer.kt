package com.example.data.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class TunnelServer(
    val id: String = "",
    val name: String = "",
    val country: String = "",
    val flag: String = "🌐",
    val host: String = "",
    val port: Int = 22,
    val protocol: String = "SSH",
    val status: String = "active",
    @Json(name = "max_users") val maxUsers: Int = 500,
    @Json(name = "current_users") val currentUsers: Int = 0,
    val load: Int = 10,
    val ping: Int = 30,
    val premium: Boolean = false,
    val priority: Int = 1,
    var isFavorite: Boolean = false
)
