package com.example.mpi.ui.atividade

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.mpi.databinding.FragmentAtividadeItemBinding
import com.example.mpi.data.Atividade
import com.example.mpi.repository.UsuarioRepository
import android.content.Context

/**
 * [AtividadeAdapter] é um adaptador para `RecyclerView` responsável por exibir uma lista de objetos [Atividade].
 *
 * Cada item da lista é representado por um [FragmentAtividadeItemBinding], que exibe detalhes
 * como nome, descrição, datas de início e término, responsável, status de aprovação e finalização,
 * e orçamento. O adaptador também fornece callbacks para as ações de edição e exclusão de atividades.
 *
 * @property listaAtividades A [List] de objetos [Atividade] a serem exibidos no RecyclerView.
 * @property onEditarClicked Um lambda que será invocado quando o botão de edição de um item for clicado,
 * passando a [Atividade] correspondente.
 * @property onExcluirClicked Um lambda que será invocado quando o botão de exclusão de um item for clicado,
 * passando a [Atividade] correspondente.
 * @property context O contexto da aplicação, usado para inicializar o [UsuarioRepository].
 */
class AtividadeAdapter(
    private val listaAtividades: List<Atividade>,
    private val onEditarClicked: (Atividade) -> Unit,
    private val onExcluirClicked: (Atividade) -> Unit,
    private val context: Context
) : RecyclerView.Adapter<AtividadeAdapter.AtividadeViewHolder>() {

    private val usuarioRepository: UsuarioRepository = UsuarioRepository.getInstance(context)

    /**
     * [AtividadeViewHolder] é uma classe interna `ViewHolder` que mantém as referências
     * dos componentes de UI de cada item da lista.
     *
     * @param binding A instância de [FragmentAtividadeItemBinding] que representa
     * a visualização de um único item da atividade.
     */
    inner class AtividadeViewHolder(binding: FragmentAtividadeItemBinding) : RecyclerView.ViewHolder(binding.root) {
        val tvNome = binding.tvNomeAtividadeItem
        val tvDescricao = binding.tvDescricaoAtividadeItem
        val tvDataInicio = binding.tvDataInicioAtividadeItem
        val tvDataTermino = binding.tvDataTerminoAtividadeItem
        val tvResponsavel = binding.tvResponsavelAtividadeItem
        val tvAprovado = binding.tvAprovadoAtividadeItem
        val tvOrcamento = binding.tvOrcamentoAtividadeItem
        val tvFinalizada = binding.tvFinalizadaAtividadeItem
        val btnEditar = binding.btnEditarAtividade
        val btnExcluir = binding.btnExcluirAtividade
    }

    /**
     * Chamado quando o `RecyclerView` precisa de um novo `ViewHolder` do tipo dado
     * para representar um item.
     *
     * Infla o layout `fragment_atividade_item.xml` usando View Binding para criar a View do item
     * e a encapsula em um [AtividadeViewHolder].
     *
     * @param parent O `ViewGroup` ao qual a nova `View` será anexada depois de ser vinculada
     * a uma posição do adaptador.
     * @param viewType O tipo de view da nova `View`.
     * @return Um novo [AtividadeViewHolder] que contém a `View` para um item da lista.
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AtividadeViewHolder {
        val binding = FragmentAtividadeItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return AtividadeViewHolder(binding)
    }

    /**
     * Chamado pelo `RecyclerView` para exibir os dados na posição especificada.
     *
     * Este método atualiza o conteúdo do `ViewHolder.itemView` para refletir o item
     * na posição dada, preenchendo os TextViews com os dados da [Atividade] e configurando
     * os listeners de clique para os botões de edição e exclusão.
     *
     * @param holder O [AtividadeViewHolder] que deve ser atualizado para representar o conteúdo
     * do item na posição dada na lista de dados.
     * @param position A posição do item dentro do conjunto de dados do adaptador.
     */
    override fun onBindViewHolder(holder: AtividadeViewHolder, position: Int) {
        val atividade = listaAtividades[position]
        holder.tvNome.text = atividade.nome
        holder.tvDescricao.text = atividade.descricao
        holder.tvDataInicio.text = "Início: ${atividade.dataInicio}"
        holder.tvDataTermino.text = "Término: ${atividade.dataTermino}"
        val nomeResponsavel = usuarioRepository.obterNomeUsuarioPorId(atividade.responsavel)
        holder.tvResponsavel.text = "Responsável: ${nomeResponsavel ?: "Desconhecido"}"
        holder.tvAprovado.text = if (atividade.aprovado) "Aprovada" else "Não Aprovada"
        holder.tvFinalizada.text = if (atividade.finalizado) "Finalizada" else "Não Finalizada"
        holder.tvOrcamento.text = "Orçamento: ${String.format("%.2f", atividade.orcamento)}"

        holder.btnEditar.setOnClickListener {
            onEditarClicked(atividade)
        }

        holder.btnExcluir.setOnClickListener {
            onExcluirClicked(atividade)
        }
    }

    /**
     * Retorna o número total de itens no conjunto de dados mantido pelo adaptador.
     *
     * @return O número total de atividades na lista.
     */
    override fun getItemCount(): Int = listaAtividades.size
}
