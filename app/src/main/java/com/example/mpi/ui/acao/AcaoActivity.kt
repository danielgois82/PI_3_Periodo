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

        binding.recyclerViewAcoes.layoutManager = LinearLayoutManager(this)
        acaoAdapter = AcaoAdapter(listaAcoes,
            onEditarClicked = { acao -> editarAcao(acao) },
            onExcluirClicked = { acao -> excluirAcao(acao) },
            this)
        binding.recyclerViewAcoes.adapter = acaoAdapter

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

    override fun onResume() {
        super.onResume()
        aplicarFiltros()
    }

    // Configurando os listeners para os Spinners
    private fun setupSpinners() {
        binding.spinnerPilar.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                selectedPilar = if (position > 0) pilares[position - 1] else null
                carregarSubpilaresParaSpinner(selectedPilar) // Recarrega subpilares ao mudar pilar
                aplicarFiltros() // Aplica filtro com o novo pilar/subpilar
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
            }
        }

        binding.spinnerSubpilar.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                selectedSubpilar = if (position > 0) subpilares[position - 1] else null
                aplicarFiltros() // Aplica filtro com o novo subpilar
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
            }
        }
    }

    // Carrega os Pilares para o Spinner de Pilares
    private fun carregarPilaresParaSpinner() {
        val calendarioPadrao = Calendario(1, 2025)
        pilares = pilarRepository.obterTodosPilares(calendarioPadrao)

        val pilarNames = mutableListOf("Todos os Pilares")
        pilarNames.addAll(pilares.map { it.nome })

        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, pilarNames)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerPilar.adapter = adapter
    }

    // Carrega os Subpilares para o Spinner de Subpilares com base no Pilar selecionado
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

    // Método para aplicar os filtros e carregar as ações na lista
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
