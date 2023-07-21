package dev.spravedlivo.flibuster

import android.content.Context
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.first


enum class SettingsDefaults(val key: String, val value: String) {
    URL("flibusta_url", "https://flibusta.is");

    companion object {
        fun findValue(key: String): String? {
            for (entry in SettingsDefaults.values()) {
                if (entry.key == key) return entry.value
            }
            return null
        }
    }
}


object Settings {
    private val changeCallbacks = mutableMapOf<String, (String, String) -> Unit>()
    suspend fun read(context: Context, key: String): String? {
        val dataStoreKey = stringPreferencesKey(key)
        val preferences = context.dataStore.data.first()
        return preferences[dataStoreKey]
    }
    fun registerChangeCallback(key: String, block: (String, String) -> Unit) {
        changeCallbacks[key] = block
    }
    fun unregisterChangeCallback(key: String) {
        changeCallbacks.remove(key)
    }
    fun readFromPreferences(preferences: Preferences, key: String): String? {
        val dataStoreKey = stringPreferencesKey(key)
        return preferences[dataStoreKey]
    }
    fun readFromPreferencesOrDefault(preferences: Preferences, key: String): String? {
        val dataStoreKey = stringPreferencesKey(key)
        return preferences[dataStoreKey] ?: return SettingsDefaults.findValue(key)
    }
    fun readFromPreferencesOrDefault(preferences: Preferences, key: String, default: String?): String? {
        val dataStoreKey = stringPreferencesKey(key)
        return preferences[dataStoreKey] ?: return default
    }

    suspend fun readOrDefault(context: Context, key: String): String? {
        return read(context, key) ?: return SettingsDefaults.findValue(key)
    }
    suspend fun readOrDefault(context: Context, key: String, default: String?): String? {
        return read(context, key) ?: return default
    }

    suspend fun save(context: Context, key: String, value: String) {
        val cb = changeCallbacks[key]
        if (cb != null) cb(key, value)
        val dataStoreKey = stringPreferencesKey(key)
        context.dataStore.edit { settings ->
            settings[dataStoreKey] = value
        }
    }
}