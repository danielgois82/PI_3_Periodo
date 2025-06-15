package com.example.mpi.repository

import android.content.Context
import com.example.mpi.data.Atividade
import com.example.mpi.data.Calendario
import com.example.mpi.data.DatabaseHelper
import com.example.mpi.data.Pilar
import com.example.mpi.repository.AcaoRepository
import com.example.mpi.repository.SubpilarRepository


/**
 * Repositório para gerenciar operações de dados relacionadas aos Pilares no sistema.
 *
 * Esta classe é responsável por interagir com a tabela `pilar` no banco de dados SQLite
 * (via [DatabaseHelper]), fornecendo métodos para buscar pilares, calcular seu progresso,
 * e validar sua exclusão. Ela também coordena com outros repositórios (como [AcaoRepository]
 * e [SubpilarRepository]) para obter dados hierárquicos e calcular métricas.
 *
 * Adota o padrão Singleton para garantir que apenas uma instância deste repositório
 * exista em toda a aplicação, otimizando o gerenciamento da conexão com o banco de dados.
 *
 * @property dataBase Uma instância de [DatabaseHelper] para acessar o banco de dados.
 * @property atividadeRepository Uma instância de [AtividadeRepository] para operações relacionadas a atividades.
 * @property acaoRepository Uma instância de [AcaoRepository] para operações relacionadas a ações.
 * @property subpilarRepository Uma instância de [SubpilarRepository] para operações relacionadas a subpilares.
 */
class PilarRepository (context: Context) {
    private var dataBase: DatabaseHelper = DatabaseHelper(context)
    private val atividadeRepository: AtividadeRepository = AtividadeRepository.getInstance(context)
    private val acaoRepository: AcaoRepository = AcaoRepository.getInstance(context)
    private val subpilarRepository: SubpilarRepository = SubpilarRepository.getInstance(context)


    companion object {
        private lateinit var instance: PilarRepository

        /**
         * Retorna a única instância de [PilarRepository] (Singleton).
         * Se a instância ainda não foi inicializada, ela é criada de forma segura para threads.
         *
         * @param context O [Context] da aplicação.
         * @return A instância de [PilarRepository].
         */
        fun getInstance(context: Context): PilarRepository {
            synchronized(this) {
                if (!::instance.isInitialized) {
                    instance = PilarRepository(context)
                }
            }
            return instance
        }
    }

    /**
     * Obtém uma lista de todos os [Pilar]es associados a um [Calendario] específico.
     *
     * @param calendario O objeto [Calendario] cujos pilares serão buscados.
     * @return Uma [MutableList] de objetos [Pilar] pertencentes ao calendário especificado.
     */
    fun obterTodosPilares(calendario: Calendario): MutableList<Pilar> {
        val db = dataBase.readableDatabase

        val cursor = db.rawQuery(
            "SELECT * FROM pilar WHERE id_calendario = ?",
            arrayOf(calendario.id.toString())
        )

        val pilares: MutableList<Pilar> = arrayListOf()
        while (cursor.moveToNext()) {
            val id = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_PILAR_ID))
            val nome =
                cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_PILAR_NOME))
            val descricao =
                cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_PILAR_DESCRICAO))
            val dataInicio =
                cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_PILAR_DATA_INICIO))
            val dataTermino =
                cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_PILAR_DATA_TERMINO))
            val percentual =
                cursor.getDouble(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_PILAR_PERCENTUAL)) // Quero tirar
            val idCalendario =
                cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_PILAR_ID_CALENDARIO))
            val idUsuario =
                cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_PILAR_ID_USUARIO))

            val pilar = Pilar(
                id,
                nome,
                descricao,
                dataInicio,
                dataTermino,
                percentual,
                idCalendario,
                idUsuario
            )
            pilares.add(pilar)
        }

        cursor.close()
        db.close()

        return pilares
    }

    /**
     * Obtém um [Pilar] específico com base no seu ID e no [Calendario] ao qual pertence.
     *
     * @param calendario O objeto [Calendario] para filtrar a busca do pilar.
     * @param idPilar O ID único do pilar a ser buscado.
     * @return O objeto [Pilar] se encontrado, ou `null` caso contrário.
     */
    fun obterPilarPorId(calendario: Calendario, idPilar: Int): Pilar? {
        val db = dataBase.readableDatabase

        val cursor = db.rawQuery(
            "SELECT * FROM pilar WHERE id_calendario = ? AND id = ?",
            arrayOf(calendario.id.toString(), idPilar.toString())
        )

        var pilar: Pilar? = null
        if (cursor.moveToFirst()) {
            val id = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_PILAR_ID))
            val nome =
                cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_PILAR_NOME))
            val descricao =
                cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_PILAR_DESCRICAO))
            val dataInicio =
                cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_PILAR_DATA_INICIO))
            val dataTermino =
                cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_PILAR_DATA_TERMINO))
            val percentual =
                cursor.getDouble(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_PILAR_PERCENTUAL)) // Quero tirar
            val idCalendario =
                cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_PILAR_ID_CALENDARIO))
            val idUsuario =
                cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_PILAR_ID_USUARIO))

            pilar = Pilar(
                id,
                nome,
                descricao,
                dataInicio,
                dataTermino,
                percentual,
                idCalendario,
                idUsuario
            )
        }

        cursor.close()
        db.close()

        return pilar
    }

    /**
     * Calcula o progresso total de um [Pilar], considerando o percentual de conclusão de suas Ações
     * (diretamente ou via Subpilares).
     *
     * Se o Pilar contiver Subpilares, o progresso é calculado pela média dos progressos dos Subpilares.
     * Se não houver Subpilares, o progresso é calculado pela média dos progressos das Ações diretamente ligadas ao Pilar.
     *
     * @param pilar O objeto [Pilar] para o qual o progresso será calculado.
     * @return O percentual de progresso total do pilar como um [Double] (entre 0.0 e 1.0).
     */
    fun obterProgressoPilar(pilar: Pilar): Double {
        var percentualTotalPilar = 0.0

        val listaSubpilar = subpilarRepository.obterTodosSubpilares(pilar)

        if (listaSubpilar.isEmpty()) {
            val listaAcoes = acaoRepository.obterTodasAcoes(pilar)
            var qtdAcoes = 0
            var somaPercentualAcoes = 0.0
            for (acao in listaAcoes) {
                somaPercentualAcoes += acaoRepository.obterPercentualTotalAcao(acao)
                qtdAcoes++
            }
            percentualTotalPilar = somaPercentualAcoes / qtdAcoes
        }

        if (listaSubpilar.isNotEmpty()) {
            var percentualTotalSubpilar = 0.0
            var qtdSubpilar = 0
            for (subpilar in listaSubpilar) {
                qtdSubpilar++
                val listaAcoes = acaoRepository.obterTodasAcoes(subpilar)
                var qtdAcoes = 0
                var somaPercentualAcoes = 0.0
                for (acao in listaAcoes) {
                    somaPercentualAcoes += acaoRepository.obterPercentualTotalAcao(acao)
                    qtdAcoes++
                }
                percentualTotalSubpilar += somaPercentualAcoes / qtdAcoes
            }
            percentualTotalPilar = percentualTotalSubpilar / qtdSubpilar
        }

        return percentualTotalPilar
    }

    /**
     * Calcula o percentual de progresso de um [Pilar] para um mês específico.
     *
     * Se o Pilar contiver Subpilares, o percentual do mês é calculado pela média dos percentuais
     * mensais dos Subpilares. Se não houver Subpilares, é a média dos percentuais mensais
     * das Ações diretamente ligadas ao Pilar.
     *
     * @param pilar O objeto Pilar para o qual o percentual do mês será calculado.
     * @param mes O número do mês (1 a 12) para o qual o percentual será obtido.
     * @return O percentual de progresso do pilar para o mês especificado como um [Double].
     */
    fun obterPercentualMes(pilar: Pilar, mes: Int): Double {
        var percentualMesPilar = 0.0

        val listaSubpilar = subpilarRepository.obterTodosSubpilares(pilar)

        if (listaSubpilar.isEmpty()) {
            val listaAcoes = acaoRepository.obterTodasAcoes(pilar)
            var qtdAcoes = 0
            var somaPercentualAcoes = 0.0
            for (acao in listaAcoes) {
                val listarAtividade = atividadeRepository.obterTodasAtividades(acao)
                somaPercentualAcoes += acaoRepository.obterPercentualMes(listarAtividade, mes)
                qtdAcoes++
            }
            percentualMesPilar = somaPercentualAcoes / qtdAcoes
        }

        if (listaSubpilar.isNotEmpty()) {
            var percentualTotalSubpilar = 0.0
            var qtdSubpilar = 0
            for (subpilar in listaSubpilar) {
                qtdSubpilar++
                val listaAcoes = acaoRepository.obterTodasAcoes(subpilar)
                var qtdAcoes = 0
                var somaPercentualAcoes = 0.0
                for (acao in listaAcoes) {
                    val listarAtividade = atividadeRepository.obterTodasAtividades(acao)
                    somaPercentualAcoes += acaoRepository.obterPercentualMes(listarAtividade, mes)
                    qtdAcoes++
                }
                percentualTotalSubpilar += somaPercentualAcoes / qtdAcoes
            }
            percentualMesPilar = percentualTotalSubpilar / qtdSubpilar
        }

        return percentualMesPilar
    }

    /**
     * Valida se um Pilar pode ser excluído do sistema.
     * Um pilar não pode ser excluído se tiver subpilares ou ações associadas a ele.
     *
     * @param pilar O objeto [Pilar] a ser validado para exclusão.
     * @return `true` se o pilar pode ser excluído (não tem subpilares ou ações associadas), `false` caso contrário.
     */
    fun validarExclusaoPilar(pilar: Pilar): Boolean {
        val todosSubpilares = subpilarRepository.obterTodosSubpilares()
        val todasAcoes = acaoRepository.obterTodasAcoes()
        var temSubpilarAssociado = false
        var temAcaoAssociada = false

        for (subpilar in todosSubpilares) {
            if (subpilar.idPilar == pilar.id) {
                temSubpilarAssociado = true
                break
            }
        }
        for (acao in todasAcoes) {
            if (acao.idPilar == pilar.id) {
                temAcaoAssociada = true
                break
            }
        }

        if (temSubpilarAssociado == true || temAcaoAssociada == true){
            return false
        }else{
            return true
        }

    }

    /**
     * Calcula o orçamento total de um [Pilar], somando os orçamentos de todas as [Atividade]s
     * a ele vinculadas (diretamente através de Ações ou indiretamente via Subpilares e Ações).
     *
     * @param pilar O objeto [Pilar] para o qual o orçamento total será calculado.
     * @return O orçamento total do pilar como um [Double].
     */
    fun obterOrcamentoTotalPilar(pilar: Pilar): Double {
        val db = dataBase.readableDatabase
        var orcamentoTotal = 0.0

        val query = """
        SELECT SUM(${DatabaseHelper.TABLE_ATIVIDADE}.${DatabaseHelper.COLUMN_ATIVIDADE_ORCAMENTO})
        FROM ${DatabaseHelper.TABLE_ATIVIDADE}
        INNER JOIN ${DatabaseHelper.TABLE_ACAO}
            ON ${DatabaseHelper.TABLE_ATIVIDADE}.${DatabaseHelper.COLUMN_ATIVIDADE_ID_ACAO} = ${DatabaseHelper.TABLE_ACAO}.${DatabaseHelper.COLUMN_ACAO_ID}
        LEFT JOIN ${DatabaseHelper.TABLE_SUBPILAR}
            ON ${DatabaseHelper.TABLE_ACAO}.${DatabaseHelper.COLUMN_ACAO_ID_SUBPILAR} = ${DatabaseHelper.TABLE_SUBPILAR}.${DatabaseHelper.COLUMN_SUBPILAR_ID}
        WHERE
            (${DatabaseHelper.TABLE_ACAO}.${DatabaseHelper.COLUMN_ACAO_ID_PILAR} = ?
            OR ${DatabaseHelper.TABLE_SUBPILAR}.${DatabaseHelper.COLUMN_SUBPILAR_ID_PILAR} = ?);
    """.trimIndent()

        val selectionArgs = arrayOf(pilar.id.toString(), pilar.id.toString())

        val cursor = db.rawQuery(query, selectionArgs)

        if (cursor.moveToFirst()) {
            orcamentoTotal = cursor.getDouble(0)
        }

        cursor.close()
        db.close()
        return orcamentoTotal
    }

}