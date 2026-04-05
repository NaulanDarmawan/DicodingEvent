package com.example.dicodingevent.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.dicodingevent.data.response.ListEventsItem
import com.example.dicodingevent.data.retrofit.ApiConfig
import kotlinx.coroutines.launch

class EventViewModel : ViewModel() {

    private val _listEvent = MutableLiveData<List<ListEventsItem>>()
    val listEvent: LiveData<List<ListEventsItem>> = _listEvent

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _detailEvent = MutableLiveData<ListEventsItem>()
    val detailEvent: LiveData<ListEventsItem> = _detailEvent

    private val _errorMessage = MutableLiveData<String?>()
    val errorMessage: LiveData<String?> = _errorMessage

    // Parameter active: 1 untuk Upcoming, 0 untuk Finished
    fun findEvents(active: Int) {
        _isLoading.value = true
        viewModelScope.launch {
            try {
                val response = ApiConfig.getApiService().getEvents(active)
                _listEvent.value = response.listEvents
            } catch (e: Exception) {
                _errorMessage.value = "Gagal memuat data. Periksa koneksi internet."
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun getDetailEvent(id: String) {
        _isLoading.value = true
        viewModelScope.launch {
            try {
                val response = ApiConfig.getApiService().getDetailEvent(id)
                _detailEvent.value = response.event
            } catch (e: Exception) {
                _errorMessage.value = "Gagal memuat data. Periksa koneksi internet."
            } finally {
                _isLoading.value = false
            }
        }
    }
}