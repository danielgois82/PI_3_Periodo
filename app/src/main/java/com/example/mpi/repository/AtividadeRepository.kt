package com.example.mpi.repository

import android.content.ContentValues
import android.content.Context
import com.example.mpi.data.Acao
import com.example.mpi.data.Atividade
import com.example.mpi.data.DatabaseHelper
import com.example.mpi.data.DatabaseHelper.Companion.COLUMN_ATIVIDADE_ID
import com.example.mpi.data.DatabaseHelper.Companion.COLUMN_ATIVIDADE_IS_APROVADO
import com.example.mpi.data.DatabaseHelper.Companion.COLUMN_ATIVIDADE_IS_FINALIZADO
import com.example.mpi.data.DatabaseHelper.Companion.TABLE_ATIVIDADE

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

    fun obterTodasAtividades(): List<Atividade>{
        val db = dataBase.readableDatabase
        val atividades: MutableList<Atividade> = arrayListOf()
        val cursor = db.rawQuery("SELECT * FROM ${DatabaseHelper.TABLE_ATIVIDADE}", null)

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

    fun obterPercentualMes(atividade: Atividade, mes: Int): Double {
        val db = dataBase.readableDatabase

        val cursor = db.rawQuery("SELECT percentual FROM percentual_atividade WHERE id_atividade = ? AND mes = ?", arrayOf(atividade.id.toString(), mes.toString()))

        var percentual = 0.0
        if (cursor.moveToFirst()) {
            percentual = cursor.getDouble(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_PERCENTUAL_ATIVIDADE_PERCENTUAL))
        }

        cursor.close()
        db.close()

        return percentual
    }

    //FUNÇÃO PARA APROVAÇÃO DAS ATIVIDADES

    fun aprovarAtividade(id: Int): Boolean {
        val db = dataBase.writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_ATIVIDADE_IS_APROVADO, 1)
        }
        val result = db.update(TABLE_ATIVIDADE, values, "$COLUMN_ATIVIDADE_ID = ?", arrayOf(id.toString()))
        db.close()
        return result > 0
    }

    fun obterAtividadesNaoAprovadas(): List<Atividade> {
        val db = dataBase.readableDatabase
        val atividades: MutableList<Atividade> = arrayListOf()
        val query = """
            SELECT A.*
            FROM ${DatabaseHelper.TABLE_ATIVIDADE} AS A
            INNER JOIN ${DatabaseHelper.TABLE_ACAO} AS B
            ON A.${DatabaseHelper.COLUMN_ATIVIDADE_ID_ACAO} = B.${DatabaseHelper.COLUMN_ACAO_ID}
            WHERE A.${DatabaseHelper.COLUMN_ATIVIDADE_IS_APROVADO} = 0
            AND B.${DatabaseHelper.COLUMN_ACAO_IS_APROVADO} = 1;
        """.trimIndent()
        val cursor = db.rawQuery(query, null)

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

    // FUNÇÃO PARA FINALIZAÇÃO DE ATIVIDADES

    fun finalizarAtividade(id: Int): Boolean {
        val db = dataBase.writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_ATIVIDADE_IS_FINALIZADO, 1)
        }
        val result = db.update(TABLE_ATIVIDADE, values, "$COLUMN_ATIVIDADE_ID = ?", arrayOf(id.toString()))
        db.close()
        return result > 0
    }

    fun obterAtividadesNaoFinalizadas() :List<Atividade>{
        val db = dataBase.readableDatabase
        val atividades: MutableList<Atividade> = arrayListOf()
        val cursor = db.rawQuery(
            "SELECT * FROM ${DatabaseHelper.TABLE_ATIVIDADE} WHERE ${DatabaseHelper.COLUMN_ATIVIDADE_IS_APROVADO} = 1 AND ${DatabaseHelper.COLUMN_ATIVIDADE_IS_FINALIZADO} = 0",
            null
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

            if(obterPercentualTotalAtividade(atividade) == 100.0){
                atividades.add(atividade)
            }else{
                continue
            }

        }
        cursor.close()
        db.close()

        return atividades
    }

}