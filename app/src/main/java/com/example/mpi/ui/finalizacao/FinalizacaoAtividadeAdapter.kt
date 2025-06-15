package com.example.mpi.ui.finalizacao

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.mpi.R
import com.example.mpi.data.Atividade
import com.example.mpi.repository.UsuarioRepository

/**
 * [FinalizacaoAtividadeAdapter] é um adaptador para `RecyclerView` que exibe uma lista de [Atividade]s
 * que podem ser finalizadas.
 *
 * Cada item da lista apresenta os detalhes de uma atividade, como título, descrição, datas,
 * orçamento, responsável e criador. Ele também inclui um botão para finalizar a atividade,
 * que aciona um callback definido.
 *
 * @property atividades A lista de [Atividade]s a serem exibidas.
 * @property onFinalizarAtividadeClick Um lambda que será invocado quando o botão de finalizar uma atividade for clicado,
 * passando a [Atividade] correspondente.
 * @property usuarioRepository Uma instância de [UsuarioRepository] para obter os nomes dos usuários
 * responsáveis e criadores.
 */
class FinalizacaoAtividadeAdapter(
    private var atividades: List<Atividade>,
    private val onFinalizarAtividadeClick: (Atividade) -> Unit,
    private val usuarioRepository: UsuarioRepository
) : RecyclerView.Adapter<FinalizacaoAtividadeAdapter.AtividadeViewHolder>() {

    /**
     * [AtividadeViewHolder] é uma classe interna `ViewHolder` que mantém as referências
     * dos componentes de UI de cada item da lista.
     *
     * @param view A `View` raiz do layout do item (`item_finalizacao_atividade.xml`).
     */
    class AtividadeViewHolder(view: View) : RecyclerView.ViewHolder(view) {
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
     * Chamado quando o `RecyclerView` precisa de um novo `ViewHolder` do tipo dado
     * para representar um item.
     *
     * @param parent O `ViewGroup` ao qual a nova `View` será anexada depois de ser vinculada
     * a uma posição do adaptador.
     * @param viewType O tipo de view da nova `View`.
     * @return Um novo `AtividadeViewHolder` que contém a `View` para cada item da lista.
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AtividadeViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_finalizacao_atividade, parent, false)
        return AtividadeViewHolder(view)
    }

    /**
     * Chamado pelo `RecyclerView` para exibir os dados na posição especificada.
     *
     * Este método atualiza o conteúdo do `ViewHolder.itemView` para refletir o item
     * na posição dada. Também configura o `OnClickListener` para o botão de finalização.
     *
     * @param holder O `AtividadeViewHolder` que deve ser atualizado para representar o conteúdo
     * do item na posição dada na lista de dados.
     * @param position A posição do item dentro do conjunto de dados do adaptador.
     */
    override fun onBindViewHolder(holder: AtividadeViewHolder, position: Int) {
        val atividade = atividades[position]
        holder.tvAtividadeTitulo.text = "Atividade: ${atividade.nome}"
        holder.tvAtividadeDescricao.text = "Descrição: ${atividade.descricao}"
        holder.tvAtividadeDataInicio.text = "Início: ${atividade.dataInicio}"
        holder.tvAtividadeDataTermino.text = "Término: ${atividade.dataTermino}"
        holder.tvAtividadeOrcamento.text = "Orçamento: R$ ${String.format("%.2f", atividade.orcamento)}"


        val responsavel = usuarioRepository.obterUsuarioPorId(atividade.responsavel)
        holder.tvAtividadeResponsavel.text = "Responsável: ${responsavel?.nome ?: "Não atribuído"}"

        val criador = usuarioRepository.obterUsuarioPorId(atividade.idUsuario)
        holder.tvAtividadeCriador.text = "Criador: ${criador?.nome ?: "Desconhecido"}"

        holder.btnFinalizarAtividade.setOnClickListener {
            onFinalizarAtividadeClick(atividade)
        }
    }

    /**
     * Retorna o número total de itens no conjunto de dados mantido pelo adaptador.
     *
     * @return O número total de atividades na lista.
     */
    override fun getItemCount(): Int = atividades.size

    /**
     * Atualiza a lista de atividades exibidas pelo adaptador e notifica o `RecyclerView`
     * sobre a mudança para que a UI seja redesenhada.
     *
     * @param novasAtividades A nova lista de [Atividade]s para ser exibida.
     */
    fun atualizarAtividades(novasAtividades: List<Atividade>) {
        this.atividades = novasAtividades
        notifyDataSetChanged()
    }
}