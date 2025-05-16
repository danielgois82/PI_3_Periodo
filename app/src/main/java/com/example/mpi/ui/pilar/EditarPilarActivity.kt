package com.example.mpi.ui.pilar

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.mpi.databinding.ActivityEditarPilarBinding
import com.example.mpi.data.DatabaseHelper

class EditarPilarActivity : AppCompatActivity() {

    private lateinit var binding: ActivityEditarPilarBinding
    private lateinit var dbHelper: DatabaseHelper
    private var pilarId: Long = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditarPilarBinding.inflate(layoutInflater)
        setContentView(binding.root)

        dbHelper = DatabaseHelper(this)


        val extras = intent.extras
        if (extras != null) {
            pilarId = extras.getLong("pilar_id", -1)
            val nome = extras.getString("pilar_nome")
            val descricao = extras.getString("pilar_descricao")
            val dataInicio = extras.getString("pilar_data_inicio")
            val dataTermino = extras.getString("pilar_data_termino")
            val aprovado = extras.getBoolean("pilar_aprovado")
            val percentual = extras.getDouble("pilar_percentual")
            val idCalendario = extras.getInt("pilar_id_calendario") // Recupera o ID do Calendário


            binding.etEditarNomePilar.setText(nome)
            binding.etEditarDescricaoPilar.setText(descricao)
            binding.etEditarDataInicio.setText(dataInicio)
            binding.etEditarDataTermino.setText(dataTermino)
            binding.etEditarIdCalendario.setText(idCalendario.toString()) // Preenche o ID do Calendário
            binding.tvExibirPercentual.text = String.format("%.2f%%", percentual * 100)
            binding.tvExibirAprovado.text = if (aprovado) "Sim" else "Não"
        }

        binding.btnSalvarEdicao.setOnClickListener {
            salvarEdicaoPilar()
        }

        binding.btnVoltarEditar.setOnClickListener {
            finish() // Simplesmente finaliza a EditarPilarActivity e volta para PilarActivity
        }
    }

    private fun salvarEdicaoPilar() {
        val nome = binding.etEditarNomePilar.text.toString()
        val descricao = binding.etEditarDescricaoPilar.text.toString()
        val dataInicio = binding.etEditarDataInicio.text.toString()
        val dataTermino = binding.etEditarDataTermino.text.toString()
        val idCalendarioStr = binding.etEditarIdCalendario.text.toString().trim() // Obtém o ID do Calendário

        if (nome.isEmpty() || descricao.isEmpty() || dataInicio.isEmpty() || dataTermino.isEmpty() || idCalendarioStr.isEmpty()) {
            Toast.makeText(this, "Preencha todos os campos!", Toast.LENGTH_SHORT).show()
            return
        }

        val idCalendario = idCalendarioStr.toIntOrNull()
        if (idCalendario == null) {
            Toast.makeText(this, "ID do Calendário inválido", Toast.LENGTH_SHORT).show()
            return
        }

        if (pilarId != -1L) {
            val db = dbHelper.writableDatabase
            val values = android.content.ContentValues().apply {
                put(DatabaseHelper.COLUMN_PILAR_NOME, nome)
                put(DatabaseHelper.COLUMN_PILAR_DESCRICAO, descricao)
                put(DatabaseHelper.COLUMN_PILAR_DATA_INICIO, dataInicio)
                put(DatabaseHelper.COLUMN_PILAR_DATA_TERMINO, dataTermino)
                put(DatabaseHelper.COLUMN_PILAR_ID_CALENDARIO, idCalendario)

            }

            val whereClause = "${DatabaseHelper.COLUMN_PILAR_ID} = ?" // Use a constante do ID
            val whereArgs = arrayOf(pilarId.toString())
            val rowsAffected = db.update(
                DatabaseHelper.TABLE_PILAR, // Use a constante da tabela
                values,
                whereClause,
                whereArgs
            )
            db.close() // Feche o banco de dados após a operação

            if (rowsAffected > 0) {
                Toast.makeText(this, "Pilar atualizado com sucesso!", Toast.LENGTH_SHORT).show()
                setResult(RESULT_OK)
                finish() // Volta para PilarActivity após a atualização
            } else {
                Toast.makeText(this, "Erro ao atualizar o pilar.", Toast.LENGTH_SHORT).show()
            }
        } else {
            Toast.makeText(this, "Erro: ID do pilar não encontrado.", Toast.LENGTH_SHORT).show()
        }
    }
}