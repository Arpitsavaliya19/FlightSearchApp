package com.arpit.flightsearchapp.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface FavoriteDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertFavorite(favorite: Favorite)

    @Query("SELECT * FROM Favorite")
    fun getFavorites(): Flow<List<Favorite>>

    @Delete
    suspend fun deleteFavorite(favorite: Favorite)

}