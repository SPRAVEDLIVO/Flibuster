package dev.spravedlivo.flibuster.data

import androidx.datastore.preferences.core.Preferences

data class SettingsScreenData(var vmInit: Boolean = false, val loaded: Boolean = false, val preferences: Preferences?)
