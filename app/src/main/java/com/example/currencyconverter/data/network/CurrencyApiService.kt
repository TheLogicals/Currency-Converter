package com.example.currencyconverter.data.network

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path

interface CurrencyApiService {
    @GET("latest/{base}")
    suspend fun getExchangeRates(
        @Path("base") base: String
    ): Response<CurrencyNetworkResponse>
}