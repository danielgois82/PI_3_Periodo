package com.example.mpi.ui.acao

import android.content.ContentValues
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.RadioButton
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.example.mpi.data.DatabaseHelper
import com.example.mpi.databinding.ActivityEditarAcaoBinding
import com.example.mpi.data.Pilar
import com.example.mpi.data.Subpilar
import com.example.mpi.data.Usuario
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

/**
 * [EditarAcaoActivity] é a Activity responsável por permitir a edição de uma [Acao] existente
 * no sistema.
 *
 * Esta tela carrega os dados de uma ação específica pelo seu ID e permite ao usuário
 * modificar seu nome, descrição, datas de início e término, o responsável, e o vínculo
 * (Pilar ou Subpilar). Realiza validações rigorosas das datas, garantindo que estejam
 * no formato correto e dentro dos períodos permitidos pelo Pilar ou Subpilar pai.
 * Após a edição, as alterações são persistidas no banco de dados.
 */
class EditarAcaoActivity : AppCompatActivity() {
    private lateinit var binding: ActivityEditarAcaoBinding
    private lateinit var dbHelper: DatabaseHelper
    private var acaoId: Int = -1
    private var listaPilaresNomes = mutableListOf<String>()
    private var listaPilaresObjetos = mutableListOf<Pilar>()
    private var listaSubpilaresNomes = mutableListOf<String>()
    private var listaSubpilaresObjetos = mutableListOf<Subpilar>()
    private var idVinculoSelecionado: Int = -1
    private var tipoVinculo: String = ""
    private lateinit var listaUsuariosNomes: MutableList<String>
    private lateinit var listaUsuariosObjetos: MutableList<Usuario>
    private var idResponsavelSelecionado: Int = -1
    private var acaoAprovada: Int = 0
    private var acaoFinalizada: Int = 0

    /**
     * Chamado quando a Activity é criada pela primeira vez.
     *
     * Inicializa a interface do usuário, configura o [DatabaseHelper],
     * recupera o ID da ação a ser editada da Intent, carrega os dados existentes da ação,
     * popula os Spinners de usuários e de vínculo (Pilar/Subpilar) e
     * configura os listeners para os elementos de UI.
     *
     * @param savedInstanceState Se não for nulo, esta Activity está sendo recriada
     * a partir de um estado salvo.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityEditarAcaoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        dbHelper = DatabaseHelper(this)

        listaUsuariosNomes = mutableListOf()
        listaUsuariosObjetos = mutableListOf()
        val responsavelAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, listaUsuariosNomes)
        binding.spinnerResponsavelEditar.adapter = responsavelAdapter

        binding.spinnerVinculoEditar.isEnabled = false
        binding.spinnerVinculoEditar.adapter =
            ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, listOf("Selecione o tipo de vínculo"))

        acaoId = intent.getIntExtra("acao_id", -1)
        if (acaoId == -1) {
            Toast.makeText(this, "Erro: ID da ação não encontrado", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        carregarDadosAcao()
        carregarUsuariosNoSpinner()

        // Listener para o Spinner de Responsável
        binding.spinnerResponsavelEditar.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            /**
             * Chamado quando um item do Spinner de Responsável é selecionado.
             */
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                if (position > 0) {
                    idResponsavelSelecionado = listaUsuariosObjetos[position - 1].id
                } else {
                    idResponsavelSelecionado = -1
                }
            }

            /**
             * Chamado quando nada é selecionado no Spinner de Responsável.
             */
            override fun onNothingSelected(parent: AdapterView<*>?) {
                idResponsavelSelecionado = -1
            }
        }

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

        // Listener para o Spinner de Vínculo
        binding.spinnerVinculoEditar.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            /**
             * Chamado quando um item do Spinner de Vínculo é selecionado.
             */
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
            /**
             * Chamado quando nada é selecionado no Spinner de Vínculo.
             */
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

    /**
     * Carrega os dados da [Acao] do banco de dados com base no `acaoId` e preenche os campos
     * da interface de usuário.
     *
     * Este método também determina o tipo de vínculo (Pilar ou Subpilar) e seleciona
     * o RadioButton e o item correto no Spinner de vínculo.
     */
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
            DatabaseHelper.COLUMN_ACAO_ID_SUBPILAR
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

                idResponsavelSelecionado = getInt(getColumnIndexOrThrow(DatabaseHelper.COLUMN_ACAO_RESPONSAVEL))

                acaoAprovada = getInt(getColumnIndexOrThrow(DatabaseHelper.COLUMN_ACAO_IS_APROVADO))
                acaoFinalizada = getInt(getColumnIndexOrThrow(DatabaseHelper.COLUMN_ACAO_IS_FINALIZADO))
                val id_pilar = getInt(getColumnIndexOrThrow(DatabaseHelper.COLUMN_ACAO_ID_PILAR))
                val id_subpilar = getInt(getColumnIndexOrThrow(DatabaseHelper.COLUMN_ACAO_ID_SUBPILAR))

                if (id_pilar != 0) {
                    tipoVinculo = "pilar"
                    idVinculoSelecionado = id_pilar
                    binding.radioPilarEditar.isChecked = true
                    carregarPilaresNoSpinner()
                } else if (id_subpilar != 0) {
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

    /**
     * Carrega a lista de [Pilar]es do banco de dados e popula o Spinner de vínculo.
     * Também tenta pré-selecionar o Pilar já vinculado à ação, se aplicável.
     */
    private fun carregarPilaresNoSpinner() {
        val db = dbHelper.readableDatabase
        val projection = arrayOf(DatabaseHelper.COLUMN_PILAR_ID, DatabaseHelper.COLUMN_PILAR_NOME)
        val cursor = db.query(DatabaseHelper.TABLE_PILAR, projection, null, null, null, null, DatabaseHelper.COLUMN_PILAR_NOME)

        listaPilaresNomes.clear()
        listaPilaresObjetos.clear()
        listaPilaresNomes.add("Selecione o Pilar")

        with(cursor) {
            while (moveToNext()) {
                val id = getInt(getColumnIndexOrThrow(DatabaseHelper.COLUMN_PILAR_ID))
                val nome = getString(getColumnIndexOrThrow(DatabaseHelper.COLUMN_PILAR_NOME))
                listaPilaresObjetos.add(Pilar(id, nome, "", "", "",  0.0, 0, 0))
                listaPilaresNomes.add(nome)
            }
        }
        cursor.close()
        db.close()

        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, listaPilaresNomes)
        binding.spinnerVinculoEditar.adapter = adapter

        if (idVinculoSelecionado != -1 && tipoVinculo == "pilar") {
            val index = listaPilaresObjetos.indexOfFirst { it.id == idVinculoSelecionado } + 1
            if (index > 0 && index < listaPilaresNomes.size) {
                binding.spinnerVinculoEditar.setSelection(index)
            }
        }
    }

    /**
     * Carrega a lista de [Usuario]s (exceto Gestores) do banco de dados e popula o Spinner de responsáveis.
     * Também tenta pré-selecionar o Responsável já atribuído à ação.
     */
    private fun carregarUsuariosNoSpinner() {
        val db = dbHelper.readableDatabase
        val cursor = db.rawQuery("SELECT * FROM ${DatabaseHelper.TABLE_USUARIO} WHERE ${DatabaseHelper.COLUMN_USUARIO_ID_TIPOUSUARIO} != 3", null)


        listaUsuariosNomes.clear()
        listaUsuariosObjetos.clear()
        listaUsuariosNomes.add("Selecione o Responsável")

        with(cursor) {
            while (moveToNext()) {
                val id = getInt(getColumnIndexOrThrow(DatabaseHelper.COLUMN_USUARIO_ID))
                val nome = getString(getColumnIndexOrThrow(DatabaseHelper.COLUMN_USUARIO_NOME))
                listaUsuariosObjetos.add(Usuario(id, nome, "", "", 0))
                listaUsuariosNomes.add(nome)
            }
        }
        cursor.close()
        db.close()

        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, listaUsuariosNomes)
        binding.spinnerResponsavelEditar.adapter = adapter

        if (idResponsavelSelecionado != -1) {
            val posicao = listaUsuariosObjetos.indexOfFirst { it.id == idResponsavelSelecionado }
            if (posicao != -1) {
                binding.spinnerResponsavelEditar.setSelection(posicao + 1)
            }
        }
        binding.spinnerResponsavelEditar.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                if (position > 0) {
                    idResponsavelSelecionado = listaUsuariosObjetos[position - 1].id
                } else {
                    idResponsavelSelecionado = -1
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                idResponsavelSelecionado = -1
            }
        }
    }

    /**
     * Carrega a lista de [Subpilar]es do banco de dados e popula o Spinner de vínculo.
     * Também tenta pré-selecionar o Subpilar já vinculado à ação, se aplicável.
     */
    private fun carregarSubpilaresNoSpinner() {
        val db = dbHelper.readableDatabase
        val projection = arrayOf(DatabaseHelper.COLUMN_SUBPILAR_ID, DatabaseHelper.COLUMN_SUBPILAR_NOME)
        val cursor = db.query(DatabaseHelper.TABLE_SUBPILAR, projection, null, null, null, null, DatabaseHelper.COLUMN_SUBPILAR_NOME)

        listaSubpilaresNomes.clear()
        listaSubpilaresObjetos.clear()
        listaSubpilaresNomes.add("Selecione o Subpilar")

        with(cursor) {
            while (moveToNext()) {
                val id = getInt(getColumnIndexOrThrow(DatabaseHelper.COLUMN_SUBPILAR_ID))
                val nome = getString(getColumnIndexOrThrow(DatabaseHelper.COLUMN_SUBPILAR_NOME))
                listaSubpilaresObjetos.add(Subpilar(id, nome, "", "", "",  0, 0))
                listaSubpilaresNomes.add(nome)
            }
        }
        cursor.close()
        db.close()

        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, listaSubpilaresNomes)
        binding.spinnerVinculoEditar.adapter = adapter


        if (idVinculoSelecionado != -1 && tipoVinculo == "subpilar") {
            val index = listaSubpilaresObjetos.indexOfFirst { it.id == idVinculoSelecionado } + 1
            if (index > 0 && index < listaSubpilaresNomes.size) {
                binding.spinnerVinculoEditar.setSelection(index)
            }
        }
    }

    /**
     * Salva as edições da [Acao] no banco de dados.
     *
     * Este método recupera os dados dos campos da interface, realiza validações,
     * e então atualiza o registro da ação no banco de dados.
     * Exibe mensagens de sucesso ou erro e retorna à Activity anterior após a operação.
     */
    private fun salvarEdicaoAcao() {
        val nome = binding.etEditarNomeAcao.text.toString()
        val descricao = binding.etEditarDescricaoAcao.text.toString()
        val dataInicio = binding.etEditarDataInicio.text.toString().trim()
        val dataTermino = binding.etEditarDataTermino.text.toString().trim()

        if (nome.isEmpty() || descricao.isEmpty() || dataInicio.isEmpty() || dataTermino.isEmpty() || idVinculoSelecionado == -1) {
            Toast.makeText(this, "Preencha todos os campos e selecione um Pilar ou Subpilar", Toast.LENGTH_SHORT).show()
            return
        }
        val dataInicioFormatada = validarEFormatarDataInicial(dataInicio, tipoVinculo, idVinculoSelecionado)
        val dataTerminoFormatada = validarEFormatarDataFinal(dataTermino, dataInicio, tipoVinculo, idVinculoSelecionado)

        if (dataInicioFormatada == null) {
            Toast.makeText(this, "Data de Início inválida. Use o formato dd/mm/aaaa e respeite as datas do Pilar/Subpilar.", Toast.LENGTH_SHORT).show()
            return
        }

        if (dataTerminoFormatada == null) {
            Toast.makeText(this, "Data de Término inválida. Use o formato dd/mm/aaaa e respeite as datas do Pilar/Subpilar.", Toast.LENGTH_SHORT).show()
            return
        }


        val db = dbHelper.writableDatabase
        val values = ContentValues().apply {
            put(DatabaseHelper.COLUMN_ACAO_NOME, nome)
            put(DatabaseHelper.COLUMN_ACAO_DESCRICAO, descricao)
            put(DatabaseHelper.COLUMN_ACAO_DATA_INICIO, dataInicio)
            put(DatabaseHelper.COLUMN_ACAO_DATA_TERMINO, dataTermino)
            put(DatabaseHelper.COLUMN_ACAO_RESPONSAVEL, idResponsavelSelecionado)

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

    /**
     * Valida e formata a data de início da Ação, garantindo que esteja no formato "dd/MM/yyyy"
     * e que esteja dentro do período válido do Pilar ou Subpilar pai.
     * Também verifica se o ano da ação é o mesmo do Pilar/Subpilar.
     *
     * @param dataAcaoStr A string da data de início da Ação a ser validada.
     * @param tipoVinculo O tipo de vínculo ("pilar" ou "subpilar").
     * @param idVinculoSelecionado O ID do Pilar ou Subpilar selecionado.
     * @return A data formatada como String se for válida, caso contrário, `null`.
     */
    private fun validarEFormatarDataInicial(dataAcaoStr: String, tipoVinculo: String, idVinculoSelecionado: Int): String? {
        if (dataAcaoStr.isNullOrEmpty()) {
            return null
        }

        val partesAcao = dataAcaoStr.split("/")
        if (partesAcao.size != 3) {
            return null
        }

        val diaAcaoStr = partesAcao[0]
        val mesAcaoStr = partesAcao[1]
        val anoAcaoStr = partesAcao[2]

        if (diaAcaoStr.length != 2 || mesAcaoStr.length != 2 || anoAcaoStr.length != 4) {
            return null
        }

        if (idVinculoSelecionado != -1 && tipoVinculo.isNotEmpty()) {
            val db = dbHelper.readableDatabase
            val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
            sdf.isLenient = false

            try {
                if (tipoVinculo == "pilar") {
                    val cursorPilar = db.query(
                        DatabaseHelper.TABLE_PILAR,
                        arrayOf(DatabaseHelper.COLUMN_PILAR_DATA_INICIO, DatabaseHelper.COLUMN_PILAR_DATA_TERMINO),
                        "${DatabaseHelper.COLUMN_PILAR_ID} = ?",
                        arrayOf(idVinculoSelecionado.toString()),
                        null,
                        null,
                        null
                    )
                    cursorPilar?.use {
                        if (it.moveToFirst()) {
                            val dataInicioPilarStr = it.getString(it.getColumnIndexOrThrow(DatabaseHelper.COLUMN_PILAR_DATA_INICIO))
                            val dataTerminoPilarStr = it.getString(it.getColumnIndexOrThrow(DatabaseHelper.COLUMN_PILAR_DATA_TERMINO))

                            val dataInicioPilar = sdf.parse(dataInicioPilarStr)
                            val dataTerminoPilar = sdf.parse(dataTerminoPilarStr)
                            val dataAcao = sdf.parse(dataAcaoStr)

                            if (dataAcao.before(dataInicioPilar)) {
                                Toast.makeText(this@EditarAcaoActivity, "A data de início da Ação não pode ser anterior à data de início do Pilar.", Toast.LENGTH_SHORT).show()
                                return null
                            }
                            if (dataAcao.after(dataTerminoPilar)) {
                                Toast.makeText(this@EditarAcaoActivity, "A data de início da Ação não pode ser posterior à data de término do Pilar.", Toast.LENGTH_SHORT).show()
                                return null
                            }
                            if (anoAcaoStr != dataInicioPilarStr.split("/")[2]) {
                                Toast.makeText(this@EditarAcaoActivity, "O ano da data de início da Ação deve ser o mesmo do Pilar.", Toast.LENGTH_SHORT).show()
                                return null
                            }
                        }
                    }
                    cursorPilar?.close()
                } else if (tipoVinculo == "subpilar") {
                    val cursorSubpilar = db.query(
                        DatabaseHelper.TABLE_SUBPILAR,
                        arrayOf(DatabaseHelper.COLUMN_SUBPILAR_DATA_INICIO, DatabaseHelper.COLUMN_SUBPILAR_DATA_TERMINO, DatabaseHelper.COLUMN_SUBPILAR_ID_PILAR),
                        "${DatabaseHelper.COLUMN_SUBPILAR_ID} = ?",
                        arrayOf(idVinculoSelecionado.toString()),
                        null,
                        null,
                        null
                    )
                    cursorSubpilar?.use {
                        if (it.moveToFirst()) {
                            val dataInicioSubpilarStr = it.getString(it.getColumnIndexOrThrow(DatabaseHelper.COLUMN_SUBPILAR_DATA_INICIO))
                            val dataTerminoSubpilarStr = it.getString(it.getColumnIndexOrThrow(DatabaseHelper.COLUMN_SUBPILAR_DATA_TERMINO))
                            val idPilarPai = it.getLong(it.getColumnIndexOrThrow(DatabaseHelper.COLUMN_SUBPILAR_ID_PILAR))

                            val dataInicioSubpilar = sdf.parse(dataInicioSubpilarStr)
                            val dataTerminoSubpilar = sdf.parse(dataTerminoSubpilarStr)
                            val dataAcao = sdf.parse(dataAcaoStr)

                            if (dataAcao.before(dataInicioSubpilar)) {
                                Toast.makeText(this@EditarAcaoActivity, "A data de início da Ação não pode ser anterior à data de início do Subpilar.", Toast.LENGTH_SHORT).show()
                                return null
                            }
                            if (dataAcao.after(dataTerminoSubpilar)) {
                                Toast.makeText(this@EditarAcaoActivity, "A data de início da Ação não pode ser posterior à data de término do Subpilar.", Toast.LENGTH_SHORT).show()
                                return null
                            }
                            if (anoAcaoStr != dataInicioSubpilarStr.split("/")[2]) {
                                Toast.makeText(this@EditarAcaoActivity, "O ano da data de início da Ação deve ser o mesmo do Subpilar.", Toast.LENGTH_SHORT).show()
                                return null
                            }

                        }
                    }
                    cursorSubpilar?.close()
                }
            } catch (e: ParseException) {
                Toast.makeText(this@EditarAcaoActivity, "Erro ao comparar as datas.", Toast.LENGTH_SHORT).show()
                return null
            } finally {
                db.close()
            }
        } else {
            Toast.makeText(this@EditarAcaoActivity, "Selecione um Pilar ou Subpilar para validar a data de início.", Toast.LENGTH_SHORT).show()
            return null
        }

        return try {
            val dia = diaAcaoStr.toInt()
            val mes = mesAcaoStr.toInt()
            val ano = anoAcaoStr.toInt()

            if (dia < 1 || dia > 31 || mes < 1 || mes > 12 || ano < 2025 || ano > 2100) {
                return null
            }

            val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
            sdf.isLenient = false
            sdf.parse("$dia/$mes/$ano")
            dataAcaoStr
        } catch (e: NumberFormatException) {
            null
        } catch (e: ParseException) {
            null
        }
    }

    /**
     * Valida e formata a data de término da Ação, garantindo que esteja no formato "dd/MM/yyyy",
     * que não seja anterior à data de início da própria Ação,
     * que esteja dentro do período válido do Pilar ou Subpilar pai,
     * e que o ano seja o mesmo da data de início da Ação.
     *
     * @param dataTerminoAcaoStr A string da data de término da Ação a ser validada.
     * @param dataInicioAcaoStr A string da data de início da Ação para comparação.
     * @param tipoVinculo O tipo de vínculo ("pilar" ou "subpilar").
     * @param idVinculoSelecionado O ID do Pilar ou Subpilar selecionado.
     * @return A data formatada como String se for válida, caso contrário, `null`.
     */
    private fun validarEFormatarDataFinal(dataTerminoAcaoStr: String, dataInicioAcaoStr: String, tipoVinculo: String, idVinculoSelecionado: Int): String? {
        if (dataTerminoAcaoStr.isNullOrEmpty()) {
            return null
        }

        val partesTerminoAcao = dataTerminoAcaoStr.split("/")
        if (partesTerminoAcao.size != 3) {
            return null
        }

        val diaTerminoAcaoStr = partesTerminoAcao[0]
        val mesTerminoAcaoStr = partesTerminoAcao[1]
        val anoTerminoAcaoStr = partesTerminoAcao[2]

        if (diaTerminoAcaoStr.length != 2 || mesTerminoAcaoStr.length != 2 || anoTerminoAcaoStr.length != 4) {
            return null
        }

        val partesInicioAcao = dataInicioAcaoStr.split("/")
        if (partesInicioAcao.size != 3) {
            return null
        }

        val anoInicioAcaoStr = partesInicioAcao[2]
        if (anoTerminoAcaoStr != anoInicioAcaoStr) {
            Toast.makeText(this@EditarAcaoActivity, "O ano da data de término deve ser o mesmo da data de início.", Toast.LENGTH_SHORT).show()
            return null
        }
        val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        sdf.isLenient = false

        if (idVinculoSelecionado != -1 && tipoVinculo.isNotEmpty()) { // Já corrigido para usar Int diretamente
            val db = dbHelper.readableDatabase


            try {
                if (tipoVinculo == "pilar") {
                    val cursorPilar = db.query(
                        DatabaseHelper.TABLE_PILAR,
                        arrayOf(DatabaseHelper.COLUMN_PILAR_DATA_TERMINO),
                        "${DatabaseHelper.COLUMN_PILAR_ID} = ?",
                        arrayOf(idVinculoSelecionado.toString()),
                        null,
                        null,
                        null
                    )
                    cursorPilar?.use {
                        if (it.moveToFirst()) {
                            val dataTerminoPilarStr = it.getString(it.getColumnIndexOrThrow(DatabaseHelper.COLUMN_PILAR_DATA_TERMINO))
                            val dataTerminoPilar = sdf.parse(dataTerminoPilarStr)
                            val dataTerminoAcao = sdf.parse(dataTerminoAcaoStr)

                            if (dataTerminoAcao.after(dataTerminoPilar)) {
                                Toast.makeText(this@EditarAcaoActivity, "A data de término da Ação não pode ser posterior à data de término do Pilar.", Toast.LENGTH_SHORT).show()
                                return null
                            }
                        }
                    }
                    cursorPilar?.close()
                } else if (tipoVinculo == "subpilar") {
                    val cursorSubpilar = db.query(
                        DatabaseHelper.TABLE_SUBPILAR,
                        arrayOf(DatabaseHelper.COLUMN_SUBPILAR_DATA_TERMINO),
                        "${DatabaseHelper.COLUMN_SUBPILAR_ID} = ?",
                        arrayOf(idVinculoSelecionado.toString()),
                        null,
                        null,
                        null
                    )
                    cursorSubpilar?.use {
                        if (it.moveToFirst()) {
                            val dataTerminoSubpilarStr = it.getString(it.getColumnIndexOrThrow(DatabaseHelper.COLUMN_SUBPILAR_DATA_TERMINO))
                            val dataTerminoSubpilar = sdf.parse(dataTerminoSubpilarStr)
                            val dataTerminoAcao = sdf.parse(dataTerminoAcaoStr)

                            if (dataTerminoAcao.after(dataTerminoSubpilar)) {
                                Toast.makeText(this@EditarAcaoActivity, "A data de término da Ação não pode ser posterior à data de término do Subpilar.", Toast.LENGTH_SHORT).show()
                                return null
                            }
                        }
                    }
                    cursorSubpilar?.close()
                }
            } catch (e: ParseException) {
                Toast.makeText(this@EditarAcaoActivity, "Erro ao comparar as datas de término.", Toast.LENGTH_SHORT).show()
                return null
            } finally {
                db.close()
            }
        } else {
            Toast.makeText(this@EditarAcaoActivity, "Selecione um Pilar ou Subpilar para validar a data de término.", Toast.LENGTH_SHORT).show()
            return null
        }

        return try {
            val diaTermino = diaTerminoAcaoStr.toInt()
            val mesTermino = mesTerminoAcaoStr.toInt()
            val anoTermino = anoTerminoAcaoStr.toInt()

            val diaInicio = partesInicioAcao[0].toInt()
            val mesInicio = partesInicioAcao[1].toInt()
            val anoInicio = anoInicioAcaoStr.toInt()

            val calendarInicio = Calendar.getInstance()
            calendarInicio.set(anoInicio, mesInicio - 1, diaInicio)
            val dataInicioDate = calendarInicio.time

            val calendarTermino = Calendar.getInstance()
            calendarTermino.set(anoTermino, mesTermino - 1, diaTermino)
            val dataTerminoDate = calendarTermino.time

            if (dataTerminoDate.before(dataInicioDate)) {
                Toast.makeText(this@EditarAcaoActivity, "A data de término não pode ser anterior à data de início.", Toast.LENGTH_SHORT).show()
                return null
            }

            sdf.parse("$diaTermino/$mesTermino/$anoTermino")
            dataTerminoAcaoStr
        } catch (e: NumberFormatException) {
            null
        } catch (e: ParseException) {
            null
        }
    }
}