package com.example.mpi.ui.pilar

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.mpi.databinding.FragmentPilarItemBinding
import com.example.mpi.data.Pilar
import com.example.mpi.repository.PilarRepository
import android.content.Context

class PilarAdapter(
    private val listaPilares: List<Pilar>,
    private val onEditarClicked: (Pilar) -> Unit,
    private val onExcluirClicked: (Pilar) -> Unit,
    private val context: Context
) : RecyclerView.Adapter<PilarAdapter.PilarViewHolder>() {

    private val pilarRepository: PilarRepository = PilarRepository.getInstance(context)

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

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PilarViewHolder {
        val binding = FragmentPilarItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return PilarViewHolder(binding)
    }

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

    override fun getItemCount(): Int = listaPilares.size
}