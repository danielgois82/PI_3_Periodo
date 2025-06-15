package com.example.mpi.ui.aprovacao

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.mpi.R
import com.example.mpi.data.Acao
import com.example.mpi.repository.UsuarioRepository

/**
 * [AprovacaoAcaoAdapter] é um adaptador para `RecyclerView` que exibe uma lista de [Acao]s
 * pendentes de aprovação.
 *
 * Cada item da lista apresenta os detalhes de uma ação, como título, descrição, datas,
 * responsável e criador. Ele também inclui um botão para aprovar a ação, que aciona
 * um callback definido.
 *
 * @property acoes A lista de [Acao]s a serem exibidas.
 * @property onAprovarAcaoClick Um lambda que será invocado quando o botão de aprovar uma ação for clicado,
 * passando a [Acao] correspondente.
 * @property usuarioRepository Uma instância de [UsuarioRepository] para obter os nomes dos usuários
 * responsáveis e criadores.
 */
class AprovacaoAcaoAdapter(
    private var acoes: List<Acao>,
    private val onAprovarAcaoClick: (Acao) -> Unit,
    private val usuarioRepository: UsuarioRepository
) : RecyclerView.Adapter<AprovacaoAcaoAdapter.AcaoViewHolder>() {

    /**
     * [AcaoViewHolder] é uma classe interna `ViewHolder` que mantém as referências
     * dos componentes de UI de cada item da lista.
     *
     * @param view A `View` raiz do layout do item (`item_aprovacao_acao.xml`).
     */
    class AcaoViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvAcaoTitulo: TextView = view.findViewById(R.id.tvAcaoTitulo)
        val tvAcaoDescricao: TextView = view.findViewById(R.id.tvAcaoDescricao)
        val tvAcaoDataInicio: TextView = view.findViewById(R.id.tvAcaoDataInicio)
        val tvAcaoDataTermino: TextView = view.findViewById(R.id.tvAcaoDataTermino)
        val tvAcaoResponsavel: TextView = view.findViewById(R.id.tvAcaoResponsavel)
        val tvAcaoCriador: TextView = view.findViewById(R.id.tvAcaoCriador)
        val btnAprovarAcao: Button = view.findViewById(R.id.btnAprovarAcao)
    }

    /**
     * Chamado quando o `RecyclerView` precisa de um novo `ViewHolder` do tipo dado
     * para representar um item.
     *
     * @param parent O `ViewGroup` ao qual a nova `View` será anexada depois de ser vinculada
     * a uma posição do adaptador.
     * @param viewType O tipo de view da nova `View`.
     * @return Um novo `AcaoViewHolder` que contém a `View` para cada item da lista.
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AcaoViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_aprovacao_acao, parent, false)
        return AcaoViewHolder(view)
    }

    /**
     * Chamado pelo `RecyclerView` para exibir os dados na posição especificada.
     *
     * Este método atualiza o conteúdo do `ViewHolder.itemView` para refletir o item
     * na posição dada. Também configura o `OnClickListener` para o botão de aprovação.
     *
     * @param holder O `AcaoViewHolder` que deve ser atualizado para representar o conteúdo
     * do item na posição dada na lista de dados.
     * @param position A posição do item dentro do conjunto de dados do adaptador.
     */
    override fun onBindViewHolder(holder: AcaoViewHolder, position: Int) {
        val acao = acoes[position]
        holder.tvAcaoTitulo.text = "Ação: ${acao.nome}"
        holder.tvAcaoDescricao.text = "Descrição: ${acao.descricao}"
        holder.tvAcaoDataInicio.text = "Início: ${acao.dataInicio}"
        holder.tvAcaoDataTermino.text = "Término: ${acao.dataTermino}"


        val responsavel = usuarioRepository.obterUsuarioPorId(acao.responsavel)
        holder.tvAcaoResponsavel.text = "Responsável: ${responsavel?.nome ?: "Não atribuído"}"

        val criador = usuarioRepository.obterUsuarioPorId(acao.idUsuario)
        holder.tvAcaoCriador.text = "Criador: ${criador?.nome ?: "Desconhecido"}"

        holder.btnAprovarAcao.setOnClickListener {
            onAprovarAcaoClick(acao)
        }
    }

    /**
     * Retorna o número total de itens no conjunto de dados mantido pelo adaptador.
     *
     * @return O número total de ações na lista.
     */
    override fun getItemCount(): Int = acoes.size

    /**
     * Atualiza a lista de ações exibidas pelo adaptador e notifica o `RecyclerView`
     * sobre a mudança para que a UI seja redesenhada.
     *
     * @param novasAcoes A nova lista de [Acao]s para ser exibida.
     */
    fun atualizarAcoes(novasAcoes: List<Acao>) {
        this.acoes = novasAcoes
        notifyDataSetChanged()
    }
}