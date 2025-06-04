package com.example.mpi.ui.dashboard

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.mpi.R
import com.example.mpi.databinding.ActivityOpcao3Binding
import com.example.mpi.repository.AtividadeRepository
import com.github.mikephil.charting.components.Description
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.github.mikephil.charting.formatter.ValueFormatter
import com.github.mikephil.charting.utils.ColorTemplate

class Opcao3Activity : AppCompatActivity() {
    private lateinit var binding: ActivityOpcao3Binding

    val atividadeRepository = AtividadeRepository(this)

    val USUARIO_ANALISTA = "ANALISTA"
    val USUARIO_COORDENADOR = "COORDENADOR"
    val USUARIO_GESTOR = "GESTOR"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityOpcao3Binding.inflate(layoutInflater)
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

    private fun gerarHorizontalChart() {
// Lista de entradas para o gráfico de barras
        val barEntries = ArrayList<BarEntry>()
        val total30OuMenos = atividadeRepository.obterQuantidadeAtividades30DiasOuMenos()
        val total15OuMenos = atividadeRepository.obterQuantidadeAtividades15DiasOuMenos()
        val total7OuMenos = atividadeRepository.obterQuantidadeAtividades7DiasOuMenos()
        val totalAtrasadas = atividadeRepository.obterQuantidadeAtividadesAtrasadas()
        barEntries.add(BarEntry(0f, total30OuMenos.toFloat()))
        barEntries.add(BarEntry(1f, total15OuMenos.toFloat()))
        barEntries.add(BarEntry(2f, total7OuMenos.toFloat()))
        barEntries.add(BarEntry(3f, totalAtrasadas.toFloat()))

// Lista de rótulos (caso você use em eixo X, por exemplo)
        var qtdPrazos = 4
        val nomePrazos = arrayListOf<String>()
        nomePrazos.add("30 dias ou menos")
        nomePrazos.add("15 dias ou menos")
        nomePrazos.add("7 dias ou menos")
        nomePrazos.add("Vencida")

// Criação do DataSet
        val barSet = BarDataSet(barEntries, "").apply {
            colors = ColorTemplate.COLORFUL_COLORS.toList()
        }

// Criação do BarData
        val barData = BarData(barSet)

        barData.setValueFormatter(object : ValueFormatter() {
            override fun getFormattedValue(value: Float): String {
                return value.toInt().toString()
            }
        })

// Assumindo que você tem um Chart chamado h_chartbar
        binding.hChartbar.data = barData
        binding.hChartbar.animateY(2000)
        binding.hChartbar.setFitBars(true)

        val description = Description().apply {
            text = "Atividades"
        }

        binding.hChartbar.description = description

        binding.hChartbar.xAxis.labelCount = qtdPrazos
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

        binding.hChartbar.xAxis.valueFormatter = IndexAxisValueFormatter(nomePrazos)
        binding.hChartbar.xAxis.textSize = 15f
        binding.hChartbar.xAxis.granularity = 0.2f
        binding.hChartbar.xAxis.isGranularityEnabled = true

        val leftAxis = binding.hChartbar.axisLeft
        leftAxis.granularity = 1f
        leftAxis.isGranularityEnabled = true

        val rightAxis = binding.hChartbar.axisRight
        rightAxis.granularity = 1f
        rightAxis.isGranularityEnabled = true

        binding.hChartbar.setTouchEnabled(false)

        binding.hChartbar.setVisibleXRangeMaximum(qtdPrazos.toFloat())

    }
}