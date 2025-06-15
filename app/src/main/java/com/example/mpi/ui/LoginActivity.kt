package com.example.mpi.ui

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.example.mpi.data.TipoUsuario
import com.example.mpi.data.Usuario
import com.example.mpi.databinding.ActivityLoginBinding
import com.example.mpi.repository.TipoUsuarioRepository
import com.example.mpi.repository.UsuarioRepository

/**
 * [LoginActivity] é a Activity responsável por gerenciar o processo de autenticação
 * do usuário no aplicativo.
 *
 * Esta tela permite que os usuários insiram suas credenciais (e-mail e senha)
 * para acessar as funcionalidades do sistema. Após a validação bem-sucedida,
 * redireciona o usuário para a [MenuActivity], passando suas informações (ID, nome, tipo de usuário).
 */
class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding

    var idUsuario = 0
    var nomeUsuario = ""
    var tipoUsuario = ""

    /**
     * Chamado quando a Activity é criada pela primeira vez.
     *
     * Inicializa a interface do usuário usando View Binding e configura o listener
     * para o botão de "Entrar", que aciona o processo de validação do usuário.
     *
     * @param savedInstanceState Se não for nulo, esta Activity está sendo recriada
     * a partir de um estado salvo anteriormente.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnEntrar.setOnClickListener {
            val username = binding.editEmail.text.toString()
            val password = binding.editSenha.text.toString()

            if (validateUser(username, password)) {
                val extra = Intent(this, MenuActivity::class.java)
                extra.putExtra("idUsuario", idUsuario)
                extra.putExtra("nomeUsuario", nomeUsuario)
                extra.putExtra("tipoUsuario", tipoUsuario)
                startActivity(extra)
            } else {
                Toast.makeText(this, "Credenciais inválidas", Toast.LENGTH_SHORT).show()
            }
        }
    }

    /**
     * Valida as credenciais do usuário (e-mail e senha) consultando o banco de dados.
     *
     * Se um usuário com as credenciais fornecidas for encontrado, as informações do usuário
     * (ID, nome e tipo de usuário/cargo) são armazenadas nas variáveis da Activity.
     *
     * @param username O e-mail do usuário.
     * @param password A senha do usuário.
     * @return `true` se as credenciais forem válidas e o usuário for encontrado, `false` caso contrário.
     */
    private fun validateUser(username: String, password: String): Boolean {
        val usuarioRepository = UsuarioRepository(this)
        val usuario: Usuario? = usuarioRepository.obterUsuarioPorEmailESenha(username, password)

        if (usuario != null) {
            idUsuario = usuario.id
            nomeUsuario = usuario.nome

            val tipoUsuarioRepository = TipoUsuarioRepository(this)
            val tipo: TipoUsuario? = tipoUsuarioRepository.obterTipoUsuarioPorId(usuario.idTipoUsuario)

            if (tipo != null) {
                tipoUsuario = tipo.cargo
            }
            return true
        } else {
            return false
        }
    }
}
