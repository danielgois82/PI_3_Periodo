package com.example.mpi.repository

import android.content.Context
import android.util.Log
import com.example.mpi.data.Acao
import com.example.mpi.data.Atividade
import com.example.mpi.data.DatabaseHelper
import com.example.mpi.data.Pilar
import com.example.mpi.data.Subpilar
import com.example.mpi.repository.SubpilarRepository

class AcaoRepository(context: Context) {

    private var dataBase: DatabaseHelper = DatabaseHelper(context)
    private val subpilarRepository: SubpilarRepository = SubpilarRepository.getInstance(context)
    private val atividadeRepository: AtividadeRepository = AtividadeRepository.getInstance(context)

    companion object {
        private lateinit var instance: AcaoRepository

        fun getInstance(context: Context): AcaoRepository {
            synchronized(this) {
                if (!::instance.isInitialized) {
                    instance = AcaoRepository(context)
                }
            }
            return instance
        }
    }

    fun obterTodasAcoes(pilar: Pilar): MutableList<Acao> {
        val db = dataBase.readableDatabase

        val cursor = db.rawQuery("SELECT * FROM acao WHERE id_pilar = ?", arrayOf(pilar.id.toString()))

        var acoes: MutableList<Acao> = arrayListOf()
        while (cursor.moveToNext()) {
            val id = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_ACAO_ID))
            val nome = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_ACAO_NOME))
            val descricao = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_ACAO_DESCRICAO))
            val dataInicio = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_ACAO_DATA_INICIO))
            val dataTermino = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_ACAO_DATA_TERMINO))
            val responsavel = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_ACAO_RESPONSAVEL))
            val aprovado = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_ACAO_IS_APROVADO)) != 0
            val finalizado = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_ACAO_IS_FINALIZADO)) != 0
            val idPilar = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_ACAO_ID_PILAR))
            val idSubpilar = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_ACAO_ID_SUBPILAR))
            val idUsuario = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_ACAO_ID_USUARIO))

            val acao = Acao( id, nome, descricao, dataInicio, dataTermino, responsavel, aprovado, finalizado, idPilar, idSubpilar, idUsuario)
            acoes.add(acao)
        }
        cursor.close()
        return acoes
    }

    fun obterTodasAcoes(subpilar: Subpilar): MutableList<Acao> {
        val db = dataBase.readableDatabase
      
        val cursor = db.rawQuery("SELECT * FROM acao WHERE id_subpilar = ?", arrayOf(subpilar.id.toString()))
        
        var acoes: MutableList<Acao> = arrayListOf()
        while (cursor.moveToNext()) {
            val id = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_ACAO_ID))
            val nome = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_ACAO_NOME))
            val dataInicio = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_ACAO_DATA_INICIO))
            val dataTermino = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_ACAO_DATA_TERMINO))
            val responsavel = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_ACAO_RESPONSAVEL))
            val aprovado = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_ACAO_IS_APROVADO)) != 0
            val finalizado = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_ACAO_IS_FINALIZADO)) != 0
            val descricao = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_ACAO_DESCRICAO))
            val idPilar = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_ACAO_ID_PILAR))
            val idSubpilar = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_ACAO_ID_SUBPILAR))
            val idUsuario = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_ACAO_ID_USUARIO))

            val acao = Acao(id, nome, descricao, dataInicio, dataTermino, responsavel, aprovado, finalizado, idPilar, idSubpilar, idUsuario )
            acoes.add(acao)
        }
        cursor.close()
        return acoes
    }
    
    // FUnção para consultar ações sem filtro para a listagem de ação em AcaoActivity
    fun obterTodasAcoes(): List<Acao> {
        val db = dataBase.readableDatabase
        val acoes: MutableList<Acao> = arrayListOf()
        val cursor = db.rawQuery("SELECT * FROM ${DatabaseHelper.TABLE_ACAO}", null)

        while (cursor.moveToNext()) {
            val id = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_ACAO_ID))
            val nome = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_ACAO_NOME))
            val descricao = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_ACAO_DESCRICAO))
            val dataInicio = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_ACAO_DATA_INICIO))
            val dataTermino = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_ACAO_DATA_TERMINO))
            val responsavel = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_ACAO_RESPONSAVEL))
            val aprovado = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_ACAO_IS_APROVADO)) != 0
            val finalizado = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_ACAO_IS_FINALIZADO)) != 0
            val idPilar = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_ACAO_ID_PILAR))
            val idSubpilar = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_ACAO_ID_SUBPILAR))
            val idUsuario = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_ACAO_ID_USUARIO))

            val acao = Acao(id, nome, descricao, dataInicio, dataTermino, responsavel, aprovado, finalizado, idPilar, idSubpilar, idUsuario)
            acoes.add(acao)
        }
        cursor.close()
        return acoes
    }

    // Funções exclusivas para FILTRAGEM em AtividadeActivity
    fun obterAcoesPorPilar(pilar: Pilar): List<Acao> {
        val db = dataBase.readableDatabase
        val acoesDoPilar: MutableList<Acao> = arrayListOf()

        val subpilaresDoPilar = subpilarRepository.obterTodosSubpilares(pilar)

        for (subpilar in subpilaresDoPilar) {
            val acoesDoSubpilar = obterAcoesPorSubpilar(subpilar)
            acoesDoPilar.addAll(acoesDoSubpilar)
        }

        val cursorPilarDireto = db.rawQuery(
            "SELECT * FROM ${DatabaseHelper.TABLE_ACAO} WHERE ${DatabaseHelper.COLUMN_ACAO_ID_PILAR} = ? AND ${DatabaseHelper.COLUMN_ACAO_ID_SUBPILAR} IS NULL",
            arrayOf(pilar.id.toString())
        )
        while (cursorPilarDireto.moveToNext()) {
            val id = cursorPilarDireto.getInt(cursorPilarDireto.getColumnIndexOrThrow(DatabaseHelper.COLUMN_ACAO_ID))
            val nome = cursorPilarDireto.getString(cursorPilarDireto.getColumnIndexOrThrow(DatabaseHelper.COLUMN_ACAO_NOME))
            val dataInicio = cursorPilarDireto.getString(cursorPilarDireto.getColumnIndexOrThrow(DatabaseHelper.COLUMN_ACAO_DATA_INICIO))
            val dataTermino = cursorPilarDireto.getString(cursorPilarDireto.getColumnIndexOrThrow(DatabaseHelper.COLUMN_ACAO_DATA_TERMINO))
            val responsavel = cursorPilarDireto.getInt(cursorPilarDireto.getColumnIndexOrThrow(DatabaseHelper.COLUMN_ACAO_RESPONSAVEL))
            val aprovado = cursorPilarDireto.getInt(cursorPilarDireto.getColumnIndexOrThrow(DatabaseHelper.COLUMN_ACAO_IS_APROVADO)) != 0
            val finalizado = cursorPilarDireto.getInt(cursorPilarDireto.getColumnIndexOrThrow(DatabaseHelper.COLUMN_ACAO_IS_FINALIZADO)) != 0
            val descricao = cursorPilarDireto.getString(cursorPilarDireto.getColumnIndexOrThrow(DatabaseHelper.COLUMN_ACAO_DESCRICAO))
            val idPilar = cursorPilarDireto.getInt(cursorPilarDireto.getColumnIndexOrThrow(DatabaseHelper.COLUMN_ACAO_ID_PILAR))
            val idSubpilar = cursorPilarDireto.getInt(cursorPilarDireto.getColumnIndexOrThrow(DatabaseHelper.COLUMN_ACAO_ID_SUBPILAR))
            val idUsuario = cursorPilarDireto.getInt(cursorPilarDireto.getColumnIndexOrThrow(DatabaseHelper.COLUMN_ACAO_ID_USUARIO))

            val acao = Acao(id, nome, descricao, dataInicio, dataTermino, responsavel, aprovado, finalizado, idPilar, idSubpilar, idUsuario )
            if (!acoesDoPilar.contains(acao)) {
                acoesDoPilar.add(acao)
            }
        }
        cursorPilarDireto.close()
        return acoesDoPilar.distinctBy { it.id }
    }


    // Funções exclusivas para FILTRAGEM em AtividadeActivity
    fun obterAcoesPorSubpilar(subpilar: Subpilar): List<Acao> {
        val db = dataBase.readableDatabase
        val acoes: MutableList<Acao> = arrayListOf()

        val cursor = db.rawQuery("SELECT * FROM ${DatabaseHelper.TABLE_ACAO} WHERE ${DatabaseHelper.COLUMN_ACAO_ID_SUBPILAR} = ?", arrayOf(subpilar.id.toString()))

        while (cursor.moveToNext()) {
            val id = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_ACAO_ID))
            val nome = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_ACAO_NOME))
            val dataInicio = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_ACAO_DATA_INICIO))
            val dataTermino = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_ACAO_DATA_TERMINO))
            val responsavel = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_ACAO_RESPONSAVEL))
            val aprovado = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_ACAO_IS_APROVADO)) != 0
            val finalizado = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_ACAO_IS_FINALIZADO)) != 0
            val descricao = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_ACAO_DESCRICAO))
            val idPilar = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_ACAO_ID_PILAR))
            val idSubpilar = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_ACAO_ID_SUBPILAR))
            val idUsuario = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_ACAO_ID_USUARIO))

            val acao = Acao(id, nome, descricao, dataInicio, dataTermino, responsavel, aprovado, finalizado, idPilar, idSubpilar, idUsuario )
            acoes.add(acao)
        }

        cursor.close()
        return acoes
    }

    fun obterAcoesNaoFinalizadasPorUsuario(idUsuario: Int): List<Acao> {
        val db = dataBase.readableDatabase
        val acoes: MutableList<Acao> = arrayListOf()
        val cursor = db.rawQuery(
            "SELECT * FROM ${DatabaseHelper.TABLE_ACAO} WHERE ${DatabaseHelper.COLUMN_ACAO_IS_FINALIZADO} = 0 AND ${DatabaseHelper.COLUMN_ACAO_ID_USUARIO} = ?",
            arrayOf(idUsuario.toString())
        )

        while (cursor.moveToNext()) {
            val id = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_ACAO_ID))
            val nome = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_ACAO_NOME))
            val dataInicio = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_ACAO_DATA_INICIO))
            val dataTermino = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_ACAO_DATA_TERMINO))
            val responsavel = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_ACAO_RESPONSAVEL))
            val aprovado = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_ACAO_IS_APROVADO)) != 0
            val finalizado = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_ACAO_IS_FINALIZADO)) != 0
            val descricao = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_ACAO_DESCRICAO))
            val idPilar = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_ACAO_ID_PILAR))
            val idSubpilar = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_ACAO_ID_SUBPILAR))
            val idUsuarioAcao = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_ACAO_ID_USUARIO))

            val acao = Acao(id, nome, descricao, dataInicio, dataTermino, responsavel, aprovado, finalizado, idPilar, idSubpilar, idUsuarioAcao )

            acoes.add(acao)
        }

        cursor.close()
        return acoes
    }

    fun obterQuantidadeAcoesFinalizadas(): Int {
        val db = dataBase.readableDatabase
        var quantidadeAcoesFinalizadas = 0
        val cursor = db.rawQuery("SELECT COUNT(*) AS QTDE FROM ${DatabaseHelper.TABLE_ACAO} WHERE ${DatabaseHelper.COLUMN_ACAO_IS_FINALIZADO} = 1", null)

        if (cursor.moveToNext()) {
            quantidadeAcoesFinalizadas = cursor.getInt(cursor.getColumnIndexOrThrow("QTDE"))
        }

        cursor.close()
        return quantidadeAcoesFinalizadas
    }

    fun obterQuantidadeAcoesEmAndamento(): Int {
        val db = dataBase.readableDatabase
        var quantidadeAcoesEmAndamento = 0
        val cursor = db.rawQuery("SELECT COUNT(*) AS ANDAMENTO FROM ${DatabaseHelper.TABLE_ACAO} WHERE date(substr(${DatabaseHelper.COLUMN_ACAO_DATA_TERMINO}, 7, 4) || '-' || substr(${DatabaseHelper.COLUMN_ACAO_DATA_TERMINO}, 4, 2) || '-' || substr(${DatabaseHelper.COLUMN_ACAO_DATA_TERMINO}, 1, 2)) > date('now') AND ${DatabaseHelper.COLUMN_ACAO_IS_FINALIZADO} = 0", null)

        if (cursor.moveToNext()) {
            quantidadeAcoesEmAndamento = cursor.getInt(cursor.getColumnIndexOrThrow("ANDAMENTO"))
        }

        cursor.close()
        return quantidadeAcoesEmAndamento
    }

    fun obterQuantidadeAcoesAtrasadas(): Int {
        val db = dataBase.readableDatabase
        var quantidadeAcoesAtrasadas = 0
        val cursor = db.rawQuery("SELECT COUNT(*) AS ATRASADA FROM ${DatabaseHelper.TABLE_ACAO} WHERE date(substr(${DatabaseHelper.COLUMN_ACAO_DATA_TERMINO}, 7, 4) || '-' || substr(${DatabaseHelper.COLUMN_ACAO_DATA_TERMINO}, 4, 2) || '-' || substr(${DatabaseHelper.COLUMN_ACAO_DATA_TERMINO}, 1, 2)) < date('now') AND ${DatabaseHelper.COLUMN_ACAO_IS_FINALIZADO} = 0", null)

        if (cursor.moveToNext()) {
            quantidadeAcoesAtrasadas = cursor.getInt(cursor.getColumnIndexOrThrow("ATRASADA"))
        }

        cursor.close()
        return quantidadeAcoesAtrasadas
    }

    fun obterPercentualTotalAcao(acao: Acao) : Double {
        val listaAtividade = atividadeRepository.obterTodasAtividades(acao)
        var divisor = 0
        var somaPercentualAtividade = 0.0
        for (atividade in listaAtividade) {
            somaPercentualAtividade += atividadeRepository.obterPercentualTotalAtividade(atividade)
            divisor++
        }
        val percentualTotalAcao = somaPercentualAtividade / divisor

        return percentualTotalAcao
    }

    fun obterPercentualMes(atividades: List<Atividade>, mes: Int): Double {
        val percentualTotal: Double
        var somaPercentual = 0.0
        var qtdAtividades = 0
        for (atividade in atividades) {
            val percentualMes = atividadeRepository.obterPercentualMes(atividade, mes)
            somaPercentual += percentualMes
            qtdAtividades++
        }
        percentualTotal = somaPercentual / qtdAtividades

        return percentualTotal
    }

    fun obterOrcamentoAcao(atividades: List<Atividade>): Double {
        var orcamentoTotal = 0.0
        for (atividade in atividades) {
            orcamentoTotal += atividade.orcamento
        }

        return orcamentoTotal
    }
}