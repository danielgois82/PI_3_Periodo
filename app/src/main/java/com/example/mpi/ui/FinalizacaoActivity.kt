package com.example.mpi.ui

import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import com.example.mpi.R

class FinalizacaoActivity : AppCompatActivity() {

    private lateinit var spinnerFiltro: Spinner
    private lateinit var btnFinalizar: Button
    private lateinit var card1: CardView
    private lateinit var card2: CardView
    private lateinit var card3: CardView

    private lateinit var txtStatus1: TextView
    private lateinit var txtStatus2: TextView
    private lateinit var txtStatus3: TextView

    private var cardSelecionado: CardView? = null
    private var statusSelecionado: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_finalizacao)

        // Inicializações
        spinnerFiltro = findViewById(R.id.spinnerFiltro)
        btnFinalizar = findViewById(R.id.btnFinalizar)
        card1 = findViewById(R.id.card1)
        card2 = findViewById(R.id.card2)
        card3 = findViewById(R.id.card3)

        txtStatus1 = findViewById(R.id.txtStatus1)
        txtStatus2 = findViewById(R.id.txtStatus2)
        txtStatus3 = findViewById(R.id.txtStatus3)

        btnFinalizar.isEnabled = false
        btnFinalizar.alpha = 0.5f

        configurarSpinner()
        configurarCards()

        btnFinalizar.setOnClickListener {
            Toast.makeText(this, "Finalizado com sucesso!", Toast.LENGTH_SHORT).show()
            finalizarAtividade()
        }
    }

    private fun configurarSpinner() {
        val opcoes = listOf("Buscar ação, atividade...", "Ação 2025", "Atividade 2025")
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, opcoes)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerFiltro.adapter = adapter
    }

    private fun configurarCards() {
        val cards = listOf(card1, card2, card3)
        val statusMap = mapOf(
            card1 to txtStatus1.text.toString(),
            card2 to txtStatus2.text.toString(),
            card3 to txtStatus3.text.toString()
        )

        cards.forEach { card ->
            card.setOnClickListener {
                selecionarCard(card, statusMap[card] ?: "")
            }
        }
    }

    private fun selecionarCard(card: CardView, status: String) {
        // Resetar cor anterior
        cardSelecionado?.setCardBackgroundColor(Color.WHITE)

        // Marcar card atual
        card.setCardBackgroundColor(Color.parseColor("#E0F7FA"))
        cardSelecionado = card
        statusSelecionado = status

        // Validar status para habilitar botão
        if (statusSelecionado.contains("100")) {
            btnFinalizar.isEnabled = true
            btnFinalizar.alpha = 1.0f
        } else {
            btnFinalizar.isEnabled = false
            btnFinalizar.alpha = 0.5f
            Toast.makeText(this, "A atividade precisa estar 100% concluída.", Toast.LENGTH_SHORT).show()
        }
    }

    private fun finalizarAtividade() {

    }
}
