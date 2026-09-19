package com.arpit.flightsearchapp.data

import androidx.room.Dao
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface AirportDao {

    @Query(
        "SELECT * FROM AIRPORT " +
                "WHERE name LIKE '%' || :query || '%' OR " +
                "iata_code LIKE '%' || :query || '%' " +
                "ORDER BY name ASC"
    )
    fun searchAirport(query: String): Flow<List<Airport>>


    @Query(
        "SELECT *  " +
                "FROM AIRPORT  " +
                "WHERE iata_code != :iataCode" +
                " ORDER BY name DESC LIMIT 5 "
    )
    fun getDestination(iataCode: String): Flow<List<Airport>>

}