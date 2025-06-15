package com.example.mpi.ui.notificacao

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.mpi.R
import com.example.mpi.data.Notificacao

/**
 * Adaptador para [RecyclerView] responsável por exibir uma lista de objetos [Notificacao].
 *
 * Este adaptador vincula os dados de cada [Notificacao] aos elementos de layout definidos
 * em `fragment_notificacao_item.xml` para exibição em uma lista rolagem. Ele também gerencia
 * o listener para o botão "Marcar como Visualizada" de cada item, delegando a ação
 * para um callback definido na Activity ou Fragment que o utiliza.
 *
 * @property notificacoes A [List] de objetos [Notificacao] a serem exibidos no RecyclerView.
 * @property onMarcarComoVisualizadaClick Uma função de callback que é invocada quando o botão
 * "Marcar como Visualizada" de um item é clicado, passando o ID da [Notificacao] correspondente.
 */
class NotificacaoAdapter(
    private var notificacoes: List<Notificacao>,
    private val onMarcarComoVisualizadaClick: (Int) -> Unit
) : RecyclerView.Adapter<NotificacaoAdapter.NotificacaoViewHolder>() {

    /**
     * Chamado quando o RecyclerView precisa de um novo [NotificacaoViewHolder] para representar um item.
     *
     * Infla o layout `fragment_notificacao_item.xml` para criar a View do item e a encapsula em um ViewHolder.
     *
     * @param parent O ViewGroup ao qual a nova View será adicionada após ser vinculada a uma posição do adaptador.
     * @param viewType O tipo de View da View a ser criada.
     * @return Um novo [NotificacaoViewHolder] que contém a View para um item da lista.
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NotificacaoViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.fragment_notificacao_item, parent, false)
        return NotificacaoViewHolder(view)
    }

    /**
     * Chamado pelo RecyclerView para exibir os dados em uma posição específica.
     *
     * Este método atualiza o conteúdo do [NotificacaoViewHolder] com as informações da [Notificacao]
     * na `position` atual da `notificacoes`. Delega a vinculação de dados ao método [bind] do ViewHolder.
     *
     * @param holder O [NotificacaoViewHolder] que deve ser atualizado.
     * @param position A posição do item dentro do conjunto de dados do adaptador.
     */
    override fun onBindViewHolder(holder: NotificacaoViewHolder, position: Int) {
        val notificacao = notificacoes[position]
        holder.bind(notificacao)
    }

    /**
     * Retorna o número total de itens no conjunto de dados que o adaptador mantém.
     *
     * @return O número total de [Notificacao]s na `notificacoes`.
     */
    override fun getItemCount(): Int = notificacoes.size

    /**
     * Atualiza a lista de notificações exibidas pelo adaptador e notifica o `RecyclerView`
     * sobre a mudança para que a UI seja redesenhada.
     *
     * @param novasNotificacoes A nova lista de [Notificacao]s para ser exibida.
     */
    fun atualizarNotificacoes(novasNotificacoes: List<Notificacao>) {
        this.notificacoes = novasNotificacoes
        notifyDataSetChanged()
    }

    /**
     * ViewHolder interno que representa a interface de usuário de um único item [Notificacao] na lista.
     *
     * Contém as referências para os elementos de View do layout `fragment_notificacao_item.xml`
     * e um método para vincular os dados de uma [Notificacao] à UI.
     *
     * @param itemView A View raiz do item da lista.
     */
    inner class NotificacaoViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvPrazoTerminoDias: TextView = itemView.findViewById(R.id.tvPrazoTerminoDias)
        private val tvNomeItem: TextView = itemView.findViewById(R.id.tvNomeItem)
        private val tvDescricaoItem: TextView = itemView.findViewById(R.id.tvDescricaoItem)
        private val btnMarcarComoVisualizada: Button = itemView.findViewById(R.id.btnMarcarComoVisualizada)

        /**
         * Vincula os dados de um objeto [Notificacao] aos elementos da interface do usuário.
         *
         * Popula os TextViews com o título, nome do item (extraído da mensagem, se possível)
         * e descrição da notificação. Configura também o listener de clique para o botão
         * "Marcar como Visualizada".
         *
         * @param notificacao O objeto [Notificacao] cujos dados serão exibidos.
         */
        fun bind(notificacao: Notificacao) {
            tvPrazoTerminoDias.text = notificacao.titulo

            val tipoItemDisplay = notificacao.tipoItem?.replaceFirstChar(Char::uppercaseChar) ?: "Item"
            val nomeDoItemNaMensagem = try {
                notificacao.mensagem.substringAfter("\"").substringBefore("\"")
            } catch (e: StringIndexOutOfBoundsException) {
                ""
            }

            if (nomeDoItemNaMensagem.isNotEmpty()) {
                tvNomeItem.text = "$tipoItemDisplay: $nomeDoItemNaMensagem"
            } else {
                tvNomeItem.text = notificacao.titulo
            }
            tvDescricaoItem.text = notificacao.mensagem

            btnMarcarComoVisualizada.setOnClickListener {
                onMarcarComoVisualizadaClick(notificacao.id)
            }
        }
    }
}