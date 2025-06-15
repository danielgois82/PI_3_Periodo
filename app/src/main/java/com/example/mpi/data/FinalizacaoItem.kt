package com.example.mpi.data
/**
 * Representa um item genérico que será finalizado dentro do sistema.
 * Esta sealed class é usado para encapsular e diferenciar entre dois tipos principais
 * de itens que podem ser submetidos a um processo de finalização: Ações e Atividades.
 *
 * É utilizada principalmente em com.example.mpi.ui.finalizacao.FinalizacaoActivity para listar
 * e processar dinamicamente os itens que aguardam finalização por um responsável.
 */
sealed class FinalizacaoItem {
    /**
     * Representa uma Ação específica que está pronta para a finalização.
     * @property acao : O objeto [Acao] que precisa ser finalizada.
     */
    data class AcaoFinalizacao(val acao: Acao) : FinalizacaoItem()

    /**
     * Representa uma Atividade específica que está pronta para a finalização.
     * @property atividade : O objeto [Atividade] que está pronta para a finalização.
     */
    data class AtividadeFinalizacao(val atividade: Atividade) : FinalizacaoItem()
}