package com.example.mpi.ui

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
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

/**
 * A [PercentualActivity] é uma tela que permite ao usuário visualizar e,
 * dependendo do seu tipo de perfil, gerenciar os percentuais de progresso
 * mensais de uma atividade específica.
 *
 * Esta Activity apresenta um fluxo de seleção em cascata (Pilar -> Subpilar -> Ação -> Atividade)
 * para que o usuário possa pinpoint a atividade desejada. Após a seleção,
 * são exibidos os percentuais de conclusão para cada mês do ano.
 * Usuários com o perfil de "Gestor" têm acesso apenas para leitura, enquanto
 * outros perfis podem editar os percentuais, respeitando a regra de que
 * a soma total não pode exceder 100%.
 */
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

    /**
     * Chamado quando a Activity é criada pela primeira vez.
     *
     * Este método inicializa a interface do usuário, habilita o modo edge-to-edge,
     * recupera as informações do usuário logado da Intent, configura a visibilidade
     * inicial dos componentes da UI e popula os spinners de seleção de Pilar,
     * Subpilar, Ação e Atividade. Também define os listeners para as interações
     * do usuário, como seleção de itens nos spinners, cliques nos percentuais
     * e botões de ação.
     *
     * @param savedInstanceState Se não for nulo, esta Activity está sendo recriada
     * a partir de um estado salvo anteriormente.
     */
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
        val nomeUsuario = intentExtra.getStringExtra("nomeUsuario") ?: "Nome de usuário desconhecido"
        val tipoUsuario = intentExtra.getStringExtra("tipoUsuario") ?: "Tipo de usuário desconhecido"

        binding.textviewPilar.visibility = View.INVISIBLE
        binding.spinnerPilar.visibility = View.INVISIBLE

        binding.textviewSubpilar.visibility = View.INVISIBLE
        binding.spinnerSubpilar.visibility = View.INVISIBLE

        binding.textviewAcao.visibility = View.INVISIBLE
        binding.spinnerAcao.visibility = View.INVISIBLE

        binding.textviewAtividade.visibility = View.INVISIBLE
        binding.spinnerAtividade.visibility = View.INVISIBLE

        binding.buttonPesquisar.visibility = View.INVISIBLE
        binding.buttonNovaPesquisa.visibility = View.GONE

        binding.layoutPesquisa.visibility = View.INVISIBLE

        val adapterVazio = ArrayAdapter<String>(this@PercentualActivity, android.R.layout.simple_spinner_dropdown_item, emptyList())

        var atividadeSelecionada: Atividade? = null

        val idCal = calendarioRepository.obterIdCalendarioPorAno(2025)
        val listaPilar = pilarRepository.obterTodosPilares(Calendario(idCal, 2025))

        if (listaPilar.isEmpty()) {
            Toast.makeText(this, "Não existem atividades cadastradas no sistema", Toast.LENGTH_LONG).show()
        }

        if (listaPilar.isNotEmpty()) {
            binding.textviewPilar.visibility = View.VISIBLE
            binding.spinnerPilar.visibility = View.VISIBLE

            val adapterPilar = ArrayAdapter(this@PercentualActivity, android.R.layout.simple_spinner_dropdown_item, listaPilar)
            binding.spinnerPilar.adapter = adapterPilar
        }

        /**
         * Listener para a seleção de itens no spinner de Pilar.
         *
         * Ao selecionar um pilar, limpa os spinners subsequentes e tenta
         * preencher o spinner de Subpilar. Se não houver subpilares,
         * preenche diretamente o spinner de Ação associado ao pilar selecionado.
         */
        binding.spinnerPilar.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>, view: View, position: Int, id: Long
            ) {
                val pilarSelecionado = parent.getItemAtPosition(position) as Pilar

                atividadeSelecionada = null

                binding.spinnerSubpilar.adapter = adapterVazio
                binding.spinnerAcao.adapter = adapterVazio
                binding.spinnerAtividade.adapter = adapterVazio

                // Verificar se existe subpilar
                val listaSubpilar = subpilarRepository.obterTodosSubpilares(pilarSelecionado)

                // Existe subpilar
                if (listaSubpilar.isNotEmpty()) {
                    binding.textviewSubpilar.visibility = View.VISIBLE
                    binding.spinnerSubpilar.visibility = View.VISIBLE

                    // Preencher o spinner do subpilar
                    val adapterSubpilar = ArrayAdapter(this@PercentualActivity, android.R.layout.simple_spinner_dropdown_item, listaSubpilar)
                    binding.spinnerSubpilar.adapter = adapterSubpilar
                }

                // Não existe subpilar, ler ação
                if (listaSubpilar.isEmpty()) {
                    binding.textviewSubpilar.visibility = View.INVISIBLE
                    binding.spinnerSubpilar.visibility = View.INVISIBLE

                    binding.spinnerSubpilar.adapter = adapterVazio

                    var listaAcao = acaoRepository.obterTodasAcoes(pilarSelecionado)
                    val listaAcaoAux: MutableList<Acao> = arrayListOf()
                    for (item in listaAcao) {
                        if (item.aprovado) {
                            listaAcaoAux.add(item)
                        }
                    }
                    listaAcao.clear()
                    listaAcao = listaAcaoAux

                    // Existe ação
                    if (listaAcao.isNotEmpty()) {
                        binding.textviewAcao.visibility = View.VISIBLE
                        binding.spinnerAcao.visibility = View.VISIBLE

                        // Preencher o spinner da ação
                        val adapterAcao = ArrayAdapter(this@PercentualActivity, android.R.layout.simple_spinner_dropdown_item, listaAcao)
                        binding.spinnerAcao.adapter = adapterAcao
                    }

                    if (listaAcao.isEmpty()) {
                        binding.textviewAcao.visibility = View.INVISIBLE
                        binding.spinnerAcao.visibility = View.INVISIBLE

                        binding.spinnerAcao.adapter = adapterVazio

                        binding.textviewAtividade.visibility = View.INVISIBLE
                        binding.spinnerAtividade.visibility = View.INVISIBLE

                        binding.spinnerAtividade.adapter = adapterVazio
                    }
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                // Nada selecionado
            }
        }

        /**
         * Listener para a seleção de itens no spinner de Subpilar.
         *
         * Ao selecionar um subpilar, limpa os spinners subsequentes e tenta
         * preencher o spinner de Ação associado ao subpilar selecionado.
         */
        binding.spinnerSubpilar.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>, view: View, position: Int, id: Long
            ) {
                val subpilarSelecionado = parent.getItemAtPosition(position) as Subpilar

                atividadeSelecionada = null

                binding.spinnerAcao.adapter = adapterVazio
                binding.spinnerAtividade.adapter = adapterVazio

                var listaAcao = acaoRepository.obterTodasAcoes(subpilarSelecionado)
                val listaAcaoAux: MutableList<Acao> = arrayListOf()
                for (item in listaAcao) {
                    if (item.aprovado) {
                        listaAcaoAux.add(item)
                    }
                }
                listaAcao.clear()
                listaAcao = listaAcaoAux

                // Existe ação
                if (listaAcao.isNotEmpty()) {
                    binding.textviewAcao.visibility = View.VISIBLE
                    binding.spinnerAcao.visibility = View.VISIBLE

                    // Preencher o spinner da ação
                    val adapterAcao = ArrayAdapter(this@PercentualActivity, android.R.layout.simple_spinner_dropdown_item, listaAcao)
                    binding.spinnerAcao.adapter = adapterAcao
                }

                if (listaAcao.isEmpty()) {
                    binding.textviewAcao.visibility = View.INVISIBLE
                    binding.spinnerAcao.visibility = View.INVISIBLE

                    binding.spinnerAcao.adapter = adapterVazio
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                // Nada selecionado
            }
        }

        /**
         * Listener para a seleção de itens no spinner de Ação.
         *
         * Ao selecionar uma ação, limpa os spinners subsequentes e tenta
         * preencher o spinner de Atividade associado à ação selecionada.
         */
        binding.spinnerAcao.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>, view: View, position: Int, id: Long
            ) {
                val acaoSelecionada = parent.getItemAtPosition(position) as Acao

                atividadeSelecionada = null

                binding.spinnerAtividade.adapter = adapterVazio

                var listaAtividade = atividadeRepository.obterTodasAtividades(acaoSelecionada)
                val listaAtividadeAux: MutableList<Atividade> = arrayListOf()
                for (item in listaAtividade) {
                    if (item.aprovado) {
                        listaAtividadeAux.add(item)
                    }
                }
                listaAtividade.clear()
                listaAtividade = listaAtividadeAux

                // Existe atividade
                if (listaAtividade.isNotEmpty()) {
                    binding.textviewAtividade.visibility = View.VISIBLE
                    binding.spinnerAtividade.visibility = View.VISIBLE

                    // Preencher o spinner da ação
                    val adapterAtividade = ArrayAdapter(this@PercentualActivity, android.R.layout.simple_spinner_dropdown_item, listaAtividade)
                    binding.spinnerAtividade.adapter = adapterAtividade
                }

                if (listaAtividade.isEmpty()) {
                    binding.textviewAtividade.visibility = View.INVISIBLE
                    binding.spinnerAtividade.visibility = View.INVISIBLE

                    binding.spinnerAtividade.adapter = adapterVazio
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                // Nada selecionado
            }
        }

        /**
         * Listener para a seleção de itens no spinner de Atividade.
         *
         * Ao selecionar uma atividade, armazena o objeto da atividade selecionada
         * e torna o botão "Pesquisar" visível.
         */
        binding.spinnerAtividade.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>, view: View, position: Int, id: Long
            ) {
                atividadeSelecionada = null

                atividadeSelecionada = parent.getItemAtPosition(position) as Atividade

                binding.buttonPesquisar.visibility = View.VISIBLE
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                // Nada selecionado
            }
        }

        val openMenuPrincipal: ImageView = findViewById(R.id.imageview_voltarPercentualParaMenuPrincipal)
        openMenuPrincipal.setOnClickListener {
            val extra = Intent(this, MenuActivity::class.java)
            extra.putExtra("idUsuario", idUsuario)
            extra.putExtra("nomeUsuario", nomeUsuario)
            extra.putExtra("tipoUsuario", tipoUsuario)
            finish()
            startActivity(extra)
        }

        binding.buttonPesquisar.setOnClickListener {
            if (atividadeSelecionada != null) {
                binding.spinnerPilar.isEnabled = false
                binding.spinnerSubpilar.isEnabled = false
                binding.spinnerAcao.isEnabled = false
                binding.spinnerAtividade.isEnabled = false

                binding.layoutSelecao.visibility = View.GONE

                binding.buttonPesquisar.visibility = View.GONE

                binding.buttonNovaPesquisa.visibility = View.VISIBLE

                binding.textviewNomeAtividade.text = "Atividade: ${atividadeSelecionada!!.nome}"

                if (tipoUsuario.uppercase() == USUARIO_GESTOR) {
                    percentualApenasLeitura("Visualização do progresso da atividade")
                } else if (atividadeSelecionada!!.finalizado) {
                    percentualApenasLeitura("Obs: Atividade finalizada, não é possível atualizar os percentuais")
                }

                binding.layoutPesquisa.visibility = View.VISIBLE

                carregarDados(atividadeSelecionada!!)
            } else {
                Toast.makeText(this, "Não existe atividade para esta seleção!", Toast.LENGTH_LONG).show()
            }
        }

        val reloadPercentual: Button = findViewById(R.id.button_novaPesquisa)
        reloadPercentual.setOnClickListener {
            val extra = Intent(this, PercentualActivity::class.java)
            extra.putExtra("idUsuario", idUsuario)
            extra.putExtra("nomeUsuario", nomeUsuario)
            extra.putExtra("tipoUsuario", tipoUsuario)
            finish()
            startActivity(extra)
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
            if (atividadeSelecionada != null) {
                salvarAlteracoes(atividadeSelecionada!!)
            }
        }

    }

    /**
     * Define a exibição dos percentuais para o modo somente leitura.
     *
     * Desabilita a clicabilidade de todos os TextViews de percentuais mensais e
     * esconde o botão "Salvar Alterações". Também define um texto de observação
     * indicando o motivo dos campos serem somente leitura.
     *
     * @param texto A mensagem a ser exibida como observação.
     */
    private fun percentualApenasLeitura(texto: String) {
        binding.textviewPercentualAtivJan.isClickable = false
        binding.textviewPercentualAtivFev.isClickable = false
        binding.textviewPercentualAtivMar.isClickable = false
        binding.textviewPercentualAtivAbr.isClickable = false
        binding.textviewPercentualAtivMai.isClickable = false
        binding.textviewPercentualAtivJun.isClickable = false
        binding.textviewPercentualAtivJul.isClickable = false
        binding.textviewPercentualAtivAgo.isClickable = false
        binding.textviewPercentualAtivSet.isClickable = false
        binding.textviewPercentualAtivOut.isClickable = false
        binding.textviewPercentualAtivNov.isClickable = false
        binding.textviewPercentualAtivDez.isClickable = false

        binding.buttonSalvarAlteracoes.visibility = View.GONE

        binding.textviewObs.text = texto
    }

    /**
     * Carrega os dados de percentual mensal para a [Atividade] fornecida e os exibe
     * nos respectivos TextViews.
     *
     * @param atividade O objeto [Atividade] para o qual carregar os percentuais.
     */
    private fun carregarDados(atividade: Atividade) {

        val percentuais = percentualAtividadeRepository.obterTodosPercentuais(atividade)

        for (p in percentuais) {
            if (p.mes == 1) {
                binding.textviewPercentualAtivJan.text = p.percentual.toString() + "%"
            }
            if (p.mes == 2) {
                binding.textviewPercentualAtivFev.text = p.percentual.toString() + "%"
            }
            if (p.mes == 3) {
                binding.textviewPercentualAtivMar.text = p.percentual.toString() + "%"
            }
            if (p.mes == 4) {
                binding.textviewPercentualAtivAbr.text = p.percentual.toString() + "%"
            }
            if (p.mes == 5) {
                binding.textviewPercentualAtivMai.text = p.percentual.toString() + "%"
            }
            if (p.mes == 6) {
                binding.textviewPercentualAtivJun.text = p.percentual.toString() + "%"
            }
            if (p.mes == 7) {
                binding.textviewPercentualAtivJul.text = p.percentual.toString() + "%"
            }
            if (p.mes == 8) {
                binding.textviewPercentualAtivAgo.text = p.percentual.toString() + "%"
            }
            if (p.mes == 9) {
                binding.textviewPercentualAtivSet.text = p.percentual.toString() + "%"
            }
            if (p.mes == 10) {
                binding.textviewPercentualAtivOut.text = p.percentual.toString() + "%"
            }
            if (p.mes == 11) {
                binding.textviewPercentualAtivNov.text = p.percentual.toString() + "%"
            }
            if (p.mes == 12) {
                binding.textviewPercentualAtivDez.text = p.percentual.toString() + "%"
            }
        }

    }

    /**
     * Exibe um [AlertDialog] para permitir que o usuário atualize o percentual para um mês específico.
     *
     * Inclui validação de entrada para garantir que o valor inserido seja um número
     * e esteja dentro do intervalo válido de 0 a 100.
     *
     * @param inputTextView O [TextView] (representando o percentual de um mês) cujo valor será atualizado.
     */
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

    /**
     * Valida se a string [input] fornecida pode ser convertida para um [Double].
     *
     * @param input A string a ser validada.
     * @return `true` se a entrada for um número válido, `false` caso contrário.
     */
    private fun validarEditTextComoNumero(input: String): Boolean {
        if (input.toDoubleOrNull() == null) {
            return false
        } else {
            return true
        }
    }

    /**
     * Valida se a string [input] fornecida (que deve ser um número válido) está dentro
     * do intervalo de 0 a 100 (inclusive).
     *
     * @param input A string que representa um número a ser validado.
     * @return `true` se o número estiver entre 0 e 100, `false` caso contrário.
     */
    private fun validarFaixaDeValoresValidos(input: String): Boolean {
        val inputNumber = input.toDouble()
        if (inputNumber < 0 || inputNumber > 100) {
            return false
        } else {
            return true
        }
    }

    /**
     * Salva os percentuais mensais atualizados para a [Atividade] fornecida no banco de dados.
     *
     * Primeiro, calcula a soma de todos os percentuais mensais. Se a soma exceder 100%,
     * exibe uma mensagem de toast e não salva as alterações. Caso contrário, atualiza
     * o percentual de cada mês no banco de dados e exibe uma mensagem de sucesso.
     *
     * @param atividade O objeto [Atividade] para o qual salvar os percentuais atualizados.
     */
    private fun salvarAlteracoes(atividade: Atividade) {
        val percentuais = percentualAtividadeRepository.obterTodosPercentuais(atividade)

        var somaPercentuais = 0.0

        var novoPercentual: Double

        for (p in percentuais) {
            if (p.mes == 1) {
                novoPercentual = binding.textviewPercentualAtivJan.text.toString().replace("%", "").toDouble()
                somaPercentuais += novoPercentual
            }
            if (p.mes == 2) {
                novoPercentual = binding.textviewPercentualAtivFev.text.toString().replace("%", "").toDouble()
                somaPercentuais += novoPercentual
            }
            if (p.mes == 3) {
                novoPercentual = binding.textviewPercentualAtivMar.text.toString().replace("%", "").toDouble()
                somaPercentuais += novoPercentual
            }
            if (p.mes == 4) {
                novoPercentual = binding.textviewPercentualAtivAbr.text.toString().replace("%", "").toDouble()
                somaPercentuais += novoPercentual
            }
            if (p.mes == 5) {
                novoPercentual = binding.textviewPercentualAtivMai.text.toString().replace("%", "").toDouble()
                somaPercentuais += novoPercentual
            }
            if (p.mes == 6) {
                novoPercentual = binding.textviewPercentualAtivJun.text.toString().replace("%", "").toDouble()
                somaPercentuais += novoPercentual
            }
            if (p.mes == 7) {
                novoPercentual = binding.textviewPercentualAtivJul.text.toString().replace("%", "").toDouble()
                somaPercentuais += novoPercentual
            }
            if (p.mes == 8) {
                novoPercentual = binding.textviewPercentualAtivAgo.text.toString().replace("%", "").toDouble()
                somaPercentuais += novoPercentual
            }
            if (p.mes == 9) {
                novoPercentual = binding.textviewPercentualAtivSet.text.toString().replace("%", "").toDouble()
                somaPercentuais += novoPercentual
            }
            if (p.mes == 10) {
                novoPercentual = binding.textviewPercentualAtivOut.text.toString().replace("%", "").toDouble()
                somaPercentuais += novoPercentual
            }
            if (p.mes == 11) {
                novoPercentual = binding.textviewPercentualAtivNov.text.toString().replace("%", "").toDouble()
                somaPercentuais += novoPercentual
            }
            if (p.mes == 12) {
                novoPercentual = binding.textviewPercentualAtivDez.text.toString().replace("%", "").toDouble()
                somaPercentuais += novoPercentual
            }
        }

        if (somaPercentuais > 100.0) {
            Toast.makeText(this, "A soma dos percentuais não pode passar de 100%", Toast.LENGTH_LONG).show()
            return
        }

        for (p in percentuais) {
            if (p.mes == 1) {
                novoPercentual = binding.textviewPercentualAtivJan.text.toString().replace("%", "").toDouble()
                percentualAtividadeRepository.atualizarPercentualMes(p, novoPercentual)
            }
            if (p.mes == 2) {
                novoPercentual = binding.textviewPercentualAtivFev.text.toString().replace("%", "").toDouble()
                percentualAtividadeRepository.atualizarPercentualMes(p, novoPercentual)
            }
            if (p.mes == 3) {
                novoPercentual = binding.textviewPercentualAtivMar.text.toString().replace("%", "").toDouble()
                percentualAtividadeRepository.atualizarPercentualMes(p, novoPercentual)
            }
            if (p.mes == 4) {
                novoPercentual = binding.textviewPercentualAtivAbr.text.toString().replace("%", "").toDouble()
                percentualAtividadeRepository.atualizarPercentualMes(p, novoPercentual)
            }
            if (p.mes == 5) {
                novoPercentual = binding.textviewPercentualAtivMai.text.toString().replace("%", "").toDouble()
                percentualAtividadeRepository.atualizarPercentualMes(p, novoPercentual)
            }
            if (p.mes == 6) {
                novoPercentual = binding.textviewPercentualAtivJun.text.toString().replace("%", "").toDouble()
                percentualAtividadeRepository.atualizarPercentualMes(p, novoPercentual)
            }
            if (p.mes == 7) {
                novoPercentual = binding.textviewPercentualAtivJul.text.toString().replace("%", "").toDouble()
                percentualAtividadeRepository.atualizarPercentualMes(p, novoPercentual)
            }
            if (p.mes == 8) {
                novoPercentual = binding.textviewPercentualAtivAgo.text.toString().replace("%", "").toDouble()
                percentualAtividadeRepository.atualizarPercentualMes(p, novoPercentual)
            }
            if (p.mes == 9) {
                novoPercentual = binding.textviewPercentualAtivSet.text.toString().replace("%", "").toDouble()
                percentualAtividadeRepository.atualizarPercentualMes(p, novoPercentual)
            }
            if (p.mes == 10) {
                novoPercentual = binding.textviewPercentualAtivOut.text.toString().replace("%", "").toDouble()
                percentualAtividadeRepository.atualizarPercentualMes(p, novoPercentual)
            }
            if (p.mes == 11) {
                novoPercentual = binding.textviewPercentualAtivNov.text.toString().replace("%", "").toDouble()
                percentualAtividadeRepository.atualizarPercentualMes(p, novoPercentual)
            }
            if (p.mes == 12) {
                novoPercentual = binding.textviewPercentualAtivDez.text.toString().replace("%", "").toDouble()
                percentualAtividadeRepository.atualizarPercentualMes(p, novoPercentual)
            }
        }

        Toast.makeText(this, "Atualizações gravadas com sucesso", Toast.LENGTH_LONG).show()

    }

}