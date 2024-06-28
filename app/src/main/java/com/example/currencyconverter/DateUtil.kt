package com.example.currencyconverter

import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter

object DateUtil {
    fun getFormatedDate(timestamp: Long): String {
        val instant = Instant.ofEpochMilli(timestamp)

        // Convert Instant to LocalDate (day, month, year)
        val date = instant.atZone(ZoneId.systemDefault()).toLocalDate()

        // Format the LocalDate as a String (optional step)
        return date.format(DateTimeFormatter.ofPattern("dd-MM-yyyy"))
    }
}