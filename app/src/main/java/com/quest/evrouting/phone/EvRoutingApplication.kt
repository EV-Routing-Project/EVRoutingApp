package com.quest.evrouting.phone

import android.app.Application
import com.quest.evrouting.phone.configuration.AppConfig

class EvRoutingApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        AppConfig.initialize(this)
    }
}
    