package com.example.dicodingevent.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

class SettingViewModel(private val pref: SettingPreferences) : ViewModel() {

    // Mengambil status tema (Flow diubah menjadi LiveData)
    fun getThemeSettings(): LiveData<Boolean> {
        return pref.getThemeSetting().asLiveData()
    }

    // Menyimpan status tema baru
    fun saveThemeSetting(isDarkModeActive: Boolean) {
        viewModelScope.launch {
            pref.saveThemeSetting(isDarkModeActive)
        }
    }

    fun getReminderSettings(): LiveData<Boolean> {
        return pref.getReminderSetting().asLiveData()
    }

    fun saveReminderSetting(isActive: Boolean) {
        viewModelScope.launch {
            pref.saveReminderSetting(isActive)
        }
    }
}