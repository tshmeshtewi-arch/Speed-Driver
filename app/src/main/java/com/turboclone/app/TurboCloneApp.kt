package com.turboclone.app

import android.app.Application
import android.preference.PreferenceManager
import org.osmdroid.config.Configuration

class TurboCloneApp : Application() {
    override fun onCreate() {
        super.onCreate()
        // إعداد osmdroid (خرائط OpenStreetMap الحقيقية)
        Configuration.getInstance().load(this, PreferenceManager.getDefaultSharedPreferences(this))
        Configuration.getInstance().userAgentValue = packageName
    }
}
