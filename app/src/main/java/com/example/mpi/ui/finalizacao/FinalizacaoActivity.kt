package com.example.mpi.ui.finalizacao

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.Spinner
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.mpi.R
import com.example.mpi.data.Acao
import com.example.mpi.data.Atividade
import com.example.mpi.data.DatabaseHelper
import com.example.mpi.repository.AcaoRepository
import com.example.mpi.repository.AtividadeRepository
import com.example.mpi.repository.UsuarioRepository
import com.example.mpi.ui.MenuActivity
import com.example.mpi.data.FinalizacaoItem

class FinalizacaoActivity : AppCompatActivity() {

    private lateinit var dbHelper: DatabaseHelper
    private lateinit var acaoRepository: AcaoRepository
    private lateinit var atividadeRepository: AtividadeRepository
    private lateinit var usuarioRepository: UsuarioRepository

    // Use apenas um adaptador misto
    private lateinit var finalizacaoMistaAdapter: FinalizacaoMistaAdapter

    private lateinit var recyclerView: RecyclerView
    private lateinit var spinnerItem: Spinner
    private lateinit var btnVoltar: ImageView

    private var idUsuarioLogado: Int = 999999
    private var nomeUsuario: String = "Nome de usuário desconhecido"
    private var tipoUsuario: String = "Tipo de usuário desconhecido"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_finalizacao)

        dbHelper = DatabaseHelper(this)
        acaoRepository = AcaoRepository.getInstance(this)
        atividadeRepository = AtividadeRepository.getInstance(this)
        usuarioRepository = UsuarioRepository.getInstance(this)

        val intentExtra = intent
        idUsuarioLogado = intentExtra.getIntExtra("idUsuario", 999999)
        nomeUsuario = intentExtra.getStringExtra("nomeUsuario") ?: "Nome de usuário desconhecido"
        tipoUsuario = intentExtra.getStringExtra("tipoUsuario") ?: "Tipo de usuário desconhecido"

        recyclerView = findViewById(R.id.recyclerViewSubpilares)
        spinnerItem = findViewById(R.id.spinnerItem)
        btnVoltar = findViewById(R.id.btnVoltar)

        recyclerView.layoutManager = LinearLayoutManager(this)

        // Inicializa o adaptador misto
        finalizacaoMistaAdapter = FinalizacaoMistaAdapter(
            emptyList(),
            onFinalizarAcaoClick = { acaoId ->
                finalizarAcao(acaoId)
            },
            onFinalizarAtividadeClick = { atividadeId ->
                finalizarAtividade(atividadeId)
            },
            usuarioRepository
        )
        recyclerView.adapter = finalizacaoMistaAdapter

        val opcoesSpinner = arrayOf("Mostrar tudo", "Ações", "Atividades")
        val adapterSpinner = ArrayAdapter(this, android.R.layout.simple_spinner_item, opcoesSpinner)
        adapterSpinner.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerItem.adapter = adapterSpinner

        spinnerItem.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                carregarItensParaFinalizacao(position)
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
            }
        }

        btnVoltar.setOnClickListener {
            val extra = Intent(this, MenuActivity::class.java)
            extra.putExtra("idUsuario", idUsuarioLogado)
            extra.putExtra("nomeUsuario", nomeUsuario)
            extra.putExtra("tipoUsuario", tipoUsuario)
            startActivity(extra)
            finish()
        }

        carregarItensParaFinalizacao(0)
    }

    private fun carregarItensParaFinalizacao(filtro: Int) {
        val itensParaExibir = mutableListOf<FinalizacaoItem>()

        when (filtro) {
            0 -> { // Mostrar tudo
                val acoesNaoFinalizadas = obterAcoesNaoFinalizadas()
                val atividadesNaoFinalizadas = obterAtividadesNaoFinalizadas()

                // Converte e adiciona ações
                acoesNaoFinalizadas.mapTo(itensParaExibir) { FinalizacaoItem.AcaoFinalizacao(it) }
                // Converte e adiciona atividades
                atividadesNaoFinalizadas.mapTo(itensParaExibir) { FinalizacaoItem.AtividadeFinalizacao(it) }

            }
            1 -> { // Ações
                val acoesNaoFinalizadas = obterAcoesNaoFinalizadas()
                acoesNaoFinalizadas.mapTo(itensParaExibir) { FinalizacaoItem.AcaoFinalizacao(it) }
            }
            2 -> { // Atividades
                val atividadesNaoFinalizadas = obterAtividadesNaoFinalizadas()
                atividadesNaoFinalizadas.mapTo(itensParaExibir) { FinalizacaoItem.AtividadeFinalizacao(it) }
            }
        }
        finalizacaoMistaAdapter.atualizarItens(itensParaExibir)
    }

    private fun obterAcoesNaoFinalizadas(): List<Acao> {
        return acaoRepository.obterAcoesNaoFinalizadas()
    }

    private fun obterAtividadesNaoFinalizadas(): List<Atividade> {
        return atividadeRepository.obterAtividadesNaoFinalizadas()
    }

    private fun finalizarAcao(acaoId: Int) {
        acaoRepository.finalizarAcao(acaoId)
        carregarItensParaFinalizacao(spinnerItem.selectedItemPosition)
    }

    private fun finalizarAtividade(atividadeId: Int) {
        atividadeRepository.finalizarAtividade(atividadeId)
        carregarItensParaFinalizacao(spinnerItem.selectedItemPosition)
    }

}
