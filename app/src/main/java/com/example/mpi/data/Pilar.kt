package com.example.mpi.data

data class Pilar (
    val id: Long,
    val nome: String,
    val descricao: String,
    val responsavel: Int,
    val dataInicio: String,
    val dataTermino: String,
    val aprovado: Boolean,
    val percentual: Double
)