package com.example.mpi.data

data class Notificacao(
    val id: Int,
    val isVisualizado: Boolean,
    val titulo: String,
    val mensagem: String,
    val idUsuario: Int,
    val idItem: Int?,
    val tipoItem: String?
)
