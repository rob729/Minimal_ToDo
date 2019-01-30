package com.example.robin.roomwordsample

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context.NOTIFICATION_SERVICE
import android.content.Intent
import android.graphics.Color
import android.os.Looper
import android.preference.PreferenceManager
import androidx.core.app.NotificationCompat
import androidx.work.WorkManager
import androidx.work.Worker

class notify : Worker() {

    private val b = "420"
    var task = " "
    override fun doWork(): Result {
        Looper.prepare()
        val appSharedPrefs = PreferenceManager
            .getDefaultSharedPreferences(this.applicationContext)
        val notificationManager = applicationContext.getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            val notificationChannel = NotificationChannel(b, "Default Channel", NotificationManager.IMPORTANCE_DEFAULT)
            notificationManager.createNotificationChannel(notificationChannel)
        }

        if (appSharedPrefs.contains("Task")) {
            task = appSharedPrefs.getString("Task", " ")
        }
        val intent = Intent(applicationContext, MainActivity::class.java)
        val pi = PendingIntent.getActivity(applicationContext,333, intent, PendingIntent.FLAG_UPDATE_CURRENT)
        val notification = NotificationCompat.Builder(applicationContext, b)
            .setSmallIcon(R.drawable.ic_notification)
            .setColor(Color.rgb(30,136, 229))
            .setContentTitle("Reminder")
            .setContentText(task)
            .setAutoCancel(true)
            .setContentIntent(pi)
            .build()
        notificationManager.notify(1112, notification)
        Looper.loop()
        return Worker.Result.SUCCESS

    }

}