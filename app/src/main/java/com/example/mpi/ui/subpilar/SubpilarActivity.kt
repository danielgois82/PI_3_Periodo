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

    override fun onResume() {
        super.onResume()
        aplicarFiltros()
    }

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

    // Carregando os Pilares para o Spinner de Pilares
    private fun carregarPilaresParaSpinner() {
        val calendarioPadrao = Calendario(1, 2025)
        pilares = pilarRepository.obterTodosPilares(calendarioPadrao)

        val pilarNames = mutableListOf("Todos os Pilares")
        pilarNames.addAll(pilares.map { it.nome })

        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, pilarNames)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerPilar.adapter = adapter
    }

    // Método para aplicar os filtros e carregar os subpilares na lista
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

    //Validar exclusão do subpilar
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