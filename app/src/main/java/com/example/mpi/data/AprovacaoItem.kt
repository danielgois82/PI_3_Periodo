package com.example.mpi.data
/**
 * Representa um item genérico que necessita de aprovação dentro do sistema.
 * Esta sealed class é usado para encapsular e diferenciar entre dois tipos principais
 * de itens que podem ser submetidos a um processo de aprovação: Ações e Atividades.
 *
 * É utilizada principalmente em com.example.mpi.ui.aprovacao.AprovacaoActivity para listar
 * e processar dinamicamente os itens que aguardam validação por um responsável.
 */
sealed class AprovacaoItem {
    /**
     * Representa uma Ação específica que está aguando aprovação.
     * @property acao : O objeto [Acao] que precisa ser aprovado.
     */
    data class AcaoAprovacao(val acao: Acao) : AprovacaoItem()

    /**
     * Representa uma Atividade específica que está aguando aprovação.
     * @property atividade O objeto [Atividade] que precisa ser aprovado.
     */
    data class AtividadeAprovacao(val atividade: Atividade) : AprovacaoItem()
}