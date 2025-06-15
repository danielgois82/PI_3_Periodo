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

/**
 * [NotificacaoActivity] é a Activity responsável por exibir a lista de notificações
 * para o usuário logado.
 *
 * Esta tela recupera e exibe notificações não lidas, permitindo que o usuário as marque
 * como visualizadas. Ela utiliza um [RecyclerView] para listar as notificações
 * e interage com o [NotificacaoRepository] para gerenciar o status das notificações.
 */
class NotificacaoActivity : AppCompatActivity() {
    private lateinit var binding: ActivityNotificacaoBinding
    private lateinit var notificacaoRepository: NotificacaoRepository
    private lateinit var notificacaoAdapter: NotificacaoAdapter
    private var idUsuarioLogado: Int = 999999

    /**
     * Chamado quando a Activity é criada pela primeira vez.
     *
     * Inicializa a interface do usuário, configura o [NotificacaoRepository],
     * recupera as informações do usuário da Intent, configura o [RecyclerView]
     * com o [NotificacaoAdapter] e seus listeners, e carrega as notificações.
     *
     * @param savedInstanceState Se não for nulo, esta Activity está sendo recriada
     * a partir de um estado salvo anteriormente.
     */
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

    /**
     * Carrega as notificações não lidas do usuário logado e atualiza o adaptador do [RecyclerView].
     */
    private fun carregarNotificacoes() {
        val notificacoesNaoLidas = notificacaoRepository.obterNotificacoesNaoLidasPorUsuario(idUsuarioLogado)
        notificacaoAdapter.atualizarNotificacoes(notificacoesNaoLidas)
    }
}