package com.arpit.flightsearchapp.model

import android.content.Context

interface DefaultAppcontainer {
    val airportRepository: AirportRepository
}

class AppContainer(
    private val context: Context
) : DefaultAppcontainer {

    private val database by lazy {
        AirportDatabase.getDatabase(context)
    }

    override val airportRepository: AirportRepository by lazy {
        AirportRoomRepository(
            airportDao = database.airportDao(),
            favoriteDao = database.favoriteDao()
        )
    }

}
