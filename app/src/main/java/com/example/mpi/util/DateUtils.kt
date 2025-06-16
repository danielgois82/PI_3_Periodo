package com.example.mpi.util

import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.concurrent.TimeUnit

/**
 * [DateUtils] é um objeto utilitário que fornece funções convenientes para
 * manipulação e conversão de datas no formato "dd/MM/yyyy".
 *
 * Inclui métodos para converter entre [String] e [Date], calcular a diferença
 * em dias entre duas datas, e obter a data atual sem informações de tempo.
 */
object DateUtils {

    /**
     * Define o formato padrão para as operações de data.
     */
    const val DATE_FORMAT = "dd/MM/yyyy"

    /**
     * Converte uma string de data no formato "dd/MM/yyyy" para um objeto [Date].
     *
     * Se a string não estiver no formato esperado ou for inválida, uma exceção
     * [ParseException] será capturada e `null` será retornado.
     *
     * @param dateString A string de data a ser convertida (ex: "25/12/2024").
     * @return Um objeto [Date] correspondente à string, ou `null` se a conversão falhar.
     */
    fun convertStringToDate(dateString: String): Date? {
        return try {
            val formatter = SimpleDateFormat(DATE_FORMAT, Locale.getDefault())
            formatter.parse(dateString)
        } catch (e: ParseException) {
            e.printStackTrace()
            null
        }
    }

    /**
     * Converte um objeto [Date] para uma string no formato "dd/MM/yyyy".
     *
     * @param date O objeto [Date] a ser convertido.
     * @return A representação em string da data no formato "dd/MM/yyyy".
     */
    fun convertDateToString(date: Date): String {
        val formatter = SimpleDateFormat(DATE_FORMAT, Locale.getDefault())
        return formatter.format(date)
    }

    /**
     * Calcula a diferença em dias entre duas datas.
     *
     * O cálculo é feito como `date2 - date1`.
     * Retorna um valor positivo se a `date1` for anterior à `date2`,
     * e um valor negativo se a `date1` for posterior à `date2`.
     *
     * @param date1 A primeira data para o cálculo.
     * @param date2 A segunda data para o cálculo.
     * @return A diferença em dias entre `date2` e `date1`.
     */
    fun getDaysDifference(date1: Date, date2: Date): Long {
        val diffInMillies = date2.time - date1.time
        return TimeUnit.DAYS.convert(diffInMillies, TimeUnit.MILLISECONDS)
    }

    /**
     * Obtém a data atual sem as informações de hora, minuto, segundo e milissegundo.
     *
     * Isso é útil para comparações de datas que só consideram o dia, mês e ano,
     * ignorando o tempo exato.
     *
     * @return Um objeto [Date] representando a data atual à meia-noite (00:00:00.000).
     */
    fun getCurrentDateWithoutTime(): Date {
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)
        return calendar.time
    }
}