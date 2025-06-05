package com.example.mpi.data

data class Pilar (
    val id: Int,
    val nome: String,
    val descricao: String,
    val dataInicio: String,
    val dataTermino: String,
    val percentual: Double, // Quero tirar
    val idCalendario: Int,
    val idUsuario: Int
) {
    override fun toString(): String {
        return nome
    }
}
