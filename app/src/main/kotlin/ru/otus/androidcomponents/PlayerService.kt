package ru.otus.androidcomponents

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Intent
import android.content.pm.ServiceInfo
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.ServiceCompat
import androidx.lifecycle.LifecycleService
import androidx.lifecycle.lifecycleScope
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import ru.otus.androidcomponents.player.IPlayerService
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

/**
 * Plays the song
 */
class PlayerService : LifecycleService() {
    private var job: Job? = null

    /**
     * Called when service is created
     */
    override fun onCreate() {
        super.onCreate()
        Log.i(TAG, "Service created")
        startForeground()
        startPlayback()
    }

    override fun onBind(intent: Intent): IBinder {
        super.onBind(intent)
        return playerService
    }

    override fun onDestroy() {
        Log.i(TAG, "Stopping service...")
        job?.cancel()
        super.onDestroy()
        stopForeground(STOP_FOREGROUND_REMOVE)
    }

    private fun startPlayback() {
        Log.i(TAG, "Starting playback...")
        job = lifecycleScope.launch {
            while (isActive) {
                lyrics.forEach {
                    sing(it)
                    delay(500)
                }
            }
        }
    }

    private fun sing(message: String) {
        Log.i(TAG, message)

        // Change notification with the current message
        val notificationManager = getSystemService(NotificationManager::class.java)
        val notifications = notificationManager.activeNotifications
        val ongoingNotification = notifications.find { NOTIFICATION_ID == it.id }
        if (null != ongoingNotification) {
            notificationManager.notify(NOTIFICATION_ID, buildNotification(message))
        }

        // Broadcast current message
        val intent = Intent(SONG_ACTION)
        intent.putExtra(SONG_LINE, message)
        LocalBroadcastManager
            .getInstance(this)
            .sendBroadcast(intent);
    }

    /**
     * Called when the calling code runs `Context.startService`
     */
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.i(TAG, "New service intent")
        super.onStartCommand(intent, flags, startId)
        return START_NOT_STICKY
    }

    private fun startForeground() {
        val channel = NotificationChannel(CHANNEL_ID, "Player", NotificationManager.IMPORTANCE_LOW)
        channel.description = "Baby shark player"

        val notificationManager = getSystemService(NotificationManager::class.java)
        notificationManager.createNotificationChannel(channel)

        ServiceCompat.startForeground(
            this,
            NOTIFICATION_ID,
            buildNotification("Play baby shark song"),
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                ServiceInfo.FOREGROUND_SERVICE_TYPE_MEDIA_PLAYBACK
            } else {
                0
            }
        )
    }

    private fun buildNotification(text: String): Notification = NotificationCompat.Builder(this, CHANNEL_ID)
        .setContentTitle("Baby shark player")
        .setContentText(text)
        .setSmallIcon(R.drawable.ic_service)
        .setOngoing(true)
        .build()


    private val playerService = object : IPlayerService.Stub() {
        override fun asBinder(): IBinder = this

        override fun getTrackTitle(): String = title
    }

    companion object {
        private const val TAG = "Player service"
        private const val CHANNEL_ID = "player_channel"
        private const val NOTIFICATION_ID = 100500

        const val SONG_ACTION = "actionSong"
        const val SONG_LINE = "extraLine"

        private const val title = "Baby Shark"
        private val lyrics = listOf(
            "Baby shark, doo doo doo doo doo doo",
            "Baby shark, doo doo doo doo doo doo",
            "Baby shark, doo doo doo doo doo doo",
            "Baby shark!",
            "Daddy shark, doo doo doo doo doo doo",
            "Daddy shark, doo doo doo doo doo doo",
            "Daddy shark, doo doo doo doo doo doo",
            "Daddy shark!",
            "Mommy shark, doo doo doo doo doo doo",
            "Mommy shark, doo doo doo doo doo doo",
            "Mommy shark, doo doo doo doo doo doo",
            "Mommy shark!"
        )
    }
}