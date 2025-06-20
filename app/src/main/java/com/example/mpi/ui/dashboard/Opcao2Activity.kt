package com.example.mpi.ui.dashboard

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.mpi.R
import com.example.mpi.data.Calendario
import com.example.mpi.databinding.ActivityOpcao2Binding
import com.example.mpi.repository.CalendarioRepository
import com.example.mpi.repository.PilarRepository
import com.github.mikephil.charting.components.Description
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.utils.ColorTemplate

/**
 * [Opcao2Activity] é uma Activity de dashboard que exibe um gráfico de barras horizontais
 * representando o progresso dos Pilares do programa de integridade.
 *
 * Esta tela recupera os dados de progresso dos pilares do banco de dados e os visualiza
 * em um gráfico usando a biblioteca MPAndroidChart. Também gera uma legenda dinâmica
 * para os pilares abaixo do gráfico.
 */
class Opcao2Activity : AppCompatActivity() {
    private lateinit var binding: ActivityOpcao2Binding

    val calendarioRepository = CalendarioRepository(this)
    val pilarRepository = PilarRepository(this)

    val USUARIO_ANALISTA = "ANALISTA"
    val USUARIO_COORDENADOR = "COORDENADOR"
    val USUARIO_GESTOR = "GESTOR"

    /**
     * Chamado quando a Activity é criada pela primeira vez.
     *
     * Inicializa a interface do usuário, ajusta o preenchimento da janela
     * para o modo edge-to-edge, recupera as informações do usuário da Intent,
     * configura o listener para o botão de voltar ao dashboard e gera o gráfico.
     *
     * @param savedInstanceState Se não for nulo, esta Activity está sendo recriada
     * a partir de um estado salvo anteriormente.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityOpcao2Binding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val intentExtra = intent
        val idUsuario = intentExtra.getIntExtra("idUsuario", 999999)
        val nomeUsuario =
            intentExtra.getStringExtra("nomeUsuario") ?: "Nome de usuário desconhecido"
        val tipoUsuario =
            intentExtra.getStringExtra("tipoUsuario") ?: "Tipo de usuário desconhecido"

        val openDashboard: ImageView = findViewById(R.id.button_voltar)
        openDashboard.setOnClickListener {
            val extra = Intent(this, DashboardActivity::class.java)
            extra.putExtra("idUsuario", idUsuario)
            extra.putExtra("nomeUsuario", nomeUsuario)
            extra.putExtra("tipoUsuario", tipoUsuario)
            finish()
            startActivity(extra)
        }

        gerarHorizontalChart()
    }

    /**
     * Gera e configura um gráfico de barras horizontais (Horizontal Bar Chart)
     * exibindo o progresso de cada pilar.
     *
     * O método recupera os dados dos pilares do repositório, cria as entradas para o gráfico,
     * configura o dataset, os dados do gráfico e personaliza a aparência do gráfico,
     * incluindo eixos e uma legenda dinâmica.
     */
    private fun gerarHorizontalChart() {
        val idCal = calendarioRepository.obterIdCalendarioPorAno(2025)
        val todosPilares = pilarRepository.obterTodosPilares(Calendario(idCal, 2025))

// Lista de entradas para o gráfico de barras
        val barEntries = ArrayList<BarEntry>()
        var idx = 1f
        for (p in todosPilares) {
            val progresso = pilarRepository.obterProgressoPilar(p).toFloat()
            barEntries.add(BarEntry(idx, progresso))
            idx++
        }

// Lista de rótulos (caso você use em eixo X, por exemplo)
        var qtdPilares = 0
        val nomePilares = arrayListOf<String>()
        for (p in todosPilares) {
            qtdPilares++
            nomePilares.add(p.nome)
        }

// Criação do DataSet
        val barSet = BarDataSet(barEntries, "").apply {
            colors = ColorTemplate.COLORFUL_COLORS.toList()
        }

// Criação do BarData
        val barData = BarData(barSet)

// Assumindo que você tem um Chart chamado h_chartbar
        binding.hChartbar.data = barData
        binding.hChartbar.animateY(2000)
        binding.hChartbar.setFitBars(true)

        val description = Description().apply {
            text = "Pilares"
        }

        binding.hChartbar.description = description

        binding.hChartbar.xAxis.labelCount = qtdPilares
        binding.hChartbar.extraRightOffset = 50f

        barData.setValueTextSize(15f)

        binding.hChartbar.invalidate() // Atualiza o gráfico

        val legend = binding.hChartbar.legend
        legend.isEnabled = false

        val xAxis: XAxis = binding.hChartbar.xAxis
        xAxis.setDrawGridLines(true)
        xAxis.position = XAxis.XAxisPosition.BOTTOM
        xAxis.isGranularityEnabled = true
        xAxis.granularity = 1f
        xAxis.setDrawLabels(true)
        xAxis.xOffset = 10f
        xAxis.setDrawAxisLine(true)

//        binding.hChartbar.xAxis.valueFormatter = IndexAxisValueFormatter(nomePilares)
        binding.hChartbar.xAxis.textSize = 15f
        binding.hChartbar.xAxis.granularity = 0.2f
        binding.hChartbar.xAxis.isGranularityEnabled = true

        val leftAxis = binding.hChartbar.axisLeft
        leftAxis.axisMaximum = 100f
        leftAxis.axisMinimum = 0f

        val rightAxis = binding.hChartbar.axisRight
        rightAxis.axisMaximum = 100f
        rightAxis.axisMinimum = 0f

        binding.hChartbar.setTouchEnabled(false)

        binding.hChartbar.setVisibleXRangeMaximum(qtdPilares.toFloat())

        leftAxis.setDrawLabels(false)
        leftAxis.setDrawGridLines(false)
        leftAxis.setDrawAxisLine(false)

        val legendContainer = binding.legendContainer
        legendContainer.removeAllViews()

        nomePilares.forEachIndexed { index, nome ->
            val itemLayout = LinearLayout(this).apply {
                orientation = LinearLayout.HORIZONTAL
                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                )
                setPadding(0, 8, 0, 8)
            }

            val colorBox = View(this).apply {
                layoutParams = LinearLayout.LayoutParams(40, 40).apply {
                    setMargins(0, 0, 16, 0)
                }
                setBackgroundColor(ColorTemplate.COLORFUL_COLORS[index % ColorTemplate.COLORFUL_COLORS.size])
            }

            val label = TextView(this).apply {
                text = "${index + 1} - $nome"
                textSize = 16f
                setTextColor(Color.BLACK)
            }

            itemLayout.addView(colorBox)
            itemLayout.addView(label)
            legendContainer.addView(itemLayout)
        }
    }
}