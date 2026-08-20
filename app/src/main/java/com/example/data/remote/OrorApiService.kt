package com.example.data.remote

import com.example.data.model.*
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface OrorApiService {

    @GET("index.php?action=servers")
    suspend fun getServers(): ApiResponse<List<TunnelServer>>

    @GET("index.php?action=configs")
    suspend fun getConfigs(): ApiResponse<List<TunnelConfig>>

    @GET("index.php?action=settings")
    suspend fun getSettings(): ApiResponse<Map<String, Any>>

    @GET("index.php?action=announcements")
    suspend fun getAnnouncements(): ApiResponse<List<Announcement>>

    @GET("index.php?action=notifications")
    suspend fun getNotifications(): ApiResponse<List<NotificationItem>>

    @GET("index.php?action=updates")
    suspend fun getUpdates(): ApiResponse<AppUpdateInfo>

    @GET("index.php?action=maintenance")
    suspend fun getMaintenance(): ApiResponse<MaintenanceInfo>

    @POST("index.php?action=register")
    suspend fun registerDevice(@Body body: Map<String, String>): ApiResponse<Map<String, Any>>

    @POST("index.php?action=stats")
    suspend fun sendStats(@Body body: Map<String, Long>): ApiResponse<Map<String, Any>>

    @GET("index.php?action=heartbeat")
    suspend fun sendHeartbeat(@Query("device_id") deviceId: String): ApiResponse<Unit>
}
