package com.tanveer.lostandcampusapp.App

import android.app.Application
import com.onesignal.OneSignal
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class MyApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        OneSignal.initWithContext(this, "812c59fb-aed1-4bf7-b899-b87dbc43880e")


    }
}
