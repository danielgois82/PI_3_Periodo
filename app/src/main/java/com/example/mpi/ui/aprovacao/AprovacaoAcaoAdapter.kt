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

class AprovacaoAcaoAdapter(
    private var acoes: List<Acao>,
    private val onAprovarAcaoClick: (Acao) -> Unit,
    private val usuarioRepository: UsuarioRepository
) : RecyclerView.Adapter<AprovacaoAcaoAdapter.AcaoViewHolder>() {

    class AcaoViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvAcaoTitulo: TextView = view.findViewById(R.id.tvAcaoTitulo)
        val tvAcaoDescricao: TextView = view.findViewById(R.id.tvAcaoDescricao)
        val tvAcaoDataInicio: TextView = view.findViewById(R.id.tvAcaoDataInicio)
        val tvAcaoDataTermino: TextView = view.findViewById(R.id.tvAcaoDataTermino)
        val tvAcaoResponsavel: TextView = view.findViewById(R.id.tvAcaoResponsavel)
        val tvAcaoCriador: TextView = view.findViewById(R.id.tvAcaoCriador)
        val btnAprovarAcao: Button = view.findViewById(R.id.btnAprovarAcao)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AcaoViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_aprovacao_acao, parent, false)
        return AcaoViewHolder(view)
    }

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

    override fun getItemCount(): Int = acoes.size

    fun atualizarAcoes(novasAcoes: List<Acao>) {
        this.acoes = novasAcoes
        notifyDataSetChanged()
    }
}