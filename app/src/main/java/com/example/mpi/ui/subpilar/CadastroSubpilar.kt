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
import com.example.mpi.data.Pilar
import java.lang.NumberFormatException
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

/**
 * [cadastroSubpilar] é uma Activity responsável pelo cadastro de novos subpilares no sistema.
 *
 * Esta Activity permite que o usuário insira informações como nome, descrição,
 * data de início e data de término para um novo subpilar. É necessário selecionar
 * um "Pilar Pai" ao qual o subpilar estará associado.
 * Realiza validações de entrada, incluindo a consistência das datas do subpilar
 * em relação às datas do pilar pai, e interage com o banco de dados para persistir os dados.
 */
class cadastroSubpilar : AppCompatActivity() {
    private lateinit var binding: ActivityCadastroSubpilarBinding
    private lateinit var dbHelper: DatabaseHelper
    private var listaPilaresNomes = mutableListOf<String>()
    private var listaPilaresObjetos = mutableListOf<Pilar>()
    private var idPilarSelecionado: Int = -1
    private var idUsuarioRecebido: Int = -1

    /**
     * Chamado quando a Activity é criada pela primeira vez.
     *
     * Inicializa o binding, o DatabaseHelper.
     * Recupera o ID do usuário logado da Intent.
     * Carrega os pilares no spinner e configura seu listener.
     * Configura o listener para o botão de confirmação de cadastro,
     * realizando validações de dados e persistência no banco de dados.
     * Configura o listener para o botão de voltar.
     *
     * @param savedInstanceState Se não for nulo, esta Activity está sendo recriada
     * a partir de um estado salvo anteriormente.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityCadastroSubpilarBinding.inflate(layoutInflater)
        setContentView(binding.root)

        dbHelper = DatabaseHelper(this)


        // Recebendo os dados de usuário
        val extras = intent.extras
        if (extras != null) {
            idUsuarioRecebido = extras.getInt("idUsuario", 999999)
            val nomeUsuario = extras.getString("nomeUsuario") ?: "Nome de usuário desconhecido"
            val tipoUsuario = extras.getString("tipoUsuario") ?: "Tipo de usuário desconhecido"
        }


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

            if (nome.isEmpty() || descricao.isEmpty() || dataInicio.isEmpty() || dataTermino.isEmpty() || idPilarSelecionado == -1L.toInt()) {
                Toast.makeText(this, "Preencha todos os campos e selecione um Pilar Pai", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Validação das datas informadas
            val dataInicioFormatada = validarEFormatarDataInicial(dataInicio)
            val dataTerminoFormatada = validarEFormatarDataFinal(dataTermino, dataInicio)

            if (dataInicioFormatada == null) {
                Toast.makeText(this, "Data de Início inválida. Use o formato dd/mm/aaaa.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (dataTerminoFormatada == null) {
                Toast.makeText(this, "Data de Término inválida. Use o formato dd/mm/aaaa.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val db = dbHelper.writableDatabase
            val values = android.content.ContentValues().apply {
                put(DatabaseHelper.COLUMN_SUBPILAR_NOME, nome)
                put(DatabaseHelper.COLUMN_SUBPILAR_DESCRICAO, descricao)
                put(DatabaseHelper.COLUMN_SUBPILAR_DATA_INICIO, dataInicioFormatada)
                put(DatabaseHelper.COLUMN_SUBPILAR_DATA_TERMINO, dataTerminoFormatada)
                put(DatabaseHelper.COLUMN_SUBPILAR_ID_PILAR, idPilarSelecionado)
                put(DatabaseHelper.COLUMN_SUBPILAR_ID_USUARIO, idUsuarioRecebido)
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

    /**
     * Carrega a lista de pilares do banco de dados e popula o spinner de pilares.
     *
     * Adiciona uma opção de "hint" ("Selecione o Pilar associado") no início do spinner.
     * Mapeia os nomes dos pilares para exibição e armazena os objetos Pilar para referências de ID.
     */
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
                val id = getInt(getColumnIndexOrThrow(DatabaseHelper.COLUMN_PILAR_ID))
                val nome = getString(getColumnIndexOrThrow(DatabaseHelper.COLUMN_PILAR_NOME))
                listaPilaresObjetos.add(Pilar(id, nome, "", "", "",  0.0, 0, 0)) // Cria objetos Pilar
                listaPilaresNomes.add(nome)
            }
        }
        cursor.close()
        db.close()

        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, listaPilaresNomes)
        binding.spinnerPilares.adapter = adapter
    }

    /**
     * Valida e formata a data de início de um subpilar.
     *
     * Verifica o formato da data (dd/MM/yyyy), a validade dos valores (dia, mês, ano)
     * e, crucialmente, se a data de início do subpilar está dentro do período
     * (entre a data de início e término) do pilar pai selecionado.
     * Também valida se o ano do subpilar é o mesmo do pilar pai.
     *
     * @param dataSubpilarStr A string da data de início do subpilar a ser validada (ex: "05/03/2025").
     * @return A string da data formatada se for válida, ou `null` caso contrário.
     */
    private fun validarEFormatarDataInicial(dataSubpilarStr: String): String? {
        if (dataSubpilarStr.isNullOrEmpty()) {
            return null
        }

        val partesSubpilar = dataSubpilarStr.split("/")
        if (partesSubpilar.size != 3) {
            return null
        }

        val diaSubpilarStr = partesSubpilar[0]
        val mesSubpilarStr = partesSubpilar[1]
        val anoSubpilarStr = partesSubpilar[2]

        if (diaSubpilarStr.length != 2 || mesSubpilarStr.length != 2 || anoSubpilarStr.length != 4) {
            return null
        }

        if (idPilarSelecionado != -1L.toInt()) {
            val db = dbHelper.readableDatabase
            val cursorPilar = db.query(
                DatabaseHelper.TABLE_PILAR,
                arrayOf(DatabaseHelper.COLUMN_PILAR_DATA_INICIO, DatabaseHelper.COLUMN_PILAR_DATA_TERMINO),
                "${DatabaseHelper.COLUMN_PILAR_ID} = ?",
                arrayOf(idPilarSelecionado.toString()),
                null,
                null,
                null
            )

            cursorPilar?.use {
                if (it.moveToFirst()) {
                    val dataInicioPilarStr = it.getString(it.getColumnIndexOrThrow(DatabaseHelper.COLUMN_PILAR_DATA_INICIO))
                    val dataTerminoPilarStr = it.getString(it.getColumnIndexOrThrow(DatabaseHelper.COLUMN_PILAR_DATA_TERMINO))

                    val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                    sdf.isLenient = false

                    try {
                        val dataInicioPilar = sdf.parse(dataInicioPilarStr)
                        val dataTerminoPilar = sdf.parse(dataTerminoPilarStr)
                        val dataSubpilar = sdf.parse(dataSubpilarStr)

                        if (dataSubpilar.before(dataInicioPilar)) {
                            Toast.makeText(
                                this@cadastroSubpilar,
                                "A data de início do Subpilar não pode ser anterior à data de início do Pilar.",
                                Toast.LENGTH_SHORT
                            ).show()
                            return null
                        }

                        if (dataSubpilar.after(dataTerminoPilar)) {
                            Toast.makeText(
                                this@cadastroSubpilar,
                                "A data de início do Subpilar não pode ser posterior à data de término do Pilar.",
                                Toast.LENGTH_SHORT
                            ).show()
                            return null
                        }

                        val partesPilarInicio = dataInicioPilarStr.split("/")
                        val anoPilarInicioStr = partesPilarInicio.getOrNull(2)

                        if (anoPilarInicioStr != anoSubpilarStr) {
                            Toast.makeText(
                                this@cadastroSubpilar,
                                "O ano da data de início do Subpilar deve ser o mesmo do Pilar Pai ($anoPilarInicioStr).",
                                Toast.LENGTH_SHORT
                            ).show()
                            return null
                        }

                    } catch (e: ParseException) {
                        Toast.makeText(this@cadastroSubpilar, "Erro ao comparar as datas.", Toast.LENGTH_SHORT).show()
                        return null
                    }
                }
            }
            cursorPilar?.close()
            db.close()
        } else {
            Toast.makeText(this@cadastroSubpilar, "Selecione um Pilar Pai para validar a data de início.", Toast.LENGTH_SHORT).show()
            return null // Não pode validar sem um pilar selecionado
        }

        return try {
            val dia = diaSubpilarStr.toInt()
            val mes = mesSubpilarStr.toInt()
            val ano = anoSubpilarStr.toInt()

            if (dia < 1 || dia > 31 || mes < 1 || mes > 12 || ano < 2025 || ano > 2100) {
                return null
            }

            val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
            sdf.isLenient = false
            sdf.parse("$dia/$mes/$ano")
            dataSubpilarStr
        } catch (e: NumberFormatException) {
            null // Algum valor não é um número
        } catch (e: ParseException) {
            null // Data inválida
        }
    }

    /**
     * Valida e formata a data de término de um subpilar.
     *
     * Verifica o formato da data (dd/MM/yyyy), a validade dos valores,
     * se a data de término do subpilar não é anterior à sua data de início,
     * se o ano da data de término do subpilar é o mesmo do pilar pai
     * e se a data de término do subpilar não é posterior à data de término do pilar pai.
     *
     * @param dataTerminoStr A string da data de término do subpilar a ser validada (ex: "15/07/2025").
     * @param dataInicioStr A string da data de início do subpilar (para comparação interna).
     * @return A string da data formatada se for válida, ou `null` caso contrário.
     */
    private fun validarEFormatarDataFinal(dataTerminoStr: String, dataInicioStr: String): String? {
        if (dataTerminoStr.isNullOrEmpty()) {
            return null
        }

        val partesTermino = dataTerminoStr.split("/")
        if (partesTermino.size != 3) {
            return null
        }

        val diaTerminoStr = partesTermino[0]
        val mesTerminoStr = partesTermino[1]
        val anoTerminoStr = partesTermino[2]

        if (diaTerminoStr.length != 2 || mesTerminoStr.length != 2 || anoTerminoStr.length != 4) {
            return null
        }



        val partesInicio = dataInicioStr.split("/")
        if (partesInicio.size != 3) {
            return null
        }

        val anoInicioStr = partesInicio[2]
        // Validação do ano
        if (anoTerminoStr != anoInicioStr) {
            Toast.makeText(this@cadastroSubpilar, "O ano da data de término deve ser o mesmo da data de início.", Toast.LENGTH_SHORT).show()
            return null
        }


        if (idPilarSelecionado != -1L.toInt()) {
            val db = dbHelper.readableDatabase
            val cursorPilar = db.query(
                DatabaseHelper.TABLE_PILAR,
                arrayOf(DatabaseHelper.COLUMN_PILAR_DATA_TERMINO),
                "${DatabaseHelper.COLUMN_PILAR_ID} = ?",
                arrayOf(idPilarSelecionado.toString()),
                null,
                null,
                null
            )

            cursorPilar?.use {
                if (it.moveToFirst()) {
                    val dataTerminoPilarStr = it.getString(it.getColumnIndexOrThrow(DatabaseHelper.COLUMN_PILAR_DATA_TERMINO))

                    val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                    sdf.isLenient = false

                    try {
                        val dataTerminoPilar = sdf.parse(dataTerminoPilarStr)
                        val dataTerminoSubpilar = sdf.parse(dataTerminoStr)

                        if (dataTerminoSubpilar.after(dataTerminoPilar)) {
                            Toast.makeText(
                                this@cadastroSubpilar,
                                "A data de término do Subpilar não pode ser posterior à data de término do Pilar.",
                                Toast.LENGTH_SHORT
                            ).show()
                            return null
                        }

                    } catch (e: ParseException) {
                        Toast.makeText(this@cadastroSubpilar, "Erro ao comparar as datas de término.", Toast.LENGTH_SHORT).show()
                        return null
                    }
                }
            }
            cursorPilar?.close()
            db.close()
        } else {
            Toast.makeText(this@cadastroSubpilar, "Selecione um Pilar Pai para validar a data de término.", Toast.LENGTH_SHORT).show()
            return null // Não pode validar sem um pilar selecionado
        }


        return try {
            val diaTermino = diaTerminoStr.toInt()
            val mesTermino = mesTerminoStr.toInt()
            val anoTermino = anoTerminoStr.toInt()

            val diaInicio = partesInicio[0].toInt()
            val mesInicio = partesInicio[1].toInt()
            val anoInicio = anoInicioStr.toInt()

            val calendarInicio = Calendar.getInstance()
            calendarInicio.set(anoInicio, mesInicio - 1, diaInicio) // Mês em Calendar é de 0 a 11
            val dataInicioDate = calendarInicio.time

            val calendarTermino = Calendar.getInstance()
            calendarTermino.set(anoTermino, mesTermino - 1, diaTermino)
            val dataTerminoDate = calendarTermino.time

            if (dataTerminoDate.before(dataInicioDate)) {
                Toast.makeText(this@cadastroSubpilar, "A data de término não pode ser anterior à data de início.", Toast.LENGTH_SHORT).show()
                return null
            }

            val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
            sdf.isLenient = false
            sdf.parse("$diaTermino/$mesTermino/$anoTermino")
            dataTerminoStr
        } catch (e: NumberFormatException) {
            null // Algum valor não é um número
        } catch (e: ParseException) {
            null // Data inválida
        }
    }
}