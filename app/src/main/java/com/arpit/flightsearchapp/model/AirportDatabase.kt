package com.arpit.flightsearchapp.model

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.arpit.flightsearchapp.data.Airport
import com.arpit.flightsearchapp.data.AirportDao
import com.arpit.flightsearchapp.data.Favorite
import com.arpit.flightsearchapp.data.FavoriteDao

@Database(
    entities = [Airport::class, Favorite::class],
    version = 1,
    exportSchema = false
)
abstract class AirportDatabase : RoomDatabase() {

    abstract fun airportDao(): AirportDao

    abstract fun favoriteDao(): FavoriteDao


    companion object {
        @Volatile
        private var Instance: AirportDatabase? = null

        fun getDatabase(context: Context): AirportDatabase {
            return Instance ?: synchronized(this) {
                Room.databaseBuilder(
                    context.applicationContext,
                    AirportDatabase::class.java,
                    "flight_search.db"
                )
                    .createFromAsset("flight_search.db")
                    .build()
                    .also { Instance = it }
            }
        }
    }

}