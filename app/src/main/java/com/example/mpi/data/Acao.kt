package com.example.mpi.data
/**
 * Representa uma Ação no sistema de monitoramento do Programa de Integridade, que pode estar associada a um Pilar ou Subpilar.
 *
 * @property id : O identificador único da ação.
 * @property nome : O nome descritivo da ação.
 * @property descricao : Uma descrição detalhada da ação.
 * @property dataInicio : A data de início prevista para a ação (formato de texto, "YYYY-MM-DD").
 * @property dataTermino : A data de término prevista para a ação (formato de texto, "YYYY-MM-DD").
 * @property responsavel : O ID do usuário responsável pela execução desta ação.
 * @property aprovado : Indica se a ação foi aprovada (true) ou não (false).
 * @property finalizado : Indica se a ação foi concluída (true) ou não (false).
 * @property idPilar : O ID do Pilar ao qual esta ação está diretamente associada (pode ser 0 se estiver em um Subpilar).
 * @property idSubpilar : O ID do Subpilar ao qual esta ação está associada (pode ser 0 se estiver em um Pilar direto).
 * @property idUsuario : O ID do usuário que criou esta ação.
 */
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