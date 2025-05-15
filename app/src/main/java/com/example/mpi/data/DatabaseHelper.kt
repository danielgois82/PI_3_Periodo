package com.example.mpi.data

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DatabaseHelper(context: Context) :
    SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {
    override fun onCreate(db: SQLiteDatabase) {

        val createCalendarioTable = """
            CREATE TABLE $TABLE_CALENDARIO (
                $COLUMN_CALENDARIO_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COLUMN_CALENDARIO_ANO INTEGER NOT NULL
            );
        """.trimIndent()

        val createTipoUsuarioTable = """
            CREATE TABLE $TABLE_TIPOUSUARIO (
                $COLUMN_TIPOUSUARIO_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COLUMN_TIPOUSUARIO_CARGO TEXT NOT NULL
            );
        """.trimIndent()

        val createUsuarioTable = """
            CREATE TABLE $TABLE_USUARIO (
                $COLUMN_USUARIO_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COLUMN_USUARIO_NOME TEXT NOT NULL,
                $COLUMN_USUARIO_EMAIL TEXT NOT NULL,
                $COLUMN_USUARIO_SENHA TEXT NOT NULL,
                $COLUMN_USUARIO_ID_TIPOUSUARIO INTEGER NOT NULL,
                FOREIGN KEY ($COLUMN_USUARIO_ID_TIPOUSUARIO) REFERENCES $TABLE_TIPOUSUARIO($COLUMN_TIPOUSUARIO_ID)
            );
        """.trimIndent()

        val createNotificacaoTable = """
            CREATE TABLE $TABLE_NOTIFICACAO (
                $COLUMN_NOTIFICACAO_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COLUMN_NOTIFICACAO_ISVISUALIZADO INTEGER DEFAULT 0,
                $COLUMN_NOTIFICACAO_TITULO TEXT NOT NULL,
                $COLUMN_NOTIFICACAO_MENSAGEM TEXT NOT NULL,
                $COLUMN_NOTIFICACAO_ID_USUARIO INTEGER NOT NULL,
                FOREIGN KEY ($COLUMN_NOTIFICACAO_ID_USUARIO) REFERENCES $TABLE_USUARIO($COLUMN_USUARIO_ID)
            );
        """.trimIndent()

        db.execSQL(createCalendarioTable)
        db.execSQL(createTipoUsuarioTable)
        db.execSQL(createUsuarioTable)
        db.execSQL(createNotificacaoTable)

        inserirTipoUsuario(db, obterTipoUsuario())
        inserirUsuarios(db, obterUsuarios())
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE_CALENDARIO")
        db.execSQL("DROP TABLE IF EXISTS $TABLE_TIPOUSUARIO")
        db.execSQL("DROP TABLE IF EXISTS $TABLE_USUARIO")
        db.execSQL("DROP TABLE IF EXISTS $TABLE_NOTIFICACAO")

        onCreate(db)
    }

    private fun inserirTipoUsuario(db: SQLiteDatabase, tipoUsuario: List<TipoUsuario>) {
        for (tipo in tipoUsuario) {
            val values = ContentValues().apply {
                put(COLUMN_TIPOUSUARIO_CARGO, tipo.cargo)
            }
            db.insert(TABLE_TIPOUSUARIO, null, values)
        }
    }

    private fun inserirUsuarios(db: SQLiteDatabase, usuarios: List<Usuario>) {
        for (usuario in usuarios) {
            val values = ContentValues().apply {
                put(COLUMN_USUARIO_NOME, usuario.nome)
                put(COLUMN_USUARIO_EMAIL, usuario.email)
                put(COLUMN_USUARIO_SENHA, usuario.senha)
                put(COLUMN_USUARIO_ID_TIPOUSUARIO, usuario.idTipoUsuario)
            }
            db.insert(TABLE_USUARIO, null, values)
        }
    }

    private fun obterTipoUsuario(): List<TipoUsuario> {
        return listOf(
            TipoUsuario(1, "Analista"),
            TipoUsuario(2, "Coordenador"),
            TipoUsuario(3, "Gestor")
        )
    }

    private fun obterUsuarios(): List<Usuario> {
        return listOf(
            Usuario(1, "Analista José", "jose@jose.com", "jose123", 1),
            Usuario(2, "Analista João", "joao@joao.com", "joao123", 1),
            Usuario(3, "Coordenador Marcos", "marcos@marcos.com", "marcos123", 2),
            Usuario(4, "Coordenadora Marta", "marta@marta.com", "marta123", 2),
            Usuario(5, "Gestor Geraldo", "geraldo@geraldo.com", "geraldo123", 3),
            Usuario(6, "Gestora Goreti", "goreti@goreti.com", "goreti123", 3)
        )
    }

    // Essa parte descarta fazer as classes PilarContract e demais, mas não apaga elas não caso der errado isso aqui
    companion object {
        private const val DATABASE_NAME = "mpi.db"
        private const val DATABASE_VERSION = 1

        const val TABLE_CALENDARIO = "calendario"
        const val COLUMN_CALENDARIO_ID = "id"
        const val COLUMN_CALENDARIO_ANO = "ano"

        const val TABLE_TIPOUSUARIO = "tipoUsuario"
        const val COLUMN_TIPOUSUARIO_ID = "id"
        const val COLUMN_TIPOUSUARIO_CARGO = "cargo"

        const val TABLE_USUARIO = "usuario"
        const val COLUMN_USUARIO_ID = "id"
        const val COLUMN_USUARIO_NOME = "nome"
        const val COLUMN_USUARIO_EMAIL = "email"
        const val COLUMN_USUARIO_SENHA = "senha"
        const val COLUMN_USUARIO_ID_TIPOUSUARIO = "id_tipoUsuario"

        const val TABLE_NOTIFICACAO = "notificacao"
        const val COLUMN_NOTIFICACAO_ID = "id"
        const val COLUMN_NOTIFICACAO_ISVISUALIZADO = "isVisualizado"
        const val COLUMN_NOTIFICACAO_TITULO = "titulo"
        const val COLUMN_NOTIFICACAO_MENSAGEM = "mensagem"
        const val COLUMN_NOTIFICACAO_ID_USUARIO = "id_usuario"
    }
}