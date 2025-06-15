package com.example.mpi.data
/**
 * Representa um calendário anual no sistema de monitoramento do Programa de Integridade.
 *
 * @property id : O identificador único do Calendário.
 * @property ano : O ano em que o calendário está relacionado.
 */
data class Calendario(
    val id: Int,
    val ano: Int
) {
    override fun toString(): String {
        return ano.toString()
    }
}