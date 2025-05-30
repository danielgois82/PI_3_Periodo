package com.example.mpi.ui.notificacao

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.mpi.R
import com.example.mpi.data.Notificacao

class NotificacaoAdapter(
    private var notificacoes: List<Notificacao>,
    private val onMarcarComoVisualizadaClick: (Int) -> Unit
) : RecyclerView.Adapter<NotificacaoAdapter.NotificacaoViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NotificacaoViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.fragment_notificacao_item, parent, false)
        return NotificacaoViewHolder(view)
    }

    override fun onBindViewHolder(holder: NotificacaoViewHolder, position: Int) {
        val notificacao = notificacoes[position]
        holder.bind(notificacao)
    }

    override fun getItemCount(): Int = notificacoes.size

    fun atualizarNotificacoes(novasNotificacoes: List<Notificacao>) {
        this.notificacoes = novasNotificacoes
        notifyDataSetChanged()
    }

    inner class NotificacaoViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvPrazoTerminoDias: TextView = itemView.findViewById(R.id.tvPrazoTerminoDias)
        private val tvNomeItem: TextView = itemView.findViewById(R.id.tvNomeItem)
        private val tvDescricaoItem: TextView = itemView.findViewById(R.id.tvDescricaoItem)
        private val btnMarcarComoVisualizada: Button = itemView.findViewById(R.id.btnMarcarComoVisualizada)

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