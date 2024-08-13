package com.example.messagesender

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat

class SecondActivity : AppCompatActivity() {

    private val CHANNEL_ID = "message_channel"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_second)

        // Retrieve the message from the Intent
        val message = intent.getStringExtra("EXTRA_MESSAGE")

        // Debugging: Log the received message
        Log.d("SecondActivity", "Received message: $message")

        // Check for null and display a fallback message if null
        val messageTextView = findViewById<TextView>(R.id.messageTextView)
        messageTextView.text = message ?: "No message received"

        // Create notification channel
        createNotificationChannel()

        // Request notification permission and send notification
        checkNotificationPermissionAndNotify(message ?: "No message received")
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "Message Channel"
            val descriptionText = "Channel for message notifications"
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
                description = descriptionText
            }
            val notificationManager: NotificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    private fun checkNotificationPermissionAndNotify(message: String) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) ==
                PackageManager.PERMISSION_GRANTED) {
                sendNotification(message)
            } else {
                requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        } else {
            sendNotification(message)
        }
    }

    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted: Boolean ->
            if (isGranted) {
                sendNotification("Notification permission granted")
            } else {
                // Handle the case where the permission was denied
                Log.d("SecondActivity", "Notification permission denied")
            }
        }

    private fun sendNotification(message: String) {
        // First, check if the permission is granted
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.POST_NOTIFICATIONS
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // If permission is not granted, exit the function
            return
        }

        // Create the notification builder
        val builder = NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_message)
            .setContentTitle("New Message")
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setVibrate(longArrayOf(1000, 1000, 1000, 1000))
            .setLights(0xff00ff00.toInt(), 300, 1000)

        // Display the notification
        with(NotificationManagerCompat.from(this)) {
            notify(1, builder.build())
        }
    }

}
