package com.example.mpi.repository

import android.content.ContentValues
import android.content.Context
import com.example.mpi.data.Atividade
import com.example.mpi.data.DatabaseHelper
import com.example.mpi.data.PercentualAtividade

class PercentualAtividadeRepository(context: Context) {

    private var dataBase: DatabaseHelper = DatabaseHelper(context)

    companion object {
        private lateinit var instance: PercentualAtividadeRepository

        fun getInstance(context: Context): PercentualAtividadeRepository {
            synchronized(this) {
                if (!::instance.isInitialized) {
                    instance = PercentualAtividadeRepository(context)
                }
            }
            return instance
        }
    }

    fun inserirPercentuaisAtividade(atividade: Atividade) {
        val db = dataBase.writableDatabase

        for (mes in 1..12) {
            val values = ContentValues().apply {
                put(DatabaseHelper.COLUMN_PERCENTUAL_ATIVIDADE_MES, mes)
                put(DatabaseHelper.COLUMN_PERCENTUAL_ATIVIDADE_ID_ATIVIDADE, atividade.id)
            }
            db.insert(DatabaseHelper.TABLE_PERCENTUAL_ATIVIDADE, null, values)
        }
    }

    fun obterTodosPercentuais(atividade: Atividade): List<PercentualAtividade> {
        val db = dataBase.readableDatabase

        val cursor = db.rawQuery("SELECT * FROM percentual_atividade WHERE id_atividade = ?", arrayOf(atividade.id.toString()))

        val percentuaisAtividade: MutableList<PercentualAtividade> = arrayListOf()
        while (cursor.moveToNext()) {
            val id = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_PERCENTUAL_ATIVIDADE_ID))
            val mes = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_PERCENTUAL_ATIVIDADE_MES))
            val percentual = cursor.getDouble(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_PERCENTUAL_ATIVIDADE_PERCENTUAL))
            val idAtividade = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_PERCENTUAL_ATIVIDADE_ID_ATIVIDADE))

            val percentualAtividade = PercentualAtividade(id, mes, percentual, idAtividade)
            percentuaisAtividade.add(percentualAtividade)
        }

        cursor.close()
        db.close()

        return percentuaisAtividade
    }

    fun obterPercentualMes(atividade: Atividade, mes: Int): PercentualAtividade? {
        val db = dataBase.readableDatabase

        val cursor = db.rawQuery("SELECT * FROM percentual_atividade WHERE id_atividade = ? AND mes = ?", arrayOf(atividade.id.toString(), mes.toString()))

        var percentualMes: PercentualAtividade? = null
        if (cursor.moveToFirst()) {
            val id = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_PERCENTUAL_ATIVIDADE_ID))
            val mesEscolhido = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_PERCENTUAL_ATIVIDADE_MES))
            val percentual = cursor.getDouble(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_PERCENTUAL_ATIVIDADE_PERCENTUAL))
            val idAtividade = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_PERCENTUAL_ATIVIDADE_ID_ATIVIDADE))

            percentualMes = PercentualAtividade(id, mesEscolhido, percentual, idAtividade)
        }

        cursor.close()
        db.close()

        return percentualMes
    }

    fun atualizarPercentualMes(percentualAtividade: PercentualAtividade, novoPercentual: Double): Boolean {
        val db = dataBase.writableDatabase

        val valor = ContentValues().apply {
            put("percentual", novoPercentual)
        }

        val linhasAfetadas = db.update(
            DatabaseHelper.TABLE_PERCENTUAL_ATIVIDADE,
            valor,
            "id = ? AND mes = ?",
            arrayOf(percentualAtividade.id.toString(), percentualAtividade.mes.toString())
        )

        return linhasAfetadas > 0
    }

    fun removerPercentuaisAtividade(atividade: Atividade) {
        val db = dataBase.writableDatabase

        db.delete(
            DatabaseHelper.TABLE_PERCENTUAL_ATIVIDADE,
            "${DatabaseHelper.COLUMN_PERCENTUAL_ATIVIDADE_ID} = ?",
            arrayOf(atividade.id.toString())
        )
    }
}