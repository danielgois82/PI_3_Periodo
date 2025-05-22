package com.example.mpi.ui

import android.graphics.Color
import android.os.Bundle
import android.view.animation.AlphaAnimation
import android.view.animation.Animation
import android.widget.*
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
    private lateinit var spinnerFiltro: Spinner

    private var cardSelecionado: CardView? = null
    private var ultimoCardClicado: CardView? = null
    private var tempoUltimoClique: Long = 0

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
        spinnerFiltro = findViewById(R.id.spinnerFiltro)

        // botão começa desativado
        btnAprovar.isEnabled = false
        btnAprovar.alpha = 0.5f

        val cards = listOf(card1, card2, card3)

        cards.forEach { card ->
            card.setOnClickListener {
                val agora = System.currentTimeMillis()
                if (card == ultimoCardClicado && (agora - tempoUltimoClique < 400)) {
                    // Duplo clique: desmarcar seleção
                    desmarcarCard(card)
                } else {
                    selecionarCard(card)
                }
                tempoUltimoClique = agora
                ultimoCardClicado = card
            }
        }

        btnAprovar.setOnClickListener {
            Toast.makeText(this, "Aprovado com sucesso", Toast.LENGTH_SHORT).show()
            aprovarAtividade()
        }

        val opcoes = listOf("Buscar ação, atividade...", "Ação 2025", "Atividade 2025")
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, opcoes)
        spinnerFiltro.adapter = adapter
    }

    private fun selecionarCard(card: CardView) {
        cardSelecionado?.setCardBackgroundColor(Color.WHITE)
        card.setCardBackgroundColor(Color.parseColor("#E0F7FA"))
        cardSelecionado = card

        val animacao: Animation = AlphaAnimation(0.3f, 1.0f)
        animacao.duration = 300
        card.startAnimation(animacao)

        btnAprovar.isEnabled = true
        btnAprovar.alpha = 1.0f
    }

    private fun desmarcarCard(card: CardView) {
        card.setCardBackgroundColor(Color.WHITE)
        cardSelecionado = null

        btnAprovar.isEnabled = false
        btnAprovar.alpha = 0.5f
    }

    private fun aprovarAtividade() {

    }
}
