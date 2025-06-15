package com.example.mpi.ui.finalizacao

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.mpi.R
import com.example.mpi.data.FinalizacaoItem
import com.example.mpi.data.FinalizacaoItem.AcaoFinalizacao
import com.example.mpi.data.FinalizacaoItem.AtividadeFinalizacao
import com.example.mpi.repository.UsuarioRepository

/**
 * [FinalizacaoMistaAdapter] é um adaptador para `RecyclerView` capaz de exibir uma lista heterogênea
 * de itens, que podem ser tanto [Acao]s quanto [Atividade]s, ambas representadas pelo selado
 * `class` [FinalizacaoItem].
 *
 * Este adaptador gerencia diferentes tipos de layout de item e `ViewHolder`s para apresentar
 * os detalhes de ações e atividades que podem ser finalizadas. Ele inclui botões de finalização
 * que acionam callbacks específicos.
 *
 * @property itensFinalizacao A lista de [FinalizacaoItem]s a serem exibidos.
 * @property onFinalizarAcaoClick Um lambda que será invocado quando o botão de finalizar uma ação for clicado,
 * passando o ID da [Acao] correspondente.
 * @property onFinalizarAtividadeClick Um lambda que será invocado quando o botão de finalizar uma atividade for clicado,
 * passando o ID da [Atividade] correspondente.
 * @property usuarioRepository Uma instância de [UsuarioRepository] para obter os nomes dos usuários
 * responsáveis e criadores de ações e atividades.
 */
class FinalizacaoMistaAdapter(
    private var itensFinalizacao: List<FinalizacaoItem>,
    private val onFinalizarAcaoClick: (Int) -> Unit,
    private val onFinalizarAtividadeClick: (Int) -> Unit,
    private val usuarioRepository: UsuarioRepository
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    // Constantes para identificar os tipos de view
    private val TYPE_ACAO = 0
    private val TYPE_ATIVIDADE = 1

    /**
     * [AcaoViewHolder] é uma classe interna `ViewHolder` que mantém as referências
     * dos componentes de UI para um item de [Acao].
     *
     * @param view A `View` raiz do layout do item de ação (`item_finalizacao_acao.xml`).
     */
    inner class AcaoViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvAcaoTitulo: TextView = view.findViewById(R.id.tvAcaoTitulo)
        val tvAcaoDescricao: TextView = view.findViewById(R.id.tvAcaoDescricao)
        val tvAcaoDataInicio: TextView = view.findViewById(R.id.tvAcaoDataInicio)
        val tvAcaoDataTermino: TextView = view.findViewById(R.id.tvAcaoDataTermino)
        val tvAcaoResponsavel: TextView = view.findViewById(R.id.tvAcaoResponsavel)
        val tvAcaoCriador: TextView = view.findViewById(R.id.tvAcaoCriador)
        val btnFinalizarAcao: Button = view.findViewById(R.id.btnFinalizarAcao)
    }

    /**
     * [AtividadeViewHolder] é uma classe interna `ViewHolder` que mantém as referências
     * dos componentes de UI para um item de [Atividade].
     *
     * @param view A `View` raiz do layout do item de atividade (`item_finalizacao_atividade.xml`).
     */
    inner class AtividadeViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvAtividadeTitulo: TextView = view.findViewById(R.id.tvAtividadeTitulo)
        val tvAtividadeDescricao: TextView = view.findViewById(R.id.tvAtividadeDescricao)
        val tvAtividadeDataInicio: TextView = view.findViewById(R.id.tvAtividadeDataInicio)
        val tvAtividadeDataTermino: TextView = view.findViewById(R.id.tvAtividadeDataTermino)
        val tvAtividadeOrcamento: TextView = view.findViewById(R.id.tvAtividadeOrcamento)
        val tvAtividadeResponsavel: TextView = view.findViewById(R.id.tvAtividadeResponsavel)
        val tvAtividadeCriador: TextView = view.findViewById(R.id.tvAtividadeCriador)
        val btnFinalizarAtividade: Button = view.findViewById(R.id.btnFinalizarAtividade)
    }

    /**
     * Retorna o tipo de view do item na posição especificada, permitindo que o `RecyclerView`
     * utilize diferentes layouts para diferentes tipos de dados.
     *
     * @param position A posição do item no conjunto de dados.
     * @return Um inteiro que representa o tipo de view (0 para Ação, 1 para Atividade).
     */
    override fun getItemViewType(position: Int): Int {
        return when (itensFinalizacao[position]) {
            is AcaoFinalizacao -> TYPE_ACAO
            is AtividadeFinalizacao -> TYPE_ATIVIDADE
        }
    }

    /**
     * Chamado quando o `RecyclerView` precisa de um novo `ViewHolder` do tipo de view dado.
     *
     * Infla o layout apropriado com base no `viewType` e retorna o `ViewHolder` correspondente.
     *
     * @param parent O `ViewGroup` ao qual a nova `View` será anexada depois de ser vinculada
     * a uma posição do adaptador.
     * @param viewType O tipo de view da nova `View`, conforme retornado por [getItemViewType].
     * @return Um novo `RecyclerView.ViewHolder` que contém a `View` para o item.
     * @throws IllegalArgumentException se o `viewType` for desconhecido.
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            TYPE_ACAO -> {
                val view = LayoutInflater.from(parent.context).inflate(R.layout.item_finalizacao_acao, parent, false)
                AcaoViewHolder(view)
            }
            TYPE_ATIVIDADE -> {
                val view = LayoutInflater.from(parent.context).inflate(R.layout.item_finalizacao_atividade, parent, false)
                AtividadeViewHolder(view)
            }
            else -> throw IllegalArgumentException("Tipo de view desconhecido: $viewType")
        }
    }

    /**
     * Chamado pelo `RecyclerView` para exibir os dados na posição especificada.
     *
     * Este método atualiza o conteúdo do `ViewHolder.itemView` para refletir o item
     * na posição dada. Ele lida com a vinculação de dados para ambos os tipos de `ViewHolder`
     * (Ação e Atividade) e configura os `OnClickListener`s para os botões de finalização.
     *
     * @param holder O `RecyclerView.ViewHolder` que deve ser atualizado para representar o conteúdo
     * do item na posição dada na lista de dados.
     * @param position A posição do item dentro do conjunto de dados do adaptador.
     */
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (val item = itensFinalizacao[position]) {
            is AcaoFinalizacao -> {
                val acaoHolder = holder as AcaoViewHolder
                val acao = item.acao
                acaoHolder.tvAcaoTitulo.text = "Ação: ${acao.nome}"
                acaoHolder.tvAcaoDescricao.text = "Descrição: ${acao.descricao}"
                acaoHolder.tvAcaoDataInicio.text = "Início: ${acao.dataInicio}"
                acaoHolder.tvAcaoDataTermino.text = "Término: ${acao.dataTermino}"

                val responsavelAcao = usuarioRepository.obterUsuarioPorId(acao.responsavel)
                acaoHolder.tvAcaoResponsavel.text = "Responsável: ${responsavelAcao?.nome ?: "Não atribuído"}"

                val criadorAcao = usuarioRepository.obterUsuarioPorId(acao.idUsuario)
                acaoHolder.tvAcaoCriador.text = "Criador: ${criadorAcao?.nome ?: "Desconhecido"}"

                acaoHolder.btnFinalizarAcao.setOnClickListener {
                    onFinalizarAcaoClick(acao.id)
                }
            }
            is AtividadeFinalizacao -> {
                val atividadeHolder = holder as AtividadeViewHolder
                val atividade = item.atividade
                atividadeHolder.tvAtividadeTitulo.text = "Atividade: ${atividade.nome}"
                atividadeHolder.tvAtividadeDescricao.text = "Descrição: ${atividade.descricao}"
                atividadeHolder.tvAtividadeDataInicio.text = "Início: ${atividade.dataInicio}"
                atividadeHolder.tvAtividadeDataTermino.text = "Término: ${atividade.dataTermino}"
                atividadeHolder.tvAtividadeOrcamento.text = "Orçamento: R$ ${String.format("%.2f", atividade.orcamento)}"

                val responsavelAtividade = usuarioRepository.obterUsuarioPorId(atividade.responsavel)
                atividadeHolder.tvAtividadeResponsavel.text = "Responsável: ${responsavelAtividade?.nome ?: "Não atribuído"}"

                val criadorAtividade = usuarioRepository.obterUsuarioPorId(atividade.idUsuario)
                atividadeHolder.tvAtividadeCriador.text = "Criador: ${criadorAtividade?.nome ?: "Desconhecido"}"

                atividadeHolder.btnFinalizarAtividade.setOnClickListener {
                    onFinalizarAtividadeClick(atividade.id)
                }
            }
        }
    }

    /**
     * Retorna o número total de itens no conjunto de dados mantido pelo adaptador.
     *
     * @return O número total de itens de finalização na lista.
     */
    override fun getItemCount(): Int = itensFinalizacao.size

    /**
     * Atualiza a lista de itens de finalização exibidos pelo adaptador e notifica o `RecyclerView`
     * sobre a mudança para que a UI seja redesenhada.
     *
     * @param novosItens A nova lista de [FinalizacaoItem]s para ser exibida.
     */
    fun atualizarItens(novosItens: List<FinalizacaoItem>) {
        this.itensFinalizacao = novosItens
        notifyDataSetChanged()
    }
}