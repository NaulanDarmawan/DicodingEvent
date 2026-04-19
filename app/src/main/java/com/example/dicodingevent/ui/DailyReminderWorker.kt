package com.example.dicodingevent.ui

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.dicodingevent.R
import com.example.dicodingevent.data.retrofit.ApiConfig

class DailyReminderWorker(context: Context, workerParams: WorkerParameters) :
    CoroutineWorker(context, workerParams) {

    override suspend fun doWork(): Result {
        return try {
            // Ambil 1 event aktif terdekat dari API
            val response = ApiConfig.getApiService().getEvents(active = 1, limit = 1)
            val nearestEvent = response.listEvents.firstOrNull()

            if (nearestEvent != null) {
                val title = nearestEvent.name
                val message = "Dimulai pada: ${nearestEvent.beginTime}"
                showNotification(title, message)
            }

            Result.success()
        } catch (e: Exception) {
            Log.e("DailyReminderWorker", "Gagal memuat reminder: ${e.message}")
            Result.retry()
        }
    }

    private fun showNotification(title: String, message: String) {
        val notificationManager = applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val channelId = "event_reminder_channel"
        val channelName = "Event Daily Reminder"

        val builder = NotificationCompat.Builder(applicationContext, channelId)
            .setSmallIcon(R.drawable.ic_settings) // Gunakan ikon setting atau buat ikon lonceng baru
            .setContentTitle(title)
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_HIGH)
            notificationManager.createNotificationChannel(channel)
        }

        // Munculkan Notifikasi
        notificationManager.notify(1, builder.build())
    }
}