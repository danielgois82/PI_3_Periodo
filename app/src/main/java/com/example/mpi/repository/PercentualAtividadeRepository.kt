package com.example.mpi.repository

import android.content.ContentValues
import android.content.Context
import com.example.mpi.data.Atividade
import com.example.mpi.data.DatabaseHelper
import com.example.mpi.data.PercentualAtividade

/**
 * Repositório para gerenciar operações de dados relacionadas aos percentuais de conclusão de [Atividade]s
 * por mês no sistema.
 *
 * Esta classe é responsável por interagir com a tabela `percentual_atividade` no banco de dados SQLite,
 * provendo métodos para inserir, consultar, atualizar e remover os registros de percentuais mensais
 * para cada atividade.
 *
 * Adota o padrão **Singleton** para garantir que apenas uma instância deste repositório
 * exista em toda a aplicação, otimizando o gerenciamento da conexão com o banco de dados.
 *
 * @property dataBase Uma instância de [DatabaseHelper] para acessar o banco de dados.
 */
class PercentualAtividadeRepository(context: Context) {

    private var dataBase: DatabaseHelper = DatabaseHelper(context)

    companion object {
        private lateinit var instance: PercentualAtividadeRepository

        /**
         * Retorna a única instância de [PercentualAtividadeRepository] (Singleton).
         * Se a instância ainda não foi inicializada, ela é criada de forma segura para threads.
         *
         * @param context O [Context] da aplicação.
         * @return A instância de [PercentualAtividadeRepository].
         */
        fun getInstance(context: Context): PercentualAtividadeRepository {
            synchronized(this) {
                if (!::instance.isInitialized) {
                    instance = PercentualAtividadeRepository(context)
                }
            }
            return instance
        }
    }

    /**
     * Insere os registros de percentual mensal para uma [Atividade] específica.
     *
     * Este método inicializa 12 registros na tabela `percentual_atividade` (um para cada mês do ano)
     * associados à atividade fornecida, com o percentual inicializado em 0.0.
     *
     * @param atividade : O objeto [Atividade] para o qual os percentuais mensais serão inseridos.
     */
    fun inserirPercentuaisAtividade(atividade: Atividade) {
        val db = dataBase.writableDatabase

        for (mes in 1..12) {
            val values = ContentValues().apply {
                put(DatabaseHelper.COLUMN_PERCENTUAL_ATIVIDADE_MES, mes)
                put(DatabaseHelper.COLUMN_PERCENTUAL_ATIVIDADE_ID_ATIVIDADE, atividade.id)
            }
            db.insert(DatabaseHelper.TABLE_PERCENTUAL_ATIVIDADE, null, values)
        }

        db.close()
    }

    /**
     * Obtém todos os registros de percentuais mensais associados a uma [Atividade] específica.
     *
     * @param atividade O objeto [Atividade] para o qual os percentuais serão buscados.
     * @return Uma [List] de objetos [PercentualAtividade] representando o progresso mensal da atividade.
     */
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


    /**
     * Atualiza o percentual de conclusão para um registro de [PercentualAtividade] existente.
     *
     * @param percentualAtividade O objeto [PercentualAtividade] a ser atualizado. É importante que
     * este objeto contenha o `id` e o `mes` corretos para identificar o registro.
     * @param novoPercentual O novo valor percentual (entre 0.0 e 1.0) a ser atribuído.
     * @return `true` se a atualização foi bem-sucedida (uma ou mais linhas foram afetadas), `false` caso contrário.
     */
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

    /**
     * Remove todos os registros de percentuais mensais associados a uma [Atividade] específica.
     *
     * Este método é útil quando uma atividade é excluída e seus dados de percentual não são mais necessários.
     *
     * @param atividade : O objeto [Atividade] cujos percentuais mensais serão removidos.
     */
    fun removerPercentuaisAtividade(atividade: Atividade) {
        val db = dataBase.writableDatabase

        db.delete(
            DatabaseHelper.TABLE_PERCENTUAL_ATIVIDADE,
            "${DatabaseHelper.COLUMN_PERCENTUAL_ATIVIDADE_ID_ATIVIDADE} = ?",
            arrayOf(atividade.id.toString())
        )
        db.close()
    }

    /*
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
    */
}