package ru.otus.androidcomponents.player

import android.content.Intent

/**
 * Creates service intent
 */
object PlayerIntentBuilder {
    fun getIntent(pkg: String): Intent = Intent(ACTION).apply {
        setPackage(pkg)
    }

    const val ACTION = "ru.otus.androidcomponents.player.BIND"
}