package com.tanveer.lostandcampusapp.data

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

val Context.dataStore by preferencesDataStore(name = "user_prefs")

object DataStoreManager {
    private val USER_NAME = stringPreferencesKey("user_name")
    private val USER_REGNO = stringPreferencesKey("user_regno")

    suspend fun saveUserData(context: Context, name: String, regNo: String) {
        context.dataStore.edit { prefs ->
            prefs[USER_NAME] = name
            prefs[USER_REGNO] = regNo
        }
    }

    suspend fun getUserData(context: Context): Pair<String, String> {
        val prefs = context.dataStore.data.map { prefs ->
            val name = prefs[USER_NAME] ?: ""
            val regNo = prefs[USER_REGNO] ?: ""
            Pair(name, regNo)
        }
        return prefs.first()
    }
}
