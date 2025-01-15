package ru.otus.androidcomponents.listener

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Bundle
import android.os.IBinder
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import ru.otus.androidcomponents.listener.databinding.ActivityMainBinding
import ru.otus.androidcomponents.player.IPlayerService
import ru.otus.androidcomponents.player.PlayerIntentBuilder
import ru.otus.androidcomponents.player.PlayerIntentBuilder.ACTION
import kotlin.properties.Delegates

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private var serviceConnection: ServiceConnection? = null
    private var player: IPlayerService? by Delegates.observable(null) { _, _, value ->
        with(binding) {
            if (null == value) {
                bindService.isEnabled = true
                getTitle.isEnabled = false
                unbindService.isEnabled = false
            } else {
                bindService.isEnabled = false
                getTitle.isEnabled = true
                unbindService.isEnabled = true
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        with(binding) {
            bindService.setOnClickListener {
                startService()
            }
            getTitle.setOnClickListener {
                trackTitle.text = player?.trackTitle
            }
            unbindService.setOnClickListener {
                stopService()
            }
        }
    }

    private fun startService() {
        if (null !== player) {
            return
        }
        val connection = object : ServiceConnection {
            override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
                val playerService = IPlayerService.Stub.asInterface(service)
                if (null != playerService) {
                    Log.i(TAG, "Service connected")
                    player = playerService
                } else {
                    Log.e(TAG, "Service interface not created")
                }
            }

            override fun onServiceDisconnected(name: ComponentName?) {
                Log.i(TAG, "Service disconnected")
                player = null
            }
        }

        Log.i(TAG, "Binding service...")

        val intent = Intent("ru.otus.androidcomponents.player.BIND")
        intent.setPackage("ru.otus.androidcomponents.app")

        val result = bindService(
            intent,
            connection,
            Context.BIND_AUTO_CREATE
        )


        Log.i(TAG, "Binding result: $result")
        serviceConnection = connection
    }

    private fun stopService() {
        val connection = serviceConnection ?: return
        Log.i(TAG, "Unbinding service...")
        unbindService(connection)
        serviceConnection = null
        player = null
    }

    companion object {
        private const val TAG = "MainActivity"

        fun buildIntent(): Intent = PlayerIntentBuilder.getIntent("ru.otus.androidcomponents.app")
    }
}