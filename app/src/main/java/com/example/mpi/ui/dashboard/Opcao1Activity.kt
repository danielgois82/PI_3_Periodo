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

/**
 * [Opcao1Activity] é uma Activity de dashboard que exibe um gráfico de pizza (Pie Chart)
 * representando o status das Ações do programa de integridade.
 *
 * Esta tela recupera a contagem de ações finalizadas, em andamento e atrasadas do
 * banco de dados e as visualiza em um gráfico de pizza usando a biblioteca MPAndroidChart.
 * As porcentagens e a legenda são exibidas para facilitar a compreensão do status geral das ações.
 */
class Opcao1Activity : AppCompatActivity() {
    private lateinit var binding: ActivityOpcao1Binding

    val acaoRepository = AcaoRepository(this)

    val USUARIO_ANALISTA = "ANALISTA"
    val USUARIO_COORDENADOR = "COORDENADOR"
    val USUARIO_GESTOR = "GESTOR"

    lateinit var pieChart: PieChart

    /**
     * Chamado quando a Activity é criada pela primeira vez.
     *
     * Inicializa a interface do usuário, ajusta o preenchimento da janela
     * para o modo edge-to-edge, recupera as informações do usuário da Intent,
     * configura o listener para o botão de voltar ao dashboard e gera o gráfico de pizza.
     *
     * @param savedInstanceState Se não for nulo, esta Activity está sendo recriada
     * a partir de um estado salvo anteriormente.
     */
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
            finish()
            startActivity(extra)
        }

        gerarPieChart()

    }

    /**
     * Gera e configura um gráfico de pizza (Pie Chart) exibindo a distribuição
     * das ações por status (Finalizado, Em Andamento, Em Atraso).
     *
     * O método coleta os dados de status das ações, cria o dataset do gráfico,
     * define as cores, tamanhos de texto, configurações de percentual e
     * personaliza a descrição e a legenda do gráfico.
     */
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

    /**
     * Coleta os dados de status das ações (finalizadas, em andamento, atrasadas)
     * do [AcaoRepository] e os formata como [PieEntry] para o gráfico de pizza.
     *
     * @return Um [ArrayList] de [PieEntry] contendo as contagens e os rótulos
     * para cada categoria de status da ação.
     */
    private fun dadosDoPieChart(): ArrayList<PieEntry> {
        val qtdeFinalizado = acaoRepository.obterQuantidadeAcoesFinalizadas()
        val qtdeEmAndamento = acaoRepository.obterQuantidadeAcoesEmAndamento()
        val qtdeAtrasado = acaoRepository.obterQuantidadeAcoesAtrasadas()

        val dataVals = ArrayList<PieEntry>()

        dataVals.add(PieEntry(qtdeFinalizado.toFloat(), "Finalizado"))
        dataVals.add(PieEntry(qtdeEmAndamento.toFloat(), "Em andamento"))
        dataVals.add(PieEntry(qtdeAtrasado.toFloat(), "Em atraso"))

        return dataVals
    }

}


