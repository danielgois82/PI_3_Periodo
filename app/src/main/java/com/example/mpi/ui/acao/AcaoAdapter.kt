package com.example.mpi.ui.acao

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.mpi.databinding.AcaoItemFragmentBinding
import com.example.mpi.data.Acao
import com.example.mpi.repository.UsuarioRepository
import android.content.Context

/**
 * Adaptador para [RecyclerView] responsável por exibir uma lista de objetos [Acao].
 *
 * Este adaptador vincula os dados de cada [Acao] aos elementos de layout definidos
 * em [AcaoItemFragmentBinding] para exibição em uma lista rolagem.
 * Ele também gerencia os listeners para os botões de edição e exclusão de cada item,
 * delegando as ações para callbacks definidos na Activity ou Fragment que o utiliza.
 *
 * @param listaAcoes A [List] de objetos [Acao] a serem exibidos no RecyclerView.
 * @param onEditarClicked Uma função de callback que é invocada quando o botão "Editar"
 * de um item é clicado, passando o objeto [Acao] correspondente.
 * @param onExcluirClicked Uma função de callback que é invocada quando o botão "Excluir"
 * de um item é clicado, passando o objeto [Acao] correspondente.
 * @param context O [Context] da aplicação, utilizado para inicializar [UsuarioRepository].
 */
class AcaoAdapter(
    private val listaAcoes: List<Acao>,
    private val onEditarClicked: (Acao) -> Unit,
    private val onExcluirClicked: (Acao) -> Unit,
    private val context: Context
) : RecyclerView.Adapter<AcaoAdapter.AcaoViewHolder>() {

    private val usuarioRepository: UsuarioRepository = UsuarioRepository.getInstance(context)

    /**
     * ViewHolder que representa a interface de usuário de um único item [Acao] na lista.
     *
     * Contém as referências para os elementos de View do layout [AcaoItemFragmentBinding]
     * e serve como um contêiner para a View de cada item da lista.
     *
     * @param binding A instância de [AcaoItemFragmentBinding] que representa o layout do item.
     */
    inner class AcaoViewHolder(binding: AcaoItemFragmentBinding) : RecyclerView.ViewHolder(binding.root) {
        val tvNome = binding.tvNomeAcaoItem
        val tvDescricao = binding.tvDescricaoAcaoItem
        val tvDataInicio = binding.tvDataInicioAcaoItem
        val tvDataTermino = binding.tvDataTerminoAcaoItem
        val tvResponsavel = binding.tvResponsavelAcaoItem
        val tvAprovado = binding.tvAprovadoAcaoItem
        val tvFinalizada = binding.tvFinalizadaAcaoItem
        val btnEditar = binding.btnEditarAcao
        val btnExcluir = binding.btnExcluirAcao
    }

    /**
     * Chamado quando o RecyclerView precisa de um novo [AcaoViewHolder] para representar um item.
     *
     * Infla o layout [AcaoItemFragmentBinding] para criar a View do item e a encapsula em um ViewHolder.
     *
     * @param parent O ViewGroup ao qual a nova View será adicionada após ser vinculada a uma posição do adaptador.
     * @param viewType O tipo de View da View a ser criada.
     * @return Um novo [AcaoViewHolder] que contém a View para um item da lista.
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AcaoViewHolder {
        val binding = AcaoItemFragmentBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return AcaoViewHolder(binding)
    }

    /**
     * Chamado pelo RecyclerView para exibir os dados em uma posição específica.
     *
     * Este método atualiza o conteúdo do [AcaoViewHolder] com as informações da [Acao]
     * na `position` atual da `listaAcoes`. Também configura os listeners de clique
     * para os botões de edição e exclusão.
     *
     * @param holder O [AcaoViewHolder] que deve ser atualizado.
     * @param position A posição do item dentro do conjunto de dados do adaptador.
     */
    override fun onBindViewHolder(holder: AcaoViewHolder, position: Int) {
        val acao = listaAcoes[position]
        holder.tvNome.text = acao.nome
        holder.tvDescricao.text = acao.descricao
        holder.tvDataInicio.text = "Início: ${acao.dataInicio}"
        holder.tvDataTermino.text = "Término: ${acao.dataTermino}"
        val nomeResponsavel = usuarioRepository.obterNomeUsuarioPorId(acao.responsavel)
        holder.tvResponsavel.text = "Responsável: ${nomeResponsavel ?: "Desconhecido"}"
        holder.tvAprovado.text = if (acao.aprovado) "Aprovada" else "Não Aprovada"
        holder.tvFinalizada.text = if (acao.finalizado) "Finalizada" else "Não Finalizada"

        holder.btnEditar.setOnClickListener {
            onEditarClicked(acao)
        }

        holder.btnExcluir.setOnClickListener {
            onExcluirClicked(acao)
        }
    }

    /**
     * Retorna o número total de itens no conjunto de dados que o adaptador mantém.
     *
     * @return O número total de [Acao]s na `listaAcoes`.
     */
    override fun getItemCount(): Int = listaAcoes.size
}

