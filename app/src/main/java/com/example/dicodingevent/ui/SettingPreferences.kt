package com.example.dicodingevent.ui // Sesuaikan jika kamu menaruhnya di folder lain

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

// Inisialisasi DataStore
val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

class SettingPreferences private constructor(private val dataStore: DataStore<Preferences>) {

    // Key untuk menyimpan status tema gelap (true/false)
    private val THEME_KEY = booleanPreferencesKey("theme_setting")
    private val REMINDER_KEY = booleanPreferencesKey("reminder_setting")

    // Fungsi untuk membaca status tema
    fun getThemeSetting(): Flow<Boolean> {
        return dataStore.data.map { preferences ->
            preferences[THEME_KEY] ?: false // Default-nya false (Light Mode)
        }
    }

    // Fungsi untuk menyimpan status tema
    suspend fun saveThemeSetting(isDarkModeActive: Boolean) {
        dataStore.edit { preferences ->
            preferences[THEME_KEY] = isDarkModeActive
        }
    }

    fun getReminderSetting(): Flow<Boolean> {
        return dataStore.data.map { preferences ->
            preferences[REMINDER_KEY] ?: false
        }
    }

    suspend fun saveReminderSetting(isActive: Boolean) {
        dataStore.edit { preferences ->
            preferences[REMINDER_KEY] = isActive
        }
    }

    companion object {
        @Volatile
        private var INSTANCE: SettingPreferences? = null

        fun getInstance(dataStore: DataStore<Preferences>): SettingPreferences {
            return INSTANCE ?: synchronized(this) {
                val instance = SettingPreferences(dataStore)
                INSTANCE = instance
                instance
            }
        }
    }
}