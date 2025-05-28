package com.example.mpi.repository

import android.content.ContentValues
import android.content.Context
import com.example.mpi.data.DatabaseHelper
import com.example.mpi.data.Notificacao

class NotificacaoRepository(context: Context) {

    private val databaseHelper: DatabaseHelper = DatabaseHelper(context)

    companion object {
        @Volatile
        private var INSTANCE: NotificacaoRepository? = null

        fun getInstance(context: Context): NotificacaoRepository {
            return INSTANCE ?: synchronized(this) {
                val instance = NotificacaoRepository(context)
                INSTANCE = instance
                instance
            }
        }
    }

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