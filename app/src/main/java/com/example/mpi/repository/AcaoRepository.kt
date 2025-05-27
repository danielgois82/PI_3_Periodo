package com.example.mpi.repository

import android.content.Context
import com.example.mpi.data.Acao
import com.example.mpi.data.DatabaseHelper
import com.example.mpi.data.Pilar
import com.example.mpi.data.Subpilar

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
            val descricao = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_ACAO_DESCRICAO))
            val dataInicio = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_ACAO_DATA_INICIO))
            val dataTermino = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_ACAO_DATA_TERMINO))
            val responsavel = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_ACAO_RESPONSAVEL))
            val aprovado = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_ACAO_IS_APROVADO)) != 0
            val finalizado = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_ACAO_IS_FINALIZADO)) != 0
            val idPilar = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_ACAO_ID_PILAR))
            val idSubpilar = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_ACAO_ID_SUBPILAR))
            val idUsuario = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_ACAO_ID_USUARIO))

            val acao = Acao(id, nome, descricao, dataInicio, dataTermino, responsavel, aprovado, finalizado, idPilar, idSubpilar, idUsuario)
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
            val responsavel = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_ACAO_RESPONSAVEL))
            val aprovado = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_ACAO_IS_APROVADO)) != 0
            val finalizado = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_ACAO_IS_FINALIZADO)) != 0
            val descricao = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_ACAO_DESCRICAO))
            val idPilar = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_ACAO_ID_PILAR))
            val idSubpilar = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_ACAO_ID_SUBPILAR))
            val idUsuario = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_ACAO_ID_USUARIO))

            val acao = Acao(id, nome, descricao, dataInicio, dataTermino, responsavel, aprovado, finalizado, idPilar, idSubpilar, idUsuario)

            acoes.add(acao)
        }

        cursor.close()
        db.close()

        return acoes
    }


    fun obterAcoesNaoFinalizadasPorUsuario(idUsuario: Int): List<Acao> {
        val db = dataBase.readableDatabase
        val acoes: MutableList<Acao> = arrayListOf()
        val cursor = db.rawQuery(
            "SELECT * FROM ${DatabaseHelper.TABLE_ACAO} WHERE ${DatabaseHelper.COLUMN_ACAO_IS_FINALIZADO} = 0 AND ${DatabaseHelper.COLUMN_ACAO_ID_USUARIO} = ?",
            arrayOf(idUsuario.toString())
        )

        while (cursor.moveToNext()) {
            val id = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_ACAO_ID))
            val nome = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_ACAO_NOME))
            val dataInicio = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_ACAO_DATA_INICIO))
            val dataTermino = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_ACAO_DATA_TERMINO))
            val responsavel = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_ACAO_RESPONSAVEL))
            val aprovado = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_ACAO_IS_APROVADO)) != 0
            val finalizado = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_ACAO_IS_FINALIZADO)) != 0
            val descricao = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_ACAO_DESCRICAO))
            val idPilar = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_ACAO_ID_PILAR))
            val idSubpilar = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_ACAO_ID_SUBPILAR))
            val idUsuarioAcao = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_ACAO_ID_USUARIO))

            val acao = Acao(id, nome, descricao, dataInicio, dataTermino, responsavel, aprovado, finalizado, idPilar, idSubpilar, idUsuarioAcao)

            acoes.add(acao)
        }

        cursor.close()
        db.close()

        return acoes
    }

}