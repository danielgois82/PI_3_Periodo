package com.example.mpi.ui

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.mpi.data.TipoUsuario
import com.example.mpi.data.Usuario
import com.example.mpi.databinding.ActivityLoginBinding
import com.example.mpi.repository.TipoUsuarioRepository
import com.example.mpi.repository.UsuarioRepository

class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding

    var idUsuario = 0
    var nomeUsuario = ""
    var tipoUsuario = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

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
