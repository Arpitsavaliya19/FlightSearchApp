package com.arpit.flightsearchapp.data


import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "Airport")
data class Airport(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    val id: Int,
    @ColumnInfo(name = "iata_code")
    val iataCode: String,
    @ColumnInfo(name = "name")
    val name: String,
    @ColumnInfo(name = "passengers")
    val passengers: Int
)