package com.example.dicodingevent.ui

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.dicodingevent.data.EventRepository // Import Repository
import com.example.dicodingevent.data.response.ListEventsItem
import kotlinx.coroutines.launch

class HomeViewModel(private val eventRepository: EventRepository) : ViewModel() {

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

    fun fetchHomeData() {
        _isLoading.value = true
        viewModelScope.launch {
            try {
                // 🔥 Panggil API lewat Repository
                val responseActive = eventRepository.getEventsFromApi(1)
                val responseFinished = eventRepository.getEventsFromApi(0)

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
                // 🔥 Panggil API lewat Repository dengan keyword
                val response = eventRepository.getEventsFromApi(-1, keyword)
                _listFinished.value = response.listEvents
            } catch (e: Exception) {
                _errorMessage.value = "Koneksi internet bermasalah atau data gagal dimuat."
                Log.e("HomeViewModel", "Error: ${e.message}")
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun clearErrorMessage() {
        _errorMessage.value = null
    }
}