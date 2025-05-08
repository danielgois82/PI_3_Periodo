package com.example.mpi.ui.subpilar

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.mpi.R
import com.example.mpi.ui.subpilar.cadastroSubPilar


class SubpilarActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_subpilar)


        val cadSubPilar: Button = findViewById(R.id.btnAdicionarSubPilar)
        cadSubPilar.setOnClickListener {
            val intent = Intent(this, cadastroSubPilar::class.java)
            startActivity(intent)
        }
        val voltar: ImageView = findViewById(R.id.btnVoltar)
        voltar.setOnClickListener {
            finish()
        }


        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }
}