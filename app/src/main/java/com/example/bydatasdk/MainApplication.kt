package com.example.bydatasdk

import android.app.Application
import android.util.Log
import com.eventslogger.EventsLoggerSdk

class MainApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        Log.d("ADSTest","onCreate");
        EventsLoggerSdk.init(this)

        // Add SDK initialization here so it runs when the app process starts.
    }
}
