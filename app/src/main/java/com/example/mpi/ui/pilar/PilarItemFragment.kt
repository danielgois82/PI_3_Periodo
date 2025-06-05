package com.example.mpi.ui.pilar

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.mpi.databinding.FragmentPilarItemBinding
import com.example.mpi.data.Pilar
import com.example.mpi.repository.PilarRepository
import com.example.mpi.repository.UsuarioRepository

class PilarItemFragment(
    private val pilar: Pilar,
    private val onEditarClicked: (Pilar) -> Unit,
    private val onExcluirClicked: (Pilar) -> Unit
) : Fragment() {

    private var _binding: FragmentPilarItemBinding? = null
    private val binding get() = _binding!!

    private lateinit var pilarRepository: PilarRepository

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPilarItemBinding.inflate(inflater, container, false)
        pilarRepository = PilarRepository.getInstance(requireContext())
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        var percentualPilar = 0.0
        binding.tvNomePilarItem.text = pilar.nome
        binding.tvDescricaoPilarItem.text = pilar.descricao
        binding.tvDataInicioPilarItem.text = "Início: ${pilar.dataInicio}"
        binding.tvDataTerminoPilarItem.text = "Término: ${pilar.dataTermino}"
        val percento = pilarRepository.obterProgressoPilar(pilar)
        if (percento.isNaN()){
            percentualPilar = 0.0
        }else{
            percentualPilar = percento
        }
        val orcamento = pilarRepository.obterOrcamentoTotalPilar(pilar)
        binding.tvPercentualPilarItem.text = "Percentual: ${String.format("%.2f%%", percentualPilar * 100)}"
        binding.tvIdUsuarioPilarItem.text = "ID Usuário: ${pilar.idUsuario}"
        binding.tvOrcamentoPilarItem.text = "Orçamento: R$ $orcamento"

        binding.btnEditarPilar.setOnClickListener {
            onEditarClicked(pilar)
        }

        binding.btnExcluirPilar.setOnClickListener {
            onExcluirClicked(pilar)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}