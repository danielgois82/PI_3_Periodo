package com.example.mpi.data

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.provider.BaseColumns

private const val DB_NAME = "Pilar.db"
private const val DB_VERSION = 1

class PilarDbHelper(context: Context)
    : SQLiteOpenHelper(context, DB_NAME, null, DB_VERSION) {

    override fun onCreate(db: SQLiteDatabase) {
        val sqlCreatePilar = """
            CREATE TABLE ${PilarContract.UserEntry.TABLE_NAME} (
              ${BaseColumns._ID} INTEGER PRIMARY KEY AUTOINCREMENT,
              ${PilarContract.UserEntry.COL_NOME} TEXT NOT NULL UNIQUE,
              ${PilarContract.UserEntry.COL_DESCRICAO} TEXT NOT NULL,
              ${PilarContract.UserEntry.COL_RESPONSAVEL} INTEGER NOT NULL,
              ${PilarContract.UserEntry.COL_DATAI} TEXT NOT NULL,
              ${PilarContract.UserEntry.COL_DATAT} TEXT NOT NULL,
              ${PilarContract.UserEntry.COL_APROVADO} INTEGER DEFAULT 0,
              ${PilarContract.UserEntry.COL_PERCENTUAL} REAL DEFAULT 0.0
            )
        """.trimIndent()
        db.execSQL(sqlCreatePilar)

        val sqlCreateUsuarios = """
            CREATE TABLE usuarios (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                nome TEXT NOT NULL,
                email TEXT NOT NULL UNIQUE,
                senha TEXT NOT NULL,
                tipo TEXT NOT NULL
            )
        """.trimIndent()
        db.execSQL(sqlCreateUsuarios)

        // usuários de teste
        val sqlInsert = """
            INSERT INTO usuarios (nome, email, senha, tipo) VALUES
            ('Analista 1', 'analista@gmail.com', '1234', 'ANALISTA'),
            ('Coordenador 1', 'coordenador@gmail.com', 'abcd', 'COORDENADOR'),
            ('Gestor 1', 'gestor@gmail.com', '5678', 'GESTOR')
        """.trimIndent()
        db.execSQL(sqlInsert)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS ${PilarContract.UserEntry.TABLE_NAME}")
        db.execSQL("DROP TABLE IF EXISTS usuarios")
        onCreate(db)
    }

    // Método para validar login
    fun validarLogin(email: String, senha: String): Usuario? {
        val db = this.readableDatabase
        val cursor = db.rawQuery(
            "SELECT * FROM usuarios WHERE email = ? AND senha = ?",
            arrayOf(email, senha)
        )
        var usuario: Usuario? = null
        if (cursor.moveToFirst()) {
            val id = cursor.getInt(cursor.getColumnIndexOrThrow("id"))
            val nome = cursor.getString(cursor.getColumnIndexOrThrow("nome"))
            val tipo = cursor.getString(cursor.getColumnIndexOrThrow("tipo"))
            usuario = Usuario(id, nome, email, tipo)
        }
        cursor.close()
        return usuario
    }
}
