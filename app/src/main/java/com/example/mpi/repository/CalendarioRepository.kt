package com.example.mpi.repository

import android.content.Context
import com.example.mpi.data.Calendario
import com.example.mpi.data.DatabaseHelper
import android.content.ContentValues

class CalendarioRepository (context: Context) {
    private var dataBase: DatabaseHelper = DatabaseHelper(context)

//class CalendarioRepository private constructor(context: Context) {
//    private val dataBase: DatabaseHelper = DatabaseHelper(context)

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

    fun obterTodosCalendarios(): MutableList<Calendario> {
        val db = dataBase.readableDatabase

        val cursor = db.rawQuery("SELECT * FROM calendario", null)

        var calendarios: MutableList<Calendario> = arrayListOf()
        while (cursor.moveToNext()) {
            val id = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_CALENDARIO_ID))
            val ano = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_CALENDARIO_ANO))

            val calendario = Calendario(id, ano)
            calendarios.add(calendario)
        }

        cursor.close()
        db.close()

        return calendarios
    }

    fun obterIdCalendarioPorAno(ano: Int): Int {
        val db = dataBase.readableDatabase
        var calendarioId: Int = -1
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
                calendarioId = it.getInt(0)
            }
        }
        cursor.close()
        db.close()
        return calendarioId
    }

    fun inserirCalendario(ano: Int): Int {
        val db = dataBase.writableDatabase
        val values = ContentValues().apply {
            put(DatabaseHelper.COLUMN_CALENDARIO_ANO, ano)
        }
        val newRowId = db.insert(DatabaseHelper.TABLE_CALENDARIO, null, values)
        db.close()
        return newRowId.toInt()
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
