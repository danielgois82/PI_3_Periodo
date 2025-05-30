package com.example.mpi.ui

import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.example.mpi.R
import com.example.mpi.data.DatabaseHelper

class AprovacaoActivity : AppCompatActivity() {

    private lateinit var dbHelper: DatabaseHelper
    private lateinit var containerCards: LinearLayout
    private lateinit var spinnerTipo: Spinner
    private lateinit var btnVoltar: ImageButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_aprovacao)

        dbHelper = DatabaseHelper(this)
        containerCards = findViewById(R.id.containerCards)
        spinnerTipo = findViewById(R.id.spinnerTipo)
        btnVoltar = findViewById(R.id.botaoVoltar)

        val tipos = arrayOf("Ação 2025", "Aprovação 2025")
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, tipos)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerTipo.adapter = adapter

        spinnerTipo.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                val tipoSelecionado = tipos[position]
                exibirCards(tipoSelecionado)
            }

            override fun onNothingSelected(parent: AdapterView<*>) {}
        }

        btnVoltar.setOnClickListener {
            finish()
        }
    }

    private fun exibirCards(tipo: String) {
        containerCards.removeAllViews()
        val db = dbHelper.readableDatabase

        val query = """
            SELECT a.id, a.nome, a.dataInicio, a.dataTermino, a.descricao
            FROM atividade a
            INNER JOIN acao ac ON a.id_acao = ac.id
            WHERE a.isAprovado = 0 AND (
                ? = 'Ação 2025' OR ? = 'Aprovação 2025'
            )
        """.trimIndent()

        val cursor = db.rawQuery(query, arrayOf(tipo, tipo))
        if (cursor.moveToFirst()) {
            do {
                val idAtividade = cursor.getInt(cursor.getColumnIndexOrThrow("id"))
                val nome = cursor.getString(cursor.getColumnIndexOrThrow("nome"))
                val descricao = cursor.getString(cursor.getColumnIndexOrThrow("descricao"))
                val dataInicio = cursor.getString(cursor.getColumnIndexOrThrow("dataInicio"))
                val dataTermino = cursor.getString(cursor.getColumnIndexOrThrow("dataTermino"))

                val card = layoutInflater.inflate(R.layout.card_atividade, null)

                card.findViewById<TextView>(R.id.tvNome).text = nome
                card.findViewById<TextView>(R.id.tvDescricao).text = descricao
                card.findViewById<TextView>(R.id.tvData).text = "De $dataInicio até $dataTermino"

                val btnAprovar = card.findViewById<Button>(R.id.btnAprovar)
                btnAprovar.setOnClickListener {
                    val success = dbHelper.aprovarAtividade(idAtividade)
                    if (success) {
                        containerCards.removeView(card)
                        Toast.makeText(this, "Atividade aprovada!", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(this, "Erro ao aprovar.", Toast.LENGTH_SHORT).show()
                    }
                }

                containerCards.addView(card)
            } while (cursor.moveToNext())
        } else {
            val textoVazio = TextView(this)
            textoVazio.text = "Nenhuma atividade pendente para aprovação."
            containerCards.addView(textoVazio)
        }

        cursor.close()
        db.close()
    }
}
