package com.example.mpi.ui.subpilar

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.mpi.databinding.SubpilarItemFragmentBinding
import com.example.mpi.data.Subpilar

/**
 * [SubpilarItemFragment] é um Fragment que exibe os detalhes de um único [Subpilar].
 *
 * Este Fragment é projetado para mostrar informações como nome, descrição, datas de início e término,
 * e o ID do pilar associado. Ele também inclui botões para editar e excluir o subpilar,
 * que acionam callbacks.
 *
 *
 * @property subpilar O objeto [Subpilar] cujos detalhes serão exibidos.
 * @property onEditarClicked Um lambda que será invocado quando o botão de edição for clicado,
 * passando o [Subpilar] correspondente.
 * @property onExcluirClicked Um lambda que será invocado quando o botão de exclusão for clicado,
 * passando o [Subpilar] correspondente.
 */
class SubpilarItemFragment(
    private val subpilar: Subpilar,
    private val onEditarClicked: (Subpilar) -> Unit,
    private val onExcluirClicked: (Subpilar) -> Unit
) : Fragment() {

    private var _binding: SubpilarItemFragmentBinding? = null
    private val binding get() = _binding!!

    /**
     * Chamado para que o fragmento instancie sua hierarquia de visualização.
     *
     * Infla o layout `subpilar_item_fragment.xml` usando View Binding.
     *
     * @param inflater O objeto [LayoutInflater] que pode ser usado para inflar qualquer View no fragmento.
     * @param container Se não for nulo, este é o pai ao qual a hierarquia de View do fragmento deve ser anexada.
     * @param savedInstanceState Se não for nulo, este fragmento está sendo recriado a partir de um estado salvo.
     * @return A [View] raiz do layout do fragmento.
     */
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = SubpilarItemFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    /**
     * Chamado imediatamente após [onCreateView] ter retornado,
     * mas antes que o estado salvo de qualquer subclasse de visualização tenha sido restaurado.
     *
     * Este método é onde os dados do [subpilar] são vinculados aos elementos da UI
     * e os listeners de clique para os botões de edição e exclusão são configurados.
     *
     * @param view A View retornada por [onCreateView].
     * @param savedInstanceState Se não for nulo, este fragmento está sendo recriado a partir de um estado salvo.
     */
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.tvNomeSubpilarItem.text = subpilar.nome
        binding.tvDescricaoSubpilarItem.text = subpilar.descricao
        binding.tvDataInicioSubpilarItem.text = "Início: ${subpilar.dataInicio}"
        binding.tvDataTerminoSubpilarItem.text = "Término: ${subpilar.dataTermino}"
        binding.tvIdPilarSubpilarItem.text = "ID Pilar associado: ${subpilar.idPilar}"

        binding.btnEditarSubpilar.setOnClickListener {
            onEditarClicked(subpilar)
        }

        binding.btnExcluirSubpilar.setOnClickListener {
            onExcluirClicked(subpilar)
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