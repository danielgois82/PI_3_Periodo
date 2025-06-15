package com.example.mpi.ui.acao

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.mpi.databinding.ActivityAcaoBinding
import com.example.mpi.data.DatabaseHelper
import com.example.mpi.data.Pilar
import com.example.mpi.data.Subpilar
import com.example.mpi.data.Acao
import com.example.mpi.data.Atividade
import com.example.mpi.data.Calendario
import com.example.mpi.repository.AcaoRepository
import com.example.mpi.repository.PilarRepository
import com.example.mpi.repository.SubpilarRepository
import com.example.mpi.repository.AtividadeRepository

/**
 * [AcaoActivity] é a Activity responsável por exibir e gerenciar a lista de [Acao]s do aplicativo.
 *
 * Esta tela permite ao usuário visualizar ações filtradas por [Pilar] e [Subpilar],
 * adicionar novas ações, editar ações existentes e excluir ações (com validação).
 * A interface do usuário é construída usando View Binding e exibe as ações em um [RecyclerView].
 *
 * Lida com diferentes tipos de usuário ([USUARIO_ANALISTA], [USUARIO_COORDENADOR], [USUARIO_GESTOR])
 * para controlar a visibilidade de certas funcionalidades, como o botão de adicionar ação.
 */
class AcaoActivity : AppCompatActivity() {

    val USUARIO_ANALISTA = "ANALISTA"
    val USUARIO_COORDENADOR = "COORDENADOR"
    val USUARIO_GESTOR = "GESTOR"

    private lateinit var binding: ActivityAcaoBinding
    private lateinit var dbHelper: DatabaseHelper
    private lateinit var acaoAdapter: AcaoAdapter
    private val listaAcoes = mutableListOf<Acao>()
    private lateinit var pilarRepository: PilarRepository
    private lateinit var subpilarRepository: SubpilarRepository
    private lateinit var acaoRepository: AcaoRepository
    private lateinit var atividadeRepository: AtividadeRepository

    private var pilares: List<Pilar> = emptyList()
    private var subpilares: List<Subpilar> = emptyList()

    private var selectedPilar: Pilar? = null
    private var selectedSubpilar: Subpilar? = null

    /**
     * Chamado quando a Activity é criada pela primeira vez.
     *
     * Inicializa a interface do usuário, carrega informações do usuário, configura os repositórios,
     * o [RecyclerView] e seus adaptadores, e os listeners dos Spinners.
     * Define a visibilidade do botão de adicionar ação com base no tipo de usuário.
     *
     * @param savedInstanceState Se não for nulo, esta Activity está sendo recriada a partir
     * de um estado previamente salvo.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityAcaoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ////////////////////// Carregando informações do usuário////////////////////////////////
        val intentExtra = intent
        val idUsuario = intentExtra.getIntExtra("idUsuario", 999999)
        val nomeUsuario = intentExtra.getStringExtra("nomeUsuario") ?: "Nome de usuário desconhecido"
        val tipoUsuario = intentExtra.getStringExtra("tipoUsuario") ?: "Tipo de usuário desconhecido"
        val tag = "AcaoActivityLog" // Tag ajustada para AcaoActivity
        val mensagemLog = "AcaoActivity iniciada - ID Usuário: $idUsuario, Nome: $nomeUsuario"
        Log.d(tag, mensagemLog)
        ////////////////////////////////////////////////////////////////////////////////

        dbHelper = DatabaseHelper(this)
        // Inicializando os repositórios
        pilarRepository = PilarRepository.getInstance(this)
        subpilarRepository = SubpilarRepository.getInstance(this)
        acaoRepository = AcaoRepository.getInstance(this)
        atividadeRepository = AtividadeRepository.getInstance(this)

        // Configuração do RecyclerView
        binding.recyclerViewAcoes.layoutManager = LinearLayoutManager(this)
        acaoAdapter = AcaoAdapter(listaAcoes,
            onEditarClicked = { acao -> editarAcao(acao) },
            onExcluirClicked = { acao -> excluirAcao(acao) },
            this)
        binding.recyclerViewAcoes.adapter = acaoAdapter

        // Limitando a opção de cadastrar ação
        if (tipoUsuario.uppercase() == USUARIO_GESTOR) {
            binding.btnAdicionarAcao.visibility = View.GONE
        }

        setupSpinners()
        carregarPilaresParaSpinner()

        binding.btnAdicionarAcao.setOnClickListener {
            val intent = Intent(this, CadastroAcaoActivity::class.java)
            intent.putExtra("idUsuario", idUsuario)
            intent.putExtra("nomeUsuario", nomeUsuario)
            intent.putExtra("tipoUsuario", tipoUsuario)
            startActivity(intent)
        }
        binding.btnVoltar.setOnClickListener {
            finish()
        }
    }

    /**
     * Chamado quando a Activity está prestes a se tornar visível para o usuário.
     *
     * Recarrega e aplica os filtros nas ações sempre que a Activity volta ao foco,
     * garantindo que a lista esteja atualizada com quaisquer alterações (edição, exclusão, adição)
     * feitas em outras telas.
     */
    override fun onResume() {
        super.onResume()
        aplicarFiltros()
    }

    /**
     * Configura os listeners de seleção de item para os Spinners de Pilar e Subpilar.
     *
     * Quando um item é selecionado, os filtros são aplicados novamente para atualizar a lista de ações.
     */
    private fun setupSpinners() {
        binding.spinnerPilar.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            /**
             * Chamado quando um item do Spinner de Pilar é selecionado.
             *
             * @param parent O AdapterView onde a seleção ocorreu.
             * @param view A View dentro do AdapterView que foi clicada (null se a seleção for do teclado).
             * @param position A posição da view na lista de adaptadores.
             * @param id O id da linha do item que foi selecionado.
             */
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                selectedPilar = if (position > 0) pilares[position - 1] else null
                carregarSubpilaresParaSpinner(selectedPilar) // Recarrega subpilares ao mudar pilar
                aplicarFiltros() // Aplica filtro com o novo pilar/subpilar
            }
            /**
             * Chamado quando nada é selecionado no Spinner de Pilar (não comum para seleção única).
             * @param parent O AdapterView onde a seleção (ou falta dela) ocorreu.
             */
            override fun onNothingSelected(parent: AdapterView<*>?) {
            }
        }

        binding.spinnerSubpilar.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            /**
             * Chamado quando um item do Spinner de Subpilar é selecionado.
             *
             * @param parent O AdapterView onde a seleção ocorreu.
             * @param view A View dentro do AdapterView que foi clicada (null se a seleção for do teclado).
             * @param position A posição da view na lista de adaptadores.
             * @param id O id da linha do item que foi selecionado.
             */
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                selectedSubpilar = if (position > 0) subpilares[position - 1] else null
                aplicarFiltros() // Aplica filtro com o novo subpilar
            }
            /**
             * Chamado quando nada é selecionado no Spinner de Subpilar.
             * @param parent O AdapterView onde a seleção (ou falta dela) ocorreu.
             */
            override fun onNothingSelected(parent: AdapterView<*>?) {
            }
        }
    }

    /**
     * Carrega a lista de [Pilar]es do repositório e popula o Spinner de Pilares.
     *
     * Adiciona uma opção "Todos os Pilares" no início da lista.
     */
    private fun carregarPilaresParaSpinner() {
        val calendarioPadrao = Calendario(1, 2025)
        pilares = pilarRepository.obterTodosPilares(calendarioPadrao)

        val pilarNames = mutableListOf("Todos os Pilares")
        pilarNames.addAll(pilares.map { it.nome })

        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, pilarNames)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerPilar.adapter = adapter
    }

    /**
     * Carrega a lista de [Subpilar]es para o Spinner de Subpilares com base no [Pilar] selecionado.
     *
     * Se nenhum pilar for selecionado ([pilar] é null), a lista de subpilares fica vazia.
     * Adiciona uma opção "Todos os Subpilares" no início da lista.
     *
     * @param pilar O [Pilar] atualmente selecionado. Se nulo, não há subpilares para carregar.
     */
    private fun carregarSubpilaresParaSpinner(pilar: Pilar?) {
        subpilares = if (pilar != null) {
            subpilarRepository.obterTodosSubpilares(pilar)
        } else {
            emptyList()
        }

        val subpilarNames = mutableListOf("Todos os Subpilares")
        subpilarNames.addAll(subpilares.map { it.nome })

        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, subpilarNames)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerSubpilar.adapter = adapter

        binding.spinnerSubpilar.setSelection(0)
    }

    /**
     * Aplica os filtros selecionados nos Spinners e atualiza a lista de ações exibida no [RecyclerView].
     *
     * A lógica de filtragem prioriza:
     * 1. Filtro por [Subpilar] (se um subpilar for selecionado).
     * 2. Filtro por [Pilar] (se um pilar for selecionado e nenhum subpilar).
     * 3. Nenhuma filtragem (todas as ações) se nem pilar nem subpilar forem selecionados.
     */
    private fun aplicarFiltros() {
        listaAcoes.clear()

        val filteredAcoes = when {
            selectedSubpilar != null -> {
                acaoRepository.obterAcoesPorSubpilar(selectedSubpilar!!)
            }
            selectedPilar != null -> {
                acaoRepository.obterAcoesPorPilar(selectedPilar!!)
            }
            else -> {
                acaoRepository.obterTodasAcoes() // Novo método que precisaremos adicionar no AcaoRepository
            }
        }
        listaAcoes.addAll(filteredAcoes)
        acaoAdapter.notifyDataSetChanged()
    }

    /**
     * Inicia a [EditarAcaoActivity] para permitir a edição de uma [Acao] existente.
     *
     * @param acao O objeto [Acao] a ser editado.
     */
    private fun editarAcao(acao: Acao) {
        val intent = Intent(this, EditarAcaoActivity::class.java)
        intent.putExtra("acao_id", acao.id)
        intent.putExtra("acao_nome", acao.nome)
        intent.putExtra("acao_descricao", acao.descricao)
        intent.putExtra("acao_data_inicio", acao.dataInicio)
        intent.putExtra("acao_data_termino", acao.dataTermino)
        intent.putExtra("acao_codigo_responsavel", acao.responsavel)
        intent.putExtra("acao_aprovado", acao.aprovado)
        intent.putExtra("acao_finalizada", acao.finalizado)
        intent.putExtra("acao_id_pilar", acao.idPilar)
        intent.putExtra("acao_id_subpilar", acao.idSubpilar)
        intent.putExtra("acao_id_usuario", acao.idUsuario)
        startActivity(intent)
    }

    /**
     * Exclui uma [Acao] do banco de dados após validação.
     *
     * Se a ação tiver atividades associadas, a exclusão é impedida.
     * Caso contrário, a ação é removida do banco de dados e da lista exibida.
     *
     * @param acao O objeto [Acao] a ser excluído.
     */
    private fun excluirAcao(acao: Acao) {
        if(validarExclusaoAcao(acao) == true){
            val db = dbHelper.writableDatabase
            val whereClause = "${DatabaseHelper.COLUMN_ACAO_ID} = ?"
            val whereArgs = arrayOf(acao.id.toString())
            val deletedRows = db.delete(DatabaseHelper.TABLE_ACAO, whereClause, whereArgs)
            db.close()
            if (deletedRows > 0) {
                listaAcoes.remove(acao)
                acaoAdapter.notifyDataSetChanged()
                Toast.makeText(this, "Ação '${acao.nome}' excluída com sucesso!", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Erro ao excluir a ação.", Toast.LENGTH_SHORT).show()
            }
        }else{
            Toast.makeText(this, "Erro! Existem atividades existentes vinculadas a ação.", Toast.LENGTH_SHORT).show()
        }

    }

    /**
     * Valida se uma [Acao] pode ser excluída, verificando se há [Atividade]s associadas a ela.
     *
     * @param acao O objeto [Acao] a ser validado.
     * @return `true` se a ação pode ser excluída (não tem atividades associadas), `false` caso contrário.
     */
    fun validarExclusaoAcao(acao: Acao) : Boolean{
        val todasAtividades = atividadeRepository.obterTodasAtividades()
        var temAtividadeAssociada = false

        for(atividade in todasAtividades){
            if(atividade.idAcao == acao.id){
                temAtividadeAssociada = true
                break
            }
        }

        if(temAtividadeAssociada == true){
            return false
        }else{
            return true
        }
    }
}
