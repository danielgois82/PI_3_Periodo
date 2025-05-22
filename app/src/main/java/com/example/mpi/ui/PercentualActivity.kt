package com.example.mpi.ui

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.mpi.R
import com.example.mpi.data.Acao
import com.example.mpi.data.Atividade
import com.example.mpi.data.Calendario
import com.example.mpi.data.Pilar
import com.example.mpi.data.Subpilar
import com.example.mpi.databinding.ActivityPercentualBinding
import com.example.mpi.repository.AcaoRepository
import com.example.mpi.repository.AtividadeRepository
import com.example.mpi.repository.CalendarioRepository
import com.example.mpi.repository.PercentualAtividadeRepository
import com.example.mpi.repository.PilarRepository
import com.example.mpi.repository.SubpilarRepository
import com.example.mpi.repository.UsuarioRepository

class PercentualActivity : AppCompatActivity() {
    private lateinit var binding: ActivityPercentualBinding

    val USUARIO_ANALISTA = "ANALISTA"
    val USUARIO_COORDENADOR = "COORDENADOR"
    val USUARIO_GESTOR = "GESTOR"

    val calendarioRepository = CalendarioRepository(this)
    val pilarRepository = PilarRepository(this)
    val subpilarRepository = SubpilarRepository(this)
    val acaoRepository = AcaoRepository(this)
    val atividadeRepository = AtividadeRepository(this)
    val usuarioRepository = UsuarioRepository(this)
    val percentualAtividadeRepository = PercentualAtividadeRepository(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityPercentualBinding.inflate(layoutInflater)
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

        // Carregar todos oa anos no spinner

        val listaCalendario = calendarioRepository.obterTodosCalendarios()
        val listaAnoCalendario: MutableList<Int> = arrayListOf()
        val mapaCalendario = mutableMapOf<Int, Int>()

        for (cal in listaCalendario) {
            listaAnoCalendario.add(cal.ano)
            mapaCalendario[cal.id] = cal.ano
        }

        val adapterCalendario = ArrayAdapter(
            this
            , android.R.layout.simple_spinner_dropdown_item
            , listaAnoCalendario)

        binding.spinnerAno.adapter = adapterCalendario

        // Fim do carregar todos os anos no spinner

        // Carregar todos os pilares no spinner

        val anoSelecionado = binding.spinnerAno.selectedItem.toString().toInt()

        var calendarioSelecionado: Calendario? = null
        for (cal in listaCalendario) {
            if (cal.ano == anoSelecionado) {
                calendarioSelecionado = cal
            }
        }

        val listaPilar = calendarioSelecionado?.let { pilarRepository.obterTodosPilares(it) }
        val nomePilares: MutableList<String> = arrayListOf()
        val mapaPilar = mutableMapOf<Int, String>()

        if (listaPilar != null) {
            for (pilar in listaPilar) {
                nomePilares.add(pilar.nome)
                mapaPilar[pilar.id] = pilar.nome
            }
        }

        val adapterPilar = ArrayAdapter(
            this
            , android.R.layout.simple_spinner_dropdown_item
            , nomePilares)

        binding.spinnerPilar.adapter = adapterPilar

        // Fim do carregar todos os pilares no spinner

        // Carregar todos os subpilares

        val nomePilarSelecionado = binding.spinnerPilar.selectedItem.toString()

        var pilarSelecionado: Pilar? = null
        if (listaPilar != null) {
            for (pilar in listaPilar) {
                if (pilar.nome == nomePilarSelecionado) {
                    pilarSelecionado = pilar
                }
            }
        }

        val listaSubpilar = pilarSelecionado?.let { subpilarRepository.obterTodosSubpilares(it) }
        val nomeSubpilares: MutableList<String> = arrayListOf()
        val mapaSubpilar = mutableMapOf<Int, String>()

        if (listaSubpilar != null) {
            for (subpilar in listaSubpilar) {
                nomeSubpilares.add(subpilar.nome)
                mapaSubpilar[subpilar.id] = subpilar.nome
            }
        }

        val adapterSubpilar = ArrayAdapter(
            this
            , android.R.layout.simple_spinner_dropdown_item
            , nomeSubpilares)

        binding.spinnerSubpilar.adapter = adapterSubpilar

        // Fim do carregar todos os subpilares

        // Fim do carregar todas as ações

////////////////////////////////////////////////////////////////////////////////////////////
//                                                                                        //
//                    FALTA FAZER A PESQUISA DE ACOES DIRETO PELO PILAR                   //
//                                                                                        //
////////////////////////////////////////////////////////////////////////////////////////////

        val nomeSubpilarSelecionado = binding.spinnerSubpilar.selectedItem.toString()

        var subpilarSelecionado: Subpilar? = null
        if (listaSubpilar != null) {
            for (subpilar in listaSubpilar) {
                if (subpilar.nome == nomeSubpilarSelecionado) {
                    subpilarSelecionado = subpilar
                }
            }
        }

        val listaAcao = subpilarSelecionado?.let { acaoRepository.obterTodasAcoes(it) }
        val nomeAcoes: MutableList<String> = arrayListOf()
        val mapaAcao = mutableMapOf<Int, String>()

        if (listaAcao != null) {
            for (acao in listaAcao) {
                nomeAcoes.add(acao.nome)
                mapaAcao[acao.id] = acao.nome
            }
        }

        val adapterAcao = ArrayAdapter(
            this
            , android.R.layout.simple_spinner_dropdown_item
            , nomeAcoes)

        binding.spinnerAcao.adapter = adapterAcao

        // Fim do carregar todas as ações

        // Carregar todas as atividades

        val nomeAcaoSelecionado = binding.spinnerAcao.selectedItem.toString()

        var acaoSelecionado: Acao? = null
        if (listaAcao != null) {
            for (acao in listaAcao) {
                if (acao.nome == nomeAcaoSelecionado) {
                    acaoSelecionado = acao
                }
            }
        }

        val listaAtividade = acaoSelecionado?.let { atividadeRepository.obterTodasAtividades(it) }
        val nomeAtividades: MutableList<String> = arrayListOf()
        val mapaAtividade = mutableMapOf<Int, String>()

        if (listaAtividade != null) {
            for (ativ in listaAtividade) {
                nomeAtividades.add(ativ.nome)
                mapaAtividade[ativ.id] = ativ.nome
            }
        }

        val adapterAtividade = ArrayAdapter(
            this
            , android.R.layout.simple_spinner_dropdown_item
            , nomeAtividades)

        binding.spinnerAtividade.adapter = adapterAtividade

        // Fim do carregar todas as atividades

        // Parte das atividades

        val nomeAtividadeSelecionado = binding.spinnerAtividade.selectedItem.toString()

        var atividadeSelecionado: Atividade? = null
        if (listaAtividade != null) {
            for (ativ in listaAtividade) {
                if (ativ.nome == nomeAtividadeSelecionado) {
                    atividadeSelecionado = ativ
                }
            }
        }

        // Fim da parte das atividades

        val openMenuPrincipal: ImageView = findViewById(R.id.viewVoltarMenuPrincipal)
        openMenuPrincipal.setOnClickListener {
            val extra = Intent(this, MenuActivity::class.java)
            extra.putExtra("idUsuario", idUsuario)
            extra.putExtra("nomeUsuario", nomeUsuario)
            extra.putExtra("tipoUsuario", tipoUsuario)
            startActivity(extra)
        }

        binding.buttonPesquisar.setOnClickListener {
            if (pilarSelecionado != null
                && subpilarSelecionado != null
                && acaoSelecionado != null
                && atividadeSelecionado != null) {
                carregarDados(pilarSelecionado
                    , subpilarSelecionado
                    , acaoSelecionado
                    , atividadeSelecionado)
            }
        }

        binding.textviewPercentualAtivJan.setOnClickListener {
            atualizarPercentual(binding.textviewPercentualAtivJan)
        }

        binding.textviewPercentualAtivFev.setOnClickListener {
            atualizarPercentual(binding.textviewPercentualAtivFev)
        }

        binding.textviewPercentualAtivMar.setOnClickListener {
            atualizarPercentual(binding.textviewPercentualAtivMar)
        }

        binding.textviewPercentualAtivAbr.setOnClickListener {
            atualizarPercentual(binding.textviewPercentualAtivAbr)
        }

        binding.textviewPercentualAtivMai.setOnClickListener {
            atualizarPercentual(binding.textviewPercentualAtivMai)
        }

        binding.textviewPercentualAtivJun.setOnClickListener {
            atualizarPercentual(binding.textviewPercentualAtivJun)
        }

        binding.textviewPercentualAtivJul.setOnClickListener {
            atualizarPercentual(binding.textviewPercentualAtivJul)
        }

        binding.textviewPercentualAtivAgo.setOnClickListener {
            atualizarPercentual(binding.textviewPercentualAtivAgo)
        }

        binding.textviewPercentualAtivSet.setOnClickListener {
            atualizarPercentual(binding.textviewPercentualAtivSet)
        }

        binding.textviewPercentualAtivOut.setOnClickListener {
            atualizarPercentual(binding.textviewPercentualAtivOut)
        }

        binding.textviewPercentualAtivNov.setOnClickListener {
            atualizarPercentual(binding.textviewPercentualAtivNov)
        }

        binding.textviewPercentualAtivDez.setOnClickListener {
            atualizarPercentual(binding.textviewPercentualAtivDez)
        }

        binding.buttonSalvarAlteracoes.setOnClickListener {
            salvarAlteracoes()
        }
    }

    private fun carregarDados(pilar: Pilar, subpilar: Subpilar, acao: Acao, atividade: Atividade) {

        val responsavelAcao = usuarioRepository.obterUsuarioPorId(acao.responsavel)
        val responsavelAtividade = usuarioRepository.obterUsuarioPorId(atividade.responsavel)

        val listaAtiv = atividadeRepository.obterTodasAtividades(acao)
        var percentualGeralTodasAtividade = 0.0
        for (ativ in listaAtiv) {
            val percAtividade = percentualAtividadeRepository.obterTodosPercentuais(ativ)
            for (p in percAtividade) {
                percentualGeralTodasAtividade += p.percentual
            }
        }
        percentualGeralTodasAtividade /= listaAtiv.size

        val percentuaisAtividade = percentualAtividadeRepository.obterTodosPercentuais(atividade)
        var percentualGeralAtividade = 0.0
        for (perc in percentuaisAtividade) {
            percentualGeralAtividade += perc.percentual
        }

        binding.textviewNomePilar.text = "Pilar: ${pilar.nome}"
        // Falta botar o percentual do pilar
        // Ver se dá para botar o orcamento do pilar

        binding.textviewNomeSubpilar.text = "Subpilar: ${subpilar.nome}"
        // Falta botar o percentual do subpilar
        // Ver se dá para botar o orcamento do subpilar

        binding.textviewNomeAcao.text = "Ação: ${acao.nome}"
        binding.textviewProgressoGeralAcao.text = String.format("%.2f", percentualGeralTodasAtividade) + "%"
        binding.textviewAcaoAtribuida.text = responsavelAcao!!.nome

        binding.textviewNomeAtividade.text = "Atividade: ${atividade.nome}"
        binding.textviewProgressoGeralAtividade.text = String.format("%.2f", percentualGeralAtividade) + "%"
        binding.textviewAtividadeAtribuida.text = responsavelAtividade!!.nome
        binding.textviewOrcamentoAtividade.text = "R$" + String.format("%.2f", atividade.orcamento)
        binding.textviewInicioAtividade.text = atividade.dataInicio
        binding.textviewTerminoAtividade.text = atividade.dataTermino

        // Falta todos os percentuais gerais e o percentual da atividade
    }

    private fun atualizarPercentual(inputTextView: TextView) {
        val editText = EditText(this)
        editText.hint = "Digite apenas o valor sem o '%'"
        AlertDialog.Builder(this)
            .setTitle("Insira o novo percentual")
            .setView(editText)
            .setPositiveButton("OK") { dialog, _ ->
                var validacaoOk = true
                val input = editText.text.toString()

                if (!validarEditTextComoNumero(input)) {
                    validacaoOk = false
                    Toast.makeText(
                        this, "Digite apenas números no percentual", Toast.LENGTH_LONG
                    )
                        .show()
                    dialog.dismiss()
                }

                if (validacaoOk) {
                    if (!validarFaixaDeValoresValidos(input)) {
                        validacaoOk = false
                        Toast.makeText(
                            this, "Valores devem ser entre 0 e 100", Toast.LENGTH_LONG
                        )
                            .show()
                        dialog.dismiss()
                    }
                }

                if (validacaoOk) {
                    val retorno = "${input.trim()}%"
                    inputTextView.text = retorno
                    dialog.dismiss()
                }
            }

            .setNegativeButton("Cancelar") { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }

    private fun validarEditTextComoNumero(input: String): Boolean {
        if (input.toDoubleOrNull() == null) {
            return false
        } else {
            return true
        }
    }

    private fun validarFaixaDeValoresValidos(input: String): Boolean {
        val inputNumber = input.toDouble()
        if (inputNumber < 0 || inputNumber > 100) {
            return false
        } else {
            return true
        }
    }

    private fun salvarAlteracoes() {
        binding.textviewNomePilar.text = "eueueueueueu"
    }

}