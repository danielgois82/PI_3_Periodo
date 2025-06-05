package com.example.mpi.data

sealed class FinalizacaoItem {
    data class AcaoFinalizacao(val acao: Acao) : FinalizacaoItem()
    data class AtividadeFinalizacao(val atividade: Atividade) : FinalizacaoItem()
}