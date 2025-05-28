package com.example.mpi.ui.notificacao

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.mpi.R
import com.example.mpi.ui.MenuActivity
import com.example.mpi.databinding.ActivityNotificacaoBinding
import com.example.mpi.repository.NotificacaoRepository
import com.example.mpi.ui.notificacao.NotificacaoAdapter

class NotificacaoActivity : AppCompatActivity() {
    private lateinit var binding: ActivityNotificacaoBinding
    private lateinit var notificacaoRepository: NotificacaoRepository
    private lateinit var notificacaoAdapter: NotificacaoAdapter
    private var idUsuarioLogado: Int = 999999

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityNotificacaoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val intentExtra = intent
        idUsuarioLogado = intentExtra.getIntExtra("idUsuario", 999999)
        val nomeUsuario = intentExtra.getStringExtra("nomeUsuario") ?: "Nome de usuário desconhecido"
        val tipoUsuario = intentExtra.getStringExtra("tipoUsuario") ?: "Tipo de usuário desconhecido"

        notificacaoRepository = NotificacaoRepository.getInstance(this)

        val recyclerViewNotificacoes: RecyclerView = binding.recyclerViewNotificacoes

        notificacaoAdapter = NotificacaoAdapter(emptyList()) { notificacaoId ->
            notificacaoRepository.marcarNotificacaoComoLida(notificacaoId)
            carregarNotificacoes()
        }

        recyclerViewNotificacoes.layoutManager = LinearLayoutManager(this)
        recyclerViewNotificacoes.adapter = notificacaoAdapter

        carregarNotificacoes()

        val btnVoltar: ImageView = binding.btnVoltar
        btnVoltar.setOnClickListener {
            val extra = Intent(this, MenuActivity::class.java)
            extra.putExtra("idUsuario", idUsuarioLogado)
            extra.putExtra("nomeUsuario", nomeUsuario)
            extra.putExtra("tipoUsuario", tipoUsuario)
            startActivity(extra)
            finish()
        }
    }

    private fun carregarNotificacoes() {
        val notificacoesNaoLidas = notificacaoRepository.obterNotificacoesNaoLidasPorUsuario(idUsuarioLogado)
        notificacaoAdapter.atualizarNotificacoes(notificacoesNaoLidas)
    }
}