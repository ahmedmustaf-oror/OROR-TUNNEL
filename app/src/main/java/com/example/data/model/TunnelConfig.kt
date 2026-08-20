package com.example.data.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class TunnelConfig(
    val id: String = "",
    val name: String = "",
    val protocol: String = "SSH",
    @Json(name = "server_id") val serverId: String = "",
    @Json(name = "router_id") val routerId: String = "",
    @Json(name = "server_name") val serverName: String = "",
    @Json(name = "router_name") val routerName: String = "",
    val host: String = "",
    val port: Int = 80,
    val username: String = "",
    val password: String = "",
    val payload: String = "",
    val sni: String = "",
    @Json(name = "custom_headers") val customHeaders: String = "",
    @Json(name = "socks_ip") val socksIp: String = "",
    @Json(name = "socks_port") val socksPort: Int = 0,
    val icon: String = "⚡",
    @Json(name = "logo_url") val logoUrl: String = "",
    @Json(name = "file_url") val fileUrl: String = "",
    val expiration: String = "2028-12-31",
    @Json(name = "expires_at") val expiresAt: String = "",
    val status: String = "active",
    val premium: Boolean = false,
    @Json(name = "is_premium") val isPremium: Boolean = false,
    val category: String = "General",
    var isFavorite: Boolean = false,
    var isCustom: Boolean = false
) {
    val effectiveServerId: String get() = serverId.ifEmpty { routerId }
    val effectiveServerName: String get() = serverName.ifEmpty { routerName }
    val effectiveExpiration: String get() = expiration.ifEmpty { expiresAt }
    val effectivePremium: Boolean get() = premium || isPremium
}
