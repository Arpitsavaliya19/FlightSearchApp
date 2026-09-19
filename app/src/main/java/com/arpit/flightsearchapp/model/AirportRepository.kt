package com.arpit.flightsearchapp.model

import androidx.room.Query
import com.arpit.flightsearchapp.data.Airport
import com.arpit.flightsearchapp.data.Favorite
import kotlinx.coroutines.flow.Flow

interface AirportRepository {

    fun searchAirport(query: String): Flow<List<Airport>>

    fun getDestination(iatacode: String): Flow<List<Airport>>

    suspend fun insertFavorite(favorite: Favorite)

    suspend fun deleteFavorite(favorite: Favorite)

    fun getFavorites(): Flow<List<Favorite>>

}

