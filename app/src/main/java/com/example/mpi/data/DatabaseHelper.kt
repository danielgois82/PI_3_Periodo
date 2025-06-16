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
        inserirCalendario(db, obterCalendario())
        inserirTipoUsuario(db, obterTipoUsuario())
        inserirUsuarios(db, obterUsuarios())
        inserirPilares(db, obterPilares())
        inserirSubpilares(db, obterSubpilares())
        inserirAcoes(db, obterAcoes())
        inserirAtividades(db, obterAtividades())
        inserirPercentualAtividades(db, obterPercentualAtividades())
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
     * Insere o calendario inicial na tabela 'Calendario'.
     *
     * Este método é chamado uma única vez durante a criação inicial do banco de dados
     * para popular a tabela de Calendario pois ela faz parte da associação com a tabela Pilar.
     *
     * @param db O objeto [SQLiteDatabase] para realizar a inserção.
     * @param calendario Um objeto [Calendario] a ser inserido.
     */
    private fun inserirCalendario(db: SQLiteDatabase, calendario: Calendario) {
        val value = ContentValues().apply {
            put(COLUMN_CALENDARIO_ANO, calendario.ano)
        }
        db.insert(TABLE_CALENDARIO, null, value)
    }

    /**
     * Insere os pilares iniciais na tabela 'Pilar'.
     *
     * Este método é chamado uma única vez durante a criação inicial do banco de dados
     * para popular a tabela com pilares de exemplo.
     *
     * @param db O objeto [SQLiteDatabase] para realizar a inserção.
     * @param pilares Uma lista de objetos [Pilar] a serem inseridos.
     */
    private fun inserirPilares(db: SQLiteDatabase, pilares: List<Pilar>) {
        for (pilar in pilares) {
            val values = ContentValues().apply {
                put(COLUMN_PILAR_NOME, pilar.nome)
                put(COLUMN_PILAR_DESCRICAO, pilar.descricao)
                put(COLUMN_PILAR_DATA_INICIO, pilar.dataInicio)
                put(COLUMN_PILAR_DATA_TERMINO, pilar.dataTermino)
                put(COLUMN_PILAR_ID_CALENDARIO, pilar.idCalendario)
                put(COLUMN_PILAR_ID_USUARIO, pilar.idUsuario)
            }
            db.insert(TABLE_PILAR, null, values)
        }
    }

    /**
     * Insere os subpilares iniciais na tabela 'Subpilar'.
     *
     * Este método é chamado uma única vez durante a criação inicial do banco de dados
     * para popular a tabela com subpilares de exemplo.
     *
     * @param db O objeto [SQLiteDatabase] para realizar a inserção.
     * @param subpilares Uma lista de objetos [Subpilar] a serem inseridos.
     */
    private fun inserirSubpilares(db: SQLiteDatabase, subpilares: List<Subpilar>) {
        for (subpilar in subpilares) {
            val values = ContentValues().apply {
                put(COLUMN_SUBPILAR_NOME, subpilar.nome)
                put(COLUMN_SUBPILAR_DESCRICAO, subpilar.descricao)
                put(COLUMN_SUBPILAR_DATA_INICIO, subpilar.dataInicio)
                put(COLUMN_SUBPILAR_DATA_TERMINO, subpilar.dataTermino)
                put(COLUMN_SUBPILAR_ID_PILAR, subpilar.idPilar)
                put(COLUMN_SUBPILAR_ID_USUARIO, subpilar.idUsuario)
            }
            db.insert(TABLE_SUBPILAR, null, values)
        }
    }

    /**
     * Insere as ações iniciais na tabela 'Acao'.
     *
     * Este método é chamado uma única vez durante a criação inicial do banco de dados
     * para popular a tabela com ações de exemplo.
     *
     * @param db O objeto [SQLiteDatabase] para realizar a inserção.
     * @param acoes Uma lista de objetos [Acao] a serem inseridos.
     */
    private fun inserirAcoes(db: SQLiteDatabase, acoes: List<Acao>) {
        for (acao in acoes) {
            val values = ContentValues().apply {
                put(COLUMN_ACAO_NOME, acao.nome)
                put(COLUMN_ACAO_DESCRICAO, acao.descricao)
                put(COLUMN_ACAO_DATA_INICIO, acao.dataInicio)
                put(COLUMN_ACAO_DATA_TERMINO, acao.dataTermino)
                put(COLUMN_ACAO_RESPONSAVEL, acao.responsavel)
                put(COLUMN_ACAO_IS_APROVADO, acao.aprovado)
                put(COLUMN_ACAO_IS_FINALIZADO, acao.finalizado)

                if (acao.idPilar != 0) {
                    put(COLUMN_ACAO_ID_PILAR, acao.idPilar)
                }

                if (acao.idSubpilar != 0) {
                    put(COLUMN_ACAO_ID_SUBPILAR, acao.idSubpilar)
                }

                put(COLUMN_ACAO_ID_USUARIO, acao.idUsuario)
            }
            db.insert(TABLE_ACAO, null, values)
        }
    }

    /**
     * Insere as atividades iniciais na tabela 'Atividade'.
     *
     * Este método é chamado uma única vez durante a criação inicial do banco de dados
     * para popular a tabela com atividades de exemplo.
     *
     * @param db O objeto [SQLiteDatabase] para realizar a inserção.
     * @param atividades Uma lista de objetos [Atividade] a serem inseridos.
     */
    private fun inserirAtividades(db: SQLiteDatabase, atividades: List<Atividade>) {
        for (atividade in atividades) {
            val values = ContentValues().apply {
                put(COLUMN_ATIVIDADE_NOME, atividade.nome)
                put(COLUMN_ATIVIDADE_DESCRICAO, atividade.descricao)
                put(COLUMN_ATIVIDADE_DATA_INICIO, atividade.dataInicio)
                put(COLUMN_ATIVIDADE_DATA_TERMINO, atividade.dataTermino)
                put(COLUMN_ATIVIDADE_RESPONSAVEL, atividade.responsavel)
                put(COLUMN_ATIVIDADE_IS_APROVADO, atividade.aprovado)
                put(COLUMN_ATIVIDADE_IS_FINALIZADO, atividade.finalizado)
                put(COLUMN_ATIVIDADE_ORCAMENTO, atividade.orcamento)
                put(COLUMN_ATIVIDADE_ID_ACAO, atividade.idAcao)
                put(COLUMN_ATIVIDADE_ID_USUARIO, atividade.idUsuario)
            }
            db.insert(TABLE_ATIVIDADE, null, values)
        }
    }

    /**
     * Insere os percentuais da atividade iniciais na tabela 'PercentualAtividade'.
     *
     * Este método é chamado uma única vez durante a criação inicial do banco de dados
     * para popular a tabela com percentuais da atividade de exemplo.
     *
     * @param db O objeto [SQLiteDatabase] para realizar a inserção.
     * @param percentuais Uma lista de objetos [PercentualAtividade] a serem inseridos.
     */
    private fun inserirPercentualAtividades(db: SQLiteDatabase, percentuais: List<PercentualAtividade>) {
        for (percentual in percentuais) {
            val values = ContentValues().apply {
                put(COLUMN_PERCENTUAL_ATIVIDADE_MES, percentual.mes)
                put(COLUMN_PERCENTUAL_ATIVIDADE_PERCENTUAL, percentual.percentual)
                put(COLUMN_PERCENTUAL_ATIVIDADE_ID_ATIVIDADE, percentual.idAtividade)
            }
            db.insert(TABLE_PERCENTUAL_ATIVIDADE, null, values)
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

    /**
     * Retorna um registro do tipo Calendario para inserção inicial no banco de dados.
     * @return Um objeto [Calendario].
     */
    private fun obterCalendario(): Calendario {
        return Calendario(1, 2025)
    }

    /**
     * Retorna uma lista predefinida de pilares para inserção inicial no banco de dados.
     * @return Uma [List] de objetos [Pilar].
     */
    private fun obterPilares(): List<Pilar> = listOf(
        Pilar(1, "Suporte da Alta Administração", "Descrição do Suporte da Alta Administração", "01/01/2025", "31/12/2025", 0.0, 1, 3),
        Pilar(2, "Instância responsável", "Descrição da Instância responsável", "01/01/2025", "31/12/2025", 0.0, 1, 3),
        Pilar(3, "Avaliação de Riscos", "Descrição da Avaliação de Riscos", "01/01/2025", "31/12/2025", 0.0, 1, 3),
        Pilar(4, "Estruturação das regras e instrumentos", "Descrição da Estruturação das regras e instrumentos", "01/01/2025", "31/12/2025", 0.0, 1, 3),
        Pilar(5, "Diversidade e Inclusão", "Descrição da Diversidade e Inclusão", "01/01/2025", "31/12/2025", 0.0, 1, 3),
        Pilar(6, "Auditoria e monitoramento", "Descrição da Auditoria e monitoramento", "01/01/2025", "31/12/2025", 0.0, 1, 3)
    )

    /**
     * Retorna uma lista predefinida de subpilares para inserção inicial no banco de dados.
     * @return Uma [List] de objetos [Subpilar].
     */
    private fun obterSubpilares(): List<Subpilar> = listOf(
        Subpilar(1, "Código de Ética e Conduta e Políticas de Compliance", "Descrição do Código de Ética e Conduta e Políticas de Compliance", "01/01/2025", "31/12/2025", 4, 3),
        Subpilar(2, "Comunicação e Terinamento", "Descrição da Comunicação e Terinamento", "01/01/2025", "31/12/2025", 4, 3),
        Subpilar(4, "Ouvidoria", "Descrição ds Ouvidoria", "01/01/2025", "31/12/2025", 4, 3),
        Subpilar(4, "Investigações internas", "Descrição das Investigações internas", "01/01/2025", "31/12/2025", 4, 3),
        Subpilar(5, "Processo de investigação", "Descrição do Processo de investigação", "01/01/2025", "31/12/2025", 4, 3),
        Subpilar(6, "Due Diligence", "Descrição do Due Diligence", "01/01/2025", "31/12/2025", 4, 3)
    )

    /**
     * Retorna uma lista predefinida de Ação para inserção inicial no banco de dados.
     * @return Uma [List] de objetos [Acao].
     */
    private fun obterAcoes(): List<Acao> = listOf(
        Acao(1, "Ação 1 Pilar Suporte da Alta Administração", "Descrição da Ação 1", "01/01/2025", "31/12/2025", 1, true, true, 1, 0, 3),
        Acao(2, "Ação 1 Pilar Instância responsável", "Descrição da Ação 1", "01/01/2025", "31/12/2025", 1, true, false, 2, 0, 3),
        Acao(3, "Ação 1 Pilar Avaliação de Riscos", "Descrição da Ação 1", "01/01/2025", "31/12/2025", 1, true, false, 3, 0, 3),
        Acao(4, "Ação 1 Subpilar Código de Ética", "Descrição da Ação 1", "01/01/2025", "31/12/2025", 1, true, false, 0, 1, 3),
        Acao(5, "Ação 1 Pilar Diversidade e Inclusão", "Descrição da Ação 1", "01/01/2025", "31/03/2025", 1, true, false, 5, 0, 3),
        Acao(6, "Ação 1 Pilar Auditoria e monitoramento", "Descrição da Ação 1", "01/01/2025", "31/03/2025", 1, false, false, 6, 0, 3),
    )

    /**
     * Retorna uma lista predefinida de atividades para inserção inicial no banco de dados.
     * @return Uma [List] de objetos [Atividade].
     */
    private fun obterAtividades(): List<Atividade> = listOf(
        Atividade(1, "Atividade 1 Pilar Suporte da Alta Administração", "Descrição da Atividade 1", "01/01/2025", "31/12/2025", 1, true, true, 10000.0, 1, 3),
        Atividade(2, "Atividade 1 Pilar Instância responsável", "Descrição da Atividade 1", "01/01/2025", "20/06/2025", 1, true, false, 20000.0, 2, 3),
        Atividade(3, "Atividade 1 Pilar Avaliação de Riscos", "Descrição da Atividade 1", "01/01/2025", "29/06/2025", 1, true, false, 30000.0, 3, 3),
        Atividade(4, "Atividade 1 Subpilar Código de Ética", "Descrição da Atividade 1", "01/01/2025", "13/07/2025", 1, true, false, 40000.0, 4, 3),
        Atividade(5, "Atividade 1 Pilar Diversidade e Inclusão", "Descrição da Atividade 1", "01/01/2025", "31/03/2025", 1, true, false, 50000.0, 5, 3),
        Atividade(6, "Atividade 1 Pilar Auditoria e monitoramento", "Descrição da Atividade 1", "01/01/2025", "31/03/2025", 1, false, false, 60000.0, 6, 3)
    )

    /**
     * Retorna uma lista predefinida de percentual da atividade para inserção inicial no banco de dados.
     * @return Uma [List] de objetos [PercentualAtividade].
     */
    private fun obterPercentualAtividades(): List<PercentualAtividade> = listOf(
        PercentualAtividade(1, 1, 25.0, 1),
        PercentualAtividade(2, 2, 25.0, 1),
        PercentualAtividade(3, 3, 50.0, 1),
        PercentualAtividade(4, 4, 0.0, 1),
        PercentualAtividade(5, 5, 0.0, 1),
        PercentualAtividade(6, 6, 0.0, 1),
        PercentualAtividade(7, 7, 0.0, 1),
        PercentualAtividade(8, 8, 0.0, 1),
        PercentualAtividade(9, 9, 0.0, 1),
        PercentualAtividade(10, 10, 0.0, 1),
        PercentualAtividade(11, 11, 0.0, 1),
        PercentualAtividade(12, 12, 0.0, 1),

        PercentualAtividade(13, 1, 30.0, 2),
        PercentualAtividade(14, 2, 0.0, 2),
        PercentualAtividade(15, 3, 0.0, 2),
        PercentualAtividade(16, 4, 0.0, 2),
        PercentualAtividade(17, 5, 0.0, 2),
        PercentualAtividade(18, 6, 0.0, 2),
        PercentualAtividade(19, 7, 0.0, 2),
        PercentualAtividade(20, 8, 0.0, 2),
        PercentualAtividade(21, 9, 0.0, 2),
        PercentualAtividade(22, 10, 0.0, 2),
        PercentualAtividade(23, 11, 0.0, 2),
        PercentualAtividade(24, 12, 0.0, 2),

        PercentualAtividade(25, 1, 70.0, 3),
        PercentualAtividade(26, 2, 0.0, 3),
        PercentualAtividade(27, 3, 0.0, 3),
        PercentualAtividade(28, 4, 0.0, 3),
        PercentualAtividade(29, 5, 0.0, 3),
        PercentualAtividade(30, 6, 0.0, 3),
        PercentualAtividade(31, 7, 0.0, 3),
        PercentualAtividade(32, 8, 0.0, 3),
        PercentualAtividade(33, 9, 0.0, 3),
        PercentualAtividade(34, 10, 0.0, 3),
        PercentualAtividade(35, 11, 0.0, 3),
        PercentualAtividade(36, 12, 0.0, 3),

        PercentualAtividade(37, 1, 30.0, 4),
        PercentualAtividade(38, 2, 0.0, 4),
        PercentualAtividade(39, 3, 0.0, 4),
        PercentualAtividade(40, 4, 0.0, 4),
        PercentualAtividade(41, 5, 0.0, 4),
        PercentualAtividade(42, 6, 0.0, 4),
        PercentualAtividade(43, 7, 0.0, 4),
        PercentualAtividade(44, 8, 0.0, 4),
        PercentualAtividade(45, 9, 0.0, 4),
        PercentualAtividade(46, 10, 0.0, 4),
        PercentualAtividade(47, 11, 0.0, 4),
        PercentualAtividade(48, 12, 0.0, 4),

        PercentualAtividade(49, 1, 50.0, 5),
        PercentualAtividade(50, 2, 0.0, 5),
        PercentualAtividade(51, 3, 0.0, 5),
        PercentualAtividade(52, 4, 0.0, 5),
        PercentualAtividade(53, 5, 0.0, 5),
        PercentualAtividade(54, 6, 0.0, 5),
        PercentualAtividade(55, 7, 0.0, 5),
        PercentualAtividade(56, 8, 0.0, 5),
        PercentualAtividade(57, 9, 0.0, 5),
        PercentualAtividade(58, 10, 0.0, 5),
        PercentualAtividade(59, 11, 0.0, 5),
        PercentualAtividade(60, 12, 0.0, 5),

        PercentualAtividade(61, 1, 0.0, 6),
        PercentualAtividade(62, 2, 0.0, 6),
        PercentualAtividade(63, 3, 0.0, 6),
        PercentualAtividade(64, 4, 0.0, 6),
        PercentualAtividade(65, 5, 0.0, 6),
        PercentualAtividade(66, 6, 0.0, 6),
        PercentualAtividade(67, 7, 0.0, 6),
        PercentualAtividade(68, 8, 0.0, 6),
        PercentualAtividade(68, 9, 0.0, 6),
        PercentualAtividade(70, 10, 0.0, 6),
        PercentualAtividade(71, 11, 0.0, 6),
        PercentualAtividade(72, 12, 0.0, 6)
    )
}
