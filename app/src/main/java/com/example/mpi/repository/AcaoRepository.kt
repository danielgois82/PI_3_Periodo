package com.example.mpi.repository

import android.content.Context
import com.example.mpi.data.DatabaseHelper
import com.example.mpi.ui.acao.Acao
import com.example.mpi.ui.pilar.Pilar
import com.example.mpi.ui.subpilar.Subpilar

class AcaoRepository (context: Context) {

    private var dataBase: DatabaseHelper = DatabaseHelper(context)

    companion object {
        private lateinit var instance: AcaoRepository

        fun getInstance(context: Context): AcaoRepository {
            synchronized(this) {
                if (!::instance.isInitialized) {
                    instance = AcaoRepository(context)
                }
            }
            return instance
        }
    }

    fun obterTodasAcoes(pilar: Pilar) : List<Acao> {

        val db = dataBase.readableDatabase

        val cursor = db.rawQuery("SELECT * FROM acao WHERE id_subpilar = ?", arrayOf(pilar.id.toString()))

        var acoes: MutableList<Acao> = arrayListOf()
        while (cursor.moveToNext()) {
            val id = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_ACAO_ID))
            val nome = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_ACAO_NOME))
            val dataInicio = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_ACAO_DATA_INICIO))
            val dataTermino = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_ACAO_DATA_TERMINO))
            val responsavel = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_ACAO_DATA_TERMINO))
            val aprovado = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_ACAO_IS_APROVADO)) != 0
            val finalizado = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_ACAO_IS_FINALIZADO)) != 0
            val descricao = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_ACAO_DESCRICAO))
            val idPilar = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_ACAO_ID_PILAR))
            val idSubpilar = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_ACAO_ID_SUBPILAR))
            val idUsuario = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_PILAR_ID_USUARIO))

            val acao = Acao(id.toLong(), nome, descricao, dataInicio, dataTermino, responsavel, aprovado, finalizado, idPilar.toLong(), idSubpilar.toLong(), idUsuario.toLong())
            acoes.add(acao)
        }

        cursor.close()
        db.close()

        return acoes
    }

    fun obterTodasAcoes(subpilar: Subpilar) : List<Acao> {

        val db = dataBase.readableDatabase

        val cursor = db.rawQuery("SELECT * FROM acao WHERE id_subpilar = ?", arrayOf(subpilar.id.toString()))

        var acoes: MutableList<Acao> = arrayListOf()
        while (cursor.moveToNext()) {
            val id = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_ACAO_ID))
            val nome = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_ACAO_NOME))
            val dataInicio = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_ACAO_DATA_INICIO))
            val dataTermino = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_ACAO_DATA_TERMINO))
            val responsavel = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_ACAO_DATA_TERMINO))
            val aprovado = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_ACAO_IS_APROVADO)) != 0
            val finalizado = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_ACAO_IS_FINALIZADO)) != 0
            val descricao = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_ACAO_DESCRICAO))
            val idPilar = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_ACAO_ID_PILAR))
            val idSubpilar = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_ACAO_ID_SUBPILAR))
            val idUsuario = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_PILAR_ID_USUARIO))

            val acao = Acao(id.toLong(), nome, descricao, dataInicio, dataTermino, responsavel, aprovado, finalizado, idPilar.toLong(), idSubpilar.toLong(), idUsuario.toLong())
            acoes.add(acao)
        }

        cursor.close()
        db.close()

        return acoes
    }

}