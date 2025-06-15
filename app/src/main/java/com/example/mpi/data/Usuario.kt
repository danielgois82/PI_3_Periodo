package com.example.mpi.data
/**
 * Representa um Usuário no sistema de monitoramento do Programa de Integridade.
 *
 * @property id : O identificador único do Usuário.
 * @property nome : O nome usuário.
 * @property email : O email do usuário, que será usado para o login no sistema.
 * @property senha : A senha utilizada pelo usuário para fazer login no sistema.
 * @property idTipoUsuario : O ID do tipo de usuário que está associado ao usuário.
 */
data class Usuario(
    val id: Int,
    val nome: String,
    val email: String,
    val senha: String,
    val idTipoUsuario: Int
)
