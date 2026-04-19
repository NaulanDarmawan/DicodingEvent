package com.example.dicodingevent.data

import androidx.lifecycle.LiveData
import com.example.dicodingevent.data.local.entity.FavoriteEvent
import com.example.dicodingevent.data.local.room.FavoriteEventDao
import com.example.dicodingevent.data.retrofit.ApiService

class EventRepository private constructor(
    private val apiService: ApiService,
    private val favoriteEventDao: FavoriteEventDao
) {

    // Fungsi untuk interaksi dengan Room Database (Favorit)
    fun getFavoriteEventById(id: String): LiveData<FavoriteEvent> {
        return favoriteEventDao.getFavoriteEventById(id)
    }

    suspend fun insertFavoriteEvent(event: FavoriteEvent) {
        favoriteEventDao.insert(event)
    }

    suspend fun deleteFavoriteEvent(event: FavoriteEvent) {
        favoriteEventDao.delete(event)
    }

    fun getAllFavoriteEvents(): LiveData<List<FavoriteEvent>> {
        return favoriteEventDao.getAllFavoriteEvents()
    }

    // ==========================================
    // FUNGSI UNTUK MENGAMBIL DATA DARI API (INTERNET)
    // ==========================================

    suspend fun getEventsFromApi(active: Int, q: String? = null) = apiService.getEvents(active, q)

    suspend fun getDetailEventFromApi(id: String) = apiService.getDetailEvent(id)

    companion object {
        @Volatile
        private var instance: EventRepository? = null

        fun getInstance(
            apiService: ApiService,
            favoriteEventDao: FavoriteEventDao
        ): EventRepository =
            instance ?: synchronized(this) {
                instance ?: EventRepository(apiService, favoriteEventDao)
            }.also { instance = it }
    }
}