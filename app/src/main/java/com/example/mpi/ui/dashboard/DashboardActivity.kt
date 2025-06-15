package com.example.mpi.ui.dashboard

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.mpi.R
import com.example.mpi.databinding.ActivityDashboardBinding
import com.example.mpi.ui.MenuActivity

/**
 * [DashboardActivity] é a Activity principal responsável por exibir o painel de controle
 * do aplicativo.
 *
 * Esta tela serve como um hub para navegar para diferentes opções de visualização de dados
 * do dashboard (Opção 1, Opção 2, Opção 3). Ela carrega informações do usuário
 * (ID, nome, tipo) da Intent e as passa para as próximas Activities.
 *
 * Define constantes para os tipos de usuário ([USUARIO_ANALISTA], [USUARIO_COORDENADOR], [USUARIO_GESTOR]),
 * embora não as utilize diretamente nesta Activity para controle de visibilidade,
 * elas podem ser usadas em sub-dashboards.
 */
class DashboardActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDashboardBinding

    val USUARIO_ANALISTA = "ANALISTA"
    val USUARIO_COORDENADOR = "COORDENADOR"
    val USUARIO_GESTOR = "GESTOR"

    /**
     * Chamado quando a Activity é criada pela primeira vez.
     *
     * Inicializa a interface do usuário usando View Binding, ajusta o preenchimento da janela
     * para o modo edge-to-edge, recupera as informações do usuário da Intent, e configura
     * os listeners de clique para os botões de navegação para as opções do dashboard e
     * o botão de voltar ao menu principal.
     *
     * @param savedInstanceState Se não for nulo, esta Activity está sendo recriada
     * a partir de um estado salvo anteriormente.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityDashboardBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val intentExtra = intent
        val idUsuario = intentExtra.getIntExtra("idUsuario", 999999)
        val nomeUsuario = intentExtra.getStringExtra("nomeUsuario") ?: "Nome de usuário desconhecido"
        val tipoUsuario = intentExtra.getStringExtra("tipoUsuario") ?: "Tipo de usuário desconhecido"

        val openMenuPrincipal: ImageView = findViewById(R.id.imageview_voltarDashboardParaMenuPrincipal)
        openMenuPrincipal.setOnClickListener {
            val extra = Intent(this, MenuActivity::class.java)
            extra.putExtra("idUsuario", idUsuario)
            extra.putExtra("nomeUsuario", nomeUsuario)
            extra.putExtra("tipoUsuario", tipoUsuario)
            finish()
            startActivity(extra)
        }

        val openOpcao1: Button = findViewById(R.id.btnOption1)
        openOpcao1.setOnClickListener {
            val extra = Intent(this, Opcao1Activity::class.java)
            extra.putExtra("idUsuario", idUsuario)
            extra.putExtra("nomeUsuario", nomeUsuario)
            extra.putExtra("tipoUsuario", tipoUsuario)
//            finish()
            startActivity(extra)
        }

        val openOpcao2: Button = findViewById(R.id.btnOption2)
        openOpcao2.setOnClickListener {
            val extra = Intent(this, Opcao2Activity::class.java)
            extra.putExtra("idUsuario", idUsuario)
            extra.putExtra("nomeUsuario", nomeUsuario)
            extra.putExtra("tipoUsuario", tipoUsuario)
//            finish()
            startActivity(extra)
        }

        val openOpcao3: Button = findViewById(R.id.btnOption3)
        openOpcao3.setOnClickListener {
            val extra = Intent(this, Opcao3Activity::class.java)
            extra.putExtra("idUsuario", idUsuario)
            extra.putExtra("nomeUsuario", nomeUsuario)
            extra.putExtra("tipoUsuario", tipoUsuario)
//            finish()
            startActivity(extra)
        }
    }
}