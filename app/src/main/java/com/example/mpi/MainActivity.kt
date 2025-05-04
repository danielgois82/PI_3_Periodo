package com.example.mpi

import android.os.Bundle
import android.widget.Button
import android.content.Intent
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.mpi.ui.cadastroAcao
import com.example.mpi.ui.cadastroAtividade
import com.example.mpi.ui.cadastroPilar
import com.example.mpi.ui.cadastroSubPilar

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)


        val cadPilar: Button = findViewById(R.id.cadastroPilar)
        cadPilar.setOnClickListener {
            val intent = Intent(this, cadastroPilar::class.java)
            startActivity(intent)
        }
        val cadSubPilar: Button = findViewById(R.id.cadastroSubPilar)
        cadSubPilar.setOnClickListener {
            val intent = Intent(this, cadastroSubPilar::class.java)
            startActivity(intent)
        }

        val cadAcao: Button = findViewById(R.id.cadastroAcao)
        cadAcao.setOnClickListener {
            val intent = Intent(this, cadastroAcao::class.java)
            startActivity(intent)
        }

        val cadAtividade: Button = findViewById(R.id.cadastroAtividade)
        cadAtividade.setOnClickListener {
            val intent = Intent(this, cadastroAtividade::class.java)
            startActivity(intent)
        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }
}