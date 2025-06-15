package com.example.mpi.data

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

/**
 * Auxiliar para gerenciamento do banco de dados SQLite local da aplicação.
 *
 * Esta classe estende [SQLiteOpenHelper] e é responsável por:
 * 1. **Criar o esquema do banco de dados** na primeira instalação do aplicativo.
 * Isso inclui a definição de todas as tabelas e suas colunas, além das chaves estrangeiras.
 * 2. **Realizar a atualização do banco de dados** quando a versão do esquema muda,
 * garantindo que o banco de dados seja compatível com a versão mais recente do aplicativo.
 * 3. **Popular o banco de dados com dados iniciais** para 'TipoUsuario' e 'Usuario'
 * na criação inicial do DB.
 *
 * Ele gerencia o acesso ao banco de dados SQLite, servindo como a base para os repositórios
 * de dados que interagem com as tabelas.
 *
 * @property context O contexto da aplicação necessário para abrir ou criar o banco de dados.
 */
class DatabaseHelper(context: Context) :
    SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    /**
     * Chamado quando o banco de dados é criado pela primeira vez.
     *
     * Este método executa todas as instruções SQL para criar as tabelas do esquema
     * do banco de dados e popula as tabelas iniciais com dados padrão.
     *
     * @param db O objeto [SQLiteDatabase] do banco de dados.
     */
    override fun onCreate(db: SQLiteDatabase) {
        // Definição das instruções SQL para criação de cada tabela.
        // As tabelas incluem: Calendario, TipoUsuario, Usuario, Notificacao,
        // Pilar, Subpilar, Acao, PercentualAcao, Atividade e PercentualAtividade.


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
                $COLUMN_NOTIFICACAO_ID_ITEM INTEGER,
                $COLUMN_NOTIFICACAO_TIPO_ITEM TEXT,
                FOREIGN KEY ($COLUMN_NOTIFICACAO_ID_USUARIO) REFERENCES $TABLE_USUARIO($COLUMN_USUARIO_ID)
            );
        """.trimIndent()

        val createPilarTable = """
            CREATE TABLE $TABLE_PILAR (
                $COLUMN_PILAR_ID INTEGER PRIMARY KEY,
                $COLUMN_PILAR_NOME TEXT NOT NULL,
                $COLUMN_PILAR_DATA_INICIO TEXT NOT NULL,
                $COLUMN_PILAR_DATA_TERMINO TEXT NOT NULL,
                $COLUMN_PILAR_DESCRICAO TEXT NOT NULL,
                $COLUMN_PILAR_PERCENTUAL REAL DEFAULT 0,
                $COLUMN_PILAR_ID_CALENDARIO INTEGER NOT NULL,
                $COLUMN_PILAR_ID_USUARIO INTEGER NOT NULL,
                FOREIGN KEY ($COLUMN_PILAR_ID_CALENDARIO) REFERENCES $TABLE_CALENDARIO($COLUMN_CALENDARIO_ID),
                FOREIGN KEY ($COLUMN_PILAR_ID_USUARIO) REFERENCES $TABLE_USUARIO($COLUMN_USUARIO_ID)
            );
        """.trimIndent()

        val createSubpilarTable = """
            CREATE TABLE $TABLE_SUBPILAR (
                $COLUMN_SUBPILAR_ID INTEGER PRIMARY KEY,
                $COLUMN_SUBPILAR_NOME TEXT NOT NULL,
                $COLUMN_SUBPILAR_DATA_INICIO TEXT NOT NULL,
                $COLUMN_SUBPILAR_DATA_TERMINO TEXT NOT NULL,
                $COLUMN_SUBPILAR_DESCRICAO TEXT NOT NULL,
                $COLUMN_SUBPILAR_ID_PILAR INTEGER NOT NULL,
                $COLUMN_SUBPILAR_ID_USUARIO INTEGER NOT NULL,
                FOREIGN KEY ($COLUMN_SUBPILAR_ID_PILAR) REFERENCES $TABLE_PILAR($COLUMN_PILAR_ID),
                FOREIGN KEY ($COLUMN_SUBPILAR_ID_USUARIO) REFERENCES $TABLE_USUARIO($COLUMN_USUARIO_ID)
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
                $COLUMN_ACAO_ID_USUARIO INTEGER NOT NULL,
                FOREIGN KEY ($COLUMN_ACAO_ID_PILAR) REFERENCES $TABLE_PILAR($COLUMN_PILAR_ID),
                FOREIGN KEY ($COLUMN_ACAO_ID_SUBPILAR) REFERENCES $TABLE_SUBPILAR($COLUMN_SUBPILAR_ID),
                FOREIGN KEY ($COLUMN_ACAO_ID_USUARIO) REFERENCES $TABLE_USUARIO($COLUMN_USUARIO_ID)
            );
        """.trimIndent()

        val createPercentualAcaoTable = """
            CREATE TABLE $TABLE_PERCENTUAL_ACAO (
                $COLUMN_PERCENTUAL_ACAO_ID INTEGER PRIMARY KEY,
                $COLUMN_PERCENTUAL_ACAO_MES INTEGER NOT NULL,
                $COLUMN_PERCENTUAL_ACAO_PERCENTUAL REAL DEFAULT 0,
                $COLUMN_PERCENTUAL_ACAO_ID_ACAO INTEGER NOT NULL,
                FOREIGN KEY ($COLUMN_PERCENTUAL_ACAO_ID_ACAO) REFERENCES $TABLE_ACAO($COLUMN_ACAO_ID)
            );
        """.trimIndent()

        val createAtividadeTable = """
            CREATE TABLE $TABLE_ATIVIDADE (
                $COLUMN_ATIVIDADE_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COLUMN_ATIVIDADE_NOME TEXT NOT NULL,
                $COLUMN_ATIVIDADE_DATA_INICIO TEXT NOT NULL,
                $COLUMN_ATIVIDADE_DATA_TERMINO TEXT NOT NULL,
                $COLUMN_ATIVIDADE_RESPONSAVEL INTEGER,
                $COLUMN_ATIVIDADE_IS_APROVADO INTEGER DEFAULT 0,
                $COLUMN_ATIVIDADE_IS_FINALIZADO INTEGER DEFAULT 0,
                $COLUMN_ATIVIDADE_DESCRICAO TEXT NOT NULL,
                $COLUMN_ATIVIDADE_ORCAMENTO REAL DEFAULT 0,
                $COLUMN_ATIVIDADE_ID_ACAO INTEGER NOT NULL,
                $COLUMN_ATIVIDADE_ID_USUARIO INTEGER NOT NULL,
                FOREIGN KEY ($COLUMN_ATIVIDADE_ID_USUARIO) REFERENCES $TABLE_USUARIO($COLUMN_USUARIO_ID),
                FOREIGN KEY ($COLUMN_ATIVIDADE_ID_ACAO) REFERENCES $TABLE_ACAO($COLUMN_ACAO_ID)
            );
        """.trimIndent()

        val createPercentualAtividadeTable = """
            CREATE TABLE $TABLE_PERCENTUAL_ATIVIDADE (
                $COLUMN_PERCENTUAL_ATIVIDADE_ID INTEGER PRIMARY KEY,
                $COLUMN_PERCENTUAL_ATIVIDADE_MES INTEGER NOT NULL,
                $COLUMN_PERCENTUAL_ATIVIDADE_PERCENTUAL REAL DEFAULT 0,
                $COLUMN_PERCENTUAL_ATIVIDADE_ID_ATIVIDADE INTEGER NOT NULL,
                FOREIGN KEY ($COLUMN_PERCENTUAL_ATIVIDADE_ID_ATIVIDADE) REFERENCES $TABLE_ATIVIDADE($COLUMN_ATIVIDADE_ID)
            );
        """.trimIndent()

        db.execSQL(createCalendarioTable)
        db.execSQL(createTipoUsuarioTable)
        db.execSQL(createUsuarioTable)
        db.execSQL(createNotificacaoTable)
        db.execSQL(createPilarTable)
        db.execSQL(createSubpilarTable)
        db.execSQL(createAcaoTable)
        db.execSQL(createPercentualAcaoTable)
        db.execSQL(createAtividadeTable)
        db.execSQL(createPercentualAtividadeTable)

        //Seeding: Inserção de dados iniciais
        inserirTipoUsuario(db, obterTipoUsuario())
        inserirUsuarios(db, obterUsuarios())
    }

    /**
     * Chamado quando o banco de dados precisa ser atualizado.
     *
     * Este método é invocado quando a versão do banco de dados (DATABASE_VERSION) no código
     * é maior que a versão do banco de dados no dispositivo. Ele é responsável por migrar
     * ou recriar o esquema do banco de dados para a nova versão.
     *
     *
     * @param db O objeto [SQLiteDatabase] do banco de dados.
     * @param oldVersion A versão antiga do banco de dados.
     * @param newVersion A nova versão do banco de dados.
     */
    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE_PERCENTUAL_ATIVIDADE")
        db.execSQL("DROP TABLE IF EXISTS $TABLE_ATIVIDADE")
        db.execSQL("DROP TABLE IF EXISTS $TABLE_PERCENTUAL_ACAO")
        db.execSQL("DROP TABLE IF EXISTS $TABLE_ACAO")
        db.execSQL("DROP TABLE IF EXISTS $TABLE_SUBPILAR")
        db.execSQL("DROP TABLE IF EXISTS $TABLE_PILAR")
        db.execSQL("DROP TABLE IF EXISTS $TABLE_NOTIFICACAO")
        db.execSQL("DROP TABLE IF EXISTS $TABLE_USUARIO")
        db.execSQL("DROP TABLE IF EXISTS $TABLE_TIPOUSUARIO")
        db.execSQL("DROP TABLE IF EXISTS $TABLE_CALENDARIO")

        onCreate(db)
    }

    // Constantes globais (NOMES DE TABELAS E COLUNAS)
    companion object {
        const val DATABASE_NAME = "mpi.db"
        const val DATABASE_VERSION = 1

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
        const val COLUMN_NOTIFICACAO_ID_ITEM = "id_item"
        const val COLUMN_NOTIFICACAO_TIPO_ITEM = "tipo_item"

        const val TABLE_PILAR = "pilar"
        const val COLUMN_PILAR_ID = "id"
        const val COLUMN_PILAR_NOME = "nome"
        const val COLUMN_PILAR_DATA_INICIO = "dataInicio"
        const val COLUMN_PILAR_DATA_TERMINO = "dataTermino"
        const val COLUMN_PILAR_DESCRICAO = "descricao"
        const val COLUMN_PILAR_PERCENTUAL = "percentual"
        const val COLUMN_PILAR_ID_CALENDARIO = "id_calendario"
        const val COLUMN_PILAR_ID_USUARIO = "id_usuario"

        const val TABLE_SUBPILAR = "subpilar"
        const val COLUMN_SUBPILAR_ID = "id"
        const val COLUMN_SUBPILAR_NOME = "nome"
        const val COLUMN_SUBPILAR_DATA_INICIO = "dataInicio"
        const val COLUMN_SUBPILAR_DATA_TERMINO = "dataTermino"
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

        const val TABLE_PERCENTUAL_ACAO = "percentual_acao"
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

        const val TABLE_PERCENTUAL_ATIVIDADE = "percentual_atividade"
        const val COLUMN_PERCENTUAL_ATIVIDADE_ID = "id"
        const val COLUMN_PERCENTUAL_ATIVIDADE_MES = "mes"
        const val COLUMN_PERCENTUAL_ATIVIDADE_PERCENTUAL = "percentual"
        const val COLUMN_PERCENTUAL_ATIVIDADE_ID_ATIVIDADE = "id_atividade"
    }


    /**
     * Insere os tipos de usuário predefinidos na tabela 'tipoUsuario'.
     *
     * Este método é chamado uma única vez durante a criação inicial do banco de dados
     * para popular a tabela com cargos como "Analista", "Coordenador" e "Gestor".
     *
     * @param db O objeto [SQLiteDatabase] para realizar a inserção.
     * @param tipoUsuario Uma lista de objetos [TipoUsuario] a serem inseridos.
     */
    private fun inserirTipoUsuario(db: SQLiteDatabase, tipoUsuario: List<TipoUsuario>) {
        for (tipo in tipoUsuario) {
            val values = ContentValues().apply {
                put(COLUMN_TIPOUSUARIO_CARGO, tipo.cargo)
            }
            db.insert(TABLE_TIPOUSUARIO, null, values)
        }
    }

    /**
     * Insere os usuários iniciais na tabela 'usuario'.
     *
     * Este método é chamado uma única vez durante a criação inicial do banco de dados
     * para popular a tabela com usuários de exemplo associados aos tipos de usuário.
     *
     * @param db O objeto [SQLiteDatabase] para realizar a inserção.
     * @param usuarios Uma lista de objetos [Usuario] a serem inseridos.
     */
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

    /**
     * Retorna uma lista predefinida de tipos de usuário para inserção inicial no banco de dados.
     * @return Uma [List] de objetos [TipoUsuario].
     */
    private fun obterTipoUsuario(): List<TipoUsuario> = listOf(
        TipoUsuario(1, "Analista"),
        TipoUsuario(2, "Coordenador"),
        TipoUsuario(3, "Gestor")
    )

    /**
     * Retorna uma lista predefinida de usuários para inserção inicial no banco de dados.
     * @return Uma [List] de objetos [Usuario].
     */
    private fun obterUsuarios(): List<Usuario> = listOf(
        Usuario(1, "Analista José", "jose@jose.com", "jose123", 1),
        Usuario(2, "Analista João", "joao@joao.com", "joao123", 1),
        Usuario(3, "Coordenador Marcos", "marcos@marcos.com", "marcos123", 2),
        Usuario(4, "Coordenadora Marta", "marta@marta.com", "marta123", 2),
        Usuario(5, "Gestor Geraldo", "geraldo@geraldo.com", "geraldo123", 3),
        Usuario(6, "Gestora Goreti", "goreti@goreti.com", "goreti123", 3)
    )
}
