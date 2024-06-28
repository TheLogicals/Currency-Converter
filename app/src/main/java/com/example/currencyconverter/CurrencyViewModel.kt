package com.example.currencyconverter

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.core.text.isDigitsOnly
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.currencyconverter.data.CurrencyRepository
import com.example.currencyconverter.data.ExchangeRate
import com.example.currencyconverter.data.local.ConversionHistory
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.math.BigDecimal
import javax.inject.Inject

@HiltViewModel
class CurrencyViewModel @Inject constructor(
    private val repository: CurrencyRepository
) : ViewModel() {
    var enteredAmount by mutableStateOf("")
    var baseCurrency by mutableStateOf("USD")
    var targetCurrency by mutableStateOf("INR")

    private val _exchangeRate = MutableStateFlow(ExchangeRate(baseCurrency, emptyMap()))
    val exchangeRate: StateFlow<ExchangeRate> = _exchangeRate

    val conversionHistory: Flow<List<ConversionHistory>> = repository.getConversionHistory()

    var targetAmount by mutableStateOf("")

    init {
        viewModelScope.launch {
            _exchangeRate.value = repository.getCurrencyExchangeRates(baseCurrency)
        }
    }

    fun getExchangeRates(base: String = baseCurrency) {
        viewModelScope.launch {
            val rates = repository.getCurrencyExchangeRates(base)
            _exchangeRate.value = rates
            updateTargetAmount()
        }
    }

    fun enteredAmountChanged(enteredAmount: String) {
        if (enteredAmount.isDigitsOnly()) {
            this.enteredAmount = enteredAmount
            updateTargetAmount()
        }
    }

    fun swapCurrencies() {
        val prevBase = baseCurrency
        getExchangeRates(targetCurrency)
        baseCurrency = targetCurrency
        targetCurrency = prevBase
    }

    fun onBaseCurrencyChange(baseCurrency: String) {
        this.baseCurrency = baseCurrency
        _exchangeRate.value = ExchangeRate(this.baseCurrency, emptyMap())
        getExchangeRates(this.baseCurrency)
    }

    fun onTargetCurrencyChange(targetCurrency: String) {
        this.targetCurrency = targetCurrency
        updateTargetAmount()
    }

    val enteredAmountDouble get() =  if (enteredAmount.isEmpty()) 0.0 else enteredAmount.toDouble()

    private fun updateTargetAmount() {
        if(enteredAmount.isEmpty()) targetAmount = "";
        val targetAmountDouble = (BigDecimal(enteredAmountDouble).multiply(BigDecimal(exchangeRate.value.rates[targetCurrency] ?: 1.0))).toDouble()
        targetAmount =  String.format("%.2f", targetAmountDouble)
    }

    fun saveConversion(history: ConversionHistory) {
        viewModelScope.launch {
            repository.saveConversion(history)
        }
    }

    fun clearConversionHistory() {
        viewModelScope.launch {
            repository.clearConversionHistory()
        }
    }
}
