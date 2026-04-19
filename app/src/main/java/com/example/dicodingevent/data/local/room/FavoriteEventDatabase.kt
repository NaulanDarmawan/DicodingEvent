package com.example.dicodingevent.data.local.room

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.dicodingevent.data.local.entity.FavoriteEvent

@Database(entities = [FavoriteEvent::class], version = 1, exportSchema = false)
abstract class FavoriteEventDatabase : RoomDatabase() {

    abstract fun favoriteEventDao(): FavoriteEventDao

    companion object {
        @Volatile
        private var INSTANCE: FavoriteEventDatabase? = null

        @JvmStatic
        fun getDatabase(context: Context): FavoriteEventDatabase {
            if (INSTANCE == null) {
                synchronized(FavoriteEventDatabase::class.java) {
                    INSTANCE = Room.databaseBuilder(
                        context.applicationContext,
                        FavoriteEventDatabase::class.java, "favorite_event_database"
                    ).build()
                }
            }
            return INSTANCE as FavoriteEventDatabase
        }
    }
}