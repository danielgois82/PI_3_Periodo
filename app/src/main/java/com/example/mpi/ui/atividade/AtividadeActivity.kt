package com.example.mpi.ui.atividade

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.mpi.databinding.ActivityAtividadeBinding
import com.example.mpi.data.DatabaseHelper
import com.example.mpi.data.Atividade
import com.example.mpi.data.Pilar
import com.example.mpi.data.Subpilar
import com.example.mpi.data.Acao
import com.example.mpi.data.Calendario
import com.example.mpi.repository.AcaoRepository
import com.example.mpi.repository.PilarRepository
import com.example.mpi.repository.SubpilarRepository
import com.example.mpi.repository.PercentualAtividadeRepository

class AtividadeActivity : AppCompatActivity() {

    val USUARIO_ANALISTA = "ANALISTA"
    val USUARIO_COORDENADOR = "COORDENADOR"
    val USUARIO_GESTOR = "GESTOR"

    private lateinit var binding: ActivityAtividadeBinding
    private lateinit var dbHelper: DatabaseHelper
    private lateinit var atividadeAdapter: AtividadeAdapter
    private val listaAtividades = mutableListOf<Atividade>()
    private lateinit var percentualAtividadeRepository: PercentualAtividadeRepository

    private lateinit var pilarRepository: PilarRepository
    private lateinit var subpilarRepository: SubpilarRepository
    private lateinit var acaoRepository: AcaoRepository

    private var pilares: List<Pilar> = emptyList()
    private var subpilares: List<Subpilar> = emptyList()
    private var acoes: List<Acao> = emptyList()

    private var selectedPilar: Pilar? = null
    private var selectedSubpilar: Subpilar? = null
    private var selectedAcao: Acao? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAtividadeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        dbHelper = DatabaseHelper(this)
        pilarRepository = PilarRepository.getInstance(this)
        subpilarRepository = SubpilarRepository.getInstance(this)
        acaoRepository = AcaoRepository.getInstance(this)
        percentualAtividadeRepository = PercentualAtividadeRepository.getInstance(this)

        ////////////////////// Carregando informações do usuário////////////////////////////////
        val intentExtra = intent
        val idUsuario = intentExtra.getIntExtra("idUsuario", 999999)
        val nomeUsuario = intentExtra.getStringExtra("nomeUsuario") ?: "Nome de usuário desconhecido"
        val tipoUsuario = intentExtra.getStringExtra("tipoUsuario") ?: "Tipo de usuário desconhecido"
        val tag = "AtividadeActivityLog"
        val mensagemLog = "AtividadeActivity iniciada - ID Usuário: $idUsuario, Nome: $nomeUsuario"
        Log.d(tag, mensagemLog)
        ////////////////////////////////////////////////////////////////////////////////

        binding.recyclerViewAtividades.layoutManager = LinearLayoutManager(this)
        atividadeAdapter = AtividadeAdapter(
            listaAtividades,
            { atividade -> editarAtividade(atividade) },
            { atividade -> excluirAtividade(atividade) })
        binding.recyclerViewAtividades.adapter = atividadeAdapter

        if (tipoUsuario.uppercase() == USUARIO_GESTOR) {
            binding.btnCadastrarAtividade.visibility = View.GONE
        }

        setupSpinners()
        carregarPilaresParaSpinner()

        binding.btnCadastrarAtividade.setOnClickListener {
            val intent = Intent(this, CadastroAtividadeActivity::class.java)
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

    private fun setupSpinners() {
        binding.spinnerPilar.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                selectedPilar = if (position > 0) pilares[position - 1] else null
                carregarSubpilaresParaSpinner(selectedPilar)
                carregarAcoesParaSpinner(selectedPilar, null)
                aplicarFiltros()
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
            }
        }

        binding.spinnerSubpilar.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                selectedSubpilar = if (position > 0) subpilares[position - 1] else null
                carregarAcoesParaSpinner(selectedPilar, selectedSubpilar)
                aplicarFiltros()
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
            }
        }

        binding.spinnerAcao.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                selectedAcao = if (position > 0) acoes[position - 1] else null
                aplicarFiltros()
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
            }
        }
    }

    private fun carregarPilaresParaSpinner() {
        val calendarioPadrao = Calendario(1, 2025)
        pilares = pilarRepository.obterTodosPilares(calendarioPadrao)

        val pilarNames = mutableListOf("Todos os Pilares")
        pilarNames.addAll(pilares.map { it.nome })

        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, pilarNames)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerPilar.adapter = adapter
    }

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
        binding.spinnerAcao.setSelection(0)
    }

    private fun carregarAcoesParaSpinner(pilar: Pilar?, subpilar: Subpilar?) {
        acoes = when {
            subpilar != null -> acaoRepository.obterAcoesPorSubpilar(subpilar)
            pilar != null -> acaoRepository.obterAcoesPorPilar(pilar)
            else -> emptyList()
        }

        val acaoNames = mutableListOf("Todas as Ações")
        acaoNames.addAll(acoes.map { it.nome })

        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, acaoNames)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerAcao.adapter = adapter

        binding.spinnerAcao.setSelection(0)
    }

    private fun aplicarFiltros() {
        listaAtividades.clear()
        val db = dbHelper.readableDatabase

        var selection: String? = null
        var selectionArgs: Array<String>? = null

        when {
            selectedAcao != null -> {
                selection = "${DatabaseHelper.COLUMN_ATIVIDADE_ID_ACAO} = ?"
                selectionArgs = arrayOf(selectedAcao!!.id.toString())
            }
            selectedSubpilar != null -> {
                val acoesDoSubpilar = acaoRepository.obterAcoesPorSubpilar(selectedSubpilar!!) // Chamada atualizada
                if (acoesDoSubpilar.isNotEmpty()) {
                    val acaoIds = acoesDoSubpilar.joinToString(",") { it.id.toString() }
                    selection = "${DatabaseHelper.COLUMN_ATIVIDADE_ID_ACAO} IN ($acaoIds)"
                    selectionArgs = null
                } else {
                    atividadeAdapter.notifyDataSetChanged()
                    return
                }
            }
            selectedPilar != null -> {
                val acoesDoPilar = acaoRepository.obterAcoesPorPilar(selectedPilar!!)
                if (acoesDoPilar.isNotEmpty()) {
                    val acaoIds = acoesDoPilar.joinToString(",") { it.id.toString() }
                    selection = "${DatabaseHelper.COLUMN_ATIVIDADE_ID_ACAO} IN ($acaoIds)"
                    selectionArgs = null
                } else {
                    atividadeAdapter.notifyDataSetChanged()
                    return
                }
            }
        }

        val cursor = db.query(
            DatabaseHelper.TABLE_ATIVIDADE,
            arrayOf(
                DatabaseHelper.COLUMN_ATIVIDADE_ID,
                DatabaseHelper.COLUMN_ATIVIDADE_NOME,
                DatabaseHelper.COLUMN_ATIVIDADE_DESCRICAO,
                DatabaseHelper.COLUMN_ATIVIDADE_DATA_INICIO,
                DatabaseHelper.COLUMN_ATIVIDADE_DATA_TERMINO,
                DatabaseHelper.COLUMN_ATIVIDADE_RESPONSAVEL,
                DatabaseHelper.COLUMN_ATIVIDADE_IS_APROVADO,
                DatabaseHelper.COLUMN_ATIVIDADE_IS_FINALIZADO,
                DatabaseHelper.COLUMN_ATIVIDADE_ORCAMENTO,
                DatabaseHelper.COLUMN_ATIVIDADE_ID_ACAO,
                DatabaseHelper.COLUMN_ATIVIDADE_ID_USUARIO
            ),
            selection,
            selectionArgs,
            null,
            null,
            null
        )

        with(cursor) {
            while (moveToNext()) {
                val id = getInt(getColumnIndexOrThrow(DatabaseHelper.COLUMN_ATIVIDADE_ID))
                val nome = getString(getColumnIndexOrThrow(DatabaseHelper.COLUMN_ATIVIDADE_NOME))
                val descricao = getString(getColumnIndexOrThrow(DatabaseHelper.COLUMN_ATIVIDADE_DESCRICAO))
                val dataInicio = getString(getColumnIndexOrThrow(DatabaseHelper.COLUMN_ATIVIDADE_DATA_INICIO))
                val dataTermino = getString(getColumnIndexOrThrow(DatabaseHelper.COLUMN_ATIVIDADE_DATA_TERMINO))
                val responsavel = getInt(getColumnIndexOrThrow(DatabaseHelper.COLUMN_ATIVIDADE_RESPONSAVEL))
                val aprovado = getInt(getColumnIndexOrThrow(DatabaseHelper.COLUMN_ATIVIDADE_IS_APROVADO)) > 0
                val finalizado = getInt(getColumnIndexOrThrow(DatabaseHelper.COLUMN_ATIVIDADE_IS_FINALIZADO)) > 0
                val orcamento = getDouble(getColumnIndexOrThrow(DatabaseHelper.COLUMN_ATIVIDADE_ORCAMENTO))
                val idAcao = getInt(getColumnIndexOrThrow(DatabaseHelper.COLUMN_ATIVIDADE_ID_ACAO))
                val idUsuario = getInt(getColumnIndexOrThrow(DatabaseHelper.COLUMN_ATIVIDADE_ID_USUARIO))

                listaAtividades.add(
                    Atividade(
                        id,
                        nome,
                        descricao,
                        dataInicio,
                        dataTermino,
                        responsavel,
                        aprovado,
                        finalizado,
                        orcamento,
                        idAcao,
                        idUsuario
                    )
                )
            }
        }
        cursor.close()
        db.close()
        atividadeAdapter.notifyDataSetChanged()
    }
    private fun editarAtividade(atividade: Atividade) {
        val intent = Intent(this, EditarAtividadeActivity::class.java)
        intent.putExtra("atividade_id", atividade.id)
        intent.putExtra("atividade_nome", atividade.nome)
        intent.putExtra("atividade_descricao", atividade.descricao)
        intent.putExtra("atividade_data_inicio", atividade.dataInicio)
        intent.putExtra("atividade_data_termino", atividade.dataTermino)
        intent.putExtra("atividade_codigo_responsavel", atividade.responsavel)
        intent.putExtra("atividade_aprovado", atividade.aprovado)
        intent.putExtra("atividade_finalizada", atividade.finalizado)
        intent.putExtra("atividade_orcamento", atividade.orcamento)
        intent.putExtra("atividade_id_acao", atividade.idAcao)
        intent.putExtra("atividade_id_usuario", atividade.idUsuario)
        startActivity(intent)
    }

    private fun excluirAtividade(atividade: Atividade) {
        percentualAtividadeRepository.removerPercentuaisAtividade(atividade)
        val db = dbHelper.writableDatabase
        val whereClause = "${DatabaseHelper.COLUMN_ATIVIDADE_ID} = ?"
        val whereArgs = arrayOf(atividade.id.toString())
        val deletedRows = db.delete(DatabaseHelper.TABLE_ATIVIDADE, whereClause, whereArgs)
        db.close()
        if (deletedRows > 0) {
            listaAtividades.remove(atividade)
            atividadeAdapter.notifyDataSetChanged()
            Toast.makeText(this, "Atividade '${atividade.nome}' excluída com sucesso!", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(this, "Erro ao excluir a atividade.", Toast.LENGTH_SHORT).show()
        }
    }
}
