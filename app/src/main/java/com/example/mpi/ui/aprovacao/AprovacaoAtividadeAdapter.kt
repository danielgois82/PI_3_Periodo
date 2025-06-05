package com.example.mpi.ui.aprovacao

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.mpi.R
import com.example.mpi.data.Atividade
import com.example.mpi.repository.UsuarioRepository

class AprovacaoAtividadeAdapter(
    private var atividades: List<Atividade>,
    private val onAprovarAtividadeClick: (Atividade) -> Unit,
    private val usuarioRepository: UsuarioRepository
) : RecyclerView.Adapter<AprovacaoAtividadeAdapter.AtividadeViewHolder>() {

    class AtividadeViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvAtividadeTitulo: TextView = view.findViewById(R.id.tvAtividadeTitulo)
        val tvAtividadeDescricao: TextView = view.findViewById(R.id.tvAtividadeDescricao)
        val tvAtividadeDataInicio: TextView = view.findViewById(R.id.tvAtividadeDataInicio)
        val tvAtividadeDataTermino: TextView = view.findViewById(R.id.tvAtividadeDataTermino)
        val tvAtividadeOrcamento: TextView = view.findViewById(R.id.tvAtividadeOrcamento)
        val tvAtividadeResponsavel: TextView = view.findViewById(R.id.tvAtividadeResponsavel)
        val tvAtividadeCriador: TextView = view.findViewById(R.id.tvAtividadeCriador)
        val btnAprovarAtividade: Button = view.findViewById(R.id.btnAprovarAtividade)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AtividadeViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_aprovacao_atividade, parent, false)
        return AtividadeViewHolder(view)
    }

    override fun onBindViewHolder(holder: AtividadeViewHolder, position: Int) {
        val atividade = atividades[position]
        holder.tvAtividadeTitulo.text = "Atividade: ${atividade.nome}"
        holder.tvAtividadeDescricao.text = "Descrição: ${atividade.descricao}"
        holder.tvAtividadeDataInicio.text = "Início: ${atividade.dataInicio}"
        holder.tvAtividadeDataTermino.text = "Término: ${atividade.dataTermino}"
        holder.tvAtividadeOrcamento.text = "Orçamento: R$ ${String.format("%.2f", atividade.orcamento)}"

        // Obter o nome do responsável
        val responsavel = usuarioRepository.obterUsuarioPorId(atividade.responsavel)
        holder.tvAtividadeResponsavel.text = "Responsável: ${responsavel?.nome ?: "Não atribuído"}"

        // Obter o nome do criador (id_usuario)
        val criador = usuarioRepository.obterUsuarioPorId(atividade.idUsuario)
        holder.tvAtividadeCriador.text = "Criador: ${criador?.nome ?: "Desconhecido"}"

        holder.btnAprovarAtividade.setOnClickListener {
            onAprovarAtividadeClick(atividade)
        }
    }

    override fun getItemCount(): Int = atividades.size

    fun atualizarAtividades(novasAtividades: List<Atividade>) {
        this.atividades = novasAtividades
        notifyDataSetChanged()
    }
}