package com.example.mpi.services

import android.content.Context
import com.example.mpi.data.Acao
import com.example.mpi.data.Atividade
import com.example.mpi.data.Notificacao
import com.example.mpi.repository.AcaoRepository
import com.example.mpi.repository.AtividadeRepository
import com.example.mpi.repository.NotificacaoRepository
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class NotificacaoService(private val context: Context) {

    private val acaoRepository: AcaoRepository by lazy { AcaoRepository.getInstance(context) }
    private val atividadeRepository: AtividadeRepository by lazy { AtividadeRepository.getInstance(context) }
    private val notificacaoRepository: NotificacaoRepository by lazy { NotificacaoRepository.getInstance(context) }

    private val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())

    companion object {
        const val TIPO_ITEM_ACAO = "ACAO"
        const val TIPO_ITEM_ATIVIDADE = "ATIVIDADE"

        @Volatile
        private var INSTANCE: NotificacaoService? = null

        fun getInstance(context: Context): NotificacaoService {
            return INSTANCE ?: synchronized(this) {
                val instance = NotificacaoService(context)
                INSTANCE = instance
                instance
            }
        }
    }

    // Método principal para verificar e gerar notificações
    fun verificarEGerarNotificacoes(idUsuarioLogado: Int) {
        val acoes = acaoRepository.obterAcoesNaoFinalizadasPorUsuario(idUsuarioLogado)
        val atividades = atividadeRepository.obterAtividadesNaoFinalizadasPorUsuario(idUsuarioLogado)

        acoes.forEach { acao ->
            processarItem(acao.id, acao.nome, acao.dataTermino, TIPO_ITEM_ACAO, idUsuarioLogado, acao.finalizado)
        }

        atividades.forEach { atividade ->
            processarItem(atividade.id, atividade.nome, atividade.dataTermino, TIPO_ITEM_ATIVIDADE, idUsuarioLogado, atividade.finalizado)
        }
    }

    private fun processarItem(
        itemId: Int,
        itemNome: String,
        dataTerminoStr: String,
        tipoItem: String,
        idUsuario: Int,
        isFinalizado: Boolean
    ) {
        if (isFinalizado) {
            return
        }

        val dataTermino: Date
        try {
            dataTermino = dateFormat.parse(dataTerminoStr) ?: return
        } catch (e: ParseException) {
            e.printStackTrace()
            return
        }

        val hoje = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }.time

        if (dataTermino.before(hoje)) {
            gerarNotificacaoParaItemVencido(itemId, itemNome, tipoItem, idUsuario)
        } else {
            val diasRestantes = calcularDiasEntreDatas(hoje, dataTermino)
            gerarNotificacaoParaPrazoProximo(itemId, itemNome, tipoItem, idUsuario, diasRestantes)
        }
    }

    private fun calcularDiasEntreDatas(dataInicial: Date, dataFinal: Date): Long {
        val diff = dataFinal.time - dataInicial.time
        return diff / (1000 * 60 * 60 * 24)
    }

    private fun gerarNotificacaoParaPrazoProximo(
        itemId: Int,
        itemNome: String,
        tipoItem: String,
        idUsuario: Int,
        diasRestantes: Long
    ) {
        val titulo: String
        val mensagem: String
        var deveGerar = false

        when (diasRestantes.toInt()) {
            30 -> {
                titulo = "Prazo Próximo!"
                mensagem = "Faltam 30 dias para o vencimento do prazo da ${tipoItem.lowercase()} \"$itemNome\"."
                deveGerar = true
            }
            15 -> {
                titulo = "Atenção ao Prazo!"
                mensagem = "Faltam 15 dias para o vencimento do prazo da ${tipoItem.lowercase()} \"$itemNome\"."
                deveGerar = true
            }
            7 -> {
                titulo = "Últimos Dias!"
                mensagem = "Faltam 7 dias para o vencimento do prazo da ${tipoItem.lowercase()} \"$itemNome\"."
                deveGerar = true
            }
            else -> {
                return
            }
        }

        if (deveGerar) {
            val ultimaNotificacao = notificacaoRepository.obterUltimaNotificacaoPorItemETipo(itemId, tipoItem, idUsuario)

            if (ultimaNotificacao == null || ultimaNotificacao.mensagem != mensagem) {
                val novaNotificacao = Notificacao(
                    id = 0,
                    isVisualizado = false,
                    titulo = titulo,
                    mensagem = mensagem,
                    idUsuario = idUsuario,
                    idItem = itemId,
                    tipoItem = tipoItem
                )
                notificacaoRepository.inserirNotificacao(novaNotificacao)
            }
        }
    }

    private fun gerarNotificacaoParaItemVencido(
        itemId: Int,
        itemNome: String,
        tipoItem: String,
        idUsuario: Int
    ) {
        val titulo = "PRAZO VENCIDO!"
        val mensagem = "O prazo da ${tipoItem.lowercase()} \"$itemNome\" VENCEU!"

        val ultimaNotificacao = notificacaoRepository.obterUltimaNotificacaoPorItemETipo(itemId, tipoItem, idUsuario)

        if (ultimaNotificacao == null || ultimaNotificacao.isVisualizado) {
            val novaNotificacao = Notificacao(
                id = 0,
                isVisualizado = false,
                titulo = titulo,
                mensagem = mensagem,
                idUsuario = idUsuario,
                idItem = itemId,
                tipoItem = tipoItem
            )
            notificacaoRepository.inserirNotificacao(novaNotificacao)
        }

    }
}