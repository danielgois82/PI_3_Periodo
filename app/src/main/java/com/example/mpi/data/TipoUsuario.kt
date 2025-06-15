package com.example.mpi.data
/**
 * Representa os tipos de Uusários do sistema.
 * É utilizado para o controle de acesso.
 *
 * @property id : O identificador único do tipo de usuário.
 * @property cargo : O cargo do usuário dentro do sistema.
 */
data class TipoUsuario(
    val id: Int,
    val cargo: String
)
