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

/**
 * Repositório para gerenciar operações de dados relacionadas às [Atividade]s no sistema.
 *
 * Esta classe é responsável por interagir com a tabela `atividade` no banco de dados SQLite
 * (via [DatabaseHelper]), fornecendo métodos para buscar atividades por diferentes critérios,
 * calcular seu progresso, e gerenciar seus status de aprovação e finalização.
 * Ela também colabora com o [PercentualAtividadeRepository] para obter e manipular
 * os percentuais mensais de cada atividade.
 *
 * Adota o padrão **Singleton** para garantir que apenas uma instância deste repositório
 * exista em toda a aplicação, otimizando o gerenciamento da conexão com o banco de dados.
 *
 * @property dataBase Uma instância de [DatabaseHelper] para acessar o banco de dados.
 * @property percentualAtividadeRepository Uma instância de [PercentualAtividadeRepository] para operações relacionadas aos percentuais mensais das atividades.
 */
class AtividadeRepository (context: Context) {

    private var dataBase: DatabaseHelper = DatabaseHelper(context)
    private val percentualAtividadeRepository: PercentualAtividadeRepository = PercentualAtividadeRepository.getInstance(context)

    companion object {
        private lateinit var instance: AtividadeRepository

        /**
         * Retorna a única instância de [AtividadeRepository] (Singleton).
         * Se a instância ainda não foi inicializada, ela é criada de forma segura para threads.
         *
         * @param context O [Context] da aplicação.
         * @return A instância de [AtividadeRepository].
         */
        fun getInstance(context: Context): AtividadeRepository {
            synchronized(this) {
                if (!::instance.isInitialized) {
                    instance = AtividadeRepository(context)
                }
            }
            return instance
        }
    }

    /**
     * Obtém uma lista contendo todas as [Atividade]s existentes no banco de dados, sem nenhum filtro.
     *
     * Este método é útil para operações que requerem a listagem completa de todas as atividades
     * cadastradas no sistema.
     *
     * @return Uma [List] de objetos [Atividade] representando todas as atividades disponíveis.
     */
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

    /**
     * Obtém uma lista de todas as [Atividade]s diretamente associadas a uma [Acao] específica.
     *
     * @param acao O objeto [Acao] cujas atividades serão buscadas.
     * @return Uma [MutableList] de objetos [Atividade] que pertencem diretamente à ação especificada.
     */
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

    /**
     * Obtém uma lista de [Atividade]s que não foram finalizadas e são atribuídas a um usuário específico.
     *
     * Este método é comumente usado para funcionalidades de notificação ou listagem de pendências.
     *
     * @param idUsuario O ID do usuário para o qual as atividades não finalizadas serão buscadas.
     * @return Uma [List] de objetos [Atividade] que ainda não foram concluídas por esse usuário.
     */
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

    /**
     * Calcula o percentual total de conclusão de uma [Atividade], somando os percentuais de cada mês.
     *
     * Assume que a soma dos 12 percentuais mensais (que variam de 0 a 100) resulta no percentual total de conclusão.
     * Por exemplo, se cada mês for 100%, o total seria 1200%. Se você quiser a média ou algo diferente,
     * a lógica aqui precisará ser ajustada.
     *
     * @param atividade O objeto [Atividade] para o qual o percentual total será calculado.
     * @return O percentual total da atividade como um [Double].
     */
    fun obterPercentualTotalAtividade(atividade: Atividade) : Double {
        val listaPercentualAtividade = percentualAtividadeRepository.obterTodosPercentuais(atividade)
        var somaPercentualAtividade = 0.0
        for (item in listaPercentualAtividade) {
            somaPercentualAtividade += item.percentual
        }

        return somaPercentualAtividade
    }

    /**
     * Obtém a quantidade de [Atividade]s que têm sua data de término em até 30 dias a partir da data atual.
     *
     * @return O número inteiro de atividades que terminam em 30 dias ou menos (e mais de 15 dias).
     */
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

    /**
     * Obtém a quantidade de [Atividade]s que têm sua data de término em até 15 dias a partir da data atual.
     *
     * @return O número inteiro de atividades que terminam em 15 dias ou menos (e mais de 7 dias).
     */
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

    /**
     * Obtém a quantidade de [Atividade]s que têm sua data de término em até 7 dias a partir da data atual.
     *
     * @return O número inteiro de atividades que terminam em 7 dias ou menos (incluindo hoje).
     */
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

    /**
     * Obtém a quantidade de [Atividade]s que estão atrasadas (com data de término passada).
     *
     * @return O número inteiro de atividades atrasadas.
     */
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

    /**
     * Obtém o percentual de conclusão de uma [Atividade] para um mês específico.
     *
     * @param atividade O objeto [Atividade] para o qual o percentual do mês será obtido.
     * @param mes O número do mês (1 a 12) para o qual o percentual será buscado.
     * @return O percentual de progresso da atividade para o mês especificado como um [Double]. Retorna 0.0 se não for encontrado.
     */
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

    /**
     * Marca uma [Atividade] específica como aprovada no banco de dados.
     *
     * @param id O ID da atividade a ser marcada como aprovada.
     * @return `true` se a atualização foi bem-sucedida, `false` caso contrário.
     */
    fun aprovarAtividade(id: Int): Boolean {
        val db = dataBase.writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_ATIVIDADE_IS_APROVADO, 1)
        }
        val result = db.update(TABLE_ATIVIDADE, values, "$COLUMN_ATIVIDADE_ID = ?", arrayOf(id.toString()))
        db.close()
        return result > 0
    }

    /**
     * Obtém uma lista de todas as [Atividade]s que ainda não foram aprovadas,
     * mas cuja [Acao] associada já foi aprovada.
     *
     * @return Uma [List] de objetos [Atividade] que aguardam aprovação.
     */
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

    /**
     * Marca uma [Atividade] específica como finalizada no banco de dados.
     *
     * @param id O ID da atividade a ser marcada como finalizada.
     * @return `true` se a atualização foi bem-sucedida, `false` caso contrário.
     */
    fun finalizarAtividade(id: Int): Boolean {
        val db = dataBase.writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_ATIVIDADE_IS_FINALIZADO, 1)
        }
        val result = db.update(TABLE_ATIVIDADE, values, "$COLUMN_ATIVIDADE_ID = ?", arrayOf(id.toString()))
        db.close()
        return result > 0
    }

    /**
     * Obtém uma lista de todas as [Atividade]s que estão aprovadas, mas ainda não foram finalizadas,
     * e cujo percentual total de atividades é 100%.
     *
     * @return Uma [List] de objetos [Atividade] que estão prontas para serem marcadas como finalizadas manualmente (se aplicável).
     */
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