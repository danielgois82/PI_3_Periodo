package com.example.mpi.ui.atividade

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.mpi.databinding.FragmentAtividadeItemBinding
import com.example.mpi.data.Atividade
import com.example.mpi.repository.UsuarioRepository

class AtividadeItemFragment(
    private val atividade: Atividade,
    private val onEditarClicked: (Atividade) -> Unit,
    private val onExcluirClicked: (Atividade) -> Unit
) : Fragment() {

    private var _binding: FragmentAtividadeItemBinding? = null
    private val binding get() = _binding!!

    private lateinit var usuarioRepository: UsuarioRepository

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentAtividadeItemBinding.inflate(inflater, container, false)
        usuarioRepository = UsuarioRepository.getInstance(requireContext())
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.tvNomeAtividadeItem.text = atividade.nome
        binding.tvDescricaoAtividadeItem.text = atividade.descricao
        binding.tvDataInicioAtividadeItem.text = "Início: ${atividade.dataInicio}"
        binding.tvDataTerminoAtividadeItem.text = "Término: ${atividade.dataTermino}"
        val nomeResponsavel = usuarioRepository.obterNomeUsuarioPorId(atividade.responsavel)
        binding.tvResponsavelAtividadeItem.text = "Responsável: ${nomeResponsavel ?: "Desconhecido"}"
        binding.tvAprovadoAtividadeItem.text = if (atividade.aprovado) "Aprovada" else "Não Aprovada"
        binding.tvFinalizadaAtividadeItem.text = if (atividade.finalizado) "Finalizada" else "Não Finalizada"
        binding.tvOrcamentoAtividadeItem.text = "Orçamento: ${String.format("%.2f", atividade.orcamento)}"


        binding.btnEditarAtividade.setOnClickListener {
            onEditarClicked(atividade)
        }

        binding.btnExcluirAtividade.setOnClickListener {
            onExcluirClicked(atividade)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
