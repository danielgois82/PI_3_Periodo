package com.example.mpi.ui.dashboard

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
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
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.github.mikephil.charting.utils.ColorTemplate

class Opcao2Activity : AppCompatActivity() {
    private lateinit var binding: ActivityOpcao2Binding

    val calendarioRepository = CalendarioRepository(this)
    val pilarRepository = PilarRepository(this)

    val USUARIO_ANALISTA = "ANALISTA"
    val USUARIO_COORDENADOR = "COORDENADOR"
    val USUARIO_GESTOR = "GESTOR"

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

    private fun gerarHorizontalChart() {
        // https://www.youtube.com/watch?v=js1oJfRfjjI

        val idCal = calendarioRepository.obterIdCalendarioPorAno(2025)
        val todosPilares = pilarRepository.obterTodosPilares(Calendario(idCal, 2025))

// Lista de entradas para o gráfico de barras
        val barEntries = ArrayList<BarEntry>()
        var idx = 0f
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

        barData.setValueTextSize(14f)

        binding.hChartbar.invalidate() // Atualiza o gráfico

        val legend = binding.hChartbar.legend
        legend.isEnabled = true

        val xAxis: XAxis = binding.hChartbar.xAxis
        xAxis.setDrawGridLines(true)
        xAxis.position = XAxis.XAxisPosition.BOTTOM
        xAxis.isGranularityEnabled = true
        xAxis.granularity = 1f
        xAxis.setDrawLabels(true)
        xAxis.xOffset = 10f
        xAxis.setDrawAxisLine(true)

        binding.hChartbar.xAxis.valueFormatter = IndexAxisValueFormatter(nomePilares)
        binding.hChartbar.xAxis.granularity = 0.2f
        binding.hChartbar.xAxis.isGranularityEnabled = true

        val leftAxis = binding.hChartbar.axisLeft
        leftAxis.axisMaximum = 100f
        leftAxis.axisMinimum = 0f

        val rightAxis = binding.hChartbar.axisRight
        rightAxis.axisMaximum = 100f
        rightAxis.axisMinimum = 0f

        binding.hChartbar.setVisibleXRangeMaximum(qtdPilares.toFloat())

    }
}