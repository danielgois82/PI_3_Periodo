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
    val id_pilar: Int,
    val id_subpilar: Int,
    val id_usuario: Int
)