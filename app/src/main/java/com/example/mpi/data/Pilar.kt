package com.example.mpi.data
/**
 * Representa um Pilar no sistema de monitoramento do Programa de Integridade.
 *
 * @property id : O identificador único do Pilar.
 * @property nome : O nome descritivo do Pilar.
 * @property descricao : Uma descrição detalhada do Pilar.
 * @property dataInicio : A data de início prevista para o Pilar (formato de texto, "YYYY-MM-DD").
 * @property dataTermino : A data de término prevista para o Pilar (formato de texto, "YYYY-MM-DD").
 * @property percentual : O percentual de conclusão do Pilar.
 * @property idCalendario : O ID do Calendario ao qual o Pilar está associado.
 * @property idUsuario : O ID do usuário que criou o Pilar.
 */
data class Pilar (
    val id: Int,
    val nome: String,
    val descricao: String,
    val dataInicio: String,
    val dataTermino: String,
    val percentual: Double,
    val idCalendario: Int,
    val idUsuario: Int
) {
    override fun toString(): String {
        return nome
    }
}
