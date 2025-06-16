package com.example.mpi.repository

import android.content.ContentValues
import android.content.Context
import android.util.Log
import com.example.mpi.data.Acao
import com.example.mpi.data.Atividade
import com.example.mpi.data.DatabaseHelper
import com.example.mpi.data.DatabaseHelper.Companion.COLUMN_ACAO_ID
import com.example.mpi.data.DatabaseHelper.Companion.COLUMN_ACAO_IS_APROVADO
import com.example.mpi.data.DatabaseHelper.Companion.COLUMN_ACAO_IS_FINALIZADO
import com.example.mpi.data.DatabaseHelper.Companion.TABLE_ACAO
import com.example.mpi.data.Pilar
import com.example.mpi.data.Subpilar
import com.example.mpi.repository.SubpilarRepository

/**
 * Repositório para gerenciar operações de dados relacionadas às [Acao]s no sistema.
 *
 * Esta classe é responsável por interagir com a tabela `acao` no banco de dados SQLite
 * (via [DatabaseHelper]), fornecendo métodos para buscar ações por diferentes critérios,
 * calcular seu progresso e orçamento, e gerenciar seus status de aprovação e finalização.
 * Ela também coordena com outros repositórios como [SubpilarRepository] e [AtividadeRepository]
 * para obter dados hierárquicos e calcular métricas complexas.
 *
 * Adota o padrão **Singleton** para garantir que apenas uma instância deste repositório
 * exista em toda a aplicação, otimizando o gerenciamento da conexão com o banco de dados.
 *
 * @property dataBase Uma instância de [DatabaseHelper] para acessar o banco de dados.
 * @property subpilarRepository Uma instância de [SubpilarRepository] para operações relacionadas a subpilares.
 * @property atividadeRepository Uma instância de [AtividadeRepository] para operações relacionadas a atividades.
 */
class AcaoRepository(context: Context) {

    private var dataBase: DatabaseHelper = DatabaseHelper(context)
    private val subpilarRepository: SubpilarRepository = SubpilarRepository.getInstance(context)
    private val atividadeRepository: AtividadeRepository = AtividadeRepository.getInstance(context)

    companion object {
        private lateinit var instance: AcaoRepository

        /**
         * Retorna a única instância de [AcaoRepository] (Singleton).
         * Se a instância ainda não foi inicializada, ela é criada de forma segura para threads.
         *
         * @param context O [Context] da aplicação.
         * @return A instância de [AcaoRepository].
         */
        fun getInstance(context: Context): AcaoRepository {
            synchronized(this) {
                if (!::instance.isInitialized) {
                    instance = AcaoRepository(context)
                }
            }
            return instance
        }
    }

    /**
     * Obtém uma lista de todas as [Acao]s diretamente associadas a um [Pilar] específico.
     *
     * @param pilar O objeto [Pilar] cujas ações serão buscadas.
     * @return Uma [MutableList] de objetos [Acao] que pertencem diretamente ao pilar especificado.
     */
    fun obterTodasAcoes(pilar: Pilar): MutableList<Acao> {
        val db = dataBase.readableDatabase

        val cursor = db.rawQuery("SELECT * FROM acao WHERE id_pilar = ?", arrayOf(pilar.id.toString()))

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

            val acao = Acao( id, nome, descricao, dataInicio, dataTermino, responsavel, aprovado, finalizado, idPilar, idSubpilar, idUsuario)
            acoes.add(acao)
        }
        cursor.close()
        return acoes
    }

    /**
     * Obtém uma lista de todas as [Acao]s diretamente associadas a um [Subpilar] específico.
     *
     * @param subpilar O objeto [Subpilar] cujas ações serão buscadas.
     * @return Uma [MutableList] de objetos [Acao] que pertencem diretamente ao subpilar especificado.
     */
    fun obterTodasAcoes(subpilar: Subpilar): MutableList<Acao> {
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

            val acao = Acao(id, nome, descricao, dataInicio, dataTermino, responsavel, aprovado, finalizado, idPilar, idSubpilar, idUsuario )
            acoes.add(acao)
        }
        cursor.close()
        return acoes
    }

    /**
     * Obtém uma lista contendo todas as [Acao]s existentes no banco de dados, sem nenhum filtro.
     *
     * Este método é útil para operações que requerem a listagem completa de todas as ações
     * cadastradas no sistema.
     *
     * @return Uma [List] de objetos [Acao] representando todas as ações disponíveis.
     */
    fun obterTodasAcoes(): List<Acao> {
        val db = dataBase.readableDatabase
        val acoes: MutableList<Acao> = arrayListOf()
        val cursor = db.rawQuery("SELECT * FROM ${DatabaseHelper.TABLE_ACAO}", null)

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
        return acoes
    }

    /**
     * Obtém uma lista de todas as [Acao]s que pertencem a um [Pilar] específico,
     * incluindo aquelas que estão sob Subpilares desse Pilar.
     *
     * Este método consolida ações diretamente ligadas ao pilar e ações de seus subpilares.
     *
     * @param pilar O objeto [Pilar] para o qual as ações serão filtradas.
     * @return Uma [List] de objetos [Acao] associadas ao pilar.
     */
    fun obterAcoesPorPilar(pilar: Pilar): List<Acao> {
        val db = dataBase.readableDatabase
        val acoesDoPilar: MutableList<Acao> = arrayListOf()

        val subpilaresDoPilar = subpilarRepository.obterTodosSubpilares(pilar)

        for (subpilar in subpilaresDoPilar) {
            val acoesDoSubpilar = obterAcoesPorSubpilar(subpilar)
            acoesDoPilar.addAll(acoesDoSubpilar)
        }

        val cursorPilarDireto = db.rawQuery(
            "SELECT * FROM ${DatabaseHelper.TABLE_ACAO} WHERE ${DatabaseHelper.COLUMN_ACAO_ID_PILAR} = ? AND ${DatabaseHelper.COLUMN_ACAO_ID_SUBPILAR} IS NULL",
            arrayOf(pilar.id.toString())
        )
        while (cursorPilarDireto.moveToNext()) {
            val id = cursorPilarDireto.getInt(cursorPilarDireto.getColumnIndexOrThrow(DatabaseHelper.COLUMN_ACAO_ID))
            val nome = cursorPilarDireto.getString(cursorPilarDireto.getColumnIndexOrThrow(DatabaseHelper.COLUMN_ACAO_NOME))
            val dataInicio = cursorPilarDireto.getString(cursorPilarDireto.getColumnIndexOrThrow(DatabaseHelper.COLUMN_ACAO_DATA_INICIO))
            val dataTermino = cursorPilarDireto.getString(cursorPilarDireto.getColumnIndexOrThrow(DatabaseHelper.COLUMN_ACAO_DATA_TERMINO))
            val responsavel = cursorPilarDireto.getInt(cursorPilarDireto.getColumnIndexOrThrow(DatabaseHelper.COLUMN_ACAO_RESPONSAVEL))
            val aprovado = cursorPilarDireto.getInt(cursorPilarDireto.getColumnIndexOrThrow(DatabaseHelper.COLUMN_ACAO_IS_APROVADO)) != 0
            val finalizado = cursorPilarDireto.getInt(cursorPilarDireto.getColumnIndexOrThrow(DatabaseHelper.COLUMN_ACAO_IS_FINALIZADO)) != 0
            val descricao = cursorPilarDireto.getString(cursorPilarDireto.getColumnIndexOrThrow(DatabaseHelper.COLUMN_ACAO_DESCRICAO))
            val idPilar = cursorPilarDireto.getInt(cursorPilarDireto.getColumnIndexOrThrow(DatabaseHelper.COLUMN_ACAO_ID_PILAR))
            val idSubpilar = cursorPilarDireto.getInt(cursorPilarDireto.getColumnIndexOrThrow(DatabaseHelper.COLUMN_ACAO_ID_SUBPILAR))
            val idUsuario = cursorPilarDireto.getInt(cursorPilarDireto.getColumnIndexOrThrow(DatabaseHelper.COLUMN_ACAO_ID_USUARIO))

            val acao = Acao(id, nome, descricao, dataInicio, dataTermino, responsavel, aprovado, finalizado, idPilar, idSubpilar, idUsuario )
            if (!acoesDoPilar.contains(acao)) {
                acoesDoPilar.add(acao)
            }
        }
        cursorPilarDireto.close()
        return acoesDoPilar.distinctBy { it.id }
    }


    /**
     * Obtém uma lista de todas as [Acao]s diretamente associadas a um [Subpilar] específico.
     *
     * @param subpilar O objeto [Subpilar] para o qual as ações serão filtradas.
     * @return Uma [List] de objetos [Acao] associadas ao subpilar.
     */
    fun obterAcoesPorSubpilar(subpilar: Subpilar): List<Acao> {
        val db = dataBase.readableDatabase
        val acoes: MutableList<Acao> = arrayListOf()

        val cursor = db.rawQuery("SELECT * FROM ${DatabaseHelper.TABLE_ACAO} WHERE ${DatabaseHelper.COLUMN_ACAO_ID_SUBPILAR} = ?", arrayOf(subpilar.id.toString()))

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

            val acao = Acao(id, nome, descricao, dataInicio, dataTermino, responsavel, aprovado, finalizado, idPilar, idSubpilar, idUsuario )
            acoes.add(acao)
        }

        cursor.close()
        return acoes
    }

    /**
     * Obtém uma lista de todas as [Acao]s que não foram finalizadas e são atribuídas a um usuário específico.
     *
     * @param idUsuario O ID do usuário para o qual as ações não finalizadas serão buscadas.
     * @return Uma [List] de objetos [Acao] que ainda não foram concluídas por esse usuário.
     */
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

            val acao = Acao(id, nome, descricao, dataInicio, dataTermino, responsavel, aprovado, finalizado, idPilar, idSubpilar, idUsuarioAcao )

            acoes.add(acao)
        }

        cursor.close()
        return acoes
    }

    /**
     * Obtém a quantidade total de [Acao]s que foram marcadas como finalizadas.
     *
     * @return O número inteiro de ações finalizadas.
     */
    fun obterQuantidadeAcoesFinalizadas(): Int {
        val db = dataBase.readableDatabase
        var quantidadeAcoesFinalizadas = 0
        val cursor = db.rawQuery("SELECT COUNT(*) AS QTDE FROM ${DatabaseHelper.TABLE_ACAO} WHERE ${DatabaseHelper.COLUMN_ACAO_IS_FINALIZADO} = 1", null)

        if (cursor.moveToNext()) {
            quantidadeAcoesFinalizadas = cursor.getInt(cursor.getColumnIndexOrThrow("QTDE"))
        }

        cursor.close()
        return quantidadeAcoesFinalizadas
    }

    /**
     * Obtém a quantidade total de [Acao]s que estão em andamento (não finalizadas e com data de término futura).
     *
     * @return O número inteiro de ações em andamento.
     */
    fun obterQuantidadeAcoesEmAndamento(): Int {
        val db = dataBase.readableDatabase
        var quantidadeAcoesEmAndamento = 0
        val cursor = db.rawQuery("SELECT COUNT(*) AS ANDAMENTO FROM ${DatabaseHelper.TABLE_ACAO} WHERE date(substr(${DatabaseHelper.COLUMN_ACAO_DATA_TERMINO}, 7, 4) || '-' || substr(${DatabaseHelper.COLUMN_ACAO_DATA_TERMINO}, 4, 2) || '-' || substr(${DatabaseHelper.COLUMN_ACAO_DATA_TERMINO}, 1, 2)) > date('now') AND ${DatabaseHelper.COLUMN_ACAO_IS_FINALIZADO} = 0", null)

        if (cursor.moveToNext()) {
            quantidadeAcoesEmAndamento = cursor.getInt(cursor.getColumnIndexOrThrow("ANDAMENTO"))
        }

        cursor.close()
        return quantidadeAcoesEmAndamento
    }

    /**
     * Obtém a quantidade total de [Acao]s que estão atrasadas (não finalizadas e com data de término passada).
     *
     * @return O número inteiro de ações atrasadas.
     */
    fun obterQuantidadeAcoesAtrasadas(): Int {
        val db = dataBase.readableDatabase
        var quantidadeAcoesAtrasadas = 0
        val cursor = db.rawQuery("SELECT COUNT(*) AS ATRASADA FROM ${DatabaseHelper.TABLE_ACAO} WHERE date(substr(${DatabaseHelper.COLUMN_ACAO_DATA_TERMINO}, 7, 4) || '-' || substr(${DatabaseHelper.COLUMN_ACAO_DATA_TERMINO}, 4, 2) || '-' || substr(${DatabaseHelper.COLUMN_ACAO_DATA_TERMINO}, 1, 2)) < date('now') AND ${DatabaseHelper.COLUMN_ACAO_IS_FINALIZADO} = 0", null)

        if (cursor.moveToNext()) {
            quantidadeAcoesAtrasadas = cursor.getInt(cursor.getColumnIndexOrThrow("ATRASADA"))
        }

        cursor.close()
        return quantidadeAcoesAtrasadas
    }

    /**
     * Calcula o percentual total de conclusão de uma [Acao], baseado na média dos percentuais totais de suas [Atividade]s.
     *
     * @param acao O objeto [Acao] para o qual o percentual total será calculado.
     * @return O percentual total da ação como um [Double]. Retorna 0.0 se não houver atividades associadas.
     */
    fun obterPercentualTotalAcao(acao: Acao) : Double {
        val percentualTotalAcao: Double
        val listaAtividade = atividadeRepository.obterTodasAtividades(acao)
        var divisor = 0
        var somaPercentualAtividade = 0.0
        for (atividade in listaAtividade) {
            somaPercentualAtividade += atividadeRepository.obterPercentualTotalAtividade(atividade)
            divisor++
        }
        if (divisor != 0) {
            percentualTotalAcao = somaPercentualAtividade / divisor
        } else {
            percentualTotalAcao = 0.0
        }

        return percentualTotalAcao
    }

    /**
     * Calcula o percentual médio de conclusão de um grupo de [Atividade]s para um mês específico.
     *
     * @param atividades Uma [List] de objetos [Atividade] para as quais o percentual do mês será calculado.
     * @param mes O número do mês (1 a 12) para o qual o percentual será obtido.
     * @return O percentual médio das atividades para o mês especificado como um [Double]. Retorna 0.0 se não houver atividades.
     */
    fun obterPercentualMes(atividades: List<Atividade>, mes: Int): Double {
        val percentualTotal: Double
        var somaPercentual = 0.0
        var qtdAtividades = 0
        for (atividade in atividades) {
            val percentualMes = atividadeRepository.obterPercentualMes(atividade, mes)
            somaPercentual += percentualMes
            qtdAtividades++
        }
        if (qtdAtividades != 0) {
            percentualTotal = somaPercentual / qtdAtividades
        } else {
            percentualTotal = 0.0
        }

        return percentualTotal
    }

    /**
     * Calcula o orçamento total de um grupo de [Atividade]s, somando o orçamento de cada atividade.
     *
     * @param atividades Uma [List] de objetos [Atividade] para as quais o orçamento será somado.
     * @return O orçamento total das atividades como um [Double].
     */
    fun obterOrcamentoAcao(atividades: List<Atividade>): Double {
        var orcamentoTotal = 0.0
        for (atividade in atividades) {
            orcamentoTotal += atividade.orcamento
        }

        return orcamentoTotal
    }

    /**
     * Marca uma [Acao] específica como aprovada no banco de dados.
     *
     * @param idAcao O ID da ação a ser marcada como aprovada.
     */
    fun aprovarAcao(idAcao: Int) {
        val db = dataBase.writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_ACAO_IS_APROVADO, 1)
        }
        db.update(TABLE_ACAO, values, "$COLUMN_ACAO_ID = ?", arrayOf(idAcao.toString()))
        db.close()
    }

    /**
     * Obtém uma lista de todas as [Acao]s que ainda não foram aprovadas.
     *
     * @return Uma [List] de objetos [Acao] que aguardam aprovação.
     */
    fun obterAcoesNaoAprovadas(): List<Acao> {
        val db = dataBase.readableDatabase
        val acoes: MutableList<Acao> = arrayListOf()
        val cursor = db.rawQuery(
            "SELECT * FROM ${DatabaseHelper.TABLE_ACAO} WHERE ${DatabaseHelper.COLUMN_ACAO_IS_APROVADO} = 0",
            null
        )

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
        return acoes
    }

    /**
     * Marca uma [Acao] específica como finalizada no banco de dados.
     *
     * @param idAcao O ID da ação a ser marcada como finalizada.
     */
    fun finalizarAcao(idAcao: Int) {
        val db = dataBase.writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_ACAO_IS_FINALIZADO, 1)
        }
        db.update(TABLE_ACAO, values, "$COLUMN_ACAO_ID = ?", arrayOf(idAcao.toString()))
        db.close()
    }

    /**
     * Obtém uma lista de todas as [Acao]s que estão aprovadas, mas ainda não foram finalizadas,
     * e cujo percentual total de atividades é 100%.
     *
     * @return Uma [List] de objetos [Acao] que estão prontas para serem marcadas como finalizadas manualmente (se aplicável).
     */
    fun obterAcoesNaoFinalizadas(): List<Acao> {
        val db = dataBase.readableDatabase
        val acoes: MutableList<Acao> = arrayListOf()
        val cursor = db.rawQuery(
            "SELECT * FROM ${DatabaseHelper.TABLE_ACAO} WHERE ${DatabaseHelper.COLUMN_ACAO_IS_APROVADO} = 1 AND ${DatabaseHelper.COLUMN_ACAO_IS_FINALIZADO} = 0",
            null
        )

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

            if(obterPercentualTotalAcao(acao) == 100.0){
                acoes.add(acao)
            }else{
                continue
            }
        }
        cursor.close()
        return acoes
    }




}