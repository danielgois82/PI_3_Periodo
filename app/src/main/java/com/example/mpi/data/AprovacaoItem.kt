package com.example.mpi.data

sealed class AprovacaoItem {
    data class AcaoAprovacao(val acao: Acao) : AprovacaoItem()
    data class AtividadeAprovacao(val atividade: Atividade) : AprovacaoItem()
}