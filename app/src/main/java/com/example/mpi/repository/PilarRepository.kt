package com.example.mpi.repository

import android.content.Context
import com.example.mpi.data.Calendario
import com.example.mpi.data.DatabaseHelper
import com.example.mpi.data.Pilar

class PilarRepository (context: Context) {
    private var dataBase: DatabaseHelper = DatabaseHelper(context)

    companion object {
        private lateinit var instance: PilarRepository

        fun getInstance(context: Context): PilarRepository {
            synchronized(this) {
                if (!::instance.isInitialized) {
                    instance = PilarRepository(context)
                }
            }
            return instance
        }
    }

    fun obterTodosPilares(calendario: Calendario): List<Pilar> {
        val db = dataBase.readableDatabase

        val cursor = db.rawQuery("SELECT * FROM pilar WHERE id_calendario = ?", arrayOf(calendario.id.toString()))

        var pilares: MutableList<Pilar> = arrayListOf()
        while (cursor.moveToNext()) {
            val id = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_PILAR_ID))
            val nome = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_PILAR_NOME))
            val descricao = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_PILAR_DESCRICAO))
            val dataInicio = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_PILAR_DATA_INICIO))
            val dataTermino = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_PILAR_DATA_TERMINO))
            val aprovado = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_PILAR_IS_APROVADO)) != 0
            val percentual = cursor.getDouble(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_PILAR_PERCENTUAL))
            val idCalendario = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_PILAR_ID_CALENDARIO))
            val idUsuario = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_PILAR_ID_USUARIO))

            val pilar = Pilar(id, nome, descricao, dataInicio, dataTermino, aprovado, percentual, idCalendario, idUsuario)
            pilares.add(pilar)
        }

        cursor.close()
        db.close()

        return pilares
    }

    fun obterPilarPorId(calendario: Calendario, idPilar: Int): Pilar? {
        val db = dataBase.readableDatabase

        val cursor = db.rawQuery("SELECT * FROM pilar WHERE id_calendario = ? AND id = ?", arrayOf(calendario.id.toString(), idPilar.toString()))

        var pilar: Pilar? = null
        if (cursor.moveToFirst()) {
            val id = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_PILAR_ID))
            val nome = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_PILAR_NOME))
            val descricao = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_PILAR_DESCRICAO))
            val dataInicio = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_PILAR_DATA_INICIO))
            val dataTermino = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_PILAR_DATA_TERMINO))
            val aprovado = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_PILAR_IS_APROVADO)) != 0
            val percentual = cursor.getDouble(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_PILAR_PERCENTUAL))
            val idCalendario = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_PILAR_ID_CALENDARIO))
            val idUsuario = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_PILAR_ID_USUARIO))

            pilar = Pilar(id, nome, descricao, dataInicio, dataTermino, aprovado, percentual, idCalendario, idUsuario)
        }

        cursor.close()
        db.close()

        return pilar
    }

}