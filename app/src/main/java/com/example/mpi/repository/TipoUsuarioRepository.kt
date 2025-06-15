package com.example.mpi.repository

import android.content.Context
import com.example.mpi.data.DatabaseHelper
import com.example.mpi.data.TipoUsuario
import com.example.mpi.data.Usuario

/**
 * Repositório para gerenciar operações de dados relacionadas aos tipos de usuário (cargos) no sistema.
 *
 * Esta classe atua como uma camada de abstração entre a interface de usuário
 * e a fonte de dados (neste caso, o banco de dados SQLite gerenciado por [DatabaseHelper]).
 * Ela facilita a busca e manipulação de objetos [TipoUsuario].
 *
 * Implementa o padrão Singleton para garantir que apenas uma instância do repositório
 * exista em todo o ciclo de vida da aplicação, otimizando o gerenciamento da conexão com o banco de dados.
 *
 * @property context O contexto da aplicação necessário para inicializar o [DatabaseHelper].
 */
class TipoUsuarioRepository (context: Context) {
    private var dataBase: DatabaseHelper = DatabaseHelper(context)

    companion object {
        private lateinit var instance: TipoUsuarioRepository

        /**
         * Retorna a única instância de [TipoUsuarioRepository] (Singleton).
         * Se a instância ainda não foi inicializada, ela é criada de forma segura para threads.
         *
         * @param context O contexto da aplicação.
         * @return A instância de [TipoUsuarioRepository].
         */
        fun getInstance(context: Context): TipoUsuarioRepository {
            synchronized(this) {
                if (!::instance.isInitialized) {
                    instance = TipoUsuarioRepository(context)
                }
            }
            return instance
        }
    }

    /**
     * Busca um [TipoUsuario] no banco de dados pelo seu identificador único.
     *
     * @param id O ID do tipo de usuário a ser buscado.
     * @return O objeto [TipoUsuario] correspondente ao ID, ou `null` se nenhum tipo de usuário for encontrado.
     */
    fun obterTipoUsuarioPorId(id: Int): TipoUsuario? {
        val db = dataBase.readableDatabase

        val cursor = db.rawQuery("SELECT * FROM tipoUsuario WHERE id = ?", arrayOf(id.toString()))

        var tipoUsuario: TipoUsuario? = null
        if (cursor.moveToFirst()) {
            val id = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_TIPOUSUARIO_ID))
            val cargo = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_TIPOUSUARIO_CARGO))

            tipoUsuario = TipoUsuario(id, cargo)
        }

        cursor.close()
        db.close()

        return tipoUsuario
    }

}