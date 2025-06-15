package com.example.mpi.ui.aprovacao

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
import com.example.mpi.data.AprovacaoItem

/**
 * [AprovacaoActivity] é uma [AppCompatActivity] responsável por exibir uma lista de
 * [Acao]s e [Atividade]s que estão pendentes de aprovação.
 *
 * Esta atividade permite que um usuário, tipicamente um gestor, visualize itens que exigem sua
 * aprovação e os aprove. Ela utiliza um [RecyclerView] com um adaptador misto para
 * exibir ambos os tipos de itens, e um [Spinner] para filtrar os itens exibidos
 * (todos, apenas ações ou apenas atividades).
 */
class AprovacaoActivity : AppCompatActivity() {

    private lateinit var dbHelper: DatabaseHelper
    private lateinit var acaoRepository: AcaoRepository
    private lateinit var atividadeRepository: AtividadeRepository
    private lateinit var usuarioRepository: UsuarioRepository

    // Use apenas um adaptador misto
    private lateinit var aprovacaoMistaAdapter: AprovacaoMistaAdapter

    private lateinit var recyclerView: RecyclerView
    private lateinit var spinnerItem: Spinner
    private lateinit var btnVoltar: ImageView

    private var idUsuarioLogado: Int = 999999
    private var nomeUsuario: String = "Nome de usuário desconhecido"
    private var tipoUsuario: String = "Tipo de usuário desconhecido"

    /**
     * Chamado quando a activity é criada pela primeira vez.
     *
     * Inicializa a interface do usuário, configura os auxiliares de banco de dados e repositórios,
     * recupera as informações do usuário da intent, configura o [RecyclerView] e seu adaptador,
     * preenche e configura o [Spinner] e define os listeners para os elementos da UI.
     *
     * @param savedInstanceState Se não for nulo, esta atividade está sendo recriada
     * a partir de um estado salvo anteriormente.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_aprovacao)

        dbHelper = DatabaseHelper(this)
        acaoRepository = AcaoRepository.getInstance(this)
        atividadeRepository = AtividadeRepository.getInstance(this)
        usuarioRepository = UsuarioRepository.getInstance(this)

        val intentExtra = intent
        idUsuarioLogado = intentExtra.getIntExtra("idUsuario", 999999)
        nomeUsuario = intentExtra.getStringExtra("nomeUsuario") ?: "Nome de usuário desconhecido"
        tipoUsuario = intentExtra.getStringExtra("tipoUsuario") ?: "Tipo de usuário desconhecido"

        recyclerView = findViewById(R.id.recyclerViewSubpilares)
        spinnerItem = findViewById(R.id.spinnerItem) // Certifique-se de que o ID do spinner está correto no seu XML
        btnVoltar = findViewById(R.id.btnVoltar)

        recyclerView.layoutManager = LinearLayoutManager(this)

        // Inicializa o adaptador misto
        aprovacaoMistaAdapter = AprovacaoMistaAdapter(
            emptyList(),
            onAprovarAcaoClick = { acaoId ->
                aprovarAcao(acaoId)
            },
            onAprovarAtividadeClick = { atividadeId ->
                aprovarAtividade(atividadeId)
            },
            usuarioRepository
        )
        recyclerView.adapter = aprovacaoMistaAdapter

        val opcoesSpinner = arrayOf("Mostrar tudo", "Ações", "Atividades")
        val adapterSpinner = ArrayAdapter(this, android.R.layout.simple_spinner_item, opcoesSpinner)
        adapterSpinner.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerItem.adapter = adapterSpinner

        spinnerItem.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            /**
             * Chamado quando um item no spinner é selecionado.
             *
             * @param parent O AdapterView onde a seleção ocorreu.
             * @param view A view dentro do AdapterView que foi clicada.
             * @param position A posição da view no adaptador.
             * @param id O ID da linha do item selecionado.
             */
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                carregarItensParaAprovacao(position)
            }

            /**
             * Chamado quando nada é selecionado no spinner.
             * Atualmente, este método não faz nada.
             *
             * @param parent O AdapterView onde a seleção ocorreu.
             */
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

        carregarItensParaAprovacao(0)
    }

    /**
     * Carrega itens (ações e/ou atividades) pendentes de aprovação com base no filtro especificado.
     *
     * @param filtro Um inteiro que representa os critérios de filtro:
     * - 0: Mostrar tudo (ações e atividades)
     * - 1: Mostrar apenas ações
     * - 2: Mostrar apenas atividades
     */
    private fun carregarItensParaAprovacao(filtro: Int) {
        val itensParaExibir = mutableListOf<AprovacaoItem>()

        when (filtro) {
            0 -> { // Mostrar tudo
                val acoesNaoAprovadas = obterAcoesNaoAprovadas()
                val atividadesNaoAprovadas = obterAtividadesNaoAprovadas()


                acoesNaoAprovadas.mapTo(itensParaExibir) { AprovacaoItem.AcaoAprovacao(it) }
                atividadesNaoAprovadas.mapTo(itensParaExibir) { AprovacaoItem.AtividadeAprovacao(it) }

            }
            1 -> { // Ações
                val acoesNaoAprovadas = obterAcoesNaoAprovadas()
                acoesNaoAprovadas.mapTo(itensParaExibir) { AprovacaoItem.AcaoAprovacao(it) }
            }
            2 -> { // Atividades
                val atividadesNaoAprovadas = obterAtividadesNaoAprovadas()
                atividadesNaoAprovadas.mapTo(itensParaExibir) { AprovacaoItem.AtividadeAprovacao(it) }
            }
        }
        aprovacaoMistaAdapter.atualizarItens(itensParaExibir)
    }

    /**
     * Recupera uma lista de ações que atualmente não estão aprovadas.
     *
     * @return Uma [List] de objetos [Acao] que ainda não foram aprovados.
     */
    private fun obterAcoesNaoAprovadas(): List<Acao> {
        return acaoRepository.obterAcoesNaoAprovadas()
    }

    /**
     * Recupera uma lista de atividades que atualmente não estão aprovadas.
     *
     * @return Uma [List] de objetos [Atividade] que ainda não foram aprovados.
     */
    private fun obterAtividadesNaoAprovadas(): List<Atividade> {
        return atividadeRepository.obterAtividadesNaoAprovadas()
    }

    /**
     * Aprova uma ação com o [acaoId] fornecido e então recarrega a lista de itens
     * para refletir a mudança.
     *
     * @param acaoId O ID da [Acao] a ser aprovada.
     */
    private fun aprovarAcao(acaoId: Int) {
        acaoRepository.aprovarAcao(acaoId)
        // Recarrega a lista para remover a ação aprovada
        carregarItensParaAprovacao(spinnerItem.selectedItemPosition)
    }

    /**
     * Aprova uma atividade com o [atividadeId] fornecido e então recarrega a lista de itens
     * para refletir a mudança.
     *
     * @param atividadeId O ID da [Atividade] a ser aprovada.
     */
    private fun aprovarAtividade(atividadeId: Int) {
        atividadeRepository.aprovarAtividade(atividadeId)
        // Recarrega a lista para remover a atividade aprovada
        carregarItensParaAprovacao(spinnerItem.selectedItemPosition)
    }
}