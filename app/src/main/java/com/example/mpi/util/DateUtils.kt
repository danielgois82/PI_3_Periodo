package com.example.mpi.util

import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.concurrent.TimeUnit

object DateUtils {

    const val DATE_FORMAT = "dd/MM/yyyy"

    // Converte String para Date
    fun convertStringToDate(dateString: String): Date? {
        return try {
            val formatter = SimpleDateFormat(DATE_FORMAT, Locale.getDefault())
            formatter.parse(dateString)
        } catch (e: ParseException) {
            e.printStackTrace()
            null
        }
    }

    // Converte Date para String
    fun convertDateToString(date: Date): String {
        val formatter = SimpleDateFormat(DATE_FORMAT, Locale.getDefault())
        return formatter.format(date)
    }

    // Calcula a diferença em dias entre duas datas (data inicial - data final)
    // Retorna um valor positivo se data inicial for anterior a data final,
    // e negativo se data inicial for posterior a data final.
    fun getDaysDifference(date1: Date, date2: Date): Long {
        val diffInMillies = date2.time - date1.time
        return TimeUnit.DAYS.convert(diffInMillies, TimeUnit.MILLISECONDS)
    }

    // Obtém a data atual sem informação de hora (para comparação de dias)
    fun getCurrentDateWithoutTime(): Date {
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)
        return calendar.time
    }
}