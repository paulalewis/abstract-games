package com.castlefrog.games.asg

import android.app.Application
import android.content.Context
import android.util.Log
import com.google.firebase.crashlytics.FirebaseCrashlytics
import timber.log.Timber

class AbstractGamesApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        setUpLogging()
    }

    private fun setUpLogging() {
        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        } else {
            Timber.plant(object : Timber.Tree() {
                override fun log(priority: Int, tag: String?, message: String, t: Throwable?) {
                    when (priority) {
                        Log.WARN, Log.ERROR -> {
                            FirebaseCrashlytics.getInstance().recordException(createException(message, t))
                        }
                        else -> {}
                    }
                }

                private fun createException(message: String, t: Throwable?): Throwable {
                    return t ?: Exception(message)
                }
            })
        }
    }
}

private fun Context.getAppContext(): AbstractGamesApplication {
    return applicationContext as AbstractGamesApplication
}
