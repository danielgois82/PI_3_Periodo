package com.example.mpi.repository

import android.content.Context
import com.example.mpi.data.Acao
import com.example.mpi.data.Atividade
import com.example.mpi.data.DatabaseHelper

class AtividadeRepository (context: Context) {

    private var dataBase: DatabaseHelper = DatabaseHelper(context)
    private val percentualAtividadeRepository: PercentualAtividadeRepository = PercentualAtividadeRepository.getInstance(context)

    companion object {
        private lateinit var instance: AtividadeRepository

        fun getInstance(context: Context): AtividadeRepository {
            synchronized(this) {
                if (!::instance.isInitialized) {
                    instance = AtividadeRepository(context)
                }
            }
            return instance
        }
    }

    fun obterTodasAtividades(acao: Acao) : MutableList<Atividade> {

        val db = dataBase.readableDatabase

        val cursor = db.rawQuery("SELECT * FROM atividade WHERE id_acao = ?", arrayOf(acao.id.toString()))

        var atividades: MutableList<Atividade> = arrayListOf()
        while (cursor.moveToNext()) {
            val id = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_ATIVIDADE_ID))
            val nome = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_ATIVIDADE_NOME))
            val descricao = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_ATIVIDADE_DESCRICAO))
            val dataInicio = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_ATIVIDADE_DATA_INICIO))
            val dataTermino = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_ATIVIDADE_DATA_TERMINO))
            val responsavel = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_ATIVIDADE_RESPONSAVEL))
            val aprovado = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_ATIVIDADE_IS_APROVADO)) != 0
            val finalizado = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_ATIVIDADE_IS_FINALIZADO)) != 0
            val orcamento = cursor.getDouble(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_ATIVIDADE_ORCAMENTO))
            val idAcao = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_ATIVIDADE_ID_ACAO))
            val idUsuario = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_ATIVIDADE_ID_USUARIO))

            val atividade = Atividade(id, nome, descricao, dataInicio, dataTermino, responsavel, aprovado, finalizado, orcamento, idAcao, idUsuario)

            atividades.add(atividade)
        }

        cursor.close()
        db.close()

        return atividades
    }

    //Criando método para a funcionalidade de notificação para obter as atividades que não foram finaliadas
    fun obterAtividadesNaoFinalizadasPorUsuario(idUsuario: Int): List<Atividade> {
        val db = dataBase.readableDatabase
        val atividades: MutableList<Atividade> = arrayListOf()

        // Seleciona atividades que não estão finalizadas (isFinalizado = 0) e pertencem ao usuário logado
        val cursor = db.rawQuery(
            "SELECT * FROM ${DatabaseHelper.TABLE_ATIVIDADE} WHERE ${DatabaseHelper.COLUMN_ATIVIDADE_IS_FINALIZADO} = 0 AND ${DatabaseHelper.COLUMN_ATIVIDADE_ID_USUARIO} = ?",
            arrayOf(idUsuario.toString())
        )

        while (cursor.moveToNext()) {
            val id = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_ATIVIDADE_ID))
            val nome = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_ATIVIDADE_NOME))
            val dataInicio = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_ATIVIDADE_DATA_INICIO))
            val dataTermino = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_ATIVIDADE_DATA_TERMINO))
            val responsavel = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_ATIVIDADE_RESPONSAVEL))
            val aprovado = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_ATIVIDADE_IS_APROVADO)) != 0
            val finalizado = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_ATIVIDADE_IS_FINALIZADO)) != 0
            val descricao = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_ATIVIDADE_DESCRICAO))
            val orcamento = cursor.getDouble(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_ATIVIDADE_ORCAMENTO))
            val idAcao = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_ATIVIDADE_ID_ACAO))
            val idUsuarioAtividade = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_ATIVIDADE_ID_USUARIO))

            val atividade = Atividade(id, nome, descricao, dataInicio, dataTermino, responsavel, aprovado, finalizado, orcamento, idAcao, idUsuarioAtividade)

            atividades.add(atividade)
        }

        cursor.close()
        db.close()

        return atividades
    }

    fun obterPercentualTotalAtividade(atividade: Atividade) : Double {
        val listaPercentualAtividade = percentualAtividadeRepository.obterTodosPercentuais(atividade)
        var somaPercentualAtividade = 0.0
        for (item in listaPercentualAtividade) {
            somaPercentualAtividade += item.percentual
        }

        return somaPercentualAtividade
    }

    fun obterQuantidadeAtividades30DiasOuMenos() : Int {
        val db = dataBase.readableDatabase

        var total = 0
        val cursor = db.rawQuery("SELECT COUNT(*) AS TOTAL FROM ${DatabaseHelper.TABLE_ATIVIDADE} WHERE DATE(substr(${DatabaseHelper.COLUMN_ATIVIDADE_DATA_TERMINO}, 7, 4) || '-' || substr(${DatabaseHelper.COLUMN_ATIVIDADE_DATA_TERMINO}, 4, 2) || '-' || substr(${DatabaseHelper.COLUMN_ATIVIDADE_DATA_TERMINO}, 1, 2)) BETWEEN DATE('now', '+16 days') AND DATE('now', '+30 days')", arrayOf())

        if (cursor.moveToFirst()) {
            total = cursor.getInt(cursor.getColumnIndexOrThrow("TOTAL"))
        }

        cursor.close()
        db.close()

        return total
    }

    fun obterQuantidadeAtividades15DiasOuMenos() : Int {
        val db = dataBase.readableDatabase

        var total = 0
        val cursor = db.rawQuery("SELECT COUNT(*) AS TOTAL FROM ${DatabaseHelper.TABLE_ATIVIDADE} WHERE DATE(substr(${DatabaseHelper.COLUMN_ATIVIDADE_DATA_TERMINO}, 7, 4) || '-' || substr(${DatabaseHelper.COLUMN_ATIVIDADE_DATA_TERMINO}, 4, 2) || '-' || substr(${DatabaseHelper.COLUMN_ATIVIDADE_DATA_TERMINO}, 1, 2)) BETWEEN DATE('now', '+8 days') AND DATE('now', '+15 days')", arrayOf())

        if (cursor.moveToFirst()) {
            total = cursor.getInt(cursor.getColumnIndexOrThrow("TOTAL"))
        }

        cursor.close()
        db.close()

        return total
    }

    fun obterQuantidadeAtividades7DiasOuMenos() : Int {
        val db = dataBase.readableDatabase

        var total = 0
        val cursor = db.rawQuery("SELECT COUNT(*) AS TOTAL FROM ${DatabaseHelper.TABLE_ATIVIDADE} WHERE DATE(substr(${DatabaseHelper.COLUMN_ATIVIDADE_DATA_TERMINO}, 7, 4) || '-' || substr(${DatabaseHelper.COLUMN_ATIVIDADE_DATA_TERMINO}, 4, 2) || '-' || substr(${DatabaseHelper.COLUMN_ATIVIDADE_DATA_TERMINO}, 1, 2)) BETWEEN DATE('now') AND DATE('now', '+7 days')", arrayOf())

        if (cursor.moveToFirst()) {
            total = cursor.getInt(cursor.getColumnIndexOrThrow("TOTAL"))
        }

        cursor.close()
        db.close()

        return total
    }

    fun obterQuantidadeAtividadesAtrasadas() : Int {
        val db = dataBase.readableDatabase

        var total = 0
        val cursor = db.rawQuery("SELECT COUNT(*) AS TOTAL FROM ${DatabaseHelper.TABLE_ATIVIDADE} WHERE DATE(substr(${DatabaseHelper.COLUMN_ATIVIDADE_DATA_TERMINO}, 7, 4) || '-' || substr(${DatabaseHelper.COLUMN_ATIVIDADE_DATA_TERMINO}, 4, 2) || '-' || substr(${DatabaseHelper.COLUMN_ATIVIDADE_DATA_TERMINO}, 1, 2)) < DATE('now')", arrayOf())

        if (cursor.moveToFirst()) {
            total = cursor.getInt(cursor.getColumnIndexOrThrow("TOTAL"))
        }

        cursor.close()
        db.close()

        return total
    }

}