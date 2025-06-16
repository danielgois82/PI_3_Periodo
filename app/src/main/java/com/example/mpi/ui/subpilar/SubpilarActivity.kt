package com.example.mpi.ui.subpilar

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.mpi.data.DatabaseHelper
import com.example.mpi.databinding.ActivitySubpilarBinding
import com.example.mpi.data.Pilar
import com.example.mpi.data.Subpilar
import com.example.mpi.data.Calendario
import com.example.mpi.repository.PilarRepository
import com.example.mpi.repository.SubpilarRepository
import com.example.mpi.repository.AcaoRepository

/**
 * [SubpilarActivity] é a tela principal para o gerenciamento de subpilares no aplicativo.
 *
 * Esta Activity exibe uma lista de subpilares em um `RecyclerView`, permitindo
 * filtrar subpilares por pilar pai, adicionar novos subpilares, editar e excluí-los.
 * Ela também carrega informações do usuário logado e interage com os repositórios
 * de Pilar, Subpilar e Ação para gerenciar os dados.
 */
class SubpilarActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySubpilarBinding
    private lateinit var dbHelper: DatabaseHelper
    private lateinit var subpilarAdapter: SubpilarAdapter
    private val listaSubpilares = mutableListOf<Subpilar>()
    private lateinit var pilarRepository: PilarRepository
    private lateinit var acaoRepository: AcaoRepository
    private lateinit var subpilarRepository: SubpilarRepository

    private var pilares: List<Pilar> = emptyList()
    private var selectedPilar: Pilar? = null

    /**
     * Chamado quando a Activity é criada pela primeira vez.
     *
     * Inicializa o binding, o DatabaseHelper e os repositórios.
     * Recupera informações do usuário logado da Intent.
     * Configura o `RecyclerView` com o [SubpilarAdapter].
     * Inicializa e carrega o spinner de pilares e define os listeners para os botões.
     *
     * @param savedInstanceState Se não for nulo, esta Activity está sendo recriada
     * a partir de um estado salvo anteriormente.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivitySubpilarBinding.inflate(layoutInflater)
        setContentView(binding.root)

        dbHelper = DatabaseHelper(this)
        pilarRepository = PilarRepository.getInstance(this)
        subpilarRepository = SubpilarRepository.getInstance(this)
        acaoRepository = AcaoRepository.getInstance(this)

        ////////////////////// Carregando informações do usuário////////////////////////////////
        val intentExtra = intent
        val idUsuario = intentExtra.getIntExtra("idUsuario", 999999)
        val nomeUsuario = intentExtra.getStringExtra("nomeUsuario") ?: "Nome de usuário desconhecido"
        val tipoUsuario = intentExtra.getStringExtra("tipoUsuario") ?: "Tipo de usuário desconhecido"
        val tag = "SubpilarActivityLog"
        val mensagemLog = "SubpilarActivity iniciada - ID Usuário: $idUsuario, Nome: $nomeUsuario"
        Log.d(tag, mensagemLog)
        ////////////////////////////////////////////////////////////////////////////////

        binding.recyclerViewSubpilares.layoutManager = LinearLayoutManager(this)
        subpilarAdapter = SubpilarAdapter(
            listaSubpilares,
            { subpilar -> editarSubpilar(subpilar) },
            { subpilar -> excluirSubpilar(subpilar) })
        binding.recyclerViewSubpilares.adapter = subpilarAdapter

        // Configura o Spinner e carrega os Pilares
        setupSpinner()
        carregarPilaresParaSpinner()

        binding.btnAdicionarSubpilar.setOnClickListener {
            val intent = Intent(this, cadastroSubpilar::class.java)
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
     * Chamado quando a Activity é retomada após estar em um estado pausado.
     *
     * Garante que a lista de subpilares seja recarregada e atualizada sempre que a Activity
     * volta ao primeiro plano (por exemplo, após o retorno de uma Activity de cadastro ou edição),
     * aplicando os filtros atuais.
     */
    override fun onResume() {
        super.onResume()
        aplicarFiltros()
    }

    /**
     * Configura o listener para o spinner de seleção de pilares.
     *
     * Quando um item é selecionado no spinner, atualiza a variável `selectedPilar`
     * e chama `aplicarFiltros()` para recarregar a lista de subpilares com base
     * na nova seleção.
     */
    private fun setupSpinner() {
        binding.spinnerPilar.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                selectedPilar = if (position > 0) pilares[position - 1] else null
                aplicarFiltros()
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
            }
        }
    }

    /**
     * Carrega a lista de todos os pilares do repositório e popula o spinner.
     *
     * Adiciona uma opção "Todos os Pilares" no início do spinner para permitir
     * visualizar todos os subpilares sem filtro.
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
     * Aplica os filtros selecionados no spinner e recarrega a lista de subpilares.
     *
     * Se um pilar específico estiver selecionado, carrega apenas os subpilares
     * associados a ele. Caso contrário, carrega todos os subpilares.
     * Atualiza o `RecyclerView` com a nova lista.
     */
    private fun aplicarFiltros() {
        listaSubpilares.clear()

        val filteredSubpilares = if (selectedPilar != null) {
            subpilarRepository.obterTodosSubpilares(selectedPilar!!)
        } else {
            subpilarRepository.obterTodosSubpilares()
        }
        listaSubpilares.addAll(filteredSubpilares)
        subpilarAdapter.notifyDataSetChanged()
    }

    /**
     * Abre a [EditarSubpilarActivity] para permitir a edição de um [Subpilar] específico.
     *
     * Passa todos os detalhes do subpilar como extras na Intent para a Activity de edição.
     *
     * @param subpilar O objeto [Subpilar] a ser editado.
     */
    private fun editarSubpilar(subpilar: Subpilar) {
        val intent = Intent(this, EditarSubpilarActivity::class.java)
        intent.putExtra("subpilar_id", subpilar.id)
        intent.putExtra("subpilar_nome", subpilar.nome)
        intent.putExtra("subpilar_descricao", subpilar.descricao)
        intent.putExtra("subpilar_data_inicio", subpilar.dataInicio)
        intent.putExtra("subpilar_data_termino", subpilar.dataTermino)
        intent.putExtra("subpilar_id_pilar", subpilar.idPilar)
        intent.putExtra("subpilar_id_usuario", subpilar.idUsuario)
        startActivity(intent)
    }

    /**
     * Exclui um [Subpilar] do banco de dados.
     *
     * Antes de excluir, valida se o subpilar pode ser excluído (ou seja, se não tem ações vinculadas).
     * Se a exclusão for bem-sucedida, remove o subpilar da lista e notifica o adaptador.
     * Exibe um Toast com o resultado da operação.
     *
     * @param subpilar O objeto [Subpilar] a ser excluído.
     */
    private fun excluirSubpilar(subpilar: Subpilar) {
        if(validarExclusaoSubpilar(subpilar) == true){
            val db = dbHelper.writableDatabase
            val whereClause = "${DatabaseHelper.COLUMN_SUBPILAR_ID} = ?"
            val whereArgs = arrayOf(subpilar.id.toString())
            val deletedRows = db.delete(DatabaseHelper.TABLE_SUBPILAR, whereClause, whereArgs)
            if (deletedRows > 0) {
                listaSubpilares.remove(subpilar)
                subpilarAdapter.notifyDataSetChanged()
                android.widget.Toast.makeText(this, "Subpilar '${subpilar.nome}' excluído com sucesso!", android.widget.Toast.LENGTH_SHORT).show()
            } else {
                android.widget.Toast.makeText(this, "Erro ao excluir o subpilar.", android.widget.Toast.LENGTH_SHORT).show()
            }
        }else{
            android.widget.Toast.makeText(this, "Erro! Existem ações existentes vinculadas ao subpilar.", android.widget.Toast.LENGTH_SHORT).show()

        }
    }

    /**
     * Valida se um [Subpilar] pode ser excluído.
     *
     * Retorna `true` se o subpilar não tiver nenhuma [Acao] associada,
     * e `false` caso contrário.
     *
     * @param subpilar O [Subpilar] a ser validado para exclusão.
     * @return `true` se o subpilar pode ser excluído, `false` caso contrário.
     */
    fun validarExclusaoSubpilar(subpilar: Subpilar) : Boolean{
        val todasAcoes = acaoRepository.obterTodasAcoes()
        var temAcaoAssociada = false

        for(acao in todasAcoes){
            if(acao.idSubpilar == subpilar.id){
                temAcaoAssociada = true
                break
            }
        }

        if(temAcaoAssociada == true){
            return false
        }else{
            return true
        }
    }
}