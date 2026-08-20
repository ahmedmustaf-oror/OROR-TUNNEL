package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "servers")
data class ServerEntity(
    @PrimaryKey val id: String,
    val name: String,
    val country: String,
    val flag: String,
    val host: String,
    val port: Int,
    val protocol: String,
    val status: String,
    val maxUsers: Int,
    val currentUsers: Int,
    val load: Int,
    val ping: Int,
    val premium: Boolean,
    val priority: Int,
    val isFavorite: Boolean
)

@Entity(tableName = "configs")
data class ConfigEntity(
    @PrimaryKey val id: String,
    val name: String,
    val protocol: String,
    val serverId: String,
    val serverName: String,
    val host: String,
    val port: Int,
    val username: String,
    val password: String,
    val payload: String,
    val sni: String,
    val customHeaders: String,
    val expiration: String,
    val status: String,
    val premium: Boolean,
    val category: String,
    val isFavorite: Boolean,
    val isCustom: Boolean,
    val socksIp: String = "",
    val socksPort: Int = 0,
    val icon: String = "⚡",
    val logoUrl: String = "",
    val fileUrl: String = ""
)
