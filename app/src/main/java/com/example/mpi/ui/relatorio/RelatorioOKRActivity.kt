package com.example.mpi.ui.relatorio

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.mpi.R
import com.example.mpi.repository.CalendarioRepository
import com.example.mpi.repository.PilarRepository
import androidx.activity.OnBackPressedCallback
import com.example.mpi.data.Pilar
import com.example.mpi.databinding.ActivityRelatorioOkractivityBinding

class RelatorioOKRActivity : AppCompatActivity() {
    private lateinit var binding: ActivityRelatorioOkractivityBinding

    val USUARIO_ANALISTA = "ANALISTA"
    val USUARIO_COORDENADOR = "COORDENADOR"
    val USUARIO_GESTOR = "GESTOR"

    val calendarioRepository = CalendarioRepository(this)
    val pilarRepository = PilarRepository(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityRelatorioOkractivityBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val intentExtra = intent
        val idUsuario = intentExtra.getIntExtra("idUsuario", 999999)
        val nomeUsuario = intentExtra.getStringExtra("nomeUsuario") ?: "Nome de usuário desconhecido"
        val tipoUsuario = intentExtra.getStringExtra("tipoUsuario") ?: "Tipo de usuário desconhecido"


        val openMenuRelatorio: ImageView = findViewById(R.id.imageview_voltarMenuRelatorio)
        openMenuRelatorio.setOnClickListener {
            val extra = Intent(this, RelatorioActivity::class.java)
            extra.putExtra("idUsuario", idUsuario)
            extra.putExtra("nomeUsuario", nomeUsuario)
            extra.putExtra("tipoUsuario", tipoUsuario)
            finish()
            startActivity(extra)
        }

        val createRelatorioOKR: Button = findViewById(R.id.btn_gerarPDFRelatorioOKR)
        createRelatorioOKR.setOnClickListener {
            gerarPDFRelatorioOKR()
        }

    }
    private fun gerarPDFRelatorioOKR() {
    }
}