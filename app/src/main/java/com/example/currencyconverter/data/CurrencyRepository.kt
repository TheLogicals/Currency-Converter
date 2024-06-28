package com.example.currencyconverter.data

import com.example.currencyconverter.data.local.ConversionHistory
import com.example.currencyconverter.data.local.CurrencyDao
import com.example.currencyconverter.data.network.CurrencyApiService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import javax.inject.Inject


class CurrencyRepository @Inject constructor(
    private val apiService: CurrencyApiService,
    private val currencyDao: CurrencyDao) {

    suspend fun getCurrencyExchangeRates(base: String): ExchangeRate {
        return withContext(Dispatchers.IO) {
            val response = apiService.getExchangeRates(base)
            if (response.isSuccessful && response.body()!= null) {
                val exchangeRates = response.body()?.conversion_rates ?: emptyMap()
                ExchangeRate(baseCurrency = base, exchangeRates)
            } else {
                ExchangeRate(base, emptyMap())
            }
        }
    }

    suspend fun saveConversion(history: ConversionHistory) {
        withContext(Dispatchers.IO) {
            currencyDao.insertConversion(history)
        }
    }

    fun getConversionHistory(): Flow<List<ConversionHistory>> {
        return currencyDao.getConversionHistory()
    }
    suspend fun clearConversionHistory() {
        return withContext(Dispatchers.IO) {
            currencyDao.deleteAll()
        }
    }

}

data class ExchangeRate(val baseCurrency: String, val rates: Map<String,Double>)