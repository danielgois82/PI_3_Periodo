package com.example.mpi.ui.dashboard

import android.content.Intent
import android.graphics.Color
import android.graphics.Color.rgb
import android.os.Bundle
import android.widget.ImageView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.mpi.R
import com.example.mpi.databinding.ActivityOpcao1Binding
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.components.Description
import com.example.mpi.repository.AcaoRepository

class Opcao1Activity : AppCompatActivity() {
    private lateinit var binding: ActivityOpcao1Binding

    val acaoRepository = AcaoRepository(this)

    val USUARIO_ANALISTA = "ANALISTA"
    val USUARIO_COORDENADOR = "COORDENADOR"
    val USUARIO_GESTOR = "GESTOR"

    lateinit var pieChart: PieChart

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityOpcao1Binding.inflate(layoutInflater)
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

        val openDashboard: ImageView = findViewById(R.id.button_voltar)
        openDashboard.setOnClickListener {
            val extra = Intent(this, DashboardActivity::class.java)
            extra.putExtra("idUsuario", idUsuario)
            extra.putExtra("nomeUsuario", nomeUsuario)
            extra.putExtra("tipoUsuario", tipoUsuario)
            startActivity(extra)
        }

        gerarPieChart()

    }

    private fun gerarPieChart() {
        pieChart = findViewById(R.id.pieChart)

        val pieDataSet = PieDataSet(dadosDoPieChart(), "")
        val listaCoresPieChart: MutableList<Int> = mutableListOf(rgb(40, 60, 90), rgb(35, 75, 35), rgb(100, 40, 40))
        pieDataSet.colors = listaCoresPieChart
        pieDataSet.valueTextColor = Color.WHITE
        pieDataSet.valueTextSize = 24f

        val pieData = PieData(pieDataSet)

        pieChart.setDrawEntryLabels(false)

        pieChart.setUsePercentValues(true)

        pieChart.setCenterText("Percentual")
        pieChart.setCenterTextSize(36f)

        val description = Description()
        description.text = "Legenda:"
        description.textColor = Color.BLACK
        description.textSize = 16f
        description.setPosition(180f, 1850f)

        pieChart.description = description
        pieChart.description.isEnabled = true

        pieChart.data = pieData

        val legend = pieChart.legend
        legend.textSize = 16f

        pieChart.invalidate()
    }

    private fun dadosDoPieChart(): ArrayList<PieEntry> {
        val qtdeFinalizado = acaoRepository.obterQuantidadeAcoesFinalizadas()
        val qtdeEmAndamento = acaoRepository.obterQuantidadeAcoesEmAndamento()
        val qtdeAtrasado = acaoRepository.obterQuantidadeAcoesAtrasadas()

        val dataVals = ArrayList<PieEntry>()

        dataVals.add(PieEntry(qtdeFinalizado.toFloat(), "Finalizado"))
        dataVals.add(PieEntry(qtdeEmAndamento.toFloat(), "Em andamento"))
        dataVals.add(PieEntry(qtdeAtrasado.toFloat(), "Atrasado"))

        return dataVals
    }

}


