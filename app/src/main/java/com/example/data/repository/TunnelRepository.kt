package com.example.data.repository

import com.example.data.local.ConfigDao
import com.example.data.local.ConfigEntity
import com.example.data.local.ServerDao
import com.example.data.local.ServerEntity
import com.example.data.model.*
import com.example.data.remote.OrorApiService
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class TunnelRepository(
    private val apiService: OrorApiService,
    private val serverDao: ServerDao,
    private val configDao: ConfigDao
) {

    val allServers: Flow<List<TunnelServer>> = serverDao.getAllServers().map { list ->
        list.map { entity ->
            TunnelServer(
                id = entity.id,
                name = entity.name,
                country = entity.country,
                flag = entity.flag,
                host = entity.host,
                port = entity.port,
                protocol = entity.protocol,
                status = entity.status,
                maxUsers = entity.maxUsers,
                currentUsers = entity.currentUsers,
                load = entity.load,
                ping = entity.ping,
                premium = entity.premium,
                priority = entity.priority,
                isFavorite = entity.isFavorite
            )
        }
    }

    val allConfigs: Flow<List<TunnelConfig>> = configDao.getAllConfigs().map { list ->
        list.map { entity ->
            TunnelConfig(
                id = entity.id,
                name = entity.name,
                protocol = entity.protocol,
                serverId = entity.serverId,
                serverName = entity.serverName,
                host = entity.host,
                port = entity.port,
                username = entity.username,
                password = entity.password,
                payload = entity.payload,
                sni = entity.sni,
                customHeaders = entity.customHeaders,
                expiration = entity.expiration,
                status = entity.status,
                premium = entity.premium,
                category = entity.category,
                isFavorite = entity.isFavorite,
                isCustom = entity.isCustom,
                socksIp = entity.socksIp,
                socksPort = entity.socksPort,
                icon = entity.icon,
                logoUrl = entity.logoUrl,
                fileUrl = entity.fileUrl
            )
        }
    }

    suspend fun fetchRemoteServers(): Result<List<TunnelServer>> {
        return try {
            val response = apiService.getServers()
            if (response.success && response.data != null) {
                val entities = response.data.map { s ->
                    ServerEntity(
                        id = s.id,
                        name = s.name,
                        country = s.country,
                        flag = s.flag,
                        host = s.host,
                        port = s.port,
                        protocol = s.protocol,
                        status = s.status,
                        maxUsers = s.maxUsers,
                        currentUsers = s.currentUsers,
                        load = s.load,
                        ping = s.ping,
                        premium = s.premium,
                        priority = s.priority,
                        isFavorite = s.isFavorite
                    )
                }
                serverDao.insertServers(entities)
                Result.success(response.data)
            } else {
                Result.failure(Exception(response.message))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun fetchRemoteConfigs(): Result<List<TunnelConfig>> {
        return try {
            val response = apiService.getConfigs()
            if (response.success && response.data != null) {
                val entities = response.data.map { c ->
                    ConfigEntity(
                        id = c.id,
                        name = c.name,
                        protocol = c.protocol,
                        serverId = c.effectiveServerId,
                        serverName = c.effectiveServerName,
                        host = c.host,
                        port = c.port,
                        username = c.username,
                        password = c.password,
                        payload = c.payload,
                        sni = c.sni,
                        customHeaders = c.customHeaders,
                        expiration = c.effectiveExpiration,
                        status = c.status,
                        premium = c.effectivePremium,
                        category = c.category,
                        isFavorite = c.isFavorite,
                        isCustom = c.isCustom,
                        socksIp = c.socksIp,
                        socksPort = c.socksPort,
                        icon = c.icon,
                        logoUrl = c.logoUrl,
                        fileUrl = c.fileUrl
                    )
                }
                configDao.insertConfigs(entities)
                Result.success(response.data)
            } else {
                Result.failure(Exception(response.message))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun addCustomConfig(config: TunnelConfig) {
        val entity = ConfigEntity(
            id = if (config.id.isEmpty()) "custom_" + System.currentTimeMillis() else config.id,
            name = config.name,
            protocol = config.protocol,
            serverId = config.serverId,
            serverName = config.serverName,
            host = config.host,
            port = config.port,
            username = config.username,
            password = config.password,
            payload = config.payload,
            sni = config.sni,
            customHeaders = config.customHeaders,
            expiration = config.expiration,
            status = "active",
            premium = false,
            category = "Custom",
            isFavorite = true,
            isCustom = true
        )
        configDao.insertConfig(entity)
    }

    suspend fun deleteConfig(id: String) {
        configDao.deleteConfig(id)
    }

    suspend fun toggleFavoriteServer(id: String, fav: Boolean) {
        serverDao.updateFavorite(id, fav)
    }

    suspend fun toggleFavoriteConfig(id: String, fav: Boolean) {
        configDao.updateFavorite(id, fav)
    }

    suspend fun fetchAnnouncements(): Result<List<Announcement>> {
        return try {
            val res = apiService.getAnnouncements()
            if (res.success && res.data != null) Result.success(res.data) else Result.failure(Exception(res.message))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun fetchNotifications(): Result<List<NotificationItem>> {
        return try {
            val res = apiService.getNotifications()
            if (res.success && res.data != null) Result.success(res.data) else Result.failure(Exception(res.message))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun fetchUpdates(): Result<AppUpdateInfo> {
        return try {
            val res = apiService.getUpdates()
            if (res.success && res.data != null) Result.success(res.data) else Result.failure(Exception(res.message))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun fetchMaintenance(): Result<MaintenanceInfo> {
        return try {
            val res = apiService.getMaintenance()
            if (res.success && res.data != null) Result.success(res.data) else Result.failure(Exception(res.message))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun registerDevice(deviceId: String, model: String, androidVer: String): Result<Unit> {
        return try {
            val map = mapOf(
                "device_id" to deviceId,
                "app_version" to "1.0.0",
                "device_model" to model,
                "android_version" to androidVer
            )
            apiService.registerDevice(map)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
