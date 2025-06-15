package com.example.mpi.ui.atividade

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.mpi.databinding.FragmentAtividadeItemBinding
import com.example.mpi.data.Atividade
import com.example.mpi.repository.UsuarioRepository

/**
 * [AtividadeItemFragment] é um Fragment que exibe os detalhes de uma única [Atividade].
 *
 * Este Fragment é projetado para mostrar informações como nome, descrição, datas,
 * responsável, status de aprovação/finalização e orçamento de uma atividade.
 * Ele também inclui botões para editar e excluir a atividade, que acionam callbacks.
 *
 *
 * @property atividade O objeto [Atividade] cujos detalhes serão exibidos.
 * @property onEditarClicked Um lambda que será invocado quando o botão de edição for clicado,
 * passando a [Atividade] correspondente.
 * @property onExcluirClicked Um lambda que será invocado quando o botão de exclusão for clicado,
 * passando a [Atividade] correspondente.
 */
class AtividadeItemFragment(
    private val atividade: Atividade,
    private val onEditarClicked: (Atividade) -> Unit,
    private val onExcluirClicked: (Atividade) -> Unit
) : Fragment() {

    private var _binding: FragmentAtividadeItemBinding? = null
    private val binding get() = _binding!!

    private lateinit var usuarioRepository: UsuarioRepository

    /**
     * Chamado para que o fragmento instancie sua hierarquia de visualização.
     *
     * Infla o layout `fragment_atividade_item.xml` usando View Binding e inicializa
     * o [UsuarioRepository].
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
        _binding = FragmentAtividadeItemBinding.inflate(inflater, container, false)
        usuarioRepository = UsuarioRepository.getInstance(requireContext())
        return binding.root
    }

    /**
     * Chamado imediatamente após [onCreateView] ter retornado,
     * mas antes que o estado salvo de qualquer subclasse de visualização tenha sido restaurado.
     *
     * Este método é onde os dados da [atividade] são vinculados aos elementos da UI
     * e os listeners de clique para os botões de edição e exclusão são configurados.
     *
     * @param view A View retornada por [onCreateView].
     * @param savedInstanceState Se não for nulo, este fragmento está sendo recriado a partir de um estado salvo.
     */
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
