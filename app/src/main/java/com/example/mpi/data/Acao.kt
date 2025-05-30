package com.example.mpi.data

data class Acao(
    val id: Int,
    val nome: String,
    val descricao: String,
    val dataInicio: String,
    val dataTermino: String,
    val responsavel: Int,
    val aprovado: Boolean,
    val finalizado: Boolean,
    val idPilar: Int,
    val idSubpilar: Int,
    val idUsuario: Int
) {
    override fun toString(): String {
        return nome
    }
}