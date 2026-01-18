package com.btech_dev.quotebro

import android.app.Application
import com.btech_dev.quotebro.data.remote.SupabaseClient

class QuoteBroApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        SupabaseClient.initialize(this)
    }
}
