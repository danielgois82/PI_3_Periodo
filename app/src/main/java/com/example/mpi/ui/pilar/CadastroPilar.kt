package com.example.mpi.ui.pilar

import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.mpi.R
import com.example.mpi.data.DatabaseHelper
import com.example.mpi.databinding.ActivityCadastroPilarBinding


class cadastroPilar : AppCompatActivity() {

    private lateinit var binding: ActivityCadastroPilarBinding
    private lateinit var dbHelper: DatabaseHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityCadastroPilarBinding.inflate(layoutInflater)
        setContentView(binding.root)

        dbHelper = DatabaseHelper(this)

        binding.btnconfirmarCadastro.setOnClickListener {
            val nome = binding.etnomePilar.text.toString()
            val descricao = binding.etdescricaoPilar.text.toString()
            val dataInicio = binding.etdataInicio.text.toString().trim()
            val dataTermino = binding.etdataTermino.text.toString().trim()
            val idCalendarioStr = binding.etidCalendario.text.toString().trim()

            if (nome.isEmpty() || descricao.isEmpty() || dataInicio.isEmpty() || dataTermino.isEmpty() || idCalendarioStr.isEmpty()) {
                Toast.makeText(this, "Preencha todos os campos", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val idCalendario = idCalendarioStr.toIntOrNull()
            if (idCalendario == null) {
                Toast.makeText(this, "ID do Calendário inválido", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val db = dbHelper.writableDatabase
            val values = android.content.ContentValues().apply {
                put(DatabaseHelper.COLUMN_PILAR_NOME, nome)
                put(DatabaseHelper.COLUMN_PILAR_DESCRICAO, descricao)
                put(DatabaseHelper.COLUMN_PILAR_DATA_INICIO, dataInicio)
                put(DatabaseHelper.COLUMN_PILAR_DATA_TERMINO, dataTermino)
                put(DatabaseHelper.COLUMN_PILAR_PERCENTUAL, 0.0)
                put(DatabaseHelper.COLUMN_PILAR_IS_APROVADO, 0)
                put(DatabaseHelper.COLUMN_PILAR_ID_CALENDARIO, idCalendario)
            }

            val newRowId = db.insert(DatabaseHelper.TABLE_PILAR, null, values)
            db.close()

            if (newRowId == -1L) {
                Toast.makeText(this, "Erro ao cadastrar o Pilar.", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Pilar cadastrado com sucesso! Atualize a página de listagem.", Toast.LENGTH_SHORT).show()
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
}