package com.example.mpi

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class LoginActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        val loginButton = findViewById<Button>(R.id.btnEntrar)
        val usernameEditText = findViewById<EditText>(R.id.editEmail)
        val passwordEditText = findViewById<EditText>(R.id.editSenha)

        loginButton.setOnClickListener {
            val username = usernameEditText.text.toString()
            val password = passwordEditText.text.toString()

            if (validateUser(username, password)) {
                Toast.makeText(this, "Login realizado com sucesso!", Toast.LENGTH_SHORT).show()

            } else {
                Toast.makeText(this, "Credenciais inválidas", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun validateUser(username: String, password: String): Boolean {
        return (username == "analista@gmail.com" && password == "1234") ||
                (username == "gestor@gmail.com" && password == "5678") ||
                (username == "coordenador@gmail.com" && password == "abcd")
    }
}
