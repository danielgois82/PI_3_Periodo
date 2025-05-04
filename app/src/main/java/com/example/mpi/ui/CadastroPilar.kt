package com.example.mpi.ui

import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.mpi.databinding.ActivityCadastroPilarBinding
import com.example.mpi.data.PilarDbHelper
import com.example.mpi.data.PilarContract
import com.example.mpi.R

class cadastroPilar : AppCompatActivity() {

    private lateinit var binding: ActivityCadastroPilarBinding
    private lateinit var binding: PilarDbHelper


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_cadastro_pilar)

        // Botão confirmar
        val confirmarCadastro: Button = findViewById(R.id.confirmarCadastro)
        confirmarCadastro.setOnClickListener{

        }
        // Botão cancelar
        val cancelarCadastro: Button = findViewById(R.id.cancelarCadastro)
        cancelarCadastro.setOnClickListener {
            finish()
        }
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }
}