package com.konkuk.kureal.util

import android.app.Application
import android.util.Log


class GlobalApplication : Application() {
    var instance: GlobalApplication? = null

    override fun onCreate() {
        super.onCreate()
        Log.d("GLOBAL", "들어옴")
    }
}