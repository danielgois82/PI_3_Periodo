package com.example.mpi.ui.subpilar

import android.content.ContentValues
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.mpi.data.DatabaseHelper
import com.example.mpi.databinding.ActivityEditarSubpilarBinding
import com.example.mpi.ui.pilar.Pilar

class EditarSubpilarActivity : AppCompatActivity() {

    private lateinit var binding: ActivityEditarSubpilarBinding
    private lateinit var dbHelper: DatabaseHelper
    private var subpilarId: Long = -1
    private var idPilarAtual: Long = -1
    private var listaPilaresNomesEditar = mutableListOf<String>()
    private var listaPilaresObjetosEditar = mutableListOf<Pilar>()
    private var novoIdPilarSelecionado: Long = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditarSubpilarBinding.inflate(layoutInflater)
        setContentView(binding.root)

        dbHelper = DatabaseHelper(this)


        val extras = intent.extras
        if (extras != null) {
            subpilarId = extras.getLong("subpilar_id", -1)
            val nome = extras.getString("subpilar_nome")
            val descricao = extras.getString("subpilar_descricao")
            val dataInicio = extras.getString("subpilar_data_inicio")
            val dataTermino = extras.getString("subpilar_data_termino")
            val aprovado = extras.getBoolean("subpilar_aprovado")
            idPilarAtual = extras.getLong("subpilar_id_pilar", -1)


            binding.etEditarNomeSubpilar.setText(nome)
            binding.etEditarDescricaoSubpilar.setText(descricao)
            binding.etEditarDataInicio.setText(dataInicio)
            binding.etEditarDataTermino.setText(dataTermino)
            binding.tvExibirAprovado.text = if (aprovado) "Sim" else "Não"

            carregarPilaresNoSpinnerEditar(idPilarAtual)
        }

        binding.spinnerPilaresEditar.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                if (position > 0) {
                    novoIdPilarSelecionado = listaPilaresObjetosEditar[position - 1].id
                } else {
                    novoIdPilarSelecionado = -1
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                novoIdPilarSelecionado = idPilarAtual // Mantém o pilar atual se nada for selecionado
            }
        }

        binding.btnSalvarEdicao.setOnClickListener {
            salvarEdicaoSubpilar()
        }

        binding.btnVoltarEditar.setOnClickListener {
            finish()
        }
    }

    private fun carregarPilaresNoSpinnerEditar(idPilarSelecionado: Long) {
        val db = dbHelper.readableDatabase
        val projection = arrayOf(DatabaseHelper.COLUMN_PILAR_ID, DatabaseHelper.COLUMN_PILAR_NOME)
        val cursor = db.query(DatabaseHelper.TABLE_PILAR, projection, null, null, null, null, DatabaseHelper.COLUMN_PILAR_NOME)

        listaPilaresNomesEditar.clear()
        listaPilaresObjetosEditar.clear()
        listaPilaresNomesEditar.add("Selecione o Pilar Pai") // Hint

        var pilarSelecionadoPosition = 0

        with(cursor) {
            while (moveToNext()) {
                val id = getLong(getColumnIndexOrThrow(DatabaseHelper.COLUMN_PILAR_ID))
                val nome = getString(getColumnIndexOrThrow(DatabaseHelper.COLUMN_PILAR_NOME))
                listaPilaresObjetosEditar.add(Pilar(id, nome, "", "", "", false, 0.0, 0, 0))
                listaPilaresNomesEditar.add(nome)
                if (id == idPilarSelecionado) {
                    pilarSelecionadoPosition = listaPilaresNomesEditar.size - 1
                }
            }
        }
        cursor.close()
        db.close()

        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, listaPilaresNomesEditar)
        binding.spinnerPilaresEditar.adapter = adapter
        binding.spinnerPilaresEditar.setSelection(pilarSelecionadoPosition)
        novoIdPilarSelecionado = idPilarSelecionado // Inicializa com o pilar atual
    }

    private fun salvarEdicaoSubpilar() {
        val nome = binding.etEditarNomeSubpilar.text.toString()
        val descricao = binding.etEditarDescricaoSubpilar.text.toString()
        val dataInicio = binding.etEditarDataInicio.text.toString()
        val dataTermino = binding.etEditarDataTermino.text.toString()

        if (nome.isEmpty() || descricao.isEmpty() || dataInicio.isEmpty() || dataTermino.isEmpty() || novoIdPilarSelecionado == -1L) {
            Toast.makeText(this, "Preencha todos os campos e selecione um Pilar Pai", Toast.LENGTH_SHORT).show()
            return
        }

        if (subpilarId != -1L) {
            val db = dbHelper.writableDatabase
            val values = ContentValues().apply {
                put(DatabaseHelper.COLUMN_SUBPILAR_NOME, nome)
                put(DatabaseHelper.COLUMN_SUBPILAR_DESCRICAO, descricao)
                put(DatabaseHelper.COLUMN_SUBPILAR_DATA_INICIO, dataInicio)
                put(DatabaseHelper.COLUMN_SUBPILAR_DATA_TERMINO, dataTermino)
                put(DatabaseHelper.COLUMN_SUBPILAR_ID_PILAR, novoIdPilarSelecionado)

            }

            val whereClause = "${DatabaseHelper.COLUMN_SUBPILAR_ID} = ?"
            val whereArgs = arrayOf(subpilarId.toString())
            val rowsAffected = db.update(
                DatabaseHelper.TABLE_SUBPILAR,
                values,
                whereClause,
                whereArgs
            )

            db.close()

            if (rowsAffected > 0) {
                Toast.makeText(this, "Subpilar atualizado com sucesso!", Toast.LENGTH_SHORT).show()
                setResult(RESULT_OK)
                finish()
            } else {
                Toast.makeText(this, "Erro ao atualizar o subpilar.", Toast.LENGTH_SHORT).show()
            }
        } else {
            Toast.makeText(this, "Erro: ID do subpilar não encontrado.", Toast.LENGTH_SHORT).show()
        }
    }
}