package com.example.mpi.ui.acao

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.mpi.databinding.AcaoItemFragmentBinding
import com.example.mpi.data.Acao

/**
 * [AcaoItemFragment] é um [Fragment] projetado para exibir os detalhes de um único objeto [Acao].
 *
 * Este Fragment infla o layout [AcaoItemFragmentBinding] e preenche seus elementos de UI
 * com os dados de uma [Acao] específica. Ele também oferece callbacks para ações de edição e exclusão,
 * permitindo interatividade com o item exibido.
 *
 * Embora o layout [AcaoItemFragmentBinding] seja frequentemente usado dentro de um [RecyclerView.ViewHolder]
 * (como em [AcaoAdapter]), este Fragment encapsula o item de forma independente, o que pode ser útil
 * para exibir detalhes de uma única ação em uma tela separada ou em layouts mais complexos
 * que utilizam Fragmentos para cada componente da UI.
 *
 * @property acao O objeto [Acao] cujos detalhes serão exibidos neste Fragment.
 * @property onEditarClicked Uma função de callback que é invocada quando o botão "Editar" é clicado,
 * passando o objeto [Acao] associado a este Fragment.
 * @property onExcluirClicked Uma função de callback que é invocada quando o botão "Excluir" é clicado,
 * passando o objeto [Acao] associado a este Fragment.
 */
class AcaoItemFragment(
    private val acao: Acao,
    private val onEditarClicked: (Acao) -> Unit,
    private val onExcluirClicked: (Acao) -> Unit
) : Fragment() {

    private var _binding: AcaoItemFragmentBinding? = null
    private val binding get() = _binding!!

    /**
     * Chamado para que o Fragment instancie sua interface de usuário.
     *
     * Infla o layout [AcaoItemFragmentBinding] usando View Binding e retorna a View raiz.
     *
     * @param inflater O objeto [LayoutInflater] que pode ser usado para inflar qualquer View no Fragment.
     * @param container Se não nulo, este é o pai ao qual a hierarquia de View do Fragment deve ser anexada.
     * @param savedInstanceState Se não nulo, este Fragment está sendo recriado a partir de um estado salvo.
     * @return A [View] raiz do layout do Fragment.
     */
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = AcaoItemFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    /**
     * Chamado imediatamente após [onCreateView] ter retornado, mas antes que qualquer estado salvo seja restaurado.
     *
     * Preenche os elementos de UI com os dados do objeto [Acao] e configura os listeners
     * para os botões de edição e exclusão.
     *
     * @param view A [View] retornada por [onCreateView].
     * @param savedInstanceState Se não nulo, este Fragment está sendo recriado a partir de um estado salvo.
     */
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.tvNomeAcaoItem.text = acao.nome
        binding.tvDescricaoAcaoItem.text = acao.descricao
        binding.tvDataInicioAcaoItem.text = "Início: ${acao.dataInicio}"
        binding.tvDataTerminoAcaoItem.text = "Término: ${acao.dataTermino}"
        binding.tvResponsavelAcaoItem.text = "Responsável: ${acao.responsavel}"
        binding.tvAprovadoAcaoItem.text = if (acao.aprovado) "Aprovada" else "Não Aprovada"
        binding.tvFinalizadaAcaoItem.text = if (acao.finalizado) "Finalizada" else "Não Finalizada"

        binding.btnEditarAcao.setOnClickListener {
            onEditarClicked(acao)
        }

        binding.btnExcluirAcao.setOnClickListener {
            onExcluirClicked(acao)
        }
    }

    /**
     * Chamado quando a View do Fragment está sendo destruída.
     *
     * É importante anular a referência [_binding] para evitar vazamentos de memória,
     * pois a View do Fragment pode ser destruída e recriada várias vezes (e.g., em transações de Fragment).
     */
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}
