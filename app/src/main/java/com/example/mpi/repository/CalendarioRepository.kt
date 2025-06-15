package com.example.mpi.repository

import android.content.Context
import com.example.mpi.data.Calendario
import com.example.mpi.data.DatabaseHelper
import android.content.ContentValues

/**
 * Repositório para gerenciar operações de dados relacionadas aos objetos [Calendario] no sistema.
 *
 * Esta classe atua como uma ponte entre a lógica de negócio do aplicativo e a tabela `calendario`
 * no banco de dados SQLite, que é acessada por meio do [DatabaseHelper]. Ela oferece métodos para
 * recuperar calendários existentes, obter seus IDs e inserir novos.
 *
 * Adota o padrão Singleton para garantir que apenas uma instância deste repositório esteja
 * ativa em toda a aplicação, otimizando o gerenciamento da conexão com o banco de dados.
 *
 * @property dataBase Uma instância de [DatabaseHelper] para interagir com o banco de dados.
 */
class CalendarioRepository (context: Context) {
    private var dataBase: DatabaseHelper = DatabaseHelper(context)

//class CalendarioRepository private constructor(context: Context) {
//    private val dataBase: DatabaseHelper = DatabaseHelper(context)

    companion object {
        private lateinit var instance: CalendarioRepository

        /**
         * Retorna a única instância de [CalendarioRepository] (Singleton).
         * Se a instância ainda não foi inicializada, ela é criada de forma segura para threads.
         *
         * @param context O [Context] da aplicação.
         * @return A instância de [CalendarioRepository].
         */
        fun getInstance(context: Context): CalendarioRepository {
            synchronized(this) {
                if (!::instance.isInitialized) {
                    instance = CalendarioRepository(context)
                }
            }
            return instance
        }
    }

    /**
     * Obtém uma lista contendo todos os calendários armazenados no banco de dados.
     *
     * @return Uma [MutableList] de objetos [Calendario] representando todos os calendários disponíveis.
     */
    fun obterTodosCalendarios(): MutableList<Calendario> {
        val db = dataBase.readableDatabase

        val cursor = db.rawQuery("SELECT * FROM calendario", null)

        var calendarios: MutableList<Calendario> = arrayListOf()
        while (cursor.moveToNext()) {
            val id = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_CALENDARIO_ID))
            val ano = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_CALENDARIO_ANO))

            val calendario = Calendario(id, ano)
            calendarios.add(calendario)
        }

        cursor.close()
        db.close()

        return calendarios
    }

    /**
     * Busca o ID de um calendário específico a partir do seu ano.
     *
     * @param ano O ano do calendário cujo ID está sendo procurado.
     * @return O ID inteiro do calendário se encontrado, ou -1 se nenhum calendário for encontrado para o ano especificado.
     */
    fun obterIdCalendarioPorAno(ano: Int): Int {
        val db = dataBase.readableDatabase
        var calendarioId: Int = -1
        val cursor = db.query(
            DatabaseHelper.TABLE_CALENDARIO,
            arrayOf(DatabaseHelper.COLUMN_CALENDARIO_ID),
            "${DatabaseHelper.COLUMN_CALENDARIO_ANO} = ?",
            arrayOf(ano.toString()),
            null,
            null,
            null
        )
        cursor?.use {
            if (it.moveToFirst()) {
                calendarioId = it.getInt(0)
            }
        }
        cursor.close()
        db.close()
        return calendarioId
    }

    /**
     * Insere um novo calendário no banco de dados com base no ano fornecido.
     *
     * @param ano O ano para o qual o novo calendário será criado.
     * @return O ID da nova linha inserida no banco de dados. Se a inserção falhar, retorna -1.
     */
    fun inserirCalendario(ano: Int): Int {
        val db = dataBase.writableDatabase
        val values = ContentValues().apply {
            put(DatabaseHelper.COLUMN_CALENDARIO_ANO, ano)
        }
        val newRowId = db.insert(DatabaseHelper.TABLE_CALENDARIO, null, values)
        db.close()
        return newRowId.toInt()
    }

    /**
     * Conta o número total de Pilares existentes no banco de dados.
     *
     * @return O número total de registros na tabela de Pilares.
     */
    fun contarPilares(): Int {
        val db = dataBase.readableDatabase
        var count = 0
        val cursor = db.rawQuery("SELECT COUNT(*) FROM ${DatabaseHelper.TABLE_PILAR}", null)
        cursor?.use {
            if (it.moveToFirst()) {
                count = it.getInt(0)
            }
        }
        cursor.close()
        db.close()
        return count
    }
}
