package com.example.mpi.repository

import android.content.ContentValues
import android.content.Context
import com.example.mpi.data.DatabaseHelper
import com.example.mpi.data.Notificacao

/**
 * Repositório para gerenciar operações de dados relacionadas às notificações no sistema.
 *
 * Esta classe fornece uma interface para interagir com a tabela de notificações no banco de dados SQLite,
 * utilizando o [DatabaseHelper] como camada de acesso direto. Ela permite a inserção, consulta
 * e atualização do status de visualização das notificações.
 *
 * O repositório segue o padrão **Singleton**, garantindo uma única instância em toda a aplicação
 * para gerenciar eficientemente as conexões com o banco de dados.
 *
 * @property databaseHelper Uma instância de [DatabaseHelper] para acessar o banco de dados.
 */
class NotificacaoRepository(context: Context) {

    private val databaseHelper: DatabaseHelper = DatabaseHelper(context)

    companion object {
        @Volatile
        private var INSTANCE: NotificacaoRepository? = null

        /**
         * Retorna a única instância de [NotificacaoRepository] (Singleton).
         * Utiliza o padrão "double-checked locking" para garantir a criação de instância
         * segura para threads e otimizada.
         *
         * @param context O [Context] da aplicação.
         * @return A instância de [NotificacaoRepository].
         */
        fun getInstance(context: Context): NotificacaoRepository {
            return INSTANCE ?: synchronized(this) {
                val instance = NotificacaoRepository(context)
                INSTANCE = instance
                instance
            }
        }
    }

    /**
     * Insere uma nova notificação no banco de dados.
     *
     * @param notificacao O objeto [Notificacao] a ser inserido.
     * @return O ID da nova linha inserida, ou -1 se ocorrer um erro.
     */
    fun inserirNotificacao(notificacao: Notificacao): Long {
        val db = databaseHelper.writableDatabase
        val values = ContentValues().apply {
            put(DatabaseHelper.COLUMN_NOTIFICACAO_ISVISUALIZADO, if (notificacao.isVisualizado) 1 else 0)
            put(DatabaseHelper.COLUMN_NOTIFICACAO_TITULO, notificacao.titulo)
            put(DatabaseHelper.COLUMN_NOTIFICACAO_MENSAGEM, notificacao.mensagem)
            put(DatabaseHelper.COLUMN_NOTIFICACAO_ID_USUARIO, notificacao.idUsuario)
            put(DatabaseHelper.COLUMN_NOTIFICACAO_ID_ITEM, notificacao.idItem)
            put(DatabaseHelper.COLUMN_NOTIFICACAO_TIPO_ITEM, notificacao.tipoItem)
        }
        val id = db.insert(DatabaseHelper.TABLE_NOTIFICACAO, null, values)
        db.close()
        return id
    }

    /**
     * Obtém uma lista de notificações não lidas para um usuário específico.
     *
     * @param idUsuario O ID do usuário para o qual as notificações não lidas serão buscadas.
     * @return Uma [List] de objetos [Notificacao] que ainda não foram visualizadas pelo usuário.
     */
    fun obterNotificacoesNaoLidasPorUsuario(idUsuario: Int): List<Notificacao> {
        val notificacoes = mutableListOf<Notificacao>()
        val db = databaseHelper.readableDatabase
        val selection = "${DatabaseHelper.COLUMN_NOTIFICACAO_ISVISUALIZADO} = ? AND ${DatabaseHelper.COLUMN_NOTIFICACAO_ID_USUARIO} = ?"
        val selectionArgs = arrayOf("0", idUsuario.toString())
        val cursor = db.query(
            DatabaseHelper.TABLE_NOTIFICACAO,
            null,
            selection,
            selectionArgs,
            null,
            null,
            null
        )

        with(cursor) {
            while (moveToNext()) {
                val id = getInt(getColumnIndexOrThrow(DatabaseHelper.COLUMN_NOTIFICACAO_ID))
                val isVisualizado = getInt(getColumnIndexOrThrow(DatabaseHelper.COLUMN_NOTIFICACAO_ISVISUALIZADO)) != 0
                val titulo = getString(getColumnIndexOrThrow(DatabaseHelper.COLUMN_NOTIFICACAO_TITULO))
                val mensagem = getString(getColumnIndexOrThrow(DatabaseHelper.COLUMN_NOTIFICACAO_MENSAGEM))
                val idUsuarioNotificacao = getInt(getColumnIndexOrThrow(DatabaseHelper.COLUMN_NOTIFICACAO_ID_USUARIO))
                val idItem = if (isNull(getColumnIndexOrThrow(DatabaseHelper.COLUMN_NOTIFICACAO_ID_ITEM))) null else getInt(getColumnIndexOrThrow(DatabaseHelper.COLUMN_NOTIFICACAO_ID_ITEM))
                val tipoItem = if (isNull(getColumnIndexOrThrow(DatabaseHelper.COLUMN_NOTIFICACAO_TIPO_ITEM))) null else getString(getColumnIndexOrThrow(DatabaseHelper.COLUMN_NOTIFICACAO_TIPO_ITEM))

                notificacoes.add(
                    Notificacao(id, isVisualizado, titulo, mensagem, idUsuarioNotificacao, idItem, tipoItem)
                )
            }
            close()
        }
        db.close()
        return notificacoes
    }

    /**
     * Define o status da notificação `isVisualizado` para `true` (1).
     *
     * @param notificacaoId O ID da notificação a ser marcada como lida.
     * @return O número de linhas afetadas pela operação de atualização ( 1 se bem-sucedido).
     */
    fun marcarNotificacaoComoLida(notificacaoId: Int): Int {
        val db = databaseHelper.writableDatabase
        val values = ContentValues().apply {
            put(DatabaseHelper.COLUMN_NOTIFICACAO_ISVISUALIZADO, 1) // 1 para visualizado (true)
        }
        val rowsAffected = db.update(
            DatabaseHelper.TABLE_NOTIFICACAO,
            values,
            "${DatabaseHelper.COLUMN_NOTIFICACAO_ID} = ?",
            arrayOf(notificacaoId.toString())
        )
        db.close()
        return rowsAffected
    }

    /**
     * Obtém a última notificação para um item e tipo específicos para um determinado usuário.
     *
     * @param idItem O ID do item ao qual a notificação está associada (e.g., ID de uma Ação ou Atividade).
     * @param tipoItem O tipo de item ao qual a notificação está associada (e.g., "Acao", "Atividade").
     * @param idUsuario O ID do usuário destinatário da notificação.
     * @return O objeto [Notificacao] mais recente que corresponde aos critérios, ou `null` se nenhuma notificação for encontrada.
     */
    fun obterUltimaNotificacaoPorItemETipo(idItem: Int, tipoItem: String, idUsuario: Int): Notificacao? {
        val db = databaseHelper.readableDatabase
        val query = """
            SELECT * FROM ${DatabaseHelper.TABLE_NOTIFICACAO}
            WHERE ${DatabaseHelper.COLUMN_NOTIFICACAO_ID_ITEM} = ?
            AND ${DatabaseHelper.COLUMN_NOTIFICACAO_TIPO_ITEM} = ?
            AND ${DatabaseHelper.COLUMN_NOTIFICACAO_ID_USUARIO} = ?
            ORDER BY ${DatabaseHelper.COLUMN_NOTIFICACAO_ID} DESC
            LIMIT 1
        """.trimIndent()
        val cursor = db.rawQuery(query, arrayOf(idItem.toString(), tipoItem, idUsuario.toString()))
        var notificacao: Notificacao? = null

        with(cursor) {
            if (moveToFirst()) {
                val id = getInt(getColumnIndexOrThrow(DatabaseHelper.COLUMN_NOTIFICACAO_ID))
                val isVisualizado = getInt(getColumnIndexOrThrow(DatabaseHelper.COLUMN_NOTIFICACAO_ISVISUALIZADO)) != 0
                val titulo = getString(getColumnIndexOrThrow(DatabaseHelper.COLUMN_NOTIFICACAO_TITULO))
                val mensagem = getString(getColumnIndexOrThrow(DatabaseHelper.COLUMN_NOTIFICACAO_MENSAGEM))
                val idUsuarioNotificacao = getInt(getColumnIndexOrThrow(DatabaseHelper.COLUMN_NOTIFICACAO_ID_USUARIO))
                val itemId = if (isNull(getColumnIndexOrThrow(DatabaseHelper.COLUMN_NOTIFICACAO_ID_ITEM))) null else getInt(getColumnIndexOrThrow(DatabaseHelper.COLUMN_NOTIFICACAO_ID_ITEM))
                val itemTipo = if (isNull(getColumnIndexOrThrow(DatabaseHelper.COLUMN_NOTIFICACAO_TIPO_ITEM))) null else getString(getColumnIndexOrThrow(DatabaseHelper.COLUMN_NOTIFICACAO_TIPO_ITEM))

                notificacao = Notificacao(id, isVisualizado, titulo, mensagem, idUsuarioNotificacao, itemId, itemTipo)
            }
            close()
        }
        db.close()
        return notificacao
    }
}