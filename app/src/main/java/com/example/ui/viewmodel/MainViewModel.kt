package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.AppDatabase
import com.example.data.local.PreferencesManager
import com.example.data.model.*
import com.example.data.remote.NetworkClient
import com.example.data.repository.TunnelRepository
import com.example.vpn.VpnManager
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class MainViewModel(application: Application) : AndroidViewModel(application) {

    private val db = AppDatabase.getDatabase(application)
    private val apiService = NetworkClient.createService()
    val repository = TunnelRepository(apiService, db.serverDao(), db.configDao())
    val preferencesManager = PreferencesManager(application)

    // Flow State
    val servers: StateFlow<List<TunnelServer>> = repository.allServers.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    val configs: StateFlow<List<TunnelConfig>> = repository.allConfigs.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    val vpnState: StateFlow<VpnState> = VpnManager.vpnState
    val vpnStats: StateFlow<VpnStats> = VpnManager.vpnStats
    val vpnLogs: StateFlow<List<String>> = VpnManager.logs

    fun clearVpnLogs() {
        VpnManager.clearLogs()
    }

    private val _updateStatusMessage = MutableStateFlow<String?>(null)
    val updateStatusMessage: StateFlow<String?> = _updateStatusMessage.asStateFlow()

    val selectedServerId: StateFlow<String> = preferencesManager.selectedServerId.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = "srv_eg_01"
    )

    val selectedConfigId: StateFlow<String> = preferencesManager.selectedConfigId.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = "cfg_ssh_fast"
    )

    val customPayload: StateFlow<String> = preferencesManager.customPayload.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = ""
    )

    private val _announcements = MutableStateFlow<List<Announcement>>(emptyList())
    val announcements: StateFlow<List<Announcement>> = _announcements.asStateFlow()

    private val _notifications = MutableStateFlow<List<NotificationItem>>(emptyList())
    val notifications: StateFlow<List<NotificationItem>> = _notifications.asStateFlow()

    private val _updateInfo = MutableStateFlow<AppUpdateInfo?>(null)
    val updateInfo: StateFlow<AppUpdateInfo?> = _updateInfo.asStateFlow()

    private val _maintenanceInfo = MutableStateFlow<MaintenanceInfo?>(null)
    val maintenanceInfo: StateFlow<MaintenanceInfo?> = _maintenanceInfo.asStateFlow()

    // Filters
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _selectedCountryFilter = MutableStateFlow("ALL")
    val selectedCountryFilter: StateFlow<String> = _selectedCountryFilter.asStateFlow()

    init {
        syncRemoteData()
    }

    fun syncRemoteData() {
        viewModelScope.launch {
            repository.fetchRemoteServers()
            repository.fetchRemoteConfigs()
            
            repository.fetchAnnouncements().onSuccess { _announcements.value = it }
            repository.fetchNotifications().onSuccess { _notifications.value = it }
            repository.fetchUpdates().onSuccess { _updateInfo.value = it }
            repository.fetchMaintenance().onSuccess { _maintenanceInfo.value = it }

            repository.registerDevice("dev_" + System.currentTimeMillis(), android.os.Build.MODEL, android.os.Build.VERSION.RELEASE)
        }
    }

    fun selectServer(id: String) {
        viewModelScope.launch { preferencesManager.saveSelectedServer(id) }
    }

    fun selectConfig(id: String) {
        viewModelScope.launch { preferencesManager.saveSelectedConfig(id) }
    }

    fun saveCustomPayload(payload: String) {
        viewModelScope.launch { preferencesManager.saveCustomPayload(payload) }
    }

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun setCountryFilter(country: String) {
        _selectedCountryFilter.value = country
    }

    fun toggleFavoriteServer(id: String, fav: Boolean) {
        viewModelScope.launch { repository.toggleFavoriteServer(id, fav) }
    }

    fun toggleFavoriteConfig(id: String, fav: Boolean) {
        viewModelScope.launch { repository.toggleFavoriteConfig(id, fav) }
    }

    fun addCustomConfig(config: TunnelConfig) {
        viewModelScope.launch { repository.addCustomConfig(config) }
    }

    fun deleteConfig(id: String) {
        viewModelScope.launch { repository.deleteConfig(id) }
    }

    fun toggleVpnConnection(context: android.content.Context) {
        val currentServer = servers.value.find { it.id == selectedServerId.value }
            ?: servers.value.firstOrNull()

        val currentConfig = configs.value.find { it.id == selectedConfigId.value }
            ?: configs.value.firstOrNull()

        if (currentServer == null || currentConfig == null) {
            // Cannot connect without a server and config
            return
        }

        if (vpnState.value == VpnState.CONNECTED || vpnState.value == VpnState.CONNECTING) {
            VpnManager.disconnect(context)
        } else {
            VpnManager.connect(context, currentServer, currentConfig)
        }
    }
}
