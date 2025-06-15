package com.example.mpi.data
/**
 * Representa um Subpilar no sistema de monitoramento do Programa de Integridade.
 *
 * @property id : O identificador único do Subpilar.
 * @property nome : O nome descritivo do Supilar.
 * @property descricao : Uma descrição detalhada do Subpilar.
 * @property dataInicio : A data de início prevista para o Subpilar (formato de texto, "YYYY-MM-DD").
 * @property dataTermino : A data de término prevista para o Subpilar (formato de texto, "YYYY-MM-DD").
 * @property idPilar : O ID do Pilar ao qual o subpilar está diretamente associado.
 * @property idUsuario : O ID do usuário que criou o Pilar.
 */
data class Subpilar(
    val id: Int,
    val nome: String,
    val descricao: String,
    val dataInicio: String,
    val dataTermino: String,
    val idPilar: Int,
    val idUsuario: Int
) {
    override fun toString(): String {
        return nome
    }
}