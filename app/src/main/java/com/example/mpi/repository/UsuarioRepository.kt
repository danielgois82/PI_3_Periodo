package com.example.mpi.repository

import android.content.Context
import com.example.mpi.data.DatabaseHelper
import com.example.mpi.data.Usuario

/**
 * Repositório para gerenciar operações de dados relacionadas aos usuários no sistema.
 *
 * Esta classe serve como uma camada de abstração entre a lógica de negócio da aplicação
 * e a fonte de dados (o banco de dados SQLite, acessado via [DatabaseHelper]).
 * Ela é responsável por todas as operações de busca de dados de usuários.
 *
 * Implementa o padrão Singleton para garantir que apenas uma instância deste repositório
 * exista durante o ciclo de vida da aplicação, otimizando o uso de recursos do banco de dados.
 *
 * @property context O [Context] da aplicação, necessário para inicializar o [DatabaseHelper].
 */
class UsuarioRepository (context: Context) {
    private var dataBase: DatabaseHelper = DatabaseHelper(context)

    companion object {
        private lateinit var instance: UsuarioRepository

        /**
         * Retorna a única instância de [UsuarioRepository] (Singleton).
         * Se a instância ainda não foi criada, ela é inicializada de forma segura para threads.
         *
         * @param context O [Context] da aplicação.
         * @return A instância de [UsuarioRepository].
         */
        fun getInstance(context: Context): UsuarioRepository {
            synchronized(this) {
                if (!::instance.isInitialized) {
                    instance = UsuarioRepository(context)
                }
            }
            return instance
        }
    }

    /**
     * Busca um [Usuario] no banco de dados utilizando seu email e senha.
     *
     * @param email O email do usuário a ser buscado.
     * @param senha A senha do usuário a ser verificada.
     * @return O objeto [Usuario] correspondente ao email e senha fornecidos, ou `null` se nenhum usuário for encontrado ou as credenciais forem inválidas.
     */
    fun obterUsuarioPorEmailESenha(email: String, senha: String): Usuario? {
        val db = dataBase.readableDatabase

        val cursor = db.rawQuery("SELECT * FROM usuario WHERE email = ? AND senha = ?", arrayOf(email, senha))

        var usuario: Usuario? = null
        if (cursor.moveToFirst()) {
            val id = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_USUARIO_ID))
            val nome = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_USUARIO_NOME))
            val email = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_USUARIO_EMAIL))
            val senha = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_USUARIO_SENHA))
            val idTipoUsuario = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_USUARIO_ID_TIPOUSUARIO))

            usuario = Usuario(id, nome, email, senha, idTipoUsuario)
        }

        cursor.close()
        db.close()

        return usuario
    }

    /**
     * Busca um [Usuario] no banco de dados pelo seu identificador único.
     *
     * @param id O ID do usuário a ser buscado.
     * @return O objeto [Usuario] correspondente ao ID fornecido, ou `null` se nenhum usuário com o ID especificado for encontrado.
     */
    fun obterUsuarioPorId(id: Int): Usuario? {
        val db = dataBase.readableDatabase

        val cursor = db.rawQuery("SELECT * FROM usuario WHERE id = ?", arrayOf(id.toString()))

        var usuario: Usuario? = null
        if (cursor.moveToFirst()) {
            val idUsuario = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_USUARIO_ID))
            val nome = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_USUARIO_NOME))
            val email = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_USUARIO_EMAIL))
            val senha = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_USUARIO_SENHA))
            val idTipoUsuario = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_USUARIO_ID_TIPOUSUARIO))

            usuario = Usuario(idUsuario, nome, email, senha, idTipoUsuario)
        }

        cursor.close()
        db.close()

        return usuario
    }

    /**
     * Obtém apenas o nome de um usuário com base no seu ID.
     * Este método é útil quando apenas o nome do usuário é necessário, evitando a recuperação
     * de todos os dados do objeto [Usuario].
     *
     * @param id O ID do usuário cujo nome será recuperado.
     * @return O nome do usuário como uma [String], ou uma string vazia se o usuário não for encontrado.
     */
    fun obterNomeUsuarioPorId(id:Int): String{
        val db = dataBase.readableDatabase

        val cursor = db.rawQuery("SELECT * FROM usuario WHERE id = ?", arrayOf(id.toString()))
        var nomeUsuario = ""
        var usuario: Usuario? = null
        if (cursor.moveToFirst()) {
            val idUsuario = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_USUARIO_ID))
            val nome = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_USUARIO_NOME))
            val email = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_USUARIO_EMAIL))
            val senha = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_USUARIO_SENHA))
            val idTipoUsuario = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_USUARIO_ID_TIPOUSUARIO))

            nomeUsuario = nome

        }

        cursor.close()
        db.close()

        return nomeUsuario
    }

}