package com.example.mpi.ui.relatorio

import android.content.ContentValues
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.mpi.R
import com.example.mpi.data.Calendario
import com.example.mpi.databinding.ActivityRelatorioPilarBinding
import com.example.mpi.repository.CalendarioRepository
import com.example.mpi.repository.PilarRepository
import com.example.mpi.data.Pilar
import com.example.mpi.repository.AcaoRepository
import com.example.mpi.repository.AtividadeRepository
import com.example.mpi.repository.SubpilarRepository
import com.example.mpi.repository.UsuarioRepository
import com.itextpdf.text.Document
import com.itextpdf.text.PageSize
import com.itextpdf.text.Paragraph
import com.itextpdf.text.pdf.PdfPTable
import com.itextpdf.text.pdf.PdfWriter
import java.io.OutputStream

/**
 * [RelatorioPilarActivity] é a Activity responsável por gerar e exibir um relatório em PDF
 * sobre o status de Pilares dentro do programa de integridade.
 *
 * Este relatório detalha cada pilar, suas ações associadas e as atividades dentro de cada ação,
 * incluindo seus respectivos status de aprovação e finalização. O PDF gerado
 * é salvo na pasta de Downloads do dispositivo e pode ser aberto automaticamente.
 */
class RelatorioPilarActivity : AppCompatActivity() {
    private lateinit var binding: ActivityRelatorioPilarBinding

    val USUARIO_ANALISTA = "ANALISTA"
    val USUARIO_COORDENADOR = "COORDENADOR"
    val USUARIO_GESTOR = "GESTOR"

    val calendarioRepository = CalendarioRepository(this)
    val pilarRepository = PilarRepository(this)
    val subpilarRepository = SubpilarRepository(this)
    val acaoRepository = AcaoRepository(this)
    val atividadeRepository = AtividadeRepository(this)
    val usuarioRepository = UsuarioRepository(this)

    private var pdfUri: Uri? = null

    /**
     * Chamado quando a Activity é criada pela primeira vez.
     *
     * Inicializa a interface do usuário usando View Binding, ajusta o preenchimento da janela
     * para o modo edge-to-edge, recupera as informações do usuário da Intent, e configura
     * os listeners de clique para o botão de voltar e o botão de gerar PDF.
     *
     * @param savedInstanceState Se não for nulo, esta Activity está sendo recriada
     * a partir de um estado salvo anteriormente.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityRelatorioPilarBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val intentExtra = intent
        val idUsuario = intentExtra.getIntExtra("idUsuario", 999999)
        val nomeUsuario = intentExtra.getStringExtra("nomeUsuario") ?: "Nome de usuário desconhecido"
        val tipoUsuario = intentExtra.getStringExtra("tipoUsuario") ?: "Tipo de usuário desconhecido"

        var pilarSelecionado: Pilar? = null

        binding.btnGerarPDFRelatorioPorPilar.visibility = View.GONE
        binding.textviewObs.visibility = View.GONE

        val idCal = calendarioRepository.obterIdCalendarioPorAno(2025)
        val listaPilar = pilarRepository.obterTodosPilares(Calendario(idCal, 2025))

        if (listaPilar.isEmpty()) {
            Toast.makeText(this, "Não existem pilares cadastrados no sistema", Toast.LENGTH_LONG).show()
        }

        if (listaPilar.isNotEmpty()) {
            val adapterPilar = ArrayAdapter(this@RelatorioPilarActivity, android.R.layout.simple_spinner_dropdown_item, listaPilar)
            binding.spinnerPilar.adapter = adapterPilar
            binding.btnGerarPDFRelatorioPorPilar.visibility = View.VISIBLE
            binding.textviewObs.visibility = View.VISIBLE
        }

        binding.spinnerPilar.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            /**
             * Chamado quando um item no spinner é selecionado.
             *
             * @param parent O AdapterView onde a seleção ocorreu.
             * @param view A view dentro do AdapterView que foi clicada.
             * @param position A posição da view no adaptador.
             * @param id O ID da linha do item selecionado.
             */
            override fun onItemSelected(
                parent: AdapterView<*>, view: View, position: Int, id: Long
            ) {
                pilarSelecionado = parent.getItemAtPosition(position) as Pilar
            }

            /**
             * Chamado quando nada é selecionado no spinner.
             * Atualmente, este método não faz nada.
             *
             * @param parent O AdapterView onde a seleção ocorreu.
             */
            override fun onNothingSelected(parent: AdapterView<*>?) {
                // Nada selecionado
            }
        }

        val openMenuRelatorio: ImageView = findViewById(R.id.imageview_voltarMenuRelatorio)
        openMenuRelatorio.setOnClickListener {
            val extra = Intent(this, RelatorioActivity::class.java)
            extra.putExtra("idUsuario", idUsuario)
            extra.putExtra("nomeUsuario", nomeUsuario)
            extra.putExtra("tipoUsuario", tipoUsuario)
            finish()
            startActivity(extra)
        }

        val createRelatorioPilar: Button = findViewById(R.id.btn_gerarPDFRelatorioPorPilar)
        createRelatorioPilar.setOnClickListener {
            if (pilarSelecionado != null) {
                gerarPDFRelatorioPilar(pilarSelecionado!!)
            }
        }

    }

    /**
     * Gera um arquivo PDF contendo o relatório detalhado de um Pilar específico.
     *
     * O PDF inclui informações sobre o pilar selecionado, suas ações e atividades,
     * juntamente com seus status de aprovação e finalização. O arquivo é salvo
     * na pasta de Downloads do dispositivo com o nome "MPI - Relatório Pilar [Nome do Pilar].pdf".
     * Se um arquivo com o mesmo nome já existir, ele é deletado antes de um novo ser criado.
     *
     * @param pilar O [Pilar] para o qual o relatório deve ser gerado.
     */
    private fun gerarPDFRelatorioPilar(pilar: Pilar) {
        try {
            val fileName = "MPI - Relatório Pilar ${pilar.nome}.pdf"
            val mimeType = "application/pdf"

            deletarPdfSeExistir(fileName)

            // Prepara MediaStore
            val contentValues = ContentValues().apply {
                put(MediaStore.Downloads.DISPLAY_NAME, fileName)
                put(MediaStore.Downloads.MIME_TYPE, mimeType)
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    put(MediaStore.Downloads.IS_PENDING, 1)
                }
            }

            val resolver = contentResolver
            val uri = resolver.insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, contentValues)
            pdfUri = uri // Guardamos para abrir depois

            if (uri != null) {
                val outputStream: OutputStream? = resolver.openOutputStream(uri)
                if (outputStream != null) {
                    
                    // Ínicio do PDF
                    val document = Document(PageSize.A4.rotate(), 36f, 36f, 50f, 50f)
                    PdfWriter.getInstance(document, outputStream)
                    document.open()

                    val todosSubpilares = subpilarRepository.obterTodosSubpilares(pilar)

/*                    // Adiciona um parágrafo
                    document.add(Paragraph("MPI - Monitoramento do Programa de Integridade"))
                    document.add(Paragraph("Relatório do Pilar: ${pilar.nome}"))

 */

                    if (todosSubpilares.isNotEmpty()) {
                        for (subpilar in todosSubpilares) {
                            // Adiciona um parágrafo
                            document.add(Paragraph("MPI - Monitoramento do Programa de Integridade"))
                            document.add(Paragraph("Relatório do Pilar: ${pilar.nome}"))
                            document.add(Paragraph("Subpilar: ${subpilar.nome}"))
                            document.add(Paragraph(" "))

                            val todasAcoes = acaoRepository.obterTodasAcoes(subpilar)

                            // Valores do cabeçalho da ação
                            if (todasAcoes.isNotEmpty()) {
                                for (acao in todasAcoes) {
                                    val todasAtividades = atividadeRepository.obterTodasAtividades(acao)

                                    // Cria uma tabela com 8 colunas para o cabeçalho da ação
                                    val tabelaAcao = PdfPTable(8)
                                    tabelaAcao.widthPercentage = 100f
                                    tabelaAcao.setWidths(floatArrayOf(3f, 1f, 1f, 1f, 1f, 1f, 1f, 1f))
                                    tabelaAcao.addCell("Ação")
                                    tabelaAcao.addCell("Atribuído")
                                    tabelaAcao.addCell("Orçamento")
                                    tabelaAcao.addCell("Aprovado")
                                    tabelaAcao.addCell("Finalizado")
                                    tabelaAcao.addCell("Progresso")
                                    tabelaAcao.addCell("Ínicio")
                                    tabelaAcao.addCell("Término")

                                    tabelaAcao.addCell(acao.nome)
                                    tabelaAcao.addCell("${usuarioRepository.obterUsuarioPorId(acao.responsavel)?.nome}")
                                    tabelaAcao.addCell(String.format("%.2f", acaoRepository.obterOrcamentoAcao(todasAtividades)).replace(".", ","))
                                    tabelaAcao.addCell(if (acao.aprovado) {"Sim"} else {"Não"})
                                    tabelaAcao.addCell(if (acao.finalizado) {"Sim"} else {"Não"})
                                    tabelaAcao.addCell(String.format("%.2f", acaoRepository.obterPercentualTotalAcao(acao)).replace(".", ",") + "%")
                                    tabelaAcao.addCell(acao.dataInicio)
                                    tabelaAcao.addCell(acao.dataTermino)

                                    document.add(tabelaAcao)
//                                    document.add(Paragraph(" "))

                                    // Cria uma tabela com 13 colunas para o progresso da ação
                                    val progressoAcao = PdfPTable(13)
                                    progressoAcao.widthPercentage = 100f
                                    progressoAcao.addCell("Progresso")
                                    progressoAcao.addCell("JAN")
                                    progressoAcao.addCell("FEV")
                                    progressoAcao.addCell("MAR")
                                    progressoAcao.addCell("ABR")
                                    progressoAcao.addCell("MAI")
                                    progressoAcao.addCell("JUN")
                                    progressoAcao.addCell("JUL")
                                    progressoAcao.addCell("AGO")
                                    progressoAcao.addCell("SET")
                                    progressoAcao.addCell("OUT")
                                    progressoAcao.addCell("NOV")
                                    progressoAcao.addCell("DEZ")

                                    // Valores do progresso da ação
                                    progressoAcao.addCell("Em %")
                                    for (mes in 1..12) {
                                        progressoAcao.addCell(String.format("%.2f", acaoRepository.obterPercentualMes(todasAtividades, mes)).replace(".", ","))
                                    }

                                    document.add(progressoAcao)

                                    // Fazer a parte da atividade
                                    document.add(Paragraph(" "))

                                    // Parte da atividade
                                    if (todasAtividades.isNotEmpty()) {
                                        for (atividade in todasAtividades) {
                                            // Cria uma tabela com 8 colunas para o cabeçalho da ação
                                            val tabelaAtividade = PdfPTable(8)
                                            tabelaAtividade.widthPercentage = 100f
                                            tabelaAtividade.setWidths(floatArrayOf(3f,1f,1f,1f,1f,1f,1f,1f))
                                            tabelaAtividade.addCell("Atividade da Ação")
                                            tabelaAtividade.addCell("Atribuído")
                                            tabelaAtividade.addCell("Orçamento")
                                            tabelaAtividade.addCell("Aprovado")
                                            tabelaAtividade.addCell("Finalizado")
                                            tabelaAtividade.addCell("Progresso")
                                            tabelaAtividade.addCell("Ínicio")
                                            tabelaAtividade.addCell("Término")

                                            tabelaAtividade.addCell(atividade.nome)
                                            tabelaAtividade.addCell("${usuarioRepository.obterUsuarioPorId(atividade.responsavel)?.nome}")
                                            tabelaAtividade.addCell(String.format("%.2f",atividade.orcamento).replace(".", ","))
                                            tabelaAtividade.addCell(if (atividade.aprovado) {"Sim"} else {"Não"})
                                            tabelaAtividade.addCell(if (atividade.finalizado) {"Sim"} else {"Não"})
                                            tabelaAtividade.addCell(String.format("%.2f", atividadeRepository.obterPercentualTotalAtividade(atividade)).replace(".", ",") + "%")
                                            tabelaAtividade.addCell(atividade.dataInicio)
                                            tabelaAtividade.addCell(atividade.dataTermino)

                                            document.add(tabelaAtividade)
//                                            document.add(Paragraph(" "))

                                            // Cria uma tabela com 13 colunas para o progresso da atividade
                                            val progressoAtividade = PdfPTable(13)
                                            progressoAtividade.widthPercentage = 100f
                                            progressoAtividade.addCell("Progresso")
                                            progressoAtividade.addCell("JAN")
                                            progressoAtividade.addCell("FEV")
                                            progressoAtividade.addCell("MAR")
                                            progressoAtividade.addCell("ABR")
                                            progressoAtividade.addCell("MAI")
                                            progressoAtividade.addCell("JUN")
                                            progressoAtividade.addCell("JUL")
                                            progressoAtividade.addCell("AGO")
                                            progressoAtividade.addCell("SET")
                                            progressoAtividade.addCell("OUT")
                                            progressoAtividade.addCell("NOV")
                                            progressoAtividade.addCell("DEZ")

                                            // Valores do progresso da ação
                                            progressoAtividade.addCell("Em %")
                                            for (mes in 1..12) {
                                                progressoAtividade.addCell(String.format("%.2f", atividadeRepository.obterPercentualMes(atividade, mes)).replace(".", ","))
                                            }

                                            document.add(progressoAtividade)
                                            document.add(Paragraph(" "))
                                        }
                                    }

                                    if (todasAtividades.isEmpty()) {
                                        val tabelaAtividade = PdfPTable(8)
                                        tabelaAtividade.widthPercentage = 100f
                                        tabelaAtividade.setWidths(floatArrayOf(3f, 1f, 1f, 1f, 1f, 1f, 1f, 1f))
                                        tabelaAtividade.addCell("Ação")
                                        tabelaAtividade.addCell("Atribuído")
                                        tabelaAtividade.addCell("Orçamento")
                                        tabelaAtividade.addCell("Aprovado")
                                        tabelaAtividade.addCell("Finalizado")
                                        tabelaAtividade.addCell("Progresso")
                                        tabelaAtividade.addCell("Ínicio")
                                        tabelaAtividade.addCell("Término")

                                        tabelaAtividade.addCell("Não existe Atividade para esta Ação")
                                        tabelaAtividade.addCell("")
                                        tabelaAtividade.addCell("")
                                        tabelaAtividade.addCell("")
                                        tabelaAtividade.addCell("")
                                        tabelaAtividade.addCell("")
                                        tabelaAtividade.addCell("")
                                        tabelaAtividade.addCell("")

                                        document.add(tabelaAtividade)
                                        document.add(Paragraph(" "))
                                    }

//                                    document.add(Paragraph(" "))
                                }
                            }

                            if (todasAcoes.isEmpty()) {
                                // Cria uma tabela com 8 colunas para o cabeçalho da ação
                                val tabelaAcao = PdfPTable(8)
                                tabelaAcao.widthPercentage = 100f
                                tabelaAcao.setWidths(floatArrayOf(3f, 1f, 1f, 1f, 1f, 1f, 1f, 1f))
                                tabelaAcao.addCell("Ação")
                                tabelaAcao.addCell("Atribuído")
                                tabelaAcao.addCell("Orçamento")
                                tabelaAcao.addCell("Aprovado")
                                tabelaAcao.addCell("Finalizado")
                                tabelaAcao.addCell("Progresso")
                                tabelaAcao.addCell("Ínicio")
                                tabelaAcao.addCell("Término")

                                tabelaAcao.addCell("Não existe Ação para este Subpilar")
                                tabelaAcao.addCell("")
                                tabelaAcao.addCell("")
                                tabelaAcao.addCell("")
                                tabelaAcao.addCell("")
                                tabelaAcao.addCell("")
                                tabelaAcao.addCell("")
                                tabelaAcao.addCell("")

                                document.add(tabelaAcao)
                                document.add(Paragraph(" "))
                            }
                            // Quebra de página ao trocar o subpilar
                            document.newPage()
                        }
                    }

                    // Ir direto para Ações
                    if (todosSubpilares.isEmpty()) {
                        document.add(Paragraph("MPI - Monitoramento do Programa de Integridade"))
                        document.add(Paragraph("Relatório do Pilar: ${pilar.nome}"))
                        document.add(Paragraph(" "))
                        document.add(Paragraph(" "))
                        val todasAcoes = acaoRepository.obterTodasAcoes(pilar)

                        // Valores do cabeçalho da ação
                        if (todasAcoes.isNotEmpty()) {
                            for (acao in todasAcoes) {
                                val todasAtividades = atividadeRepository.obterTodasAtividades(acao)

                                // Cria uma tabela com 8 colunas para o cabeçalho da ação
                                val tabelaAcao = PdfPTable(8)
                                tabelaAcao.widthPercentage = 100f
                                tabelaAcao.setWidths(floatArrayOf(3f, 1f, 1f, 1f, 1f, 1f, 1f, 1f))
                                tabelaAcao.addCell("Ação")
                                tabelaAcao.addCell("Atribuído")
                                tabelaAcao.addCell("Orçamento")
                                tabelaAcao.addCell("Aprovado")
                                tabelaAcao.addCell("Finalizado")
                                tabelaAcao.addCell("Progresso")
                                tabelaAcao.addCell("Ínicio")
                                tabelaAcao.addCell("Término")

                                tabelaAcao.addCell(acao.nome)
                                tabelaAcao.addCell("${usuarioRepository.obterUsuarioPorId(acao.responsavel)?.nome}")
                                tabelaAcao.addCell(String.format("%.2f", acaoRepository.obterOrcamentoAcao(todasAtividades)).replace(".", ","))
                                tabelaAcao.addCell(if (acao.aprovado) {"Sim"} else {"Não"})
                                tabelaAcao.addCell(if (acao.finalizado) {"Sim"} else {"Não"})
                                tabelaAcao.addCell(String.format("%.2f", acaoRepository.obterPercentualTotalAcao(acao)).replace(".", ",") + "%")
                                tabelaAcao.addCell(acao.dataInicio)
                                tabelaAcao.addCell(acao.dataTermino)

                                document.add(tabelaAcao)
//                                document.add(Paragraph(" "))

                                // Cria uma tabela com 13 colunas para o progresso da ação
                                val progressoAcao = PdfPTable(13)
                                progressoAcao.widthPercentage = 100f
                                progressoAcao.addCell("Progresso")
                                progressoAcao.addCell("JAN")
                                progressoAcao.addCell("FEV")
                                progressoAcao.addCell("MAR")
                                progressoAcao.addCell("ABR")
                                progressoAcao.addCell("MAI")
                                progressoAcao.addCell("JUN")
                                progressoAcao.addCell("JUL")
                                progressoAcao.addCell("AGO")
                                progressoAcao.addCell("SET")
                                progressoAcao.addCell("OUT")
                                progressoAcao.addCell("NOV")
                                progressoAcao.addCell("DEZ")

                                // Valores do progresso da ação
                                progressoAcao.addCell("Em %")
                                for (mes in 1..12) {
                                    progressoAcao.addCell(String.format("%.2f", acaoRepository.obterPercentualMes(todasAtividades, mes)).replace(".", ","))
                                }

                                document.add(progressoAcao)

                                // Fazer a parte da atividade
                                document.add(Paragraph(" "))

                                // Parte da atividade
                                if (todasAtividades.isNotEmpty()) {
                                    for (atividade in todasAtividades) {
                                        // Cria uma tabela com 8 colunas para o cabeçalho da ação
                                        val tabelaAtividade = PdfPTable(8)
                                        tabelaAtividade.widthPercentage = 100f
                                        tabelaAtividade.setWidths(floatArrayOf(3f,1f,1f,1f,1f,1f,1f,1f))
                                        tabelaAtividade.addCell("Atividade da Ação")
                                        tabelaAtividade.addCell("Atribuído")
                                        tabelaAtividade.addCell("Orçamento")
                                        tabelaAtividade.addCell("Aprovado")
                                        tabelaAtividade.addCell("Finalizado")
                                        tabelaAtividade.addCell("Progresso")
                                        tabelaAtividade.addCell("Ínicio")
                                        tabelaAtividade.addCell("Término")

                                        tabelaAtividade.addCell(atividade.nome)
                                        tabelaAtividade.addCell("${usuarioRepository.obterUsuarioPorId(atividade.responsavel)?.nome}")
                                        tabelaAtividade.addCell(String.format("%.2f",atividade.orcamento).replace(".", ","))
                                        tabelaAtividade.addCell(if (atividade.aprovado) {"Sim"} else {"Não"})
                                        tabelaAtividade.addCell(if (atividade.finalizado) {"Sim"} else {"Não"})
                                        tabelaAtividade.addCell(String.format("%.2f", atividadeRepository.obterPercentualTotalAtividade(atividade)).replace(".", ",") + "%")
                                        tabelaAtividade.addCell(atividade.dataInicio)
                                        tabelaAtividade.addCell(atividade.dataTermino)

                                        document.add(tabelaAtividade)
//                                        document.add(Paragraph(" "))

                                        // Cria uma tabela com 13 colunas para o progresso da atividade
                                        val progressoAtividade = PdfPTable(13)
                                        progressoAtividade.widthPercentage = 100f
                                        progressoAtividade.addCell("Progresso")
                                        progressoAtividade.addCell("JAN")
                                        progressoAtividade.addCell("FEV")
                                        progressoAtividade.addCell("MAR")
                                        progressoAtividade.addCell("ABR")
                                        progressoAtividade.addCell("MAI")
                                        progressoAtividade.addCell("JUN")
                                        progressoAtividade.addCell("JUL")
                                        progressoAtividade.addCell("AGO")
                                        progressoAtividade.addCell("SET")
                                        progressoAtividade.addCell("OUT")
                                        progressoAtividade.addCell("NOV")
                                        progressoAtividade.addCell("DEZ")

                                        // Valores do progresso da ação
                                        progressoAtividade.addCell("Em %")
                                        for (mes in 1..12) {
                                            progressoAtividade.addCell(String.format("%.2f", atividadeRepository.obterPercentualMes(atividade, mes)).replace(".", ","))
                                        }

                                        document.add(progressoAtividade)
                                        document.add(Paragraph(" "))
                                    }
                                }

                                if (todasAtividades.isEmpty()) {
                                    val tabelaAtividade = PdfPTable(8)
                                    tabelaAtividade.widthPercentage = 100f
                                    tabelaAtividade.setWidths(floatArrayOf(3f, 1f, 1f, 1f, 1f, 1f, 1f, 1f))
                                    tabelaAtividade.addCell("Ação")
                                    tabelaAtividade.addCell("Atribuído")
                                    tabelaAtividade.addCell("Orçamento")
                                    tabelaAtividade.addCell("Aprovado")
                                    tabelaAtividade.addCell("Finalizado")
                                    tabelaAtividade.addCell("Progresso")
                                    tabelaAtividade.addCell("Ínicio")
                                    tabelaAtividade.addCell("Término")

                                    tabelaAtividade.addCell("Não existe Atividade para esta Ação")
                                    tabelaAtividade.addCell("")
                                    tabelaAtividade.addCell("")
                                    tabelaAtividade.addCell("")
                                    tabelaAtividade.addCell("")
                                    tabelaAtividade.addCell("")
                                    tabelaAtividade.addCell("")
                                    tabelaAtividade.addCell("")

                                    document.add(tabelaAtividade)
                                    document.add(Paragraph(" "))
                                }

//                                document.add(Paragraph(" "))
                            }
                        }

                        if (todasAcoes.isEmpty()) {
                            // Cria uma tabela com 8 colunas para o cabeçalho da ação
                            val tabelaAcao = PdfPTable(8)
                            tabelaAcao.widthPercentage = 100f
                            tabelaAcao.setWidths(floatArrayOf(3f, 1f, 1f, 1f, 1f, 1f, 1f, 1f))
                            tabelaAcao.addCell("Ação")
                            tabelaAcao.addCell("Atribuído")
                            tabelaAcao.addCell("Orçamento")
                            tabelaAcao.addCell("Aprovado")
                            tabelaAcao.addCell("Finalizado")
                            tabelaAcao.addCell("Progresso")
                            tabelaAcao.addCell("Ínicio")
                            tabelaAcao.addCell("Término")

                            tabelaAcao.addCell("Não existe Ação para este Subpilar")
                            tabelaAcao.addCell("")
                            tabelaAcao.addCell("")
                            tabelaAcao.addCell("")
                            tabelaAcao.addCell("")
                            tabelaAcao.addCell("")
                            tabelaAcao.addCell("")
                            tabelaAcao.addCell("")

                            document.add(tabelaAcao)
                            document.add(Paragraph(" "))
                        }
                        // Quebra de página ao trocar o subpilar
                        document.newPage()
                    }

                    document.close()
                    outputStream.close()

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                        contentValues.clear()
                        contentValues.put(MediaStore.Downloads.IS_PENDING, 0)
                        resolver.update(uri, contentValues, null, null)
                    }

                    abrirPdfGerado()
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    /**
     * Abre o arquivo PDF gerado usando um Intent.
     *
     * Tenta abrir o PDF com um visualizador de PDF padrão do sistema.
     * Se não houver um aplicativo para lidar com o Intent, a exceção é capturada.
     */
    private fun abrirPdfGerado() {
        pdfUri?.let { uri ->
            val intent = Intent(Intent.ACTION_VIEW).apply {
                setDataAndType(uri, "application/pdf")
                flags = Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_ACTIVITY_NO_HISTORY
            }

            try {
                startActivity(intent)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    /**
     * Deleta um arquivo PDF existente na pasta de Downloads do dispositivo
     * com o nome especificado.
     *
     * @param nomeArquivo O nome do arquivo PDF a ser deletado.
     */
    private fun deletarPdfSeExistir(nomeArquivo: String) {
        val resolver = contentResolver
        val uriDownloads = MediaStore.Downloads.EXTERNAL_CONTENT_URI

        val selection = "${MediaStore.Downloads.DISPLAY_NAME} = ?"
        val selectionArgs = arrayOf(nomeArquivo)

        val cursor = resolver.query(
            uriDownloads,
            arrayOf(MediaStore.Downloads._ID),
            selection,
            selectionArgs,
            null
        )

        cursor?.use {
            if (it.moveToFirst()) {
                val id = it.getLong(it.getColumnIndexOrThrow(MediaStore.Downloads._ID))
                val uri = Uri.withAppendedPath(uriDownloads, id.toString())
                resolver.delete(uri, null, null)
                println("PDF anterior deletado: $uri")
            }
        }
    }
}