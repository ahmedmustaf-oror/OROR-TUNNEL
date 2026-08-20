package com.example.data.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class HttpRequestConfig(
    val method: String = "GET",
    val host: String = "",
    val path: String = "/",
    val customPayload: String = "",
    val headers: Map<String, String> = emptyMap(),
    val sni: String = "",
    val useTls: Boolean = true,
    val proxyHost: String = "",
    val proxyPort: Int = 8080
)

@JsonClass(generateAdapter = true)
data class AppUpdateInfo(
    @Json(name = "latest_version_code") val latestVersionCode: Int = 1,
    @Json(name = "latest_version_name") val latestVersionName: String = "1.0.0",
    @Json(name = "min_version_code") val minVersionCode: Int = 1,
    @Json(name = "download_url") val downloadUrl: String = "",
    @Json(name = "force_update") val forceUpdate: Boolean = false,
    val changelog: String = ""
)

@JsonClass(generateAdapter = true)
data class MaintenanceInfo(
    @Json(name = "maintenance_mode") val maintenanceMode: Boolean = false,
    val title: String = "OROR TUNNEL تحت الصيانة حاليًا",
    val message: String = "نقوم بتحسين الخدمة وتحديث السيرفرات لتقديم أسرع اتصال ممكن. يرجى المحاولة لاحقًا."
)

@JsonClass(generateAdapter = true)
data class Announcement(
    val id: String = "",
    val title: String = "",
    val description: String = "",
    val link: String = "",
    val active: Boolean = true
)

@JsonClass(generateAdapter = true)
data class NotificationItem(
    val id: String = "",
    val title: String = "",
    val message: String = "",
    val type: String = "INFO",
    val priority: String = "NORMAL",
    @Json(name = "created_at") val createdAt: String = ""
)

@JsonClass(generateAdapter = true)
data class ApiResponse<T>(
    val success: Boolean = false,
    val message: String = "",
    val code: String = "",
    val data: T? = null
)
