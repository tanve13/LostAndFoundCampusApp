package com.tanveer.lostandcampusapp.ui.theme

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

val Context.dataStore by preferencesDataStore("settings")

object ThemePreference {
    private val DARK_MODE_KEY = booleanPreferencesKey("dark_mode")

    fun getTheme(context: Context): Flow<Boolean> =
        context.dataStore.data.map { prefs -> prefs[DARK_MODE_KEY] ?: false }

    suspend fun saveTheme(context: Context, isDark: Boolean) {
        context.dataStore.edit { prefs ->
            prefs[DARK_MODE_KEY] = isDark
        }
    }
}