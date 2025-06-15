package com.example.mpi.data
/**
 * Representa o percentual mensal de uma atividade
 *
 * @property id : O identificador único do percentual da atividade
 * @property mes : O mês do percentual
 * @property percentual : O percentual da atividade em decimal
 * @property idAtividade : O Id da atividade que o percentual está associado
 *
 */
data class PercentualAtividade (
    val id: Int,
    val mes: Int,
    val percentual: Double,
    val idAtividade: Int
)