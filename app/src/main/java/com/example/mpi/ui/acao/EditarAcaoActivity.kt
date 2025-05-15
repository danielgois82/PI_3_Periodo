package com.example.mpi.ui.acao

import android.content.ContentValues
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.RadioButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.mpi.data.DatabaseHelper
import com.example.mpi.databinding.ActivityEditarAcaoBinding
import com.example.mpi.ui.pilar.Pilar
import com.example.mpi.ui.subpilar.Subpilar

class EditarAcaoActivity : AppCompatActivity() {
    private lateinit var binding: ActivityEditarAcaoBinding
    private lateinit var dbHelper: DatabaseHelper
    private var acaoId: Long = -1
    private var listaPilaresNomes = mutableListOf<String>()
    private var listaPilaresObjetos = mutableListOf<Pilar>()
    private var listaSubpilaresNomes = mutableListOf<String>()
    private var listaSubpilaresObjetos = mutableListOf<Subpilar>()
    private var idVinculoSelecionado: Long = -1
    private var tipoVinculo: String = "" // "pilar" ou "subpilar"
    private var acaoAprovada: Int = 0
    private var acaoFinalizada: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditarAcaoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        dbHelper = DatabaseHelper(this)


        acaoId = intent.getLongExtra("acao_id", -1)
        if (acaoId == -1L) {
            Toast.makeText(this, "Erro: ID da ação não encontrado", Toast.LENGTH_SHORT).show()
            finish()
            return
        }


        binding.spinnerVinculoEditar.isEnabled = false
        binding.spinnerVinculoEditar.adapter =
            ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, listOf("Selecione o tipo de vínculo"))


        carregarDadosAcao()


        binding.radioGroupVinculoEditar.setOnCheckedChangeListener { group, checkedId ->
            val selectedRadioButton = findViewById<RadioButton>(checkedId)
            tipoVinculo = selectedRadioButton.text.toString().lowercase()
            binding.spinnerVinculoEditar.isEnabled = true
            idVinculoSelecionado = -1

            if (tipoVinculo == "pilar") {
                carregarPilaresNoSpinner()
            } else if (tipoVinculo == "subpilar") {
                carregarSubpilaresNoSpinner()
            }
        }

        // Listener para o Spinner
        binding.spinnerVinculoEditar.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
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

        binding.btnSalvarEdicao.setOnClickListener {
            salvarEdicaoAcao()
        }

        binding.btnVoltarEditar.setOnClickListener {
            finish()
        }
    }

    private fun carregarDadosAcao() {
        val db = dbHelper.readableDatabase
        val projection = arrayOf(
            DatabaseHelper.COLUMN_ACAO_NOME,
            DatabaseHelper.COLUMN_ACAO_DESCRICAO,
            DatabaseHelper.COLUMN_ACAO_DATA_INICIO,
            DatabaseHelper.COLUMN_ACAO_DATA_TERMINO,
            DatabaseHelper.COLUMN_ACAO_RESPONSAVEL,
            DatabaseHelper.COLUMN_ACAO_IS_APROVADO,
            DatabaseHelper.COLUMN_ACAO_IS_FINALIZADO,
            DatabaseHelper.COLUMN_ACAO_ID_PILAR,
            DatabaseHelper.COLUMN_ACAO_ID_SUBPILAR,
            DatabaseHelper.COLUMN_ACAO_ID_USUARIO
        )
        val selection = "${DatabaseHelper.COLUMN_ACAO_ID} = ?"
        val selectionArgs = arrayOf(acaoId.toString())
        val cursor = db.query(
            DatabaseHelper.TABLE_ACAO,
            projection,
            selection,
            selectionArgs,
            null,
            null,
            null
        )

        with(cursor) {
            if (moveToFirst()) {
                binding.etEditarNomeAcao.setText(getString(getColumnIndexOrThrow(DatabaseHelper.COLUMN_ACAO_NOME)))
                binding.etEditarDescricaoAcao.setText(getString(getColumnIndexOrThrow(DatabaseHelper.COLUMN_ACAO_DESCRICAO)))
                binding.etEditarDataInicio.setText(getString(getColumnIndexOrThrow(DatabaseHelper.COLUMN_ACAO_DATA_INICIO)))
                binding.etEditarDataTermino.setText(getString(getColumnIndexOrThrow(DatabaseHelper.COLUMN_ACAO_DATA_TERMINO)))
                binding.etEditarCodigoResponsavel.setText(getString(getColumnIndexOrThrow(DatabaseHelper.COLUMN_ACAO_RESPONSAVEL)))
                acaoAprovada = getInt(getColumnIndexOrThrow(DatabaseHelper.COLUMN_ACAO_IS_APROVADO))
                acaoFinalizada = getInt(getColumnIndexOrThrow(DatabaseHelper.COLUMN_ACAO_IS_FINALIZADO))
                val id_pilar = getLong(getColumnIndexOrThrow(DatabaseHelper.COLUMN_ACAO_ID_PILAR))
                val id_subpilar = getLong(getColumnIndexOrThrow(DatabaseHelper.COLUMN_ACAO_ID_SUBPILAR))

                if (id_pilar != 0L) {
                    tipoVinculo = "pilar"
                    idVinculoSelecionado = id_pilar
                    binding.radioPilarEditar.isChecked = true
                    carregarPilaresNoSpinner()
                } else if (id_subpilar != 0L) {
                    tipoVinculo = "subpilar"
                    idVinculoSelecionado = id_subpilar
                    binding.radioSubpilarEditar.isChecked = true
                    carregarSubpilaresNoSpinner()
                }

                binding.tvExibirAprovado.text = if (acaoAprovada == 1) "Sim" else "Não"
                binding.tvExibirFinalizada.text = if (acaoFinalizada == 1) "Sim" else "Não"
            } else {
                Toast.makeText(this@EditarAcaoActivity, "Erro: Ação não encontrada", Toast.LENGTH_SHORT).show()
                finish()
            }
        }
        cursor.close()
        db.close()
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
        binding.spinnerVinculoEditar.adapter = adapter

        if (idVinculoSelecionado != -1L && tipoVinculo == "pilar") {
            val index = listaPilaresObjetos.indexOfFirst { it.id == idVinculoSelecionado } + 1
            binding.spinnerVinculoEditar.setSelection(index)
        }

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
        binding.spinnerVinculoEditar.adapter = adapter


        if (idVinculoSelecionado != -1L && tipoVinculo == "subpilar") {
            val index = listaSubpilaresObjetos.indexOfFirst { it.id == idVinculoSelecionado } + 1
            binding.spinnerVinculoEditar.setSelection(index)
        }
    }

    private fun salvarEdicaoAcao() {
        val nome = binding.etEditarNomeAcao.text.toString()
        val descricao = binding.etEditarDescricaoAcao.text.toString()
        val dataInicio = binding.etEditarDataInicio.text.toString().trim()
        val dataTermino = binding.etEditarDataTermino.text.toString().trim()
        val codigoResponsavel = binding.etEditarCodigoResponsavel.text.toString().trim()

        if (nome.isEmpty() || descricao.isEmpty() || dataInicio.isEmpty() || dataTermino.isEmpty() || codigoResponsavel.isEmpty() || idVinculoSelecionado == -1L) {
            Toast.makeText(this, "Preencha todos os campos e selecione um Pilar ou Subpilar", Toast.LENGTH_SHORT).show()
            return
        }
        val codigoResponsavelInt = codigoResponsavel.toIntOrNull()


        val db = dbHelper.writableDatabase
        val values = ContentValues().apply {
            put(DatabaseHelper.COLUMN_ACAO_NOME, nome)
            put(DatabaseHelper.COLUMN_ACAO_DESCRICAO, descricao)
            put(DatabaseHelper.COLUMN_ACAO_DATA_INICIO, dataInicio)
            put(DatabaseHelper.COLUMN_ACAO_DATA_TERMINO, dataTermino)
            put(DatabaseHelper.COLUMN_ACAO_RESPONSAVEL, codigoResponsavelInt)

            if (tipoVinculo == "pilar") {
                put(DatabaseHelper.COLUMN_ACAO_ID_PILAR, idVinculoSelecionado)
                putNull(DatabaseHelper.COLUMN_ACAO_ID_SUBPILAR)
            } else if (tipoVinculo == "subpilar") {
                put(DatabaseHelper.COLUMN_ACAO_ID_SUBPILAR, idVinculoSelecionado)
                putNull(DatabaseHelper.COLUMN_ACAO_ID_PILAR)
            }
        }

        val selection = "${DatabaseHelper.COLUMN_ACAO_ID} = ?"
        val selectionArgs = arrayOf(acaoId.toString())
        val rowsAffected = db.update(DatabaseHelper.TABLE_ACAO, values, selection, selectionArgs)
        db.close()

        if (rowsAffected > 0) {
            Toast.makeText(this, "Ação atualizada com sucesso!", Toast.LENGTH_SHORT).show()
            setResult(RESULT_OK)
            finish()
        } else {
            Toast.makeText(this, "Erro ao atualizar ação", Toast.LENGTH_SHORT).show()
        }
    }
}
