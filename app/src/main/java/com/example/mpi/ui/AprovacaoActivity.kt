package com.example.mpi.ui

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import com.example.mpi.R

class AprovacaoActivity : AppCompatActivity() {

    val USUARIO_ANALISTA = "ANALISTA"
    val USUARIO_COORDENADOR = "COORDENADOR"
    val USUARIO_GESTOR = "GESTOR"

    private lateinit var btnAprovar: Button
    private lateinit var card1: CardView
    private lateinit var card2: CardView
    private lateinit var card3: CardView

    private var cardSelecionado: CardView? = null // controle de seleção

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_aprovacao)

        val intentExtra = intent
        val idUsuario = intentExtra.getIntExtra("idUsuario", 999999)
        val nomeUsuario = intentExtra.getStringExtra("nomeUsuario") ?: "Nome de usuário desconhecido"
        val tipoUsuario = intentExtra.getStringExtra("tipoUsuario") ?: "Tipo de usuário desconhecido"

        btnAprovar = findViewById(R.id.btnAprovar)
        card1 = findViewById(R.id.card1)
        card2 = findViewById(R.id.card2)
        card3 = findViewById(R.id.card3)

        // botão começa desativado
        btnAprovar.isEnabled = false
        btnAprovar.alpha = 0.5f

        val cards = listOf(card1, card2, card3)

        cards.forEach { card ->
            card.setOnClickListener {
                selecionarCard(card)
            }
        }

        btnAprovar.setOnClickListener {
            val extra = Intent(this, FinalizacaoActivity::class.java)
            extra.putExtra("idUsuario", idUsuario)
            extra.putExtra("nomeUsuario", nomeUsuario)
            extra.putExtra("tipoUsuario", tipoUsuario)
            startActivity(extra)
        }
    }

    private fun selecionarCard(card: CardView) {
        cardSelecionado?.setCardBackgroundColor(Color.WHITE) // reseta anterior
        card.setCardBackgroundColor(Color.parseColor("#E0F7FA")) // destaque azul claro
        cardSelecionado = card

        btnAprovar.isEnabled = true
        btnAprovar.alpha = 1.0f
    }
}
