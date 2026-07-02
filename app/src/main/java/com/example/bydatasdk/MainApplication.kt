package com.example.bydatasdk

import android.app.Application
import android.util.Log
import com.bydata.sdk.ByDataConfig
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
            debug = true
            flushAt = 1
            autoPageViews = true    // auto page_view on every Activity resume
            crashReporting = true   // capture app_crash markers, replay next launch
            httpLogger = HttpLogger { line -> Log.d("byData-HTTP", line) }
        }

//        ByDataAndroid.setup(this, clientId = "asc129168") {
//            debug = ByDataConfig.
//            flushAt = 20
//            flushIntervalSeconds = 30 // sec
//            sessionTimeoutMinutes = 30
//            httpLogger = HttpLogger { line -> Log.d("byData-HTTP", line) }
//        }
    }
}


// pbjs-stra