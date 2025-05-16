package com.example.mpi.ui.atividade

import android.content.ContentValues
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.mpi.databinding.ActivityEditarAtividadeBinding
import com.example.mpi.data.DatabaseHelper
import com.example.mpi.ui.acao.Acao
import android.database.sqlite.SQLiteDatabase

class EditarAtividadeActivity : AppCompatActivity() {
    private lateinit var binding: ActivityEditarAtividadeBinding
    private lateinit var dbHelper: DatabaseHelper
    private var atividadeId: Long = -1
    private var listaAcoesNomes = mutableListOf<String>()
    private var listaAcoesObjetos = mutableListOf<Acao>()
    private var acaoAdapter: ArrayAdapter<String>? = null
    private var idAcaoSelecionada: Long = -1
    private var atividadeAprovada: Int = 0
    private var atividadeFinalizada: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditarAtividadeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        dbHelper = DatabaseHelper(this)

        acaoAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, mutableListOf())
        acaoAdapter?.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerAcaoEditarAtividade.adapter = acaoAdapter

        carregarAcoes()

        val extras = intent.extras
        if (extras != null) {
            atividadeId = extras.getLong("atividade_id", -1)
            val nome = extras.getString("atividade_nome")
            val descricao = extras.getString("atividade_descricao")
            val dataInicio = extras.getString("atividade_data_inicio")
            val dataTermino = extras.getString("atividade_data_termino")
            val codigoResponsavel = extras.getInt("atividade_codigo_responsavel")
            val aprovado = extras.getBoolean("atividade_aprovado")
            val finalizada = extras.getBoolean("atividade_finalizada")
            val orcamento = extras.getDouble("atividade_orcamento", 0.0)
            val idAcao = extras.getLong("atividade_id_acao")

            binding.etEditarNomeAtividade.setText(nome)
            binding.etEditarDescricaoAtividade.setText(descricao)
            binding.etEditarDataInicio.setText(dataInicio)
            binding.etEditarDataTermino.setText(dataTermino)
            binding.etEditarCodigoResponsavel.setText(codigoResponsavel.toString())
            atividadeAprovada = if (aprovado) 1 else 0
            atividadeFinalizada = if (finalizada) 1 else 0
            binding.tvExibirAprovado.text = if (aprovado) "Sim" else "Não"
            binding.tvExibirFinalizada.text = if (finalizada) "Sim" else "Não"
            binding.etEditarOrcamentoAtividade.setText(if (orcamento != 0.0) String.format("%.2f", orcamento) else "")

            if (idAcao != -1L) {
                idAcaoSelecionada = idAcao
                val posicao = listaAcoesObjetos.indexOfFirst { it.id == idAcao }
                if (posicao != -1) {
                    binding.spinnerAcaoEditarAtividade.setSelection(posicao)
                }
            }
        }

        binding.spinnerAcaoEditarAtividade.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                if (position > 0) {
                    idAcaoSelecionada = listaAcoesObjetos[position - 1].id
                } else {
                    idAcaoSelecionada = -1
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                idAcaoSelecionada = -1
            }
        }

        binding.btnSalvarEdicao.setOnClickListener {
            salvarEdicaoAtividade()
        }

        binding.btnVoltarEditar.setOnClickListener {
            finish()
        }
    }

    private fun carregarAcoes() {
        listaAcoesObjetos.clear()
        val db = dbHelper.readableDatabase
        val projection = arrayOf(
            DatabaseHelper.COLUMN_ACAO_ID,
            DatabaseHelper.COLUMN_ACAO_NOME
        )

        val cursor = db.query(
            DatabaseHelper.TABLE_ACAO,
            projection,
            null,
            null,
            null,
            null,
            null
        )

        listaAcoesNomes.clear()
        listaAcoesNomes.add("Selecione a Ação")
        with(cursor) {
            while (moveToNext()) {
                val id = getLong(getColumnIndexOrThrow(DatabaseHelper.COLUMN_ACAO_ID))
                val nome = getString(getColumnIndexOrThrow(DatabaseHelper.COLUMN_ACAO_NOME))
                listaAcoesObjetos.add(Acao(id, nome, "", "", "", 0, false, false, 0, 0, 0))
                listaAcoesNomes.add(nome)
            }
        }
        cursor.close()
        db.close()

        acaoAdapter?.clear()
        acaoAdapter?.addAll(listaAcoesNomes)
        acaoAdapter?.notifyDataSetChanged()

        if (idAcaoSelecionada != -1L) {
            val posicao = listaAcoesObjetos.indexOfFirst { it.id == idAcaoSelecionada } + 1
            binding.spinnerAcaoEditarAtividade.setSelection(posicao)
        }
    }

    private fun salvarEdicaoAtividade() {
        val nome = binding.etEditarNomeAtividade.text.toString().trim()
        val descricao = binding.etEditarDescricaoAtividade.text.toString().trim()
        val dataInicio = binding.etEditarDataInicio.text.toString().trim()
        val dataTermino = binding.etEditarDataTermino.text.toString().trim()
        val codigoResponsavel = binding.etEditarCodigoResponsavel.text.toString().toIntOrNull() ?: 0
        val orcamentoStr = binding.etEditarOrcamentoAtividade.text.toString().trim()
        val orcamento = if (orcamentoStr.isNotEmpty()) orcamentoStr.replace(",", ".").toDouble() else 0.0
        val aprovado = if (binding.tvExibirAprovado.text.toString() == "Sim") 1 else 0
        val finalizada = if (binding.tvExibirFinalizada.text.toString() == "Sim") 1 else 0

        if (nome.isEmpty() || descricao.isEmpty() || dataInicio.isEmpty() || dataTermino.isEmpty() || codigoResponsavel == 0) {
            Toast.makeText(this, "Preencha todos os campos obrigatórios!", Toast.LENGTH_SHORT).show()
            return
        }

        if (idAcaoSelecionada == -1L) {
            Toast.makeText(this, "Selecione uma Ação!", Toast.LENGTH_SHORT).show()
            return
        }

        val db = dbHelper.writableDatabase
        db.beginTransaction()

        try {
            if (atividadeId == -1L) {
                if (verificarSeAtividadeExiste(db, nome)) {
                    Toast.makeText(
                        this,
                        "Erro ao cadastrar: Atividade com este nome já existe!",
                        Toast.LENGTH_SHORT
                    ).show()
                    db.setTransactionSuccessful()
                    db.endTransaction()
                    return
                }

                val values = ContentValues().apply {
                    put(DatabaseHelper.COLUMN_ATIVIDADE_NOME, nome)
                    put(DatabaseHelper.COLUMN_ATIVIDADE_DESCRICAO, descricao)
                    put(DatabaseHelper.COLUMN_ATIVIDADE_DATA_INICIO, dataInicio)
                    put(DatabaseHelper.COLUMN_ATIVIDADE_DATA_TERMINO, dataTermino)
                    put(DatabaseHelper.COLUMN_ATIVIDADE_RESPONSAVEL, codigoResponsavel)
                    put(DatabaseHelper.COLUMN_ATIVIDADE_ORCAMENTO, orcamento)
                    put(DatabaseHelper.COLUMN_ATIVIDADE_ID_ACAO, idAcaoSelecionada)
                    put(DatabaseHelper.COLUMN_ATIVIDADE_IS_APROVADO, aprovado)
                    put(DatabaseHelper.COLUMN_ATIVIDADE_IS_FINALIZADO, finalizada)
                }
                val newRowId = db.insert(DatabaseHelper.TABLE_ATIVIDADE, null, values)
                if (newRowId == -1L) {
                    Toast.makeText(
                        this,
                        "Erro ao cadastrar nova atividade.",
                        Toast.LENGTH_SHORT
                    ).show()
                    db.endTransaction()
                    return
                } else {
                    Toast.makeText(
                        this,
                        "Atividade cadastrada com sucesso!",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            } else {
                val values = ContentValues().apply {
                    put(DatabaseHelper.COLUMN_ATIVIDADE_NOME, nome)
                    put(DatabaseHelper.COLUMN_ATIVIDADE_DESCRICAO, descricao)
                    put(DatabaseHelper.COLUMN_ATIVIDADE_DATA_INICIO, dataInicio)
                    put(DatabaseHelper.COLUMN_ATIVIDADE_DATA_TERMINO, dataTermino)
                    put(DatabaseHelper.COLUMN_ATIVIDADE_RESPONSAVEL, codigoResponsavel)
                    put(DatabaseHelper.COLUMN_ATIVIDADE_ORCAMENTO, orcamento)
                    put(DatabaseHelper.COLUMN_ATIVIDADE_ID_ACAO, idAcaoSelecionada)
                    put(DatabaseHelper.COLUMN_ATIVIDADE_IS_APROVADO, aprovado)
                    put(DatabaseHelper.COLUMN_ATIVIDADE_IS_FINALIZADO, finalizada)
                }

                val whereClause = "${DatabaseHelper.COLUMN_ATIVIDADE_ID} = ?"
                val whereArgs = arrayOf(atividadeId.toString())
                val rowsAffected = db.update(
                    DatabaseHelper.TABLE_ATIVIDADE,
                    values,
                    whereClause,
                    whereArgs
                )
                if (rowsAffected == 0) {
                    Toast.makeText(
                        this,
                        "Erro ao atualizar a atividade.",
                        Toast.LENGTH_SHORT
                    ).show()
                    db.endTransaction()
                    return
                } else {
                    Toast.makeText(
                        this,
                        "Atividade atualizada com sucesso!",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }

            db.setTransactionSuccessful()
            db.endTransaction()
            setResult(RESULT_OK)
            finish()

        } catch (e: Exception) {
            db.endTransaction()
            Toast.makeText(this, "Erro: ${e.message}", Toast.LENGTH_LONG).show()
        } finally {
            db.close()
        }
    }

    private fun verificarSeAtividadeExiste(db: SQLiteDatabase, nome: String): Boolean {
        val cursor = db.query(
            DatabaseHelper.TABLE_ATIVIDADE,
            arrayOf(DatabaseHelper.COLUMN_ATIVIDADE_NOME),
            "${DatabaseHelper.COLUMN_ATIVIDADE_NOME} = ?",
            arrayOf(nome),
            null,
            null,
            null
        )
        val existe = cursor.count > 0
        cursor.close()
        return existe
    }
}

