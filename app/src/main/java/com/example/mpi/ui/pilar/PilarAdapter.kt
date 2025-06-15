package com.example.mpi.ui.pilar

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.mpi.databinding.FragmentPilarItemBinding
import com.example.mpi.data.Pilar
import com.example.mpi.repository.PilarRepository
import android.content.Context

/**
 * [PilarAdapter] é um adaptador para `RecyclerView` responsável por exibir uma lista de objetos [Pilar].
 *
 * Cada item da lista é representado por um [FragmentPilarItemBinding], que exibe detalhes
 * como nome, descrição, datas de início e término, percentual de progresso e orçamento total.
 * O adaptador também fornece callbacks para as ações de edição e exclusão de pilares.
 *
 * @property listaPilares A [List] de objetos [Pilar] a serem exibidos no RecyclerView.
 * @property onEditarClicked Um lambda que será invocado quando o botão de edição de um item for clicado,
 * passando o [Pilar] correspondente.
 * @property onExcluirClicked Um lambda que será invocado quando o botão de exclusão de um item for clicado,
 * passando o [Pilar] correspondente.
 * @property context O contexto da aplicação, usado para inicializar o [PilarRepository].
 */
class PilarAdapter(
    private val listaPilares: List<Pilar>,
    private val onEditarClicked: (Pilar) -> Unit,
    private val onExcluirClicked: (Pilar) -> Unit,
    private val context: Context
) : RecyclerView.Adapter<PilarAdapter.PilarViewHolder>() {

    private val pilarRepository: PilarRepository = PilarRepository.getInstance(context)

    /**
     * [PilarViewHolder] é uma classe interna `ViewHolder` que mantém as referências
     * dos componentes de UI de cada item da lista de pilares.
     *
     * @param binding A instância de [FragmentPilarItemBinding] que representa
     * a visualização de um único item do pilar.
     */
    inner class PilarViewHolder(binding: FragmentPilarItemBinding) : RecyclerView.ViewHolder(binding.root) {
        val tvNome = binding.tvNomePilarItem
        val tvDescricao = binding.tvDescricaoPilarItem
        val tvDataInicio = binding.tvDataInicioPilarItem
        val tvDataTermino = binding.tvDataTerminoPilarItem
        val tvPercentual = binding.tvPercentualPilarItem
        val tvOrcamento = binding.tvOrcamentoPilarItem
        val btnEditar = binding.btnEditarPilar
        val btnExcluir = binding.btnExcluirPilar
    }

    /**
     * Chamado quando o `RecyclerView` precisa de um novo `ViewHolder` do tipo dado
     * para representar um item.
     *
     * Infla o layout `fragment_pilar_item.xml` usando View Binding para criar a View do item
     * e a encapsula em um [PilarViewHolder].
     *
     * @param parent O `ViewGroup` ao qual a nova `View` será anexada depois de ser vinculada
     * a uma posição do adaptador.
     * @param viewType O tipo de view da nova `View`.
     * @return Um novo [PilarViewHolder] que contém a `View` para um item da lista.
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PilarViewHolder {
        val binding = FragmentPilarItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return PilarViewHolder(binding)
    }

    /**
     * Chamado pelo `RecyclerView` para exibir os dados na posição especificada.
     *
     * Este método atualiza o conteúdo do `ViewHolder.itemView` para refletir o item
     * na posição dada, preenchendo os TextViews com os dados do [Pilar] e configurando
     * os listeners de clique para os botões de edição e exclusão. Calcula o percentual
     * de progresso e o orçamento total do pilar para exibição.
     *
     * @param holder O [PilarViewHolder] que deve ser atualizado para representar o conteúdo
     * do item na posição dada na lista de dados.
     * @param position A posição do item dentro do conjunto de dados do adaptador.
     */
    override fun onBindViewHolder(holder: PilarViewHolder, position: Int) {
        val pilar = listaPilares[position]
        var percentualPilar = 0.0
        holder.tvNome.text = pilar.nome
        holder.tvDescricao.text = pilar.descricao
        holder.tvDataInicio.text = "Início: ${pilar.dataInicio}"
        holder.tvDataTermino.text = "Término: ${pilar.dataTermino}"
        val percento = pilarRepository.obterProgressoPilar(pilar)
        if (percento.isNaN()){
            percentualPilar = 0.0
        }else{
            percentualPilar = percento
        }
        holder.tvPercentual.text = "Percentual: ${String.format("%.2f%%", percentualPilar * 100)}"
        val orcamento = pilarRepository.obterOrcamentoTotalPilar(pilar)
        holder.tvOrcamento.text = "Orçamento: R$ ${orcamento}"
        holder.btnEditar.setOnClickListener {
            onEditarClicked(pilar)
        }
        holder.btnExcluir.setOnClickListener {
            onExcluirClicked(pilar)
        }
    }

    /**
     * Retorna o número total de itens no conjunto de dados mantido pelo adaptador.
     *
     * @return O número total de pilares na lista.
     */
    override fun getItemCount(): Int = listaPilares.size
}