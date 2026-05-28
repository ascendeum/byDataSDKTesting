package com.example.bydatasdk

import android.app.Application
import android.util.Log
import com.bydata.sdk.android.BuildConfig
import com.bydata.sdk.android.ByDataAndroid
import com.bydata.sdk.android.HttpLogger
import com.eventslogger.EventsLoggerSdk

class MainApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        Log.d("ADSTest","onCreate");
        EventsLoggerSdk.init(this)
        // Add SDK initialization here so it runs when the app process starts.

        ByDataAndroid.setup(this, clientId = "asc129168") {
            debug = BuildConfig.DEBUG
            flushAt = 20
            flushIntervalSeconds = 30
            sessionTimeoutMinutes = 30
            httpLogger = HttpLogger { line -> Log.d("byData-HTTP", line) }
        }
    }
}
