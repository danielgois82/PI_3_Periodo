package com.example.mpi.data

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DatabaseHelper(context: Context) : SQLiteOpenHelper(
    context,
    DATABASE_NAME,
    null,
    DATABASE_VERSION
) {
    override fun onCreate(db: SQLiteDatabase) {

        val createPilarTable = """
            CREATE TABLE $TABLE_PILAR (
                $COLUMN_PILAR_ID INTEGER PRIMARY KEY,
                $COLUMN_PILAR_NOME TEXT NOT NULL,
                $COLUMN_PILAR_DATA_INICIO TEXT NOT NULL,
                $COLUMN_PILAR_DATA_TERMINO TEXT NOT NULL,
                $COLUMN_PILAR_IS_APROVADO INTEGER DEFAULT 0,
                $COLUMN_PILAR_DESCRICAO TEXT NOT NULL,
                $COLUMN_PILAR_PERCENTUAL REAL DEFAULT 0,
                $COLUMN_PILAR_ID_CALENDARIO INTEGER NOT NULL,
                $COLUMN_PILAR_ID_USUARIO INTEGER,
                FOREIGN KEY ($COLUMN_PILAR_ID_CALENDARIO) REFERENCES Calendario (id),
                FOREIGN KEY ($COLUMN_PILAR_ID_USUARIO) REFERENCES Usuario (id)
            );
        """.trimIndent()


        val createSubpilarTable = """
            CREATE TABLE $TABLE_SUBPILAR (
                $COLUMN_SUBPILAR_ID INTEGER PRIMARY KEY,
                $COLUMN_SUBPILAR_NOME TEXT NOT NULL,
                $COLUMN_SUBPILAR_DATA_INICIO TEXT NOT NULL,
                $COLUMN_SUBPILAR_DATA_TERMINO TEXT NOT NULL,
                $COLUMN_SUBPILAR_IS_APROVADO INTEGER DEFAULT 0,
                $COLUMN_SUBPILAR_DESCRICAO TEXT NOT NULL,
                $COLUMN_SUBPILAR_ID_PILAR INTEGER NOT NULL,
                $COLUMN_SUBPILAR_ID_USUARIO INTEGER,
                FOREIGN KEY ($COLUMN_SUBPILAR_ID_PILAR) REFERENCES Pilar (id),
                FOREIGN KEY ($COLUMN_SUBPILAR_ID_USUARIO) REFERENCES Usuario (id)
            );
        """.trimIndent()


        val createAcaoTable = """
            CREATE TABLE $TABLE_ACAO (
                $COLUMN_ACAO_ID INTEGER PRIMARY KEY,
                $COLUMN_ACAO_NOME TEXT NOT NULL,
                $COLUMN_ACAO_DATA_INICIO TEXT NOT NULL,
                $COLUMN_ACAO_DATA_TERMINO TEXT NOT NULL,
                $COLUMN_ACAO_RESPONSAVEL INTEGER,
                $COLUMN_ACAO_IS_APROVADO INTEGER DEFAULT 0,
                $COLUMN_ACAO_IS_FINALIZADO INTEGER DEFAULT 0,
                $COLUMN_ACAO_DESCRICAO TEXT NOT NULL,
                $COLUMN_ACAO_ID_PILAR INTEGER,
                $COLUMN_ACAO_ID_SUBPILAR INTEGER,
                $COLUMN_ACAO_ID_USUARIO INTEGER,
                FOREIGN KEY ($COLUMN_ACAO_ID_PILAR) REFERENCES Pilar (id),
                FOREIGN KEY ($COLUMN_ACAO_ID_SUBPILAR) REFERENCES Subpilar (id),
                FOREIGN KEY ($COLUMN_ACAO_ID_USUARIO) REFERENCES Usuario (id)
            );
        """.trimIndent()


        val createPercentualAcaoTable = """
            CREATE TABLE $TABLE_PERCENTUAL_ACAO (
                $COLUMN_PERCENTUAL_ACAO_ID INTEGER PRIMARY KEY,
                $COLUMN_PERCENTUAL_ACAO_MES INTEGER NOT NULL,
                $COLUMN_PERCENTUAL_ACAO_PERCENTUAL REAL DEFAULT 0,
                $COLUMN_PERCENTUAL_ACAO_ID_ACAO INTEGER NOT NULL,
                FOREIGN KEY ($COLUMN_PERCENTUAL_ACAO_ID_ACAO) REFERENCES Acao (id)
            );
        """.trimIndent()


        val createAtividadeTable = """
            CREATE TABLE $TABLE_ATIVIDADE (
                $COLUMN_ATIVIDADE_ID INTEGER PRIMARY KEY,
                $COLUMN_ATIVIDADE_NOME TEXT NOT NULL UNIQUE,
                $COLUMN_ATIVIDADE_DATA_INICIO TEXT NOT NULL,
                $COLUMN_ATIVIDADE_DATA_TERMINO TEXT NOT NULL,
                $COLUMN_ATIVIDADE_RESPONSAVEL INTEGER,
                $COLUMN_ATIVIDADE_IS_APROVADO INTEGER DEFAULT 0,
                $COLUMN_ATIVIDADE_IS_FINALIZADO INTEGER DEFAULT 0,
                $COLUMN_ATIVIDADE_DESCRICAO TEXT NOT NULL,
                $COLUMN_ATIVIDADE_ORCAMENTO REAL DEFAULT 0,
                $COLUMN_ATIVIDADE_ID_ACAO INTEGER NOT NULL,
                $COLUMN_ATIVIDADE_ID_USUARIO INTEGER,
                FOREIGN KEY ($COLUMN_ATIVIDADE_ID_ACAO) REFERENCES Acao (id),
                FOREIGN KEY ($COLUMN_ATIVIDADE_ID_USUARIO) REFERENCES Usuario (id)
            );
        """.trimIndent()


        val createPercentualAtividadeTable = """
            CREATE TABLE $TABLE_PERCENTUAL_ATIVIDADE (
                $COLUMN_PERCENTUAL_ATIVIDADE_ID INTEGER PRIMARY KEY,
                $COLUMN_PERCENTUAL_ATIVIDADE_MES INTEGER NOT NULL,
                $COLUMN_PERCENTUAL_ATIVIDADE_PERCENTUAL REAL DEFAULT 0,
                $COLUMN_PERCENTUAL_ATIVIDADE_ID_ATIVIDADE INTEGER NOT NULL,
                FOREIGN KEY ($COLUMN_PERCENTUAL_ATIVIDADE_ID_ATIVIDADE) REFERENCES Atividade (id)
            );
        """.trimIndent()

        db.execSQL(createPilarTable)
        db.execSQL(createSubpilarTable)
        db.execSQL(createAcaoTable)
        db.execSQL(createPercentualAcaoTable)
        db.execSQL(createAtividadeTable)
        db.execSQL(createPercentualAtividadeTable)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE_PERCENTUAL_ATIVIDADE")
        db.execSQL("DROP TABLE IF EXISTS $TABLE_ATIVIDADE")
        db.execSQL("DROP TABLE IF EXISTS $TABLE_PERCENTUAL_ACAO")
        db.execSQL("DROP TABLE IF EXISTS $TABLE_ACAO")
        db.execSQL("DROP TABLE IF EXISTS $TABLE_SUBPILAR")
        db.execSQL("DROP TABLE IF EXISTS $TABLE_PILAR")
        onCreate(db)
    }

    companion object {
        private const val DATABASE_NAME = "mpi"
        private const val DATABASE_VERSION = 1

        const val TABLE_PILAR = "pilar"
        const val COLUMN_PILAR_ID = "id"
        const val COLUMN_PILAR_NOME = "nome"
        const val COLUMN_PILAR_DATA_INICIO = "dataInicio"
        const val COLUMN_PILAR_DATA_TERMINO = "dataTermino"
        const val COLUMN_PILAR_IS_APROVADO = "isAprovado"
        const val COLUMN_PILAR_DESCRICAO = "descricao"
        const val COLUMN_PILAR_PERCENTUAL = "percentual"
        const val COLUMN_PILAR_ID_CALENDARIO = "id_calendario"
        const val COLUMN_PILAR_ID_USUARIO = "id_usuario"

        const val TABLE_SUBPILAR = "subpilar"
        const val COLUMN_SUBPILAR_ID = "id"
        const val COLUMN_SUBPILAR_NOME = "nome"
        const val COLUMN_SUBPILAR_DATA_INICIO = "dataInicio"
        const val COLUMN_SUBPILAR_DATA_TERMINO = "dataTermino"
        const val COLUMN_SUBPILAR_IS_APROVADO = "isAprovado"
        const val COLUMN_SUBPILAR_DESCRICAO = "descricao"
        const val COLUMN_SUBPILAR_ID_PILAR = "id_pilar"
        const val COLUMN_SUBPILAR_ID_USUARIO = "id_usuario"

        const val TABLE_ACAO = "acao"
        const val COLUMN_ACAO_ID = "id"
        const val COLUMN_ACAO_NOME = "nome"
        const val COLUMN_ACAO_DATA_INICIO = "dataInicio"
        const val COLUMN_ACAO_DATA_TERMINO = "dataTermino"
        const val COLUMN_ACAO_RESPONSAVEL = "responsavel"
        const val COLUMN_ACAO_IS_APROVADO = "isAprovado"
        const val COLUMN_ACAO_IS_FINALIZADO = "isFinalizado"
        const val COLUMN_ACAO_DESCRICAO = "descricao"
        const val COLUMN_ACAO_ID_PILAR = "id_pilar"
        const val COLUMN_ACAO_ID_SUBPILAR = "id_subpilar"
        const val COLUMN_ACAO_ID_USUARIO = "id_usuario"

        const val TABLE_PERCENTUAL_ACAO = "percentual_Acao"
        const val COLUMN_PERCENTUAL_ACAO_ID = "id"
        const val COLUMN_PERCENTUAL_ACAO_MES = "mes"
        const val COLUMN_PERCENTUAL_ACAO_PERCENTUAL = "percentual"
        const val COLUMN_PERCENTUAL_ACAO_ID_ACAO = "id_acao"

        const val TABLE_ATIVIDADE = "atividade"
        const val COLUMN_ATIVIDADE_ID = "id"
        const val COLUMN_ATIVIDADE_NOME = "nome"
        const val COLUMN_ATIVIDADE_DATA_INICIO = "dataInicio"
        const val COLUMN_ATIVIDADE_DATA_TERMINO = "dataTermino"
        const val COLUMN_ATIVIDADE_RESPONSAVEL = "responsavel"
        const val COLUMN_ATIVIDADE_IS_APROVADO = "isAprovado"
        const val COLUMN_ATIVIDADE_IS_FINALIZADO = "isFinalizado"
        const val COLUMN_ATIVIDADE_DESCRICAO = "descricao"
        const val COLUMN_ATIVIDADE_ORCAMENTO = "orcamento"
        const val COLUMN_ATIVIDADE_ID_ACAO = "id_acao"
        const val COLUMN_ATIVIDADE_ID_USUARIO = "id_usuario"

        const val TABLE_PERCENTUAL_ATIVIDADE = "percentual_Atividade"
        const val COLUMN_PERCENTUAL_ATIVIDADE_ID = "id"
        const val COLUMN_PERCENTUAL_ATIVIDADE_MES = "mes"
        const val COLUMN_PERCENTUAL_ATIVIDADE_PERCENTUAL = "percentual"
        const val COLUMN_PERCENTUAL_ATIVIDADE_ID_ATIVIDADE = "id_atividade"
    }

}