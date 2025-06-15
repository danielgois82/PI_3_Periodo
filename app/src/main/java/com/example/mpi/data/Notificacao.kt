package com.example.mpi.data
/**
 * Representa uma notificação de alertas de prazos críticos.
 *
 * @property id : O identificador único da Notificação.
 * @property isVisualizado : Indica se a notificação foi visualizada (true) ou não (false).
 * @property titulo : Título da notificação.
 * @property mensagem : Texto descritivo sobre a notificação.
 * @property idUsuario : Id do usuário que receberá a notificação.
 * @property idItem : Id da ação ou da atividade que a notificação está associada.
 * @property tipoItem : Tipo do item que a notificação está associada, sendo ação ou atividade.
 */
data class Notificacao(
    val id: Int,
    val isVisualizado: Boolean,
    val titulo: String,
    val mensagem: String,
    val idUsuario: Int,
    val idItem: Int?,
    val tipoItem: String?
)
