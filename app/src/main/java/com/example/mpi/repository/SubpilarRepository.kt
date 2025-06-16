package com.example.mpi.repository

import android.content.Context
import com.example.mpi.data.DatabaseHelper
import com.example.mpi.data.Pilar
import com.example.mpi.data.Subpilar

/**
 * Repositório para gerenciar operações de dados relacionadas aos subpilares no sistema.
 *
 * Esta classe é responsável por interagir com a tabela `subpilar` no banco de dados SQLite
 * (acessada via [DatabaseHelper]), fornecendo métodos para buscar subpilares.
 *
 * Adota o padrão **Singleton** para garantir que apenas uma instância deste repositório
 * exista em toda a aplicação, otimizando o gerenciamento da conexão com o banco de dados.
 *
 * @property dataBase Uma instância de [DatabaseHelper] para acessar o banco de dados.
 */
class SubpilarRepository (context: Context) {

    private var dataBase: DatabaseHelper = DatabaseHelper(context)

    companion object {
        private lateinit var instance: SubpilarRepository

        /**
         * Retorna a única instância de [SubpilarRepository] (Singleton).
         * Se a instância ainda não foi inicializada, ela é criada de forma segura para threads.
         *
         * @param context O [Context] da aplicação.
         * @return A instância de [SubpilarRepository].
         */
        fun getInstance(context: Context): SubpilarRepository {
            synchronized(this) {
                if (!::instance.isInitialized) {
                    instance = SubpilarRepository(context)
                }
            }
            return instance
        }
    }

    /**
     * Obtém uma lista contendo todos os subpilares existentes no banco de dados, sem nenhum filtro.
     *
     * Este método é útil para operações que requerem a listagem completa de todos os subpilares
     * cadastrados, independentemente do [Pilar] ao qual pertencem.
     *
     * @return Uma [List] de objetos [Subpilar] representando todos os subpilares disponíveis.
     */
    fun obterTodosSubpilares(): List<Subpilar> {
        val db = dataBase.readableDatabase
        val subpilares: MutableList<Subpilar> = arrayListOf()
        val cursor = db.rawQuery("SELECT * FROM ${DatabaseHelper.TABLE_SUBPILAR}", null)

        while (cursor.moveToNext()) {
            val id = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_SUBPILAR_ID))
            val nome = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_SUBPILAR_NOME))
            val descricao = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_SUBPILAR_DESCRICAO))
            val dataInicio = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_SUBPILAR_DATA_INICIO))
            val dataTermino = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_SUBPILAR_DATA_TERMINO))
            val idPilar = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_SUBPILAR_ID_PILAR))
            val idUsuario = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_SUBPILAR_ID_USUARIO))

            val subpilar = Subpilar(id, nome, descricao, dataInicio, dataTermino, idPilar, idUsuario)
            subpilares.add(subpilar)
        }
        cursor.close()
        return subpilares
    }

    /**
     * Obtém uma lista de todos os Subpilares que estão associados a um Pilar específico.
     *
     * Este método é utilizado para listar os subpilares que são filhos diretos de um determinado pilar.
     *
     * @param pilar O objeto [Pilar] cujos subpilares serão buscados.
     * @return Uma [MutableList] de objetos [Subpilar] pertencentes ao pilar especificado.
     */
    fun obterTodosSubpilares(pilar: Pilar): MutableList<Subpilar> {
        val db = dataBase.readableDatabase

        val cursor = db.rawQuery("SELECT * FROM subpilar WHERE id_pilar = ?", arrayOf(pilar.id.toString()))

        var subpilares: MutableList<Subpilar> = arrayListOf()
        while (cursor.moveToNext()) {
            val id = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_SUBPILAR_ID))
            val nome = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_SUBPILAR_NOME))
            val descricao = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_SUBPILAR_DESCRICAO))
            val dataInicio = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_SUBPILAR_DATA_INICIO))
            val dataTermino = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_SUBPILAR_DATA_TERMINO))
            val idPilar = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_SUBPILAR_ID_PILAR))
            val idUsuario = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_SUBPILAR_ID_USUARIO))

            val subpilar = Subpilar(id, nome, descricao, dataInicio, dataTermino, idPilar, idUsuario)
            subpilares.add(subpilar)
        }

        cursor.close()
        db.close()

        return subpilares
    }


}