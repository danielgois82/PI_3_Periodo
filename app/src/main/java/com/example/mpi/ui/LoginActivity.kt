package com.example.mpi.ui

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.mpi.R
import com.example.mpi.databinding.ActivityLoginBinding

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding

    var idUsuario = 0
    var nomeUsuario = ""
    var tipoUsuario = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val loginButton = findViewById<Button>(R.id.btnEntrar)
        val usernameEditText = findViewById<EditText>(R.id.editEmail)
        val passwordEditText = findViewById<EditText>(R.id.editSenha)

        loginButton.setOnClickListener {
            val username = usernameEditText.text.toString()
            val password = passwordEditText.text.toString()

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

        // Fazer a lógica de acessar as tabelas de usuário e tipo de usuario aqui
        // Se o email e senha for válidos
        // preencher as 3 variáveis (idUsuario, nomeUsuario e tipoUsuario) e retornar "True"
        // Senão retornar "False"

        if (username == "analista@gmail.com") { // Remover esse "if" quando acessar o banco
            idUsuario = 1 // Pegar o id do usuário e por aqui
            nomeUsuario = "Analista 1" // Pegar o nome do usuário e por aqui
            tipoUsuario = "ANALISTA" // Pegar o tipo de usuário e por aqui
        }

        if (username == "coordenador@gmail.com") { // Remover esse "if" completo
            idUsuario = 2
            nomeUsuario = "Coordenador 1"
            tipoUsuario = "COORDENADOR"
        }

        if (username == "gestor@gmail.com") { // Remover esse "if" completo também
            idUsuario = 3
            nomeUsuario = "Gestor 1"
            tipoUsuario = "GESTOR"
        }

        // Aqui é parte de retornar "True" ou "False"
        return (username == "analista@gmail.com" && password == "1234") ||
                (username == "gestor@gmail.com" && password == "5678") ||
                (username == "coordenador@gmail.com" && password == "abcd")
    }
}
