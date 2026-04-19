package com.example.dicodingevent.data.local.room

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.dicodingevent.data.local.entity.FavoriteEvent

@Dao
interface FavoriteEventDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(event: FavoriteEvent) // Pakai suspend (Coroutines) sesuai kriteria

    @Delete
    suspend fun delete(event: FavoriteEvent)

    @Query("SELECT * FROM favorite_event")
    fun getAllFavoriteEvents(): LiveData<List<FavoriteEvent>> // Return LiveData agar UI otomatis update

    @Query("SELECT * FROM favorite_event WHERE id = :id")
    fun getFavoriteEventById(id: String): LiveData<FavoriteEvent>
}