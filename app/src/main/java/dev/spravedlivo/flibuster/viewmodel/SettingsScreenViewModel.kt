package dev.spravedlivo.flibuster.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.spravedlivo.flibuster.Settings
import dev.spravedlivo.flibuster.data.SettingsScreenData
import dev.spravedlivo.flibuster.dataStore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class SettingsScreenViewModel : ViewModel() {
    private val _state = MutableStateFlow(SettingsScreenData(preferences = null))

    val state = _state.asStateFlow()
    fun readSettings(context: Context) {
        if (state.value.vmInit) {
            return
        }
        state.value.vmInit = true
        viewModelScope.launch {
            _state.update { currentState -> currentState.copy(loaded = false) }
            val preferences = context.dataStore.data.first()
            _state.update { currentState ->
                currentState.copy(
                    loaded = true,
                    preferences = preferences
                )
            }
        }
    }

    override fun onCleared() {
        Settings.unregisterChangeCallback("download_folder")
        super.onCleared()
    }


    fun updateKey(context: Context, key: String, value: String) {
        viewModelScope.launch {
            Settings.save(context, key, value)
        }
    }
    fun register(callback: (String, String) -> Unit) {
        Settings.registerChangeCallback("download_folder", callback)
    }
}