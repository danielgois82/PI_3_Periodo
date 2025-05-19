package com.example.mpi.repository

import android.content.Context
import com.example.mpi.data.Calendario
import com.example.mpi.data.DatabaseHelper

class CalendarioRepository (context: Context) {

    private var dataBase: DatabaseHelper = DatabaseHelper(context)

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

    fun obterTodosCalendarios(): List<Calendario> {
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

}
