package com.example.mpi.ui.acao

import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.RadioButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.mpi.data.DatabaseHelper
import com.example.mpi.databinding.ActivityCadastroAcaoBinding
import com.example.mpi.ui.pilar.Pilar
import com.example.mpi.ui.subpilar.Subpilar

class CadastroAcaoActivity : AppCompatActivity() {
    private lateinit var binding: ActivityCadastroAcaoBinding
    private lateinit var dbHelper: DatabaseHelper
    private var listaPilaresNomes = mutableListOf<String>()
    private var listaPilaresObjetos = mutableListOf<Pilar>()
    private var listaSubpilaresNomes = mutableListOf<String>()
    private var listaSubpilaresObjetos = mutableListOf<Subpilar>()
    private var idVinculoSelecionado: Long = -1
    private var tipoVinculo: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCadastroAcaoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        dbHelper = DatabaseHelper(this)


        binding.spinnerVinculo.isEnabled = false
        binding.spinnerVinculo.adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, listOf("Selecione o tipo de vínculo"))


        binding.radioGroupVinculo.setOnCheckedChangeListener { group, checkedId ->
            val selectedRadioButton = findViewById<RadioButton>(checkedId)
            tipoVinculo = selectedRadioButton.text.toString().lowercase()
            binding.spinnerVinculo.isEnabled = true
            idVinculoSelecionado = -1 // Resetar a seleção anterior

            if (tipoVinculo == "pilar") {
                carregarPilaresNoSpinner()
            } else if (tipoVinculo == "subpilar") {
                carregarSubpilaresNoSpinner()
            }
        }

        // Listener para o Spinner
        binding.spinnerVinculo.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                if (position > 0) {
                    if (tipoVinculo == "pilar") {
                        idVinculoSelecionado = listaPilaresObjetos[position - 1].id
                    } else if (tipoVinculo == "subpilar") {
                        idVinculoSelecionado = listaSubpilaresObjetos[position - 1].id
                    }
                } else {
                    idVinculoSelecionado = -1
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                idVinculoSelecionado = -1
            }
        }

        binding.confirmarCadastro.setOnClickListener {
            val nome = binding.nomeAcao.text.toString()
            val descricao = binding.descricaoAcao.text.toString()
            val dataInicio = binding.dataInicio.text.toString().trim()
            val dataTermino = binding.dataTermino.text.toString().trim()
            val codigoResponsavel = binding.codigoResponsavel.text.toString().trim()

            if (nome.isEmpty() || descricao.isEmpty() || dataInicio.isEmpty() || dataTermino.isEmpty() || codigoResponsavel.isEmpty() || idVinculoSelecionado == -1L) {
                Toast.makeText(this, "Preencha todos os campos e selecione um Pilar ou Subpilar", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val codigoResponsavelInt = codigoResponsavel.toIntOrNull()

            val db = dbHelper.writableDatabase
            val values = android.content.ContentValues().apply {
                put(DatabaseHelper.COLUMN_ACAO_NOME, nome)
                put(DatabaseHelper.COLUMN_ACAO_DESCRICAO, descricao)
                put(DatabaseHelper.COLUMN_ACAO_DATA_INICIO, dataInicio)
                put(DatabaseHelper.COLUMN_ACAO_DATA_TERMINO, dataTermino)
                put(DatabaseHelper.COLUMN_ACAO_RESPONSAVEL, codigoResponsavelInt)
                put(DatabaseHelper.COLUMN_ACAO_ID_USUARIO, 1)

                if (tipoVinculo == "pilar") {
                    put(DatabaseHelper.COLUMN_ACAO_ID_PILAR, idVinculoSelecionado)
                    putNull(DatabaseHelper.COLUMN_ACAO_ID_SUBPILAR)
                } else if (tipoVinculo == "subpilar") {
                    put(DatabaseHelper.COLUMN_ACAO_ID_SUBPILAR, idVinculoSelecionado)
                    putNull(DatabaseHelper.COLUMN_ACAO_ID_PILAR)
                }
            }

            val newRowId = db.insert(DatabaseHelper.TABLE_ACAO, null, values)
            db.close()

            if (newRowId == -1L) {
                Toast.makeText(this, "Erro ao cadastrar — A Ação já existe?", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Ação cadastrada com sucesso! Atualize a página de listagem.", Toast.LENGTH_SHORT).show()
                setResult(RESULT_OK)
                finish()
            }
        }

        binding.cancelarCadastro.setOnClickListener {
            finish()
        }
    }

    private fun carregarPilaresNoSpinner() {
        val db = dbHelper.readableDatabase
        val projection = arrayOf(DatabaseHelper.COLUMN_PILAR_ID, DatabaseHelper.COLUMN_PILAR_NOME)
        val cursor = db.query(DatabaseHelper.TABLE_PILAR, projection, null, null, null, null, DatabaseHelper.COLUMN_PILAR_NOME)

        listaPilaresNomes.clear()
        listaPilaresObjetos.clear()
        listaPilaresNomes.add("Selecione o Pilar")

        with(cursor) {
            while (moveToNext()) {
                val id = getLong(getColumnIndexOrThrow(DatabaseHelper.COLUMN_PILAR_ID))
                val nome = getString(getColumnIndexOrThrow(DatabaseHelper.COLUMN_PILAR_NOME))
                listaPilaresObjetos.add(Pilar(id, nome, "", "", "", false, 0.0, 0, 0))
                listaPilaresNomes.add(nome)
            }
        }
        cursor.close()
        db.close()

        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, listaPilaresNomes)
        binding.spinnerVinculo.adapter = adapter
    }

    private fun carregarSubpilaresNoSpinner() {
        val db = dbHelper.readableDatabase
        val projection = arrayOf(DatabaseHelper.COLUMN_SUBPILAR_ID, DatabaseHelper.COLUMN_SUBPILAR_NOME)
        val cursor = db.query(DatabaseHelper.TABLE_SUBPILAR, projection, null, null, null, null, DatabaseHelper.COLUMN_SUBPILAR_NOME)

        listaSubpilaresNomes.clear()
        listaSubpilaresObjetos.clear()
        listaSubpilaresNomes.add("Selecione o Subpilar")

        with(cursor) {
            while (moveToNext()) {
                val id = getLong(getColumnIndexOrThrow(DatabaseHelper.COLUMN_SUBPILAR_ID))
                val nome = getString(getColumnIndexOrThrow(DatabaseHelper.COLUMN_SUBPILAR_NOME))
                listaSubpilaresObjetos.add(Subpilar(id, nome, "", "", "", false, 0, 0))
                listaSubpilaresNomes.add(nome)
            }
        }
        cursor.close()
        db.close()

        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, listaSubpilaresNomes)
        binding.spinnerVinculo.adapter = adapter
    }
}
