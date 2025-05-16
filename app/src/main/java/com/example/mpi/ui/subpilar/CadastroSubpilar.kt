package com.example.mpi.ui.subpilar

import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.mpi.R
import android.view.View
import android.widget.AdapterView
import com.example.mpi.data.DatabaseHelper
import com.example.mpi.databinding.ActivityCadastroSubpilarBinding
import com.example.mpi.ui.pilar.Pilar

class cadastroSubpilar : AppCompatActivity() {
    private lateinit var binding: ActivityCadastroSubpilarBinding
    private lateinit var dbHelper: DatabaseHelper // Use o DatabaseHelper correto
    private var listaPilaresNomes = mutableListOf<String>()
    private var listaPilaresObjetos = mutableListOf<Pilar>()
    private var idPilarSelecionado: Long = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityCadastroSubpilarBinding.inflate(layoutInflater)
        setContentView(binding.root)

        dbHelper = DatabaseHelper(this)

        carregarPilaresNoSpinner()
        binding.spinnerPilares.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                if (position > 0) { // Ignora a hint (se houver)
                    idPilarSelecionado = listaPilaresObjetos[position - 1].id
                } else {
                    idPilarSelecionado = -1 // Nenhum pilar selecionado ou hint selecionada
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                idPilarSelecionado = -1
            }
        }

        binding.btnconfirmarCadastro.setOnClickListener {
            val nome = binding.etnomeSubpilar.text.toString()
            val descricao = binding.etdescricaoSubpilar.text.toString()
            val dataInicio = binding.etdataInicio.text.toString().trim()
            val dataTermino = binding.etdataTermino.text.toString().trim()

            if (nome.isEmpty() || descricao.isEmpty() || dataInicio.isEmpty() || dataTermino.isEmpty() || idPilarSelecionado == -1L) {
                Toast.makeText(this, "Preencha todos os campos e selecione um Pilar Pai", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val db = dbHelper.writableDatabase
            val values = android.content.ContentValues().apply {
                put(DatabaseHelper.COLUMN_SUBPILAR_NOME, nome)
                put(DatabaseHelper.COLUMN_SUBPILAR_DESCRICAO, descricao)
                put(DatabaseHelper.COLUMN_SUBPILAR_DATA_INICIO, dataInicio)
                put(DatabaseHelper.COLUMN_SUBPILAR_DATA_TERMINO, dataTermino)
                put(DatabaseHelper.COLUMN_SUBPILAR_ID_PILAR, idPilarSelecionado)
            }

            val newRowId = db.insert(DatabaseHelper.TABLE_SUBPILAR, null, values)
            db.close()

            if (newRowId == -1L) {
                Toast.makeText(this, "Erro ao cadastrar o Subpilar.", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Subpilar cadastrado com sucesso! Atualize a página de listagem.", Toast.LENGTH_SHORT).show()
                setResult(RESULT_OK)
                finish()
            }
        }

        binding.btnVoltar.setOnClickListener {
            finish()
        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    private fun carregarPilaresNoSpinner() {
        val db = dbHelper.readableDatabase
        val projection = arrayOf(
            DatabaseHelper.COLUMN_PILAR_ID,
            DatabaseHelper.COLUMN_PILAR_NOME
        )

        val cursor = db.query(
            DatabaseHelper.TABLE_PILAR,
            projection,
            null,
            null,
            null,
            null,
            DatabaseHelper.COLUMN_PILAR_NOME
        )

        listaPilaresNomes.clear()
        listaPilaresObjetos.clear()
        listaPilaresNomes.add("Selecione o Pilar associado")

        with(cursor) {
            while (moveToNext()) {
                val id = getLong(getColumnIndexOrThrow(DatabaseHelper.COLUMN_PILAR_ID))
                val nome = getString(getColumnIndexOrThrow(DatabaseHelper.COLUMN_PILAR_NOME))
                listaPilaresObjetos.add(Pilar(id, nome, "", "", "", false, 0.0, 0, 0)) // Cria objetos Pilar (outros campos não são necessários aqui)
                listaPilaresNomes.add(nome)
            }
        }
        cursor.close()
        db.close()

        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, listaPilaresNomes)
        binding.spinnerPilares.adapter = adapter
    }
}