package com.example.mpi.ui.atividade

import android.content.ContentValues
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import com.example.mpi.data.Atividade
import androidx.appcompat.app.AppCompatActivity
import com.example.mpi.data.DatabaseHelper
import com.example.mpi.data.Usuario
import com.example.mpi.repository.PercentualAtividadeRepository
import com.example.mpi.databinding.ActivityCadastroAtividadeBinding
import com.example.mpi.data.Acao
import java.lang.NumberFormatException
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

/**
 * [CadastroAtividadeActivity] é uma Activity para cadastrar novas atividades no sistema.
 *
 * Permite que o usuário insira detalhes como nome, descrição, datas, responsável,
 * orçamento e ação relacionada para uma nova atividade.
 * Valida as entradas do usuário e interage com o banco de dados para salvar a nova atividade.
 */
class CadastroAtividadeActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCadastroAtividadeBinding
    private lateinit var dbHelper: DatabaseHelper
    private lateinit var percentualAtividadeRepository: PercentualAtividadeRepository
    private var listaResponsaveisNomes = mutableListOf<String>()
    private var listaResponsaveisObjetos = mutableListOf<Usuario>()
    private var listaAcoesNomes = mutableListOf<String>()
    private var listaAcoesObjetos = mutableListOf<Acao>()
    private var idResponsavelSelecionado: Int = -1
    private var idAcaoSelecionada: Int = -1
    private var idUsuarioRecebido: Int = -1

    /**
     * Chamado quando a Activity é criada pela primeira vez.
     *
     * Inicializa o binding, o DatabaseHelper, o repositório, carrega os dados para os spinners
     * de responsáveis e ações. Recupera o ID do usuário logado e configura os listeners
     * para os spinners e botões de confirmação e voltar.
     *
     * @param savedInstanceState Se não for nulo, esta Activity está sendo recriada
     * a partir de um estado salvo anteriormente.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityCadastroAtividadeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        dbHelper = DatabaseHelper(this)
        percentualAtividadeRepository = PercentualAtividadeRepository.getInstance(this)

        carregarResponsaveisNoSpinner()
        carregarAcoesNoSpinner()


        // Recebendo os dados de usuário
        val extras = intent.extras
        if (extras != null) {
            idUsuarioRecebido = extras.getInt("idUsuario", 999999)
            val nomeUsuario = extras.getString("nomeUsuario") ?: "Nome de usuário desconhecido"
            val tipoUsuario = extras.getString("tipoUsuario") ?: "Tipo de usuário desconhecido"
        }


        // Configura o listener para o spinner de responsáveis.
        binding.spinnerResponsaveis.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                if (position > 0) {
                    idResponsavelSelecionado = listaResponsaveisObjetos[position - 1].id
                } else {
                    idResponsavelSelecionado = -1
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                idResponsavelSelecionado = -1
            }
        }

        // Configura o listener para o spinner de ações.
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
            cadastrarAtividade()
        }

        binding.btnVoltarAtividade.setOnClickListener {
            finish()
        }
    }

    /**
     * Carrega a lista de responsáveis do banco de dados e preenche o spinner de responsáveis.
     * Exclui usuários do tipo Gestor da lista.
     * Inclui a opção "Selecione o Responsável" como primeiro item.
     */
    private fun carregarResponsaveisNoSpinner() {
        val db = dbHelper.readableDatabase
        val cursor = db.rawQuery("SELECT * FROM ${DatabaseHelper.TABLE_USUARIO} WHERE ${DatabaseHelper.COLUMN_USUARIO_ID_TIPOUSUARIO} != 3", null)

        listaResponsaveisNomes.clear()
        listaResponsaveisObjetos.clear()
        listaResponsaveisNomes.add("Selecione o Responsável") // Adiciona a opção padrão

        with(cursor) {
            while (moveToNext()) {
                val id = getInt(getColumnIndexOrThrow(DatabaseHelper.COLUMN_USUARIO_ID))
                val nome = getString(getColumnIndexOrThrow(DatabaseHelper.COLUMN_USUARIO_NOME))
                listaResponsaveisObjetos.add(Usuario(id, nome, "", "", 0)) // Adapte conforme a sua classe Usuario
                listaResponsaveisNomes.add(nome)
            }
        }
        cursor.close()
        db.close()

        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, listaResponsaveisNomes)
        binding.spinnerResponsaveis.adapter = adapter
    }

    /**
     * Carrega a lista de ações do banco de dados e preenche o spinner de ações.
     * Inclui a opção "Selecione a Ação" como primeiro item.
     */
    private fun carregarAcoesNoSpinner() {
        val db = dbHelper.readableDatabase
        val cursor = db.rawQuery("SELECT * FROM ${DatabaseHelper.TABLE_ACAO}", null)
        listaAcoesNomes.clear()
        listaAcoesObjetos.clear()
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

        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, listaAcoesNomes)
        binding.spinnerAcoes.adapter = adapter
    }

    /**
     * Cadastra uma nova atividade no banco de dados.
     *
     * Coleta os dados dos campos de entrada, valida-os e, se forem válidos,
     * insere a nova atividade no banco de dados.
     * Exibe um Toast informando o resultado da operação e finaliza a Activity.
     */
    private fun cadastrarAtividade() {
        val nome = binding.etNomeAtividade.text.toString().trim()
        val descricao = binding.etDescricaoAtividade.text.toString().trim()
        val dataInicio = binding.etDataInicioAtividade.text.toString().trim()
        val dataTermino = binding.etDataTerminoAtividade.text.toString().trim()
        val orcamentoStr = binding.etOrcamentoAtividade.text.toString().trim()
        val orcamentoTratado = if (orcamentoStr.isNotEmpty()) orcamentoStr.replace(",", ".").toDouble() else 0.0

        if (nome.isEmpty() || descricao.isEmpty() || dataInicio.isEmpty() || dataTermino.isEmpty() || orcamentoStr.isEmpty() || idResponsavelSelecionado == -1L.toInt() || idAcaoSelecionada == -1L.toInt()) {
            Toast.makeText(this, "Preencha todos os campos e selecione um Responsável e uma Ação", Toast.LENGTH_SHORT).show()
            return
        }

        val orcamento = try {
            orcamentoStr.toDouble()
        } catch (e: NumberFormatException) {
            Toast.makeText(this, "Orçamento inválido", Toast.LENGTH_SHORT).show()
            return
        }

        val dataInicioFormatada = validarEFormatarDataInicio(dataInicio, idAcaoSelecionada)
        val dataTerminoFormatada = validarEFormatarDataTermino(dataTermino, dataInicio, idAcaoSelecionada)

        if (dataInicioFormatada == null || dataTerminoFormatada == null) {
            return
        }

        val db = dbHelper.writableDatabase
        val values = ContentValues().apply {
            put(DatabaseHelper.COLUMN_ATIVIDADE_NOME, nome)
            put(DatabaseHelper.COLUMN_ATIVIDADE_DESCRICAO, descricao)
            put(DatabaseHelper.COLUMN_ATIVIDADE_DATA_INICIO, dataInicioFormatada)
            put(DatabaseHelper.COLUMN_ATIVIDADE_DATA_TERMINO, dataTerminoFormatada)
            put(DatabaseHelper.COLUMN_ATIVIDADE_ORCAMENTO, orcamentoTratado)
            put(DatabaseHelper.COLUMN_ATIVIDADE_RESPONSAVEL, idResponsavelSelecionado)
            put(DatabaseHelper.COLUMN_ATIVIDADE_ID_ACAO, idAcaoSelecionada)
            put(DatabaseHelper.COLUMN_ATIVIDADE_ID_USUARIO, idUsuarioRecebido)
        }

        val newRowId:Long = db.insert(DatabaseHelper.TABLE_ATIVIDADE, null, values)
        db.close()


        if (newRowId != -1L) {
            val novaAtividade = Atividade(
                id = newRowId.toInt(),
                nome = nome,
                descricao = descricao,
                dataInicio = dataInicioFormatada,
                dataTermino = dataTerminoFormatada,
                responsavel = idResponsavelSelecionado,
                aprovado = false,
                finalizado = false,
                orcamento = orcamento,
                idAcao = idAcaoSelecionada,
                idUsuario = idUsuarioRecebido
            )
            percentualAtividadeRepository.inserirPercentuaisAtividade(novaAtividade)

            Toast.makeText(this, "Atividade cadastrada com sucesso!", Toast.LENGTH_SHORT).show()
            finish()
        } else {
            Toast.makeText(this, "Erro ao cadastrar atividade", Toast.LENGTH_SHORT).show()
        }

    }

    /**
     * Valida e formata a data de início da atividade.
     *
     * Verifica se a data está no formato correto (dd/MM/yyyy), se é válida e se está dentro
     * do período da ação selecionada.
     *
     * @param dataAtividadeStr A data de início da atividade em formato String.
     * @param idAcaoSelecionada O ID da ação selecionada.
     * @return A data formatada como String ou null se a data for inválida.
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

        if (idAcaoSelecionada != -1L.toInt()) {
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
                            Toast.makeText(this@CadastroAtividadeActivity, "A data de início da Atividade não pode ser anterior à data de início da Ação.", Toast.LENGTH_SHORT).show()
                            return null
                        }
                        if (dataAtividade.after(dataTerminoAcao)) {
                            Toast.makeText(this@CadastroAtividadeActivity, "A data de início da Atividade não pode ser posterior à data de término da Ação.", Toast.LENGTH_SHORT).show()
                            return null
                        }
                        if (anoAtividadeStr != dataInicioAcaoStr.split("/")[2]) {
                            Toast.makeText(this@CadastroAtividadeActivity, "O ano da data de início da Atividade deve ser o mesmo da Ação.", Toast.LENGTH_SHORT).show()
                            return null
                        }
                    }
                }
                cursorAcao?.close()
            } catch (e: ParseException) {
                Toast.makeText(this@CadastroAtividadeActivity, "Erro ao comparar as datas com a Ação.", Toast.LENGTH_SHORT).show()
                return null
            } finally {
                db.close()
            }
        } else {
            Toast.makeText(this@CadastroAtividadeActivity, "Selecione uma Ação para validar a data de início.", Toast.LENGTH_SHORT).show()
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
     *
     * @param dataTerminoAtividadeStr A data de término da atividade em formato String.
     * @param dataInicioAtividadeStr A data de início da atividade em formato String.
     * @param idAcaoSelecionada O ID da ação selecionada.
     * @return A data formatada como String ou null se a data for inválida.
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
            Toast.makeText(this@CadastroAtividadeActivity, "O ano da data de término deve ser o mesmo da data de início.", Toast.LENGTH_SHORT).show()
            return null
        }

        val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        sdf.isLenient = false

        if (idAcaoSelecionada != -1L.toInt()) {
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
                        val dataTerminoAcaoStr = it.getString(it.getColumnIndexOrThrow(DatabaseHelper.COLUMN_ACAO_DATA_TERMINO))
                        val dataTerminoAcao = sdf.parse(dataTerminoAcaoStr)
                        val dataTerminoAtividade = sdf.parse(dataTerminoAtividadeStr)

                        if (dataTerminoAtividade.after(dataTerminoAcao)) {
                            Toast.makeText(this@CadastroAtividadeActivity, "A data de término da Atividade não pode ser posterior à data de término da Ação.", Toast.LENGTH_SHORT).show()
                            return null
                        }
                    }
                }
                cursorAcao?.close()
            } catch (e: ParseException) {
                Toast.makeText(this@CadastroAtividadeActivity, "Erro ao comparar as datas de término com a Ação.", Toast.LENGTH_SHORT).show()
                return null
            } finally {
                db.close()
            }
        } else {
            Toast.makeText(this@CadastroAtividadeActivity, "Selecione uma Ação para validar a data de término.", Toast.LENGTH_SHORT).show()
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
                Toast.makeText(this@CadastroAtividadeActivity, "A data de término não pode ser anterior à data de início.", Toast.LENGTH_SHORT).show()
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