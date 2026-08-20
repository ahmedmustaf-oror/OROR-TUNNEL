package com.example.data.local

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore by preferencesDataStore(name = "oror_tunnel_prefs")

class PreferencesManager(private val context: Context) {

    companion object {
        val SELECTED_SERVER_ID = stringPreferencesKey("selected_server_id")
        val SELECTED_CONFIG_ID = stringPreferencesKey("selected_config_id")
        val LANGUAGE = stringPreferencesKey("app_language")
        val BASE_API_URL = stringPreferencesKey("base_api_url")
        val CUSTOM_PAYLOAD = stringPreferencesKey("custom_payload")
        val AUTO_RECONNECT = booleanPreferencesKey("auto_reconnect")
        val ONBOARDING_COMPLETED = booleanPreferencesKey("onboarding_completed")
    }

    val selectedServerId: Flow<String> = context.dataStore.data.map { it[SELECTED_SERVER_ID] ?: "srv_eg_01" }
    val selectedConfigId: Flow<String> = context.dataStore.data.map { it[SELECTED_CONFIG_ID] ?: "cfg_ssh_fast" }
    val language: Flow<String> = context.dataStore.data.map { it[LANGUAGE] ?: "ar" }
    val baseApiUrl: Flow<String> = context.dataStore.data.map { it[BASE_API_URL] ?: "https://elias555.serv00.net/orortunnel/" }
    val customPayload: Flow<String> = context.dataStore.data.map { it[CUSTOM_PAYLOAD] ?: "" }
    val autoReconnect: Flow<Boolean> = context.dataStore.data.map { it[AUTO_RECONNECT] ?: true }
    val onboardingCompleted: Flow<Boolean> = context.dataStore.data.map { it[ONBOARDING_COMPLETED] ?: false }

    suspend fun saveSelectedServer(id: String) {
        context.dataStore.edit { it[SELECTED_SERVER_ID] = id }
    }

    suspend fun saveSelectedConfig(id: String) {
        context.dataStore.edit { it[SELECTED_CONFIG_ID] = id }
    }

    suspend fun saveLanguage(lang: String) {
        context.dataStore.edit { it[LANGUAGE] = lang }
    }

    suspend fun saveBaseApiUrl(url: String) {
        context.dataStore.edit { it[BASE_API_URL] = url }
    }

    suspend fun saveCustomPayload(payload: String) {
        context.dataStore.edit { it[CUSTOM_PAYLOAD] = payload }
    }

    suspend fun setOnboardingCompleted(completed: Boolean) {
        context.dataStore.edit { it[ONBOARDING_COMPLETED] = completed }
    }
}
