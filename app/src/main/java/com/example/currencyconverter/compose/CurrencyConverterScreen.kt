package com.example.currencyconverter.compose

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.currencyconverter.CurrencyViewModel
import com.example.currencyconverter.DateUtil
import com.example.currencyconverter.data.local.ConversionHistory
import com.example.currencyconverter.ui.theme.CurrencyConverterTheme


@Composable
fun CurrencyConverterScreen(currencyViewModel: CurrencyViewModel = viewModel()) {

    val conversionHistory by currencyViewModel.conversionHistory.collectAsStateWithLifecycle(
        emptyList()
    )



    Column(modifier = Modifier
        .fillMaxSize()
        .padding(16.dp)) {
        TextField(
            value = currencyViewModel.enteredAmount,
            onValueChange = { currencyViewModel.enteredAmountChanged(it) },
            label = { Text("Amount") },
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
        )

        Spacer(modifier = Modifier.height(16.dp))

        Row(modifier = Modifier.fillMaxWidth()) {
            Surface(Modifier.weight(1f)) {
                Demo_ExposedDropdownMenuBox(currCurrency = currencyViewModel.baseCurrency,
                    allCurrencies = currencyViewModel.exchangeRate.collectAsState().value.rates.keys.toList(),
                    onCurrencyClick = {currencyViewModel.onBaseCurrencyChange(it)}
                )
            }
            Spacer(modifier = Modifier.width(16.dp))
            Surface(Modifier.weight(1f)) {
                Demo_ExposedDropdownMenuBox(currCurrency = currencyViewModel.targetCurrency,
                    allCurrencies = currencyViewModel.exchangeRate.collectAsState().value.rates.keys.toList(),
                    onCurrencyClick = {currencyViewModel.onTargetCurrencyChange(it)}
                )
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        Row(modifier = Modifier
            .fillMaxWidth()
            .padding(start = 32.dp)) {
            Text(text = "Converted Amount: ${currencyViewModel.targetAmount}")
        }

        Spacer(modifier = Modifier.height(32.dp))

        Button(onClick = {
            currencyViewModel.swapCurrencies()
        }) {
            Text(text = "Swap Currencies")
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(onClick = {
            currencyViewModel.getExchangeRates()
        }) {
            Text(text = "Refresh Currency rates")
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(onClick = {
            currencyViewModel.saveConversion(
                ConversionHistory(
                    baseCurrency = currencyViewModel.baseCurrency,
                    targetCurrency = currencyViewModel.targetCurrency,
                    amount = currencyViewModel.enteredAmountDouble,
                    result = currencyViewModel.targetAmount.toDouble(),
                    timestamp = System.currentTimeMillis()
                )
            )
        }) {
            Text(text = "Save Conversion")
        }

        Spacer(modifier = Modifier.height(16.dp))

        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(text = "Conversion History:", style = MaterialTheme.typography.titleLarge)
            Spacer(modifier = Modifier.width(16.dp))
            Button(onClick = { currencyViewModel.clearConversionHistory() }) {
               Text(text = "CLear")
            }
        }


        Spacer(modifier = Modifier.height(16.dp))

        LazyColumn(verticalArrangement = Arrangement.SpaceBetween) {
            items(items = conversionHistory) {
                Text(text = "${it.amount} ${it.baseCurrency} = ${it.result} ${it.targetCurrency} on ${DateUtil.getFormatedDate(it.timestamp)}",
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(bottom = 8.dp))
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun CurrencyConverterPreview() {
    CurrencyConverterTheme {
        CurrencyConverterScreen()
    }
}