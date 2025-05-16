package com.example.mpi.ui.atividade

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.mpi.databinding.ActivityCadastroAtividadeBinding
import com.example.mpi.data.DatabaseHelper
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.activity.enableEdgeToEdge
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.mpi.R
import com.example.mpi.ui.acao.Acao
import com.example.mpi.ui.pilar.Pilar

class CadastroAtividadeActivity : AppCompatActivity() {
    private lateinit var binding: ActivityCadastroAtividadeBinding
    private lateinit var dbHelper: DatabaseHelper
    private var listaAcoesNomes = mutableListOf<String>()
    private var listaAcoesObjetos = mutableListOf<Acao>()
    private var idAcaoSelecionada: Long = -1

override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    enableEdgeToEdge()
    binding = ActivityCadastroAtividadeBinding.inflate(layoutInflater)
    setContentView(binding.root)

    dbHelper = DatabaseHelper(this)


    carregarAcoesNoSpinner()
    binding.spinnerAcoes.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
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

    binding.btnConfirmarCadastroAtividade.setOnClickListener {
        val nome = binding.etNomeAtividade.text.toString()
        val descricao = binding.etDescricaoAtividade.text.toString()
        val dataInicio = binding.etDataInicioAtividade.text.toString().trim()
        val dataTermino = binding.etDataTerminoAtividade.text.toString().trim()
        val codigoResponsavel = binding.etCodigoResponsavelAtividade.text.toString().trim()
        val orcamentoStr = binding.etOrcamentoAtividade.text.toString().trim()

        if (nome.isEmpty() || descricao.isEmpty() || dataInicio.isEmpty() || dataTermino.isEmpty() || codigoResponsavel.isEmpty() || idAcaoSelecionada == -1L) {
            Toast.makeText(this, "Preencha todos os campos e selecione uma Ação", Toast.LENGTH_SHORT).show()
            return@setOnClickListener
        }

        val codigoResponsavelInt = codigoResponsavel.toIntOrNull()
        val orcamento = orcamentoStr.toDoubleOrNull()

        if (orcamento == null) {
            Toast.makeText(this, "Orçamento deve ser um número válido", Toast.LENGTH_SHORT).show()
            return@setOnClickListener
        }

        val db = dbHelper.writableDatabase
        val values = android.content.ContentValues().apply {
            put(DatabaseHelper.COLUMN_ATIVIDADE_NOME, nome)
            put(DatabaseHelper.COLUMN_ATIVIDADE_DESCRICAO, descricao)
            put(DatabaseHelper.COLUMN_ATIVIDADE_DATA_INICIO, dataInicio)
            put(DatabaseHelper.COLUMN_ATIVIDADE_DATA_TERMINO, dataTermino)
            put(DatabaseHelper.COLUMN_ATIVIDADE_RESPONSAVEL, codigoResponsavelInt)
            put(DatabaseHelper.COLUMN_ATIVIDADE_ORCAMENTO, orcamento)
            put(DatabaseHelper.COLUMN_ATIVIDADE_ID_ACAO, idAcaoSelecionada)
        }

        val newRowId = db.insert(DatabaseHelper.TABLE_ATIVIDADE, null, values)
        db.close()

        if (newRowId == -1L) {
            Toast.makeText(this, "Erro ao cadastrar — A Atividade já existe?", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(this, "Atividade cadastrada com sucesso! Atualize a página de listagem.", Toast.LENGTH_SHORT).show()
            setResult(RESULT_OK)
            finish()
        }
    }

    binding.btnVoltarAtividade.setOnClickListener {
        finish()
    }

    ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
        val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
        v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
        insets
    }

}

private fun carregarAcoesNoSpinner() {

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
        DatabaseHelper.COLUMN_ACAO_NOME
    )

    listaAcoesNomes.clear()
    listaAcoesObjetos.clear()
    listaAcoesNomes.add("Selecione A ação associada")

    with(cursor) {
        while (moveToNext()) {
            val id = getLong(getColumnIndexOrThrow(DatabaseHelper.COLUMN_PILAR_ID))
            val nome = getString(getColumnIndexOrThrow(DatabaseHelper.COLUMN_PILAR_NOME))
            listaAcoesObjetos.add(Acao(id, nome, "", "", "", 0,false,false, 0,0 , 0 )) // Cria objetos Pilar (outros campos não são necessários aqui)
            listaAcoesNomes.add(nome)
        }
    }
    cursor.close()
    db.close()

    val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, listaAcoesNomes)
    binding.spinnerAcoes.adapter = adapter
}
}

