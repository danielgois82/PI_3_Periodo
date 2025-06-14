package com.example.mpi.ui.atividade

import android.content.ContentValues
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.example.mpi.databinding.ActivityEditarAtividadeBinding
import com.example.mpi.data.DatabaseHelper
import com.example.mpi.data.Acao
import com.example.mpi.data.Usuario
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import android.util.Log

/**
 * [EditarAtividadeActivity] é uma Activity responsável por permitir a edição de uma atividade existente.
 *
 * Esta Activity exibe os detalhes de uma atividade carregada via Intent,
 * permitindo que o usuário modifique campos como nome, descrição, datas, orçamento,
 * responsável e a ação associada. Realiza validações nas datas e interage com o banco de dados
 * para salvar as alterações.
 */
class EditarAtividadeActivity : AppCompatActivity() {
    private lateinit var binding: ActivityEditarAtividadeBinding
    private lateinit var dbHelper: DatabaseHelper
    private var atividadeId: Int = -1
    private var listaAcoesNomes = mutableListOf<String>()
    private var listaAcoesObjetos = mutableListOf<Acao>()
    private var acaoAdapter: ArrayAdapter<String>? = null
    private var idAcaoSelecionada: Int = -1
    private var atividadeAprovada: Int = 0
    private var atividadeFinalizada: Int = 0
    private var listaResponsaveisNomes = mutableListOf<String>()
    private var listaResponsaveisObjetos = mutableListOf<Usuario>()
    private var responsavelAdapter: ArrayAdapter<String>? = null
    private var idResponsavelSelecionado: Int = -1

    /**
     * Chamado quando a Activity é criada pela primeira vez.
     *
     * Inicializa o binding, o DatabaseHelper, os adaptadores dos spinners.
     * Recupera os dados da atividade a ser editada da Intent, preenche os campos
     * da UI e carrega os spinners de ações e responsáveis, pré-selecionando os valores existentes.
     * Configura os listeners para os spinners e botões de salvar e voltar.
     *
     * @param savedInstanceState Se não for nulo, esta Activity está sendo recriada
     * a partir de um estado salvo anteriormente.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityEditarAtividadeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        dbHelper = DatabaseHelper(this)

        acaoAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, mutableListOf())
        acaoAdapter?.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerAcaoEditarAtividade.adapter = acaoAdapter

        responsavelAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, mutableListOf())
        responsavelAdapter?.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerResponsavelEditarAtividade.adapter = responsavelAdapter


        val extras = intent.extras
        if (extras != null) {
            atividadeId = extras.getInt("atividade_id", -1)
            val nome = extras.getString("atividade_nome")
            val descricao = extras.getString("atividade_descricao")
            val dataInicio = extras.getString("atividade_data_inicio")
            val dataTermino = extras.getString("atividade_data_termino")
            val aprovado = extras.getBoolean("atividade_aprovado")
            val finalizada = extras.getBoolean("atividade_finalizada")
            val orcamento = extras.getDouble("atividade_orcamento", 0.0)

            // Atribua os IDs selecionados AQUI para que estejam disponíveis antes de carregar os spinners
            idResponsavelSelecionado = extras.getInt("atividade_id_responsavel", -1)
            idAcaoSelecionada = extras.getInt("atividade_id_acao", -1)

            binding.etEditarNomeAtividade.setText(nome)
            binding.etEditarDescricaoAtividade.setText(descricao)
            binding.etEditarDataInicio.setText(dataInicio)
            binding.etEditarDataTermino.setText(dataTermino)

            atividadeAprovada = if (aprovado) 1 else 0
            atividadeFinalizada = if (finalizada) 1 else 0
            binding.tvExibirAprovado.text = if (aprovado) "Sim" else "Não"
            binding.tvExibirFinalizada.text = if (finalizada) "Sim" else "Não"
            binding.etEditarOrcamentoAtividade.setText(if (orcamento != 0.0) String.format("%.2f", orcamento) else "")
        }
        Log.d("EditarAtividade", "ID Responsável antes de carregarResponsaveis: $idResponsavelSelecionado")

        carregarAcoes()
        carregarResponsaveis()

        Log.d("EditarAtividade", "ID Responsável depois de carregarResponsaveis: $idResponsavelSelecionado")

        // Configura o listener para o spinner de ações.
        binding.spinnerAcaoEditarAtividade.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                idAcaoSelecionada = if (position > 0) listaAcoesObjetos[position - 1].id else -1
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                idAcaoSelecionada = -1
            }
        }

        // Configura o listener para o spinner de responsáveis.
        binding.spinnerResponsavelEditarAtividade.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                idResponsavelSelecionado = if (position > 0) listaResponsaveisObjetos[position - 1].id else -1
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                idResponsavelSelecionado = -1
            }
        }

        binding.btnSalvarEdicao.setOnClickListener {
            salvarEdicaoAtividade()
        }

        binding.btnVoltarEditar.setOnClickListener {
            finish()
        }
    }

    /**
     * Carrega a lista de ações do banco de dados e preenche o spinner de ações.
     * Pré-seleciona a ação previamente associada à atividade que está sendo editada.
     */
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
            DatabaseHelper.COLUMN_ACAO_NOME
        )

        listaAcoesNomes.clear()
        listaAcoesNomes.add("Selecione a Ação")
        with(cursor) {
            while (moveToNext()) {
                val id = getInt(getColumnIndexOrThrow(DatabaseHelper.COLUMN_ACAO_ID))
                val nome = getString(getColumnIndexOrThrow(DatabaseHelper.COLUMN_ACAO_NOME))
                listaAcoesObjetos.add(Acao(id, nome, "", "", "", -1, false, false, -1, 0, 0))
                listaAcoesNomes.add(nome)
            }
        }
        cursor.close()
        db.close()

        acaoAdapter?.clear()
        acaoAdapter?.addAll(listaAcoesNomes)
        acaoAdapter?.notifyDataSetChanged()

        if (idAcaoSelecionada != -1) {
            val posicao = listaAcoesObjetos.indexOfFirst { it.id == idAcaoSelecionada }
            if (posicao != -1) {
                binding.spinnerAcaoEditarAtividade.setSelection(posicao + 1)
            }
        }
    }

    /**
     * Carrega a lista de responsáveis do banco de dados e preenche o spinner de responsáveis.
     * Exclui usuários do tipo Gestor da lista e pré-seleciona o responsável
     * previamente associado à atividade.
     */
    private fun carregarResponsaveis() {
        listaResponsaveisObjetos.clear()
        val db = dbHelper.readableDatabase
        val cursor = db.rawQuery("SELECT * FROM ${DatabaseHelper.TABLE_USUARIO} WHERE ${DatabaseHelper.COLUMN_USUARIO_ID_TIPOUSUARIO} != 3", null)


        listaResponsaveisNomes.clear()
        listaResponsaveisNomes.add("Selecione o Responsável")
        with(cursor) {
            while (moveToNext()) {
                val id = getInt(getColumnIndexOrThrow(DatabaseHelper.COLUMN_USUARIO_ID))
                val nome = getString(getColumnIndexOrThrow(DatabaseHelper.COLUMN_USUARIO_NOME))
                listaResponsaveisObjetos.add(Usuario(id, nome, "", "", 0))
                listaResponsaveisNomes.add(nome)
            }
        }
        cursor.close()
        db.close()

        responsavelAdapter?.clear()
        responsavelAdapter?.addAll(listaResponsaveisNomes)
        responsavelAdapter?.notifyDataSetChanged()

        if (idResponsavelSelecionado != -1) {
            val posicao = listaResponsaveisObjetos.indexOfFirst { it.id == idResponsavelSelecionado }
            if (posicao != -1) {
                binding.spinnerResponsavelEditarAtividade.setSelection(posicao + 1)
            }
        }
    }

    /**
     * Salva as edições da atividade no banco de dados.
     *
     * Coleta os dados dos campos de entrada, valida-os e, se forem válidos,
     * atualiza a atividade no banco de dados.
     * Exibe um Toast informando o resultado da operação e finaliza a Activity.
     */
    private fun salvarEdicaoAtividade() {
        val nome = binding.etEditarNomeAtividade.text.toString().trim()
        val descricao = binding.etEditarDescricaoAtividade.text.toString().trim()
        val dataInicio = binding.etEditarDataInicio.text.toString().trim()
        val dataTermino = binding.etEditarDataTermino.text.toString().trim()
        val orcamentoStr = binding.etEditarOrcamentoAtividade.text.toString().trim()
        val orcamento = if (orcamentoStr.isNotEmpty()) orcamentoStr.replace(",", ".").toDouble() else 0.0
        val aprovado = if (binding.tvExibirAprovado.text.toString() == "Sim") 1 else 0
        val finalizada = if (binding.tvExibirFinalizada.text.toString() == "Sim") 1 else 0

        if (nome.isEmpty() || descricao.isEmpty() || dataInicio.isEmpty() || dataTermino.isEmpty() || idResponsavelSelecionado == -1) {
            Toast.makeText(this, "Preencha todos os campos obrigatórios e selecione um responsável!", Toast.LENGTH_SHORT).show()
            return
        }
        val dataInicioFormatada = validarEFormatarDataInicio(dataInicio, idAcaoSelecionada)
        val dataTerminoFormatada = validarEFormatarDataTermino(dataTermino, dataInicio, idAcaoSelecionada)
        if (dataInicioFormatada == null || dataTerminoFormatada == null) {
            return
        }

        if (idAcaoSelecionada == -1) {
            Toast.makeText(this, "Selecione uma Ação!", Toast.LENGTH_SHORT).show()
            return
        }

        val db = dbHelper.writableDatabase
        val values = ContentValues().apply {
            put(DatabaseHelper.COLUMN_ATIVIDADE_NOME, nome)
            put(DatabaseHelper.COLUMN_ATIVIDADE_DESCRICAO, descricao)
            put(DatabaseHelper.COLUMN_ATIVIDADE_DATA_INICIO, dataInicio)
            put(DatabaseHelper.COLUMN_ATIVIDADE_DATA_TERMINO, dataTermino)
            put(DatabaseHelper.COLUMN_ATIVIDADE_ORCAMENTO, orcamento)
            put(DatabaseHelper.COLUMN_ATIVIDADE_RESPONSAVEL, idResponsavelSelecionado)
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

        db.close()

        if (rowsAffected > 0) {
            Toast.makeText(this, "Atividade atualizada com sucesso!", Toast.LENGTH_SHORT).show()
            setResult(RESULT_OK)
            finish()
        } else {
            Toast.makeText(this, "Erro ao atualizar a atividade.", Toast.LENGTH_SHORT).show()
        }
    }

    /**
     * Valida e formata a data de início da atividade.
     *
     * Verifica se a data está no formato correto (dd/MM/yyyy), se é válida e se está dentro
     * do período da ação selecionada. Exibe mensagens de Toast em caso de erro.
     *
     * @param dataAtividadeStr A data de início da atividade em formato String (ex: "01/01/2025").
     * @param idAcaoSelecionada O ID da ação à qual a atividade está associada.
     * @return A data formatada como String (dd/MM/yyyy) se for válida, ou null caso contrário.
     */
    private fun validarEFormatarDataInicio(dataAtividadeStr: String, idAcaoSelecionada: Int): String? {
        if (dataAtividadeStr.isNullOrEmpty()) {
            return null
        }

        val partesAtividade = dataAtividadeStr.split("/")
        if (partesAtividade.size != 3) {
            return null
        }

        val diaAtividadeStr = partesAtividade[0]
        val mesAtividadeStr = partesAtividade[1]
        val anoAtividadeStr = partesAtividade[2]

        if (diaAtividadeStr.length != 2 || mesAtividadeStr.length != 2 || anoAtividadeStr.length != 4) {
            return null
        }

        val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        sdf.isLenient = false

        if (idAcaoSelecionada != -1) {
            val db = dbHelper.readableDatabase
            try {
                val cursorAcao = db.query(
                    DatabaseHelper.TABLE_ACAO,
                    arrayOf(DatabaseHelper.COLUMN_ACAO_DATA_INICIO, DatabaseHelper.COLUMN_ACAO_DATA_TERMINO),
                    "${DatabaseHelper.COLUMN_ACAO_ID} = ?",
                    arrayOf(idAcaoSelecionada.toString()),
                    null,
                    null,
                    null
                )
                cursorAcao?.use {
                    if (it.moveToFirst()) {
                        val dataInicioAcaoStr = it.getString(it.getColumnIndexOrThrow(DatabaseHelper.COLUMN_ACAO_DATA_INICIO))
                        val dataTerminoAcaoStr = it.getString(it.getColumnIndexOrThrow(DatabaseHelper.COLUMN_ACAO_DATA_TERMINO))
                        val dataInicioAcao = sdf.parse(dataInicioAcaoStr)
                        val dataTerminoAcao = sdf.parse(dataTerminoAcaoStr)
                        val dataAtividade = sdf.parse(dataAtividadeStr)

                        if (dataAtividade.before(dataInicioAcao)) {
                            Toast.makeText(this@EditarAtividadeActivity, "A data de início da Atividade não pode ser anterior à data de início da Ação.", Toast.LENGTH_SHORT).show()
                            return null
                        }
                        if (dataAtividade.after(dataTerminoAcao)) {
                            Toast.makeText(this@EditarAtividadeActivity, "A data de início da Atividade não pode ser posterior à data de término da Ação.", Toast.LENGTH_SHORT).show()
                            return null
                        }
                        if (anoAtividadeStr != dataInicioAcaoStr.split("/")[2]) {
                            Toast.makeText(this@EditarAtividadeActivity, "O ano da data de início da Atividade deve ser o mesmo da Ação.", Toast.LENGTH_SHORT).show()
                            return null
                        }
                    }
                }
                cursorAcao?.close()
            } catch (e: ParseException) {
                Toast.makeText(this@EditarAtividadeActivity, "Erro ao comparar as datas com a Ação.", Toast.LENGTH_SHORT).show()
                return null
            } finally {
                db.close()
            }
        } else {
            Toast.makeText(this@EditarAtividadeActivity, "Selecione uma Ação para validar a data de início.", Toast.LENGTH_SHORT).show()
            return null
        }

        return try {
            val dia = diaAtividadeStr.toInt()
            val mes = mesAtividadeStr.toInt()
            val ano = anoAtividadeStr.toInt()

            if (dia < 1 || dia > 31 || mes < 1 || mes > 12 || ano < 2025 || ano > 2100) {
                return null
            }

            sdf.parse("$dia/$mes/$ano")
            dataAtividadeStr
        } catch (e: NumberFormatException) {
            null
        } catch (e: ParseException) {
            null
        }
    }

    /**
     * Valida e formata a data de término da atividade.
     *
     * Verifica se a data está no formato correto (dd/MM/yyyy), se é válida, se é posterior
     * à data de início da atividade e se está dentro do período da ação selecionada.
     * Exibe mensagens de Toast em caso de erro.
     *
     * @param dataTerminoAtividadeStr A data de término da atividade em formato String (ex: "31/12/2025").
     * @param dataInicioAtividadeStr A data de início da atividade em formato String (usada para comparação).
     * @param idAcaoSelecionada O ID da ação à qual a atividade está associada.
     * @return A data formatada como String (dd/MM/yyyy) se for válida, ou null caso contrário.
     */
    private fun validarEFormatarDataTermino(dataTerminoAtividadeStr: String, dataInicioAtividadeStr: String, idAcaoSelecionada: Int): String? {
        if (dataTerminoAtividadeStr.isNullOrEmpty()) {
            return null
        }

        val partesTerminoAtividade = dataTerminoAtividadeStr.split("/")
        if (partesTerminoAtividade.size != 3) {
            return null
        }

        val diaTerminoAtividadeStr = partesTerminoAtividade[0]
        val mesTerminoAtividadeStr = partesTerminoAtividade[1]
        val anoTerminoAtividadeStr = partesTerminoAtividade[2]

        if (diaTerminoAtividadeStr.length != 2 || mesTerminoAtividadeStr.length != 2 || anoTerminoAtividadeStr.length != 4) {
            return null
        }

        val partesInicioAtividade = dataInicioAtividadeStr.split("/")
        if (partesInicioAtividade.size != 3) {
            return null
        }

        val anoInicioAtividadeStr = partesInicioAtividade[2]
        if (anoTerminoAtividadeStr != anoInicioAtividadeStr) {
            Toast.makeText(
                this@EditarAtividadeActivity,
                "O ano da data de término deve ser o mesmo da data de início.",
                Toast.LENGTH_SHORT
            ).show()
            return null
        }

        val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        sdf.isLenient = false

        if (idAcaoSelecionada != -1) {
            val db = dbHelper.readableDatabase
            try {
                val cursorAcao = db.query(
                    DatabaseHelper.TABLE_ACAO,
                    arrayOf(DatabaseHelper.COLUMN_ACAO_DATA_TERMINO),
                    "${DatabaseHelper.COLUMN_ACAO_ID} = ?",
                    arrayOf(idAcaoSelecionada.toString()),
                    null,
                    null,
                    null
                )
                cursorAcao?.use {
                    if (it.moveToFirst()) {
                        val dataTerminoAcaoStr =
                            it.getString(it.getColumnIndexOrThrow(DatabaseHelper.COLUMN_ACAO_DATA_TERMINO))
                        val dataTerminoAcao = sdf.parse(dataTerminoAcaoStr)
                        val dataTerminoAtividade = sdf.parse(dataTerminoAtividadeStr)

                        if (dataTerminoAtividade.after(dataTerminoAcao)) {
                            Toast.makeText(
                                this@EditarAtividadeActivity,
                                "A data de término da Atividade não pode ser posterior à data de término da Ação.",
                                Toast.LENGTH_SHORT
                            ).show()
                            return null
                        }
                    }
                }
                cursorAcao?.close()
            } catch (e: ParseException) {
                Toast.makeText(
                    this@EditarAtividadeActivity,
                    "Erro ao comparar as datas de término com a Ação.",
                    Toast.LENGTH_SHORT
                ).show()
                return null
            } finally {
                db.close()
            }
        } else {
            Toast.makeText(
                this@EditarAtividadeActivity,
                "Selecione uma Ação para validar a data de término.",
                Toast.LENGTH_SHORT
            ).show()
            return null
        }

        return try {
            val diaTermino = diaTerminoAtividadeStr.toInt()
            val mesTermino = mesTerminoAtividadeStr.toInt()
            val anoTermino = anoTerminoAtividadeStr.toInt()

            val diaInicio = dataInicioAtividadeStr.split("/")[0].toInt()
            val mesInicio = dataInicioAtividadeStr.split("/")[1].toInt()
            val anoInicio = anoInicioAtividadeStr.toInt()

            val calendarInicio = Calendar.getInstance()
            calendarInicio.set(anoInicio, mesInicio - 1, diaInicio)
            val dataInicioDate = calendarInicio.time

            val calendarTermino = Calendar.getInstance()
            calendarTermino.set(anoTermino, mesTermino - 1, diaTermino)
            val dataTerminoDate = calendarTermino.time

            if (dataTerminoDate.before(dataInicioDate)) {
                Toast.makeText(
                    this@EditarAtividadeActivity,
                    "A data de término não pode ser anterior à data de início.",
                    Toast.LENGTH_SHORT
                ).show()
                return null
            }

            sdf.parse("$diaTermino/$mesTermino/$anoTermino")
            dataTerminoAtividadeStr
        } catch (e: NumberFormatException) {
            null
        } catch (e: ParseException) {
            null
        }
    }
}