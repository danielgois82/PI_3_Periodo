package com.example.mpi.ui

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.mpi.ui.notificacao.NotificacaoActivity
import com.example.mpi.R
import com.example.mpi.databinding.ActivityMenuBinding
import com.example.mpi.services.NotificacaoService
import com.example.mpi.repository.NotificacaoRepository
import com.example.mpi.ui.acao.AcaoActivity
import com.example.mpi.ui.atividade.AtividadeActivity
import com.example.mpi.ui.dashboard.DashboardActivity
import com.example.mpi.ui.pilar.PilarActivity
import com.example.mpi.ui.subpilar.SubpilarActivity
import com.example.mpi.ui.AprovacaoActivity



class MenuActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMenuBinding
    private lateinit var notificacaoService: NotificacaoService
    private lateinit var notificacaoRepository: NotificacaoRepository
    private lateinit var notificationBadge: ImageView

    val USUARIO_ANALISTA = "ANALISTA"
    val USUARIO_COORDENADOR = "COORDENADOR"
    val USUARIO_GESTOR = "GESTOR"

    private var idUsuarioLogado: Int = 999999

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityMenuBinding.inflate(layoutInflater)
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

        // Inicializa os serviços e repositórios
        notificacaoService = NotificacaoService.getInstance(this)
        notificacaoRepository = NotificacaoRepository.getInstance(this)

        // Referências aos elementos da UI
        val openPilar: Button = findViewById(R.id.btnPilarActivity)
        val openSubpilar: Button = findViewById(R.id.btnSubpilarActivity)
        val openAcao: Button = findViewById(R.id.btnAcaoActivity)
        val openAtividade: Button = findViewById(R.id.btnAtividadeActivity)
        val openAprovacao: Button = findViewById(R.id.btnAprovacaoActivity)
        val openFinalizacao: Button = findViewById(R.id.btnFinalizacaoActivity)
        val openPercentual: Button = findViewById(R.id.btnPercentualActivity)
        val openDashboard: Button = findViewById(R.id.btnDashboardActivity)
        val openNotificacao: ImageView = findViewById(R.id.btnNotificacaoActivity)
        notificationBadge = findViewById(R.id.notificationBadge)

        val textViewNomeUsuario = findViewById<TextView>(R.id.textviewNomeUsuario)
        textViewNomeUsuario.text = nomeUsuario


        if (tipoUsuario.uppercase() == USUARIO_ANALISTA) {
            openAprovacao.visibility = View.GONE
            openFinalizacao.visibility = View.GONE
            openDashboard.visibility = View.GONE
        }

        if (tipoUsuario.uppercase() == USUARIO_COORDENADOR) {
            openDashboard.visibility = View.GONE
        }

        if (tipoUsuario.uppercase() == USUARIO_GESTOR) {
            openPilar.visibility = View.GONE
            openSubpilar.visibility = View.GONE
            openAprovacao.visibility = View.GONE
            openFinalizacao.visibility = View.GONE
            openPercentual.visibility = View.GONE
            openNotificacao.visibility = View.INVISIBLE
            notificationBadge.visibility = View.GONE
        }

        openPilar.setOnClickListener {
            val extra = Intent(this, PilarActivity::class.java)
            extra.putExtra("idUsuario", idUsuarioLogado)
            extra.putExtra("nomeUsuario", nomeUsuario)
            extra.putExtra("tipoUsuario", tipoUsuario)
            startActivity(extra)
        }

        openSubpilar.setOnClickListener {
            val extra = Intent(this, SubpilarActivity::class.java)
            extra.putExtra("idUsuario", idUsuarioLogado)
            extra.putExtra("nomeUsuario", nomeUsuario)
            extra.putExtra("tipoUsuario", tipoUsuario)
            startActivity(extra)
        }

        openAcao.setOnClickListener {
            val extra = Intent(this, AcaoActivity::class.java)
            extra.putExtra("idUsuario", idUsuarioLogado)
            extra.putExtra("nomeUsuario", nomeUsuario)
            extra.putExtra("tipoUsuario", tipoUsuario)
            startActivity(extra)
        }

        openAtividade.setOnClickListener {
            val extra = Intent(this, AtividadeActivity::class.java)
            extra.putExtra("idUsuario", idUsuarioLogado)
            extra.putExtra("nomeUsuario", nomeUsuario)
            extra.putExtra("tipoUsuario", tipoUsuario)
            startActivity(extra)
        }

        openAprovacao.setOnClickListener {
            val extra = Intent(this, AprovacaoActivity::class.java)
            extra.putExtra("idUsuario", idUsuarioLogado)
            extra.putExtra("nomeUsuario", nomeUsuario)
            extra.putExtra("tipoUsuario", tipoUsuario)
            startActivity(extra)
        }

        openFinalizacao.setOnClickListener {
            val extra = Intent(this, FinalizacaoActivity::class.java)
            extra.putExtra("idUsuario", idUsuarioLogado)
            extra.putExtra("nomeUsuario", nomeUsuario)
            extra.putExtra("tipoUsuario", tipoUsuario)
            startActivity(extra)
        }

        openPercentual.setOnClickListener {
            val extra = Intent(this, PercentualActivity::class.java)
            extra.putExtra("idUsuario", idUsuarioLogado)
            extra.putExtra("nomeUsuario", nomeUsuario)
            extra.putExtra("tipoUsuario", tipoUsuario)
            startActivity(extra)
        }

        openDashboard.setOnClickListener {
            val extra = Intent(this, DashboardActivity::class.java)
            extra.putExtra("idUsuario", idUsuarioLogado)
            extra.putExtra("nomeUsuario", nomeUsuario)
            extra.putExtra("tipoUsuario", tipoUsuario)
            startActivity(extra)
        }

        openNotificacao.setOnClickListener {
            val extra = Intent(this, NotificacaoActivity::class.java)
            extra.putExtra("idUsuario", idUsuarioLogado)
            extra.putExtra("nomeUsuario", nomeUsuario)
            extra.putExtra("tipoUsuario", tipoUsuario)
            startActivity(extra)
        }
    }

    override fun onResume() {
        super.onResume()
        // Quando a atividade volta para o foco, verificamos e atualizamos as notificações
        atualizarContadorNotificacoes()
    }

    private fun atualizarContadorNotificacoes() {
        // Chamamos o serviço para gerar novas notificações baseadas nos itens
        notificacaoService.verificarEGerarNotificacoes(idUsuarioLogado)

        // Agora verificamos se há notificações não lidas para o usuário atual
        val notificacoesNaoLidas = notificacaoRepository.obterNotificacoesNaoLidasPorUsuario(idUsuarioLogado)

        // Atualiza a visibilidade da bolinha de notificação
        if (notificacoesNaoLidas.isNotEmpty()) {
            notificationBadge.visibility = View.VISIBLE
        } else {
            notificationBadge.visibility = View.GONE
        }
    }
}