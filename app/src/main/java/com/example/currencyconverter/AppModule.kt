package com.example.currencyconverter

import android.content.Context
import androidx.room.Room
import com.example.currencyconverter.data.local.CurrencyDao
import com.example.currencyconverter.data.local.CurrencyDatabase
import com.example.currencyconverter.data.network.CurrencyApiService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    private const val exchangeRateApiKey = BuildConfig.EXCHANGE_RATE_API_KEY

    @Provides
    @Singleton
    fun provideCurrencyApiService(): CurrencyApiService {
        return Retrofit.Builder()
            .baseUrl("https://v6.exchangerate-api.com/v6/$exchangeRateApiKey/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(CurrencyApiService::class.java)
    }

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): CurrencyDatabase {
        return Room.databaseBuilder(
            context,
            CurrencyDatabase::class.java,
            "currency_database"
        ).build()
    }

    @Provides
    fun provideCurrencyDao(database: CurrencyDatabase): CurrencyDao {
        return database.currencyDao()
    }
}
