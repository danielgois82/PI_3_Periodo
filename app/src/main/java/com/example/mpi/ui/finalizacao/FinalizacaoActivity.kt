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

/**
 * [FinalizacaoActivity] é uma [AppCompatActivity] responsável por exibir uma lista de
 * [Acao]s e [Atividade]s que podem ser finalizadas pelo usuário logado.
 *
 * Esta atividade permite que um usuário visualize itens que podem ser marcados como finalizados.
 * Ela utiliza um [RecyclerView] com um adaptador misto para exibir ambos os tipos de itens,
 * e um [Spinner] para filtrar os itens exibidos (todos, apenas ações ou apenas atividades).
 */
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

    /**
     * Chamado quando a atividade é criada pela primeira vez.
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
            /**
             * Chamado quando um item no spinner é selecionado.
             *
             * @param parent O AdapterView onde a seleção ocorreu.
             * @param view A view dentro do AdapterView que foi clicada.
             * @param position A posição da view no adaptador.
             * @param id O ID da linha do item selecionado.
             */
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                carregarItensParaFinalizacao(position)
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

        carregarItensParaFinalizacao(0)
    }

    /**
     * Carrega itens (ações e/ou atividades) que podem ser finalizados com base no filtro especificado.
     *
     * @param filtro Um inteiro representando os critérios de filtro:
     * - 0: Mostrar tudo (ações e atividades)
     * - 1: Mostrar apenas ações
     * - 2: Mostrar apenas atividades
     */
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

    /**
     * Recupera uma lista de ações que atualmente não estão finalizadas.
     *
     * @return Uma [List] de objetos [Acao] que ainda não foram finalizados.
     */
    private fun obterAcoesNaoFinalizadas(): List<Acao> {
        return acaoRepository.obterAcoesNaoFinalizadas()
    }

    /**
     * Recupera uma lista de atividades que atualmente não estão finalizadas.
     *
     * @return Uma [List] de objetos [Atividade] que ainda não foram finalizados.
     */
    private fun obterAtividadesNaoFinalizadas(): List<Atividade> {
        return atividadeRepository.obterAtividadesNaoFinalizadas()
    }

    /**
     * Finaliza uma ação com o [acaoId] fornecido e então recarrega a lista de itens
     * para refletir a mudança.
     *
     * @param acaoId O ID da [Acao] a ser finalizada.
     */
    private fun finalizarAcao(acaoId: Int) {
        acaoRepository.finalizarAcao(acaoId)
        carregarItensParaFinalizacao(spinnerItem.selectedItemPosition)
    }

    /**
     * Finaliza uma atividade com o [atividadeId] fornecido e então recarrega a lista de itens
     * para refletir a mudança.
     *
     * @param atividadeId O ID da [Atividade] a ser finalizada.
     */
    private fun finalizarAtividade(atividadeId: Int) {
        atividadeRepository.finalizarAtividade(atividadeId)
        carregarItensParaFinalizacao(spinnerItem.selectedItemPosition)
    }

}
