package com.arpit.flightsearchapp

import android.app.Application
import com.arpit.flightsearchapp.model.AppContainer
import com.arpit.flightsearchapp.model.DefaultAppcontainer

class FlightSearchApplication : Application() {
    lateinit var container: DefaultAppcontainer

    override fun onCreate() {
        super.onCreate()
        container = AppContainer(this)
    }
}
