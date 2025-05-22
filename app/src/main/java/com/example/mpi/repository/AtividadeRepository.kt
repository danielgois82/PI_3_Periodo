package com.example.mpi.repository

import android.content.Context
import com.example.mpi.data.DatabaseHelper
import com.example.mpi.ui.acao.Acao
import com.example.mpi.ui.atividade.Atividade

class AtividadeRepository (context: Context) {

    private var dataBase: DatabaseHelper = DatabaseHelper(context)

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

    fun obterTodasAtividades(acao: Acao) : List<Atividade> {

        val db = dataBase.readableDatabase

        val cursor = db.rawQuery("SELECT * FROM atividade WHERE id_acao = ?", arrayOf(acao.id.toString()))

        var atividades: MutableList<Atividade> = arrayListOf()
        while (cursor.moveToNext()) {
            val id = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_ACAO_ID))
            val nome = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_ACAO_NOME))
            val dataInicio = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_ACAO_DATA_INICIO))
            val dataTermino = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_ACAO_DATA_TERMINO))
            val responsavel = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_ACAO_DATA_TERMINO))
            val aprovado = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_ACAO_IS_APROVADO)) != 0
            val finalizado = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_ACAO_IS_FINALIZADO)) != 0
            val descricao = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_ACAO_DESCRICAO))
            val orcamento = cursor.getDouble(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_ACAO_DESCRICAO))
            val idAcao = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_ACAO_ID_PILAR))
            val idUsuario = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_PILAR_ID_USUARIO))

            val atividade = Atividade(id.toLong(), nome, descricao, dataInicio, dataTermino, responsavel, aprovado, finalizado, orcamento, idAcao.toLong(), idUsuario.toLong())
            atividades.add(atividade)
        }

        cursor.close()
        db.close()

        return atividades
    }

}