package com.example.mpi.repository

import android.content.Context
import com.example.mpi.data.DatabaseHelper
import com.example.mpi.ui.pilar.Pilar
import com.example.mpi.ui.subpilar.Subpilar

class SubpilarRepository (context: Context) {

    private var dataBase: DatabaseHelper = DatabaseHelper(context)

    companion object {
        private lateinit var instance: SubpilarRepository

        fun getInstance(context: Context): SubpilarRepository {
            synchronized(this) {
                if (!::instance.isInitialized) {
                    instance = SubpilarRepository(context)
                }
            }
            return instance
        }
    }

    fun obterTodosSubpilares(pilar: Pilar): List<Subpilar> {
        val db = dataBase.readableDatabase

        val cursor = db.rawQuery("SELECT * FROM subpilar WHERE id_pilar = ?", arrayOf(pilar.id.toString()))

        var subpilares: MutableList<Subpilar> = arrayListOf()
        while (cursor.moveToNext()) {
            val id = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_SUBPILAR_ID))
            val nome = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_SUBPILAR_NOME))
            val descricao = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_SUBPILAR_DESCRICAO))
            val dataInicio = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_SUBPILAR_DATA_INICIO))
            val dataTermino = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_SUBPILAR_DATA_TERMINO))
            val aprovado = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_SUBPILAR_IS_APROVADO)) != 0
            val idPilar = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_SUBPILAR_ID_PILAR))
            val idUsuario = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_SUBPILAR_ID_USUARIO))

            val subpilar = Subpilar(id.toLong(), nome, descricao, dataInicio, dataTermino, aprovado, idPilar.toLong(), idUsuario.toLong())
            subpilares.add(subpilar)
        }

        cursor.close()
        db.close()

        return subpilares
    }

}