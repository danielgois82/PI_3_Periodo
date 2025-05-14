package com.example.mpi.ui

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.mpi.databinding.ActivityFinalizacaoBinding
import com.example.mpi.ui.MenuActivity

class FinalizacaoActivity : AppCompatActivity() {
    private lateinit var binding: ActivityFinalizacaoBinding

    val USUARIO_ANALISTA = "ANALISTA"
    val USUARIO_COORDENADOR = "COORDENADOR"
    val USUARIO_GESTOR = "GESTOR"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFinalizacaoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val intentExtra = intent
        val idUsuario = intentExtra.getIntExtra("idUsuario", 999999)
        val nomeUsuario = intentExtra.getStringExtra("nomeUsuario") ?: "Nome de usuário desconhecido"
        val tipoUsuario = intentExtra.getStringExtra("tipoUsuario") ?: "Tipo de usuário desconhecido"

        binding.btnVoltarInicio.setOnClickListener {
            // Comentei aqui esse trecho e depois quero falar com você sobre ele
            // val intent = Intent(this, MenuActivity::class.java)
            // intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
            // startActivity(intent)
            // finish()

            val extra = Intent(this, MenuActivity::class.java)
            extra.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
            extra.putExtra("idUsuario", idUsuario)
            extra.putExtra("nomeUsuario", nomeUsuario)
            extra.putExtra("tipoUsuario", tipoUsuario)
            startActivity(extra)
        }
    }
}
