package com.example.mpi.repository

import android.content.Context
import com.example.mpi.data.Atividade
import com.example.mpi.data.Calendario
import com.example.mpi.data.DatabaseHelper
import com.example.mpi.data.Pilar
import com.example.mpi.repository.AcaoRepository
import com.example.mpi.repository.SubpilarRepository

class PilarRepository (context: Context) {
    private var dataBase: DatabaseHelper = DatabaseHelper(context)
    private val atividadeRepository: AtividadeRepository = AtividadeRepository.getInstance(context)
    private val acaoRepository: AcaoRepository = AcaoRepository.getInstance(context)
    private val subpilarRepository: SubpilarRepository = SubpilarRepository.getInstance(context)


    companion object {
        private lateinit var instance: PilarRepository

        fun getInstance(context: Context): PilarRepository {
            synchronized(this) {
                if (!::instance.isInitialized) {
                    instance = PilarRepository(context)
                }
            }
            return instance
        }
    }

    fun obterTodosPilares(calendario: Calendario): MutableList<Pilar> {
        val db = dataBase.readableDatabase

        val cursor = db.rawQuery(
            "SELECT * FROM pilar WHERE id_calendario = ?",
            arrayOf(calendario.id.toString())
        )

        val pilares: MutableList<Pilar> = arrayListOf()
        while (cursor.moveToNext()) {
            val id = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_PILAR_ID))
            val nome =
                cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_PILAR_NOME))
            val descricao =
                cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_PILAR_DESCRICAO))
            val dataInicio =
                cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_PILAR_DATA_INICIO))
            val dataTermino =
                cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_PILAR_DATA_TERMINO))
            val percentual =
                cursor.getDouble(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_PILAR_PERCENTUAL)) // Quero tirar
            val idCalendario =
                cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_PILAR_ID_CALENDARIO))
            val idUsuario =
                cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_PILAR_ID_USUARIO))

            val pilar = Pilar(
                id,
                nome,
                descricao,
                dataInicio,
                dataTermino,
                percentual,
                idCalendario,
                idUsuario
            )
            pilares.add(pilar)
        }

        cursor.close()
        db.close()

        return pilares
    }

    fun obterPilarPorId(calendario: Calendario, idPilar: Int): Pilar? {
        val db = dataBase.readableDatabase

        val cursor = db.rawQuery(
            "SELECT * FROM pilar WHERE id_calendario = ? AND id = ?",
            arrayOf(calendario.id.toString(), idPilar.toString())
        )

        var pilar: Pilar? = null
        if (cursor.moveToFirst()) {
            val id = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_PILAR_ID))
            val nome =
                cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_PILAR_NOME))
            val descricao =
                cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_PILAR_DESCRICAO))
            val dataInicio =
                cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_PILAR_DATA_INICIO))
            val dataTermino =
                cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_PILAR_DATA_TERMINO))
            val percentual =
                cursor.getDouble(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_PILAR_PERCENTUAL)) // Quero tirar
            val idCalendario =
                cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_PILAR_ID_CALENDARIO))
            val idUsuario =
                cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_PILAR_ID_USUARIO))

            pilar = Pilar(
                id,
                nome,
                descricao,
                dataInicio,
                dataTermino,
                percentual,
                idCalendario,
                idUsuario
            )
        }

        cursor.close()
        db.close()

        return pilar
    }

    fun obterProgressoPilar(pilar: Pilar): Double {
        var percentualTotalPilar = 0.0

        val listaSubpilar = subpilarRepository.obterTodosSubpilares(pilar)

        if (listaSubpilar.isEmpty()) {
            val listaAcoes = acaoRepository.obterTodasAcoes(pilar)
            var qtdAcoes = 0
            var somaPercentualAcoes = 0.0
            for (acao in listaAcoes) {
                somaPercentualAcoes += acaoRepository.obterPercentualTotalAcao(acao)
                qtdAcoes++
            }
            percentualTotalPilar = somaPercentualAcoes / qtdAcoes
        }

        if (listaSubpilar.isNotEmpty()) {
            var percentualTotalSubpilar = 0.0
            var qtdSubpilar = 0
            for (subpilar in listaSubpilar) {
                qtdSubpilar++
                val listaAcoes = acaoRepository.obterTodasAcoes(subpilar)
                var qtdAcoes = 0
                var somaPercentualAcoes = 0.0
                for (acao in listaAcoes) {
                    somaPercentualAcoes += acaoRepository.obterPercentualTotalAcao(acao)
                    qtdAcoes++
                }
                percentualTotalSubpilar += somaPercentualAcoes / qtdAcoes
            }
            percentualTotalPilar = percentualTotalSubpilar / qtdSubpilar
        }

        return percentualTotalPilar
    }

    fun obterPercentualMes(pilar: Pilar, mes: Int): Double {
        var percentualMesPilar = 0.0

        val listaSubpilar = subpilarRepository.obterTodosSubpilares(pilar)

        if (listaSubpilar.isEmpty()) {
            val listaAcoes = acaoRepository.obterTodasAcoes(pilar)
            var qtdAcoes = 0
            var somaPercentualAcoes = 0.0
            for (acao in listaAcoes) {
                val listarAtividade = atividadeRepository.obterTodasAtividades(acao)
                somaPercentualAcoes += acaoRepository.obterPercentualMes(listarAtividade, mes)
                qtdAcoes++
            }
            percentualMesPilar = somaPercentualAcoes / qtdAcoes
        }

        if (listaSubpilar.isNotEmpty()) {
            var percentualTotalSubpilar = 0.0
            var qtdSubpilar = 0
            for (subpilar in listaSubpilar) {
                qtdSubpilar++
                val listaAcoes = acaoRepository.obterTodasAcoes(subpilar)
                var qtdAcoes = 0
                var somaPercentualAcoes = 0.0
                for (acao in listaAcoes) {
                    val listarAtividade = atividadeRepository.obterTodasAtividades(acao)
                    somaPercentualAcoes += acaoRepository.obterPercentualMes(listarAtividade, mes)
                    qtdAcoes++
                }
                percentualTotalSubpilar += somaPercentualAcoes / qtdAcoes
            }
            percentualMesPilar = percentualTotalSubpilar / qtdSubpilar
        }

        return percentualMesPilar
    }

    fun validarExclusaoPilar(pilar: Pilar): Boolean {
        val todosSubpilares = subpilarRepository.obterTodosSubpilares()
        val todasAcoes = acaoRepository.obterTodasAcoes()
        var temSubpilarAssociado = false
        var temAcaoAssociada = false

        for (subpilar in todosSubpilares) {
            if (subpilar.idPilar == pilar.id) {
                temSubpilarAssociado = true
                break
            }
        }
        for (acao in todasAcoes) {
            if (acao.idPilar == pilar.id) {
                temAcaoAssociada = true
                break
            }
        }

        if (temSubpilarAssociado == true || temAcaoAssociada == true){
            return false
        }else{
            return true
        }

    }

    fun obterOrcamentoTotalPilar(pilar: Pilar): Double {
        val db = dataBase.readableDatabase
        var orcamentoTotal = 0.0

        val query = """
        SELECT SUM(${DatabaseHelper.TABLE_ATIVIDADE}.${DatabaseHelper.COLUMN_ATIVIDADE_ORCAMENTO})
        FROM ${DatabaseHelper.TABLE_ATIVIDADE}
        INNER JOIN ${DatabaseHelper.TABLE_ACAO}
            ON ${DatabaseHelper.TABLE_ATIVIDADE}.${DatabaseHelper.COLUMN_ATIVIDADE_ID_ACAO} = ${DatabaseHelper.TABLE_ACAO}.${DatabaseHelper.COLUMN_ACAO_ID}
        LEFT JOIN ${DatabaseHelper.TABLE_SUBPILAR}
            ON ${DatabaseHelper.TABLE_ACAO}.${DatabaseHelper.COLUMN_ACAO_ID_SUBPILAR} = ${DatabaseHelper.TABLE_SUBPILAR}.${DatabaseHelper.COLUMN_SUBPILAR_ID}
        WHERE
            (${DatabaseHelper.TABLE_ACAO}.${DatabaseHelper.COLUMN_ACAO_ID_PILAR} = ?
            OR ${DatabaseHelper.TABLE_SUBPILAR}.${DatabaseHelper.COLUMN_SUBPILAR_ID_PILAR} = ?);
    """.trimIndent()

        val selectionArgs = arrayOf(pilar.id.toString(), pilar.id.toString())

        val cursor = db.rawQuery(query, selectionArgs)

        if (cursor.moveToFirst()) {
            orcamentoTotal = cursor.getDouble(0)
        }

        cursor.close()
        db.close()
        return orcamentoTotal
    }

}