package com.example.mpi.data

data class Calendario(
    val id: Int,
    val ano: Int
) {
    override fun toString(): String {
        return ano.toString()
    }
}