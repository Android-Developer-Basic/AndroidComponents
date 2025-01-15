package ru.otus.androidcomponents

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.core.app.LocaleManagerCompat

class LocaleBroadcastReceiver : BroadcastReceiver() {
    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onReceive(context: Context?, intent: Intent?) {
        if (null != intent && Intent.ACTION_LOCALE_CHANGED == intent.action) {
            val localeList = LocaleManagerCompat.getSystemLocales(requireNotNull(context))
            Log.i(TAG, "Locale change to: $localeList")
        }
    }

    companion object {
        private const val TAG = "LocaleReceiver"
    }
}