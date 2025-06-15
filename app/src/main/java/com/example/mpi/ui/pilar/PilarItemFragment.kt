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

/**
 * [PilarItemFragment] é um Fragment que exibe os detalhes de um único [Pilar].
 *
 * Este Fragment é projetado para mostrar informações como nome, descrição, datas,
 * percentual de progresso, ID do usuário e orçamento de um pilar.
 * Ele também inclui botões para editar e excluir o pilar, que acionam callbacks.
 *
 *
 * @property pilar O objeto [Pilar] cujos detalhes serão exibidos.
 * @property onEditarClicked Um lambda que será invocado quando o botão de edição for clicado,
 * passando o [Pilar] correspondente.
 * @property onExcluirClicked Um lambda que será invocado quando o botão de exclusão for clicado,
 * passando o [Pilar] correspondente.
 */
class PilarItemFragment(
    private val pilar: Pilar,
    private val onEditarClicked: (Pilar) -> Unit,
    private val onExcluirClicked: (Pilar) -> Unit
) : Fragment() {

    private var _binding: FragmentPilarItemBinding? = null
    private val binding get() = _binding!!

    private lateinit var pilarRepository: PilarRepository

    /**
     * Chamado para que o fragmento instancie sua hierarquia de visualização.
     *
     * Infla o layout `fragment_pilar_item.xml` usando View Binding e inicializa
     * o [PilarRepository].
     *
     * @param inflater O objeto [LayoutInflater] que pode ser usado para inflar qualquer View no fragmento.
     * @param container Se não for nulo, este é o pai ao qual a hierarquia de View do fragmento deve ser anexada.
     * @param savedInstanceState Se não for nulo, este fragmento está sendo recriado a partir de um estado salvo.
     * @return A [View] raiz do layout do fragmento.
     */
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPilarItemBinding.inflate(inflater, container, false)
        pilarRepository = PilarRepository.getInstance(requireContext())
        return binding.root
    }

    /**
     * Chamado imediatamente após [onCreateView] ter retornado,
     * mas antes que o estado salvo de qualquer subclasse de visualização tenha sido restaurado.
     *
     * Este método é onde os dados do [pilar] são vinculados aos elementos da UI
     * e os listeners de clique para os botões de edição e exclusão são configurados.
     * Calcula o percentual de progresso e o orçamento total do pilar para exibição.
     *
     * @param view A View retornada por [onCreateView].
     * @param savedInstanceState Se não for nulo, este fragmento está sendo recriado a partir de um estado salvo.
     */
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

    /**
     * Chamado quando a View do fragmento está sendo destruída.
     *
     * Isso é usado para limpar a referência ao binding, evitando vazamentos de memória.
     */
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}