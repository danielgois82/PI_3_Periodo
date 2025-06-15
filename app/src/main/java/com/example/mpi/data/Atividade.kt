package com.example.mpi.data
/**
 * Representa uma Atividade no sistema de monitoramento do Programa de Integridade, que pode estar associada a uma Ação.
 *
 * @property id : O identificador único da atividade.
 * @property nome : O nome descritivo da atividade.
 * @property descricao : Uma descrição detalhada da atividade.
 * @property dataInicio : A data de início prevista para a atividade (formato de texto, "YYYY-MM-DD").
 * @property dataTermino : A data de término prevista para a atividade (formato de texto, "YYYY-MM-DD").
 * @property responsavel : O ID do usuário responsável pela execução desta atividade.
 * @property aprovado : Indica se a atividade foi aprovada (true) ou não (false).
 * @property finalizado : Indica se a atividade foi concluída (true) ou não (false).
 * @property idAcao : O ID da Ação ao qual esta atividade está diretamente associada.
 * @property orcamento : A quantia, em reais, do orçamento que será atribuido a atividade.
 * @property idUsuario : O ID do usuário que criou esta atividade.
 */
data class Atividade(
    val id: Int,
    val nome: String,
    val descricao: String,
    val dataInicio: String,
    val dataTermino: String,
    val responsavel: Int,
    val aprovado: Boolean,
    val finalizado: Boolean,
    val orcamento: Double,
    val idAcao: Int,
    val idUsuario: Int
) {
    override fun toString(): String {
        return nome
    }
}