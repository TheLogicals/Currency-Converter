package com.example.currencyconverter.data.local

import androidx.room.Dao
import androidx.room.Database
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.PrimaryKey
import androidx.room.Query
import androidx.room.RoomDatabase
import kotlinx.coroutines.flow.Flow

@Entity
data class ConversionHistory(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val baseCurrency: String,
    val targetCurrency: String,
    val amount: Double,
    val result: Double,
    val timestamp: Long
)

@Dao
interface CurrencyDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertConversion(history: ConversionHistory)

    @Query("SELECT * FROM ConversionHistory ORDER BY timestamp DESC")
    fun getConversionHistory(): Flow<List<ConversionHistory>>

    @Query("DELETE FROM ConversionHistory")
    suspend fun deleteAll()
}

@Database(entities = [ConversionHistory::class], version = 1)
abstract class CurrencyDatabase : RoomDatabase() {
    abstract fun currencyDao(): CurrencyDao
}
