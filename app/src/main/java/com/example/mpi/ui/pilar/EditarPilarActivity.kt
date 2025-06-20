package com.example.mpi.ui.pilar

import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowInsetsCompat
import com.example.mpi.databinding.ActivityEditarPilarBinding
import com.example.mpi.data.DatabaseHelper
import com.example.mpi.repository.CalendarioRepository
import java.util.Calendar
import java.lang.NumberFormatException
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Locale

/**
 * [EditarPilarActivity] é uma Activity responsável por permitir a edição de um pilar existente.
 *
 * Esta Activity carrega os detalhes de um pilar a partir da Intent, exibe-os em campos de edição
 * e permite que o usuário modifique o nome, descrição e as datas de início e término.
 * Ela realiza validações de data e atualiza o pilar no banco de dados,
 * garantindo a consistência com o registro de calendário existente.
 */
class EditarPilarActivity : AppCompatActivity() {

    private lateinit var binding: ActivityEditarPilarBinding
    private lateinit var dbHelper: DatabaseHelper
    private var pilarId: Int = -1
    private lateinit var calendarioRepository: CalendarioRepository

    /**
     * Chamado quando a Activity é criada pela primeira vez.
     *
     * Inicializa o binding, o DatabaseHelper, o CalendarioRepository.
     * Recupera os dados do pilar a ser editado da Intent, preenche os campos
     * da UI e configura os listeners para os botões de salvar e voltar.
     *
     * @param savedInstanceState Se não for nulo, esta Activity está sendo recriada
     * a partir de um estado salvo anteriormente.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityEditarPilarBinding.inflate(layoutInflater)
        setContentView(binding.root)

        dbHelper = DatabaseHelper(this)
        calendarioRepository = CalendarioRepository.getInstance(this)

        val extras = intent.extras
        if (extras != null) {
            pilarId = extras.getInt("pilar_id", -1)
            val nome = extras.getString("pilar_nome")
            val descricao = extras.getString("pilar_descricao")
            val dataInicio = extras.getString("pilar_data_inicio")
            val dataTermino = extras.getString("pilar_data_termino")
            val percentual = extras.getDouble("pilar_percentual")
            val idCalendario = extras.getInt("pilar_id_calendario") // Recupera o ID do Calendário


            binding.etEditarNomePilar.setText(nome)
            binding.etEditarDescricaoPilar.setText(descricao)
            binding.etEditarDataInicio.setText(dataInicio)
            binding.etEditarDataTermino.setText(dataTermino)
        }

        binding.btnSalvarEdicao.setOnClickListener {
            salvarEdicaoPilar()
        }

        binding.btnVoltarEditar.setOnClickListener {
            finish() // Simplesmente finaliza a EditarPilarActivity e volta para PilarActivity
        }
    }

    /**
     * Salva as edições do pilar no banco de dados.
     *
     * Coleta os dados dos campos de entrada, realiza validações nas datas,
     * e verifica a consistência do ano da data de início com o calendário existente.
     * Se os dados forem válidos, atualiza o registro do pilar no banco de dados.
     * Exibe um Toast informando o resultado da operação e finaliza a Activity.
     */
    private fun salvarEdicaoPilar() {
        val nome = binding.etEditarNomePilar.text.toString()
        val descricao = binding.etEditarDescricaoPilar.text.toString()
        val dataInicio = binding.etEditarDataInicio.text.toString()
        val dataTermino = binding.etEditarDataTermino.text.toString()

        if (nome.isEmpty() || descricao.isEmpty() || dataInicio.isEmpty() || dataTermino.isEmpty() ) {
            Toast.makeText(this, "Preencha todos os campos!", Toast.LENGTH_SHORT).show()
            return
        }

        // Validando as datas informadas
        val dataInicioFormatada = validarEFormatarDataInicial(dataInicio)
        val dataTerminoFormatada = validarEFormatarDataFinal(dataTermino, dataInicio)

        if (dataInicioFormatada == null) {
            Toast.makeText(
                this,
                "Data de Início inválida. Use o formato dd/mm/aaaa.",
                Toast.LENGTH_SHORT
            ).show()
            return
        }

        if (dataTerminoFormatada == null) {
            Toast.makeText(
                this,
                "Data de Término inválida. Use o formato dd/mm/aaaa.",
                Toast.LENGTH_SHORT
            ).show()
            return
        }

        // Extraindo o ano informado e validado para adicionar um novo registro de calendário, em caso de ser o primeiro pilar
        val partesDataInicioFormatada = dataInicio.split("/") //
        val anoCalendarioStr = partesDataInicioFormatada.getOrNull(2)
        if (anoCalendarioStr.isNullOrEmpty()) {
            Toast.makeText(
                this,
                "Formato da data de início inválido. Use dd/mm/aaaa",
                Toast.LENGTH_SHORT
            ).show()
            return
        }

        val anoCalendario = anoCalendarioStr.toIntOrNull()
        if (anoCalendario == null) {
            Toast.makeText(this, "Ano da data de início inválido", Toast.LENGTH_SHORT).show()
            return
        }

        var idCalendario: Int

        if (calendarioRepository.contarPilares() == 0) {
            // Não existe nenhum pilar, então criamos um registro de calendário para o ano da data de início
            val calendarioIdExistente = calendarioRepository.obterIdCalendarioPorAno(anoCalendario)
            if (calendarioIdExistente == (-1L).toInt()) {
                idCalendario = calendarioRepository.inserirCalendario(anoCalendario)
                if (idCalendario == -1L.toInt()) {
                    Toast.makeText(
                        this,
                        "Erro ao criar registro de calendário inicial",
                        Toast.LENGTH_SHORT
                    ).show()
                    return
                }
            } else {
                idCalendario = calendarioIdExistente
            }
        } else {
            // Já tinha pilares, então o primeiro ID de calendário será consultado e implementado na tabela Pilar
            val db = dbHelper.readableDatabase
            val cursor = db.rawQuery(
                "SELECT ${DatabaseHelper.COLUMN_PILAR_ID_CALENDARIO} FROM ${DatabaseHelper.TABLE_PILAR} LIMIT 1",
                null
            )
            if (cursor != null && cursor.moveToFirst()) {
                idCalendario = cursor.getInt(0)
                // Validando se o ano inserido para editar é igual ao do calendário
                val idCalendarioPrimeiroPilar = cursor.getLong(0)
                cursor.close()

                val dbCalendario = dbHelper.readableDatabase
                val cursorCalendario = dbCalendario.query(
                    DatabaseHelper.TABLE_CALENDARIO,
                    arrayOf(DatabaseHelper.COLUMN_CALENDARIO_ANO),
                    "${DatabaseHelper.COLUMN_CALENDARIO_ID} = ?",
                    arrayOf(idCalendarioPrimeiroPilar.toString()),
                    null,
                    null,
                    null
                )

                var anoCalendarioExistente: Int? = null
                cursorCalendario?.use {
                    if (it.moveToFirst()) {
                        anoCalendarioExistente = it.getInt(it.getColumnIndexOrThrow(DatabaseHelper.COLUMN_CALENDARIO_ANO))
                    }
                }
                cursorCalendario?.close()
                dbCalendario.close()

                val anoCalendarioInserido = anoCalendarioStr.toIntOrNull()
                if (anoCalendarioInserido != null && anoCalendarioExistente != null && anoCalendarioInserido != anoCalendarioExistente) {
                    Toast.makeText(
                        this,
                        "O ano da data de início ($anoCalendarioInserido) não corresponde ao ano do calendário existente ($anoCalendarioExistente).",
                        Toast.LENGTH_LONG
                    ).show()
                    return
                } else if (anoCalendarioInserido == null) {
                    Toast.makeText(this, "Ano da data de início inválido", Toast.LENGTH_SHORT).show()
                    return
                }
            } else {
                Toast.makeText(this, "Erro ao obter ID do calendário existente", Toast.LENGTH_SHORT)
                    .show()
                db.close()
                return
            }
            db.close()

        }


        if (pilarId != -1L.toInt()) {
            val db = dbHelper.writableDatabase
            val values = android.content.ContentValues().apply {
                put(DatabaseHelper.COLUMN_PILAR_NOME, nome)
                put(DatabaseHelper.COLUMN_PILAR_DESCRICAO, descricao)
                put(DatabaseHelper.COLUMN_PILAR_DATA_INICIO, dataInicio)
                put(DatabaseHelper.COLUMN_PILAR_DATA_TERMINO, dataTermino)
                put(DatabaseHelper.COLUMN_PILAR_ID_CALENDARIO, idCalendario)

            }

            val whereClause = "${DatabaseHelper.COLUMN_PILAR_ID} = ?"
            val whereArgs = arrayOf(pilarId.toString())
            val rowsAffected = db.update(
                DatabaseHelper.TABLE_PILAR,
                values,
                whereClause,
                whereArgs
            )
            db.close()

            if (rowsAffected > 0) {
                Toast.makeText(this, "Pilar atualizado com sucesso!", Toast.LENGTH_SHORT).show()
                setResult(RESULT_OK)
                finish()
            } else {
                Toast.makeText(this, "Erro ao atualizar o pilar.", Toast.LENGTH_SHORT).show()
            }
        } else {
            Toast.makeText(this, "Erro: ID do pilar não encontrado.", Toast.LENGTH_SHORT).show()
        }
    }

    /**
     * Valida e formata uma string de data para o formato "dd/MM/yyyy".
     *
     * Verifica se a data está no formato correto (dd/MM/yyyy), se os valores de dia, mês e ano
     * são válidos dentro de um intervalo razoável (anos entre 2025 e 2100).
     *
     * @param data A string da data a ser validada (ex: "01/01/2025").
     * @return A string da data formatada se for válida, ou `null` caso contrário.
     */
    private fun validarEFormatarDataInicial(data: String): String? {
        if (data.isNullOrEmpty()) {
            return null
        }

        val partes = data.split("/")
        if (partes.size != 3) {
            return null
        }

        val diaStr = partes[0]
        val mesStr = partes[1]
        val anoStr = partes[2]

        if (diaStr.length != 2 || mesStr.length != 2 || anoStr.length != 4) {
            return null
        }

        return try {
            val dia = diaStr.toInt()
            val mes = mesStr.toInt()
            val ano = anoStr.toInt()

            if (dia < 1 || dia > 31 || mes < 1 || mes > 12 || ano < 2025 || ano > 2100) {
                return null
            }

            val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
            sdf.isLenient = false
            sdf.parse("$dia/$mes/$ano")
            data
        } catch (e: NumberFormatException) {
            null // Algum valor não é um número
        } catch (e: ParseException) {
            null // Data inválida
        }
    }

    /**
     * Valida e formata uma string de data de término, garantindo que seja posterior
     * ou igual à data de início e esteja no formato correto "dd/MM/yyyy".
     *
     * Verifica o formato, a validade dos valores e a relação entre as datas de início e término.
     * Exibe um Toast se o ano de término for diferente do ano de início, ou se a data de término
     * for anterior à data de início.
     *
     * @param dataTerminoStr A string da data de término a ser validada (ex: "31/12/2025").
     * @param dataInicioStr A string da data de início para comparação (ex: "01/01/2025").
     * @return A string da data de término formatada se for válida, ou `null` caso contrário.
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
            return null // Data de início com formato inválido (isso já deveria ter sido pego antes)
        }

        val anoInicioStr = partesInicio[2]

        if (anoTerminoStr != anoInicioStr) {
            Toast.makeText(
                this@EditarPilarActivity,
                "O ano da data de término deve ser o mesmo da data de início.",
                Toast.LENGTH_SHORT
            ).show()
            return null
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
                Toast.makeText(
                    this@EditarPilarActivity,
                    "A data de término não pode ser anterior à data de início.",
                    Toast.LENGTH_SHORT
                ).show()
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