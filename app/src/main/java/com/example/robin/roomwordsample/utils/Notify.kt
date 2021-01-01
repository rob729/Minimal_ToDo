package com.example.robin.roomwordsample.utils

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Context.NOTIFICATION_SERVICE
import android.content.Intent
import android.graphics.Color
import androidx.core.app.NotificationCompat
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.example.robin.roomwordsample.activity.MainActivity
import com.example.robin.roomwordsample.R

class Notify(ctx: Context, params: WorkerParameters) : Worker(ctx, params) {

    private val b = "420"
    private val task: String? by lazy {
        inputData.getString("Task Name")
    }
    private val intent: Intent by lazy {
        Intent(applicationContext, MainActivity::class.java)
    }
    private val pi: PendingIntent by lazy {
        PendingIntent.getActivity(
            applicationContext,
            333,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT
        )
    }

//    private val appSharedPrefs: SharedPreferences by lazy {
//        PreferenceManager
//            .getDefaultSharedPreferences(this.applicationContext)
//    }

    private val notificationManager: NotificationManager by lazy {
        applicationContext.getSystemService(NOTIFICATION_SERVICE) as NotificationManager
    }

    private val notification: Notification by lazy {
        NotificationCompat.Builder(applicationContext, b)
            .setSmallIcon(R.drawable.ic_notification)
            .setColor(Color.rgb(30, 136, 229))
            .setContentTitle("Reminder")
            .setContentText(task)
            .setAutoCancel(true)
            .setContentIntent(pi)
            .build()
    }

    override fun doWork(): Result {
        try {
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                val notificationChannel =
                    NotificationChannel(
                        b,
                        "Default Channel",
                        NotificationManager.IMPORTANCE_DEFAULT
                    )
                notificationManager.createNotificationChannel(notificationChannel)
            }

            notificationManager.notify(1112, notification)
            return Result.success()

        } catch (e: Exception) {

            return Result.failure()
        }

    }

}