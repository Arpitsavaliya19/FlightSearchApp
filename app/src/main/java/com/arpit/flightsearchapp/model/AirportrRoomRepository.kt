package com.arpit.flightsearchapp.model

import com.arpit.flightsearchapp.data.Airport
import com.arpit.flightsearchapp.data.AirportDao
import com.arpit.flightsearchapp.data.Favorite
import com.arpit.flightsearchapp.data.FavoriteDao
import kotlinx.coroutines.flow.Flow

class AirportRoomRepository(

    private val airportDao: AirportDao,
    private val favoriteDao: FavoriteDao

) : AirportRepository {

    override fun searchAirport(query: String): Flow<List<Airport>> {
        return airportDao.searchAirport(query)
    }

    override fun getDestination(iataCode: String): Flow<List<Airport>> {
        return airportDao.getDestination(iataCode)
    }

    override suspend fun insertFavorite(favorite: Favorite) {
        favoriteDao.insertFavorite(favorite)
    }

    override suspend fun deleteFavorite(favorite: Favorite) {
        favoriteDao.deleteFavorite(favorite)
    }

    override fun getFavorites(): Flow<List<Favorite>> {
        return favoriteDao.getFavorites()
    }
}
