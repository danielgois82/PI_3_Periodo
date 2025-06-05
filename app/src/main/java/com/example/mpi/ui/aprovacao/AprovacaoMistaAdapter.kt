package com.example.mpi.ui.aprovacao

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.mpi.R
import com.example.mpi.data.AprovacaoItem
import com.example.mpi.data.AprovacaoItem.AcaoAprovacao
import com.example.mpi.data.AprovacaoItem.AtividadeAprovacao
import com.example.mpi.repository.UsuarioRepository

class AprovacaoMistaAdapter(
    private var itensAprovacao: List<AprovacaoItem>,
    private val onAprovarAcaoClick: (Int) -> Unit,
    private val onAprovarAtividadeClick: (Int) -> Unit,
    private val usuarioRepository: UsuarioRepository
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {


    private val TYPE_ACAO = 0
    private val TYPE_ATIVIDADE = 1

    inner class AcaoViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvAcaoTitulo: TextView = view.findViewById(R.id.tvAcaoTitulo)
        val tvAcaoDescricao: TextView = view.findViewById(R.id.tvAcaoDescricao)
        val tvAcaoDataInicio: TextView = view.findViewById(R.id.tvAcaoDataInicio)
        val tvAcaoDataTermino: TextView = view.findViewById(R.id.tvAcaoDataTermino)
        val tvAcaoResponsavel: TextView = view.findViewById(R.id.tvAcaoResponsavel)
        val tvAcaoCriador: TextView = view.findViewById(R.id.tvAcaoCriador)
        val btnAprovarAcao: Button = view.findViewById(R.id.btnAprovarAcao)
    }

    // ViewHolder para Atividades
    inner class AtividadeViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvAtividadeTitulo: TextView = view.findViewById(R.id.tvAtividadeTitulo)
        val tvAtividadeDescricao: TextView = view.findViewById(R.id.tvAtividadeDescricao)
        val tvAtividadeDataInicio: TextView = view.findViewById(R.id.tvAtividadeDataInicio)
        val tvAtividadeDataTermino: TextView = view.findViewById(R.id.tvAtividadeDataTermino)
        val tvAtividadeOrcamento: TextView = view.findViewById(R.id.tvAtividadeOrcamento)
        val tvAtividadeResponsavel: TextView = view.findViewById(R.id.tvAtividadeResponsavel)
        val tvAtividadeCriador: TextView = view.findViewById(R.id.tvAtividadeCriador)
        val btnAprovarAtividade: Button = view.findViewById(R.id.btnAprovarAtividade)
    }

    override fun getItemViewType(position: Int): Int {
        return when (itensAprovacao[position]) {
            is AcaoAprovacao -> TYPE_ACAO
            is AtividadeAprovacao -> TYPE_ATIVIDADE
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            TYPE_ACAO -> {
                val view = LayoutInflater.from(parent.context).inflate(R.layout.item_aprovacao_acao, parent, false)
                AcaoViewHolder(view)
            }
            TYPE_ATIVIDADE -> {
                val view = LayoutInflater.from(parent.context).inflate(R.layout.item_aprovacao_atividade, parent, false)
                AtividadeViewHolder(view)
            }
            else -> throw IllegalArgumentException("Tipo de view desconhecido: $viewType")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (val item = itensAprovacao[position]) {
            is AcaoAprovacao -> {
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

                acaoHolder.btnAprovarAcao.setOnClickListener {
                    onAprovarAcaoClick(acao.id)
                }
            }
            is AtividadeAprovacao -> {
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

                atividadeHolder.btnAprovarAtividade.setOnClickListener {
                    onAprovarAtividadeClick(atividade.id)
                }
            }
        }
    }

    override fun getItemCount(): Int = itensAprovacao.size

    fun atualizarItens(novosItens: List<AprovacaoItem>) {
        this.itensAprovacao = novosItens
        notifyDataSetChanged()
    }
}