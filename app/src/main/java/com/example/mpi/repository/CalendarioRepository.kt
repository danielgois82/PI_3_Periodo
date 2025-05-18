package com.example.mpi.repository

import android.content.ContentValues
import android.content.Context
import com.example.mpi.data.DatabaseHelper

class CalendarioRepository private constructor(context: Context) {
    private val dataBase: DatabaseHelper = DatabaseHelper(context)

    companion object {
        private lateinit var instance: CalendarioRepository

        fun getInstance(context: Context): CalendarioRepository {
            synchronized(this) {
                if (!::instance.isInitialized) {
                    instance = CalendarioRepository(context)
                }
            }
            return instance
        }
    }

    fun obterIdCalendarioPorAno(ano: Int): Long {
        val db = dataBase.readableDatabase
        var calendarioId: Long = -1
        val cursor = db.query(
            DatabaseHelper.TABLE_CALENDARIO,
            arrayOf(DatabaseHelper.COLUMN_CALENDARIO_ID),
            "${DatabaseHelper.COLUMN_CALENDARIO_ANO} = ?",
            arrayOf(ano.toString()),
            null,
            null,
            null
        )
        cursor?.use {
            if (it.moveToFirst()) {
                calendarioId = it.getLong(0)
            }
        }
        cursor.close()
        db.close()
        return calendarioId
    }


    fun inserirCalendario(ano: Int): Long {
        val db = dataBase.writableDatabase
        val values = ContentValues().apply {
            put(DatabaseHelper.COLUMN_CALENDARIO_ANO, ano)
        }
        val newRowId = db.insert(DatabaseHelper.TABLE_CALENDARIO, null, values)
        db.close()
        return newRowId
    }

    fun contarPilares(): Int {
        val db = dataBase.readableDatabase
        var count = 0
        val cursor = db.rawQuery("SELECT COUNT(*) FROM ${DatabaseHelper.TABLE_PILAR}", null)
        cursor?.use {
            if (it.moveToFirst()) {
                count = it.getInt(0)
            }
        }
        cursor.close()
        db.close()
        return count
    }
}