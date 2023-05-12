package com.example.assignment3

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class MyFirebaseMessagingService : FirebaseMessagingService() {

        override fun onMessageReceived(remoteMessage: RemoteMessage) {
            // Check if the message contains data payload.
            if (remoteMessage.data.isNotEmpty()) {
                val title = remoteMessage.data["title"]
                val message = remoteMessage.data["message"]

                // Display the notification.
                showNotification(title, message)
            }
        }

        private fun showNotification(title: String?, message: String?) {
            val notificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

            val channelId = "channel_id"
            val channelName = "Channel Name"
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val channel = NotificationChannel(
                    channelId,
                    channelName,
                    NotificationManager.IMPORTANCE_DEFAULT
                )
                notificationManager.createNotificationChannel(channel)
            }

            val notificationBuilder = NotificationCompat.Builder(this, channelId)
                .setContentTitle(title)
                .setContentText(message)
                .setSmallIcon(R.drawable.notification_icon)
                .setAutoCancel(true)

            val intent = Intent(this, MapActivity::class.java)
            val pendingIntent = PendingIntent.getActivity(
                this,
                0,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT
            )
            notificationBuilder.setContentIntent(pendingIntent)

            notificationManager.notify(0, notificationBuilder.build())
        }

        override fun onNewToken(token: String) {
            Log.d("FCM Token", token)
            // Send the token to your server or handle it as per your requirement.
            // Store it for later use or send it to the server using an API.
        }
    }
