package com.example.dicodingevent.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.dicodingevent.data.EventRepository
import com.example.dicodingevent.data.response.ListEventsItem
import com.example.dicodingevent.data.retrofit.ApiConfig
import kotlinx.coroutines.launch
import com.example.dicodingevent.data.local.entity.FavoriteEvent

class EventViewModel(private val eventRepository: EventRepository) : ViewModel() {

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
                // Memanggil API lewat Repository
                val response = eventRepository.getEventsFromApi(active)
                _listEvent.value = response.listEvents
                _isLoading.value = false
            } catch (e: Exception) {
                // Error Handling jika internet mati atau API gagal (Syarat Bintang 5)
                _isLoading.value = false
                _errorMessage.value = "Gagal memuat data: ${e.message}"
            }
        }
    }

    fun getDetailEvent(id: String) {
        _isLoading.value = true
        viewModelScope.launch {
            try {
                // Memanggil API lewat Repository
                val response = eventRepository.getDetailEventFromApi(id)
                _detailEvent.value = response.event
                _isLoading.value = false
            } catch (e: Exception) {
                // Error Handling
                _isLoading.value = false
                _errorMessage.value = "Gagal memuat detail: ${e.message}"
            }
        }
    }

    fun clearErrorMessage() {
        _errorMessage.value = null
    }

    fun getFavoriteEventById(id: String) = eventRepository.getFavoriteEventById(id)

    fun insertFavoriteEvent(event: FavoriteEvent) {
        viewModelScope.launch {
            eventRepository.insertFavoriteEvent(event)
        }
    }

    fun deleteFavoriteEvent(event: FavoriteEvent) {
        viewModelScope.launch {
            eventRepository.deleteFavoriteEvent(event)
        }
    }

    fun getAllFavoriteEvents() = eventRepository.getAllFavoriteEvents()
}