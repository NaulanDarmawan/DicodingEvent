package com.example.dicodingevent.ui

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.dicodingevent.data.response.ListEventsItem
import com.example.dicodingevent.data.retrofit.ApiConfig
import kotlinx.coroutines.launch

class HomeViewModel : ViewModel() {

    private val _listActive = MutableLiveData<List<ListEventsItem>>()
    val listActive: LiveData<List<ListEventsItem>> = _listActive

    private val _listFinished = MutableLiveData<List<ListEventsItem>>()
    val listFinished: LiveData<List<ListEventsItem>> = _listFinished

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _errorMessage = MutableLiveData<String?>()
    val errorMessage: LiveData<String?> = _errorMessage

    init {
        fetchHomeData()
    }

    private fun fetchHomeData() {
        _isLoading.value = true
        viewModelScope.launch {
            try {
                // Panggil 2 API secara asinkron
                val responseActive = ApiConfig.getApiService().getEvents(1)
                val responseFinished = ApiConfig.getApiService().getEvents(0)

                // Ambil maksimal 5 data menggunakan fungsi .take(5)
                _listActive.value = responseActive.listEvents.take(5)
                _listFinished.value = responseFinished.listEvents.take(5)
            } catch (e: Exception) {
                _errorMessage.value = "Koneksi internet bermasalah atau data gagal dimuat."
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun searchEvents(keyword: String) {
        _isLoading.value = true
        viewModelScope.launch {
            try {
                // Memanggil API dengan active=-1 dan query=keyword
                val response = ApiConfig.getApiService().getEvents(-1, keyword)
                _listFinished.value = response.listEvents // Gunakan listFinished agar langsung tampil di list vertikal bawah
            } catch (e: Exception) {
                _errorMessage.value = "Koneksi internet bermasalah atau data gagal dimuat."
                Log.e("HomeViewModel", "Error: ${e.message}")
            } finally {
                _isLoading.value = false
            }
        }
    }
}