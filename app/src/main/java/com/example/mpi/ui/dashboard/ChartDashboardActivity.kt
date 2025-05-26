package com.example.mpi.ui.dashboard

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.mpi.databinding.ActivityChartDashboardBinding
import com.github.mikephil.charting.components.Description
import com.github.mikephil.charting.data.*
import kotlin.random.Random

class ChartDashboardActivity : AppCompatActivity() {

    private lateinit var binding: ActivityChartDashboardBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Inflar o binding
        binding = ActivityChartDashboardBinding.inflate(layoutInflater)
        // Definir o content view
        setContentView(binding.root)
        // Só agora inicializar os gráficos
        setupBarChart()
        setupPieChart()
        setupLineChart()
    }

    private fun setupBarChart() {
        val entries = List(7) { i ->
            BarEntry(i.toFloat(), Random.nextFloat() * 90f + 10f)
        }
        val dataSet = BarDataSet(entries, "Vendas Semanais")
            .apply { valueTextSize = 12f }

        binding.barChart.apply {
            data = BarData(dataSet)
            description = Description().apply { text = "Gráfico de Barras" }
            animateY(800)
            invalidate()
        }
    }

    private fun setupPieChart() {
        val labels = listOf("A", "B", "C", "D", "E")
        val entries = labels.mapIndexed { idx, label ->
            PieEntry(Random.nextFloat() * 20f + 5f, label)
        }
        val dataSet = PieDataSet(entries, "Participação")
            .apply {
                sliceSpace = 2f
                valueTextSize = 12f
            }

        binding.pieChart.apply {
            data = PieData(dataSet)
            description = Description().apply { text = "Gráfico de Pizza" }
            isRotationEnabled = true
            animateY(800)
            invalidate()
        }
    }

    private fun setupLineChart() {
        val entries = List(12) { i ->
            Entry(i.toFloat(), Random.nextFloat() * 50f + 20f)
        }
        val dataSet = LineDataSet(entries, "Temperatura Mensal (°C)")
            .apply {
                lineWidth = 2f
                circleRadius = 4f
                valueTextSize = 10f
            }

        binding.lineChart.apply {
            data = LineData(dataSet)
            description = Description().apply { text = "Gráfico de Linhas" }
            animateX(1000)
            invalidate()
        }
    }
}
