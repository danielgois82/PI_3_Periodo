package com.example.mpi.ui.relatorio

import android.content.ContentValues
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Button
import android.widget.ImageView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.mpi.R
import com.example.mpi.data.Calendario
import com.example.mpi.repository.CalendarioRepository
import com.example.mpi.repository.PilarRepository
import com.example.mpi.databinding.ActivityRelatorioOkractivityBinding
import com.itextpdf.text.Document
import com.itextpdf.text.Element
import com.itextpdf.text.PageSize
import com.itextpdf.text.Paragraph
import com.itextpdf.text.Phrase
import com.itextpdf.text.pdf.PdfPCell
import com.itextpdf.text.pdf.PdfPTable
import com.itextpdf.text.pdf.PdfWriter
import java.io.OutputStream

/**
 * [RelatorioOKRActivity] é a Activity responsável por gerar e exibir um relatório em PDF
 * sobre os números de OKR (Objetivos e Resultados-Chave) do programa de integridade.
 *
 * Este relatório apresenta o andamento mensal de cada [Pilar] do programa e o andamento total mensal
 * de todos os pilares para um ano específico (atualmente fixado em 2025). O PDF gerado
 * é salvo na pasta de Downloads do dispositivo e pode ser aberto automaticamente.
 */
class RelatorioOKRActivity : AppCompatActivity() {
    private lateinit var binding: ActivityRelatorioOkractivityBinding

    val USUARIO_ANALISTA = "ANALISTA"
    val USUARIO_COORDENADOR = "COORDENADOR"
    val USUARIO_GESTOR = "GESTOR"

    val calendarioRepository = CalendarioRepository(this)
    val pilarRepository = PilarRepository(this)

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

        binding = ActivityRelatorioOkractivityBinding.inflate(layoutInflater)
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


        val openMenuRelatorio: ImageView = findViewById(R.id.imageview_voltarMenuRelatorio)
        openMenuRelatorio.setOnClickListener {
            val extra = Intent(this, RelatorioActivity::class.java)
            extra.putExtra("idUsuario", idUsuario)
            extra.putExtra("nomeUsuario", nomeUsuario)
            extra.putExtra("tipoUsuario", tipoUsuario)
            finish()
            startActivity(extra)
        }

        val createRelatorioOKR: Button = findViewById(R.id.btn_gerarPDFRelatorioOKR)
        createRelatorioOKR.setOnClickListener {
            gerarPDFRelatorioOKR()
        }

    }

    /**
     * Gera um arquivo PDF contendo o relatório de OKR.
     *
     * O PDF inclui um cabeçalho, uma tabela com o andamento mensal de cada pilar
     * e o andamento total mensal do programa. O arquivo é salvo na pasta de Downloads
     * do dispositivo com o nome "MPI - Relatório OKR.pdf". Se um arquivo com o mesmo
     * nome já existir, ele é deletado antes de um novo ser criado.
     */
    private fun gerarPDFRelatorioOKR() {
        try {
            val fileName = "MPI - Relatório OKR.pdf"
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

                    // Adiciona um parágrafo
                    document.add(Paragraph("MPI - Monitoramento do Programa de Integridade"))
                    document.add(Paragraph("Números do OKR (Objetivos e Resultados-Chave)"))
                    document.add(Paragraph(" "))

                    val idCal = calendarioRepository.obterIdCalendarioPorAno(2025)
                    val listaPilares = pilarRepository.obterTodosPilares(Calendario(idCal, 2025))

                    val anoOKR = PdfPTable(1)
                    anoOKR.widthPercentage = 100f
                    val ano = PdfPCell(Phrase("Ano de 2025"))
                    ano.horizontalAlignment = Element.ALIGN_CENTER
                    anoOKR.addCell(ano)
                    document.add(anoOKR)

                    val tabelaOKR = PdfPTable(13)
                    tabelaOKR.widthPercentage = 100f
                    tabelaOKR.setWidths(floatArrayOf(2.75f, 1f, 1f, 1f, 1f, 1f, 1f, 1f, 1f, 1f, 1f, 1f, 1f))
                    tabelaOKR.addCell("Pilares do Programa")
                    tabelaOKR.addCell("JAN")
                    tabelaOKR.addCell("FEV")
                    tabelaOKR.addCell("MAR")
                    tabelaOKR.addCell("ABR")
                    tabelaOKR.addCell("MAI")
                    tabelaOKR.addCell("JUN")
                    tabelaOKR.addCell("JUL")
                    tabelaOKR.addCell("AGO")
                    tabelaOKR.addCell("SET")
                    tabelaOKR.addCell("OUT")
                    tabelaOKR.addCell("NOV")
                    tabelaOKR.addCell("DEZ")

                    var qtdPilares = 0

                    var acumuladorMes1 = 0.0
                    var acumuladorMes2 = 0.0
                    var acumuladorMes3 = 0.0
                    var acumuladorMes4 = 0.0
                    var acumuladorMes5 = 0.0
                    var acumuladorMes6 = 0.0
                    var acumuladorMes7 = 0.0
                    var acumuladorMes8 = 0.0
                    var acumuladorMes9 = 0.0
                    var acumuladorMes10 = 0.0
                    var acumuladorMes11 = 0.0
                    var acumuladorMes12 = 0.0

                    for (pilar in listaPilares) {
                        qtdPilares++

                        acumuladorMes1 += pilarRepository.obterPercentualMes(pilar, 1)
                        acumuladorMes2 += pilarRepository.obterPercentualMes(pilar, 2)
                        acumuladorMes3 += pilarRepository.obterPercentualMes(pilar, 3)
                        acumuladorMes4 += pilarRepository.obterPercentualMes(pilar, 4)
                        acumuladorMes5 += pilarRepository.obterPercentualMes(pilar, 5)
                        acumuladorMes6 += pilarRepository.obterPercentualMes(pilar, 6)
                        acumuladorMes7 += pilarRepository.obterPercentualMes(pilar, 7)
                        acumuladorMes8 += pilarRepository.obterPercentualMes(pilar, 8)
                        acumuladorMes9 += pilarRepository.obterPercentualMes(pilar, 9)
                        acumuladorMes10 += pilarRepository.obterPercentualMes(pilar, 10)
                        acumuladorMes11 += pilarRepository.obterPercentualMes(pilar, 11)
                        acumuladorMes12 += pilarRepository.obterPercentualMes(pilar, 12)

                        tabelaOKR.addCell(pilar.nome)
                        tabelaOKR.addCell(String.format("%.2f", pilarRepository.obterPercentualMes(pilar, 1)).replace(".", ",") + "%")
                        tabelaOKR.addCell(String.format("%.2f", pilarRepository.obterPercentualMes(pilar, 2)).replace(".", ",") + "%")
                        tabelaOKR.addCell(String.format("%.2f", pilarRepository.obterPercentualMes(pilar, 3)).replace(".", ",") + "%")
                        tabelaOKR.addCell(String.format("%.2f", pilarRepository.obterPercentualMes(pilar, 4)).replace(".", ",") + "%")
                        tabelaOKR.addCell(String.format("%.2f", pilarRepository.obterPercentualMes(pilar, 5)).replace(".", ",") + "%")
                        tabelaOKR.addCell(String.format("%.2f", pilarRepository.obterPercentualMes(pilar, 6)).replace(".", ",") + "%")
                        tabelaOKR.addCell(String.format("%.2f", pilarRepository.obterPercentualMes(pilar, 7)).replace(".", ",") + "%")
                        tabelaOKR.addCell(String.format("%.2f", pilarRepository.obterPercentualMes(pilar, 8)).replace(".", ",") + "%")
                        tabelaOKR.addCell(String.format("%.2f", pilarRepository.obterPercentualMes(pilar, 9)).replace(".", ",") + "%")
                        tabelaOKR.addCell(String.format("%.2f", pilarRepository.obterPercentualMes(pilar, 10)).replace(".", ",") + "%")
                        tabelaOKR.addCell(String.format("%.2f", pilarRepository.obterPercentualMes(pilar, 11)).replace(".", ",") + "%")
                        tabelaOKR.addCell(String.format("%.2f", pilarRepository.obterPercentualMes(pilar, 12)).replace(".", ",") + "%")
                    }

                    tabelaOKR.addCell("Andamento Total Mensal")
                    tabelaOKR.addCell(String.format("%.2f", acumuladorMes1 / qtdPilares).replace(".", ",") + "%")
                    tabelaOKR.addCell(String.format("%.2f", acumuladorMes2 / qtdPilares).replace(".", ",") + "%")
                    tabelaOKR.addCell(String.format("%.2f", acumuladorMes3 / qtdPilares).replace(".", ",") + "%")
                    tabelaOKR.addCell(String.format("%.2f", acumuladorMes4 / qtdPilares).replace(".", ",") + "%")
                    tabelaOKR.addCell(String.format("%.2f", acumuladorMes5 / qtdPilares).replace(".", ",") + "%")
                    tabelaOKR.addCell(String.format("%.2f", acumuladorMes6 / qtdPilares).replace(".", ",") + "%")
                    tabelaOKR.addCell(String.format("%.2f", acumuladorMes7 / qtdPilares).replace(".", ",") + "%")
                    tabelaOKR.addCell(String.format("%.2f", acumuladorMes8 / qtdPilares).replace(".", ",") + "%")
                    tabelaOKR.addCell(String.format("%.2f", acumuladorMes9 / qtdPilares).replace(".", ",") + "%")
                    tabelaOKR.addCell(String.format("%.2f", acumuladorMes10 / qtdPilares).replace(".", ",") + "%")
                    tabelaOKR.addCell(String.format("%.2f", acumuladorMes11 / qtdPilares).replace(".", ",") + "%")
                    tabelaOKR.addCell(String.format("%.2f", acumuladorMes12 / qtdPilares).replace(".", ",") + "%")

                    document.add(tabelaOKR)

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