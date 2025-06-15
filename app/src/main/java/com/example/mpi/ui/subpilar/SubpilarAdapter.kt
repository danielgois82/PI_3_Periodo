package com.example.mpi.ui.subpilar

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.mpi.databinding.SubpilarItemFragmentBinding
import com.example.mpi.data.Subpilar

/**
 * [SubpilarAdapter] é um adaptador para `RecyclerView` responsável por exibir uma lista de objetos [Subpilar].
 *
 * Cada item da lista é representado por um [SubpilarItemFragmentBinding], que exibe detalhes
 * como nome, descrição, datas de início e término. O adaptador também fornece callbacks
 * para as ações de edição e exclusão de subpilares.
 *
 * @property listaSubpilares A [List] de objetos [Subpilar] a serem exibidos no RecyclerView.
 * @property onEditarClicked Um lambda que será invocado quando o botão de edição de um item for clicado,
 * passando o [Subpilar] correspondente.
 * @property onExcluirClicked Um lambda que será invocado quando o botão de exclusão de um item for clicado,
 * passando o [Subpilar] correspondente.
 */
class SubpilarAdapter(
    private val listaSubpilares: List<Subpilar>,
    private val onEditarClicked: (Subpilar) -> Unit,
    private val onExcluirClicked: (Subpilar) -> Unit
) : RecyclerView.Adapter<SubpilarAdapter.SubpilarViewHolder>() {

    /**
     * [SubpilarViewHolder] é uma classe interna `ViewHolder` que mantém as referências
     * dos componentes de UI de cada item da lista de subpilares.
     *
     * @param binding A instância de [SubpilarItemFragmentBinding] que representa
     * a visualização de um único item do subpilar.
     */
    inner class SubpilarViewHolder(binding: SubpilarItemFragmentBinding) : RecyclerView.ViewHolder(binding.root) {
        val tvNome = binding.tvNomeSubpilarItem
        val tvDescricao = binding.tvDescricaoSubpilarItem
        val tvDataInicio = binding.tvDataInicioSubpilarItem
        val tvDataTermino = binding.tvDataTerminoSubpilarItem
        val btnEditar = binding.btnEditarSubpilar
        val btnExcluir = binding.btnExcluirSubpilar
    }

    /**
     * Chamado quando o `RecyclerView` precisa de um novo `ViewHolder` do tipo dado
     * para representar um item.
     *
     * Infla o layout `subpilar_item_fragment.xml` usando View Binding para criar a View do item
     * e a encapsula em um [SubpilarViewHolder].
     *
     * @param parent O `ViewGroup` ao qual a nova `View` será anexada depois de ser vinculada
     * a uma posição do adaptador.
     * @param viewType O tipo de view da nova `View`.
     * @return Um novo [SubpilarViewHolder] que contém a `View` para um item da lista.
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SubpilarViewHolder {
        val binding = SubpilarItemFragmentBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return SubpilarViewHolder(binding)
    }

    /**
     * Chamado pelo `RecyclerView` para exibir os dados na posição especificada.
     *
     * Este método atualiza o conteúdo do `ViewHolder.itemView` para refletir o item
     * na posição dada, preenchendo os TextViews com os dados do [Subpilar] e configurando
     * os listeners de clique para os botões de edição e exclusão.
     *
     * @param holder O [SubpilarViewHolder] que deve ser atualizado para representar o conteúdo
     * do item na posição dada na lista de dados.
     * @param position A posição do item dentro do conjunto de dados do adaptador.
     */
    override fun onBindViewHolder(holder: SubpilarViewHolder, position: Int) {
        val subpilar = listaSubpilares[position]
        holder.tvNome.text = subpilar.nome
        holder.tvDescricao.text = subpilar.descricao
        holder.tvDataInicio.text = "Início: ${subpilar.dataInicio}"
        holder.tvDataTermino.text = "Término: ${subpilar.dataTermino}"

        holder.btnEditar.setOnClickListener {
            onEditarClicked(subpilar)
        }

        holder.btnExcluir.setOnClickListener {
            onExcluirClicked(subpilar)
        }
    }
    /**
     * Retorna o número total de itens no conjunto de dados mantido pelo adaptador.
     *
     * @return O número total de subpilares na lista.
     */
    override fun getItemCount(): Int = listaSubpilares.size
}