    package com.example.mpi.ui.notificacao

    import android.os.Bundle
    import androidx.fragment.app.Fragment
    import android.view.LayoutInflater
    import android.view.View
    import android.view.ViewGroup
    import com.example.mpi.R

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private const val ARG_PARAM1 = "param1"
    private const val ARG_PARAM2 = "param2"

    /**
     * Um [Fragment] simples.
     */
    class NotificacaoItemFragment : Fragment() {
        // TODO: Rename and change types of parameters
        private var param1: String? = null
        private var param2: String? = null

        /**
         * Chamado para realizar a criação inicial de um fragment.
         *
         * Recupera os argumentos passados para o fragment, se houver.
         *
         * @param savedInstanceState Se não for nulo, este fragmento está sendo recriado
         * a partir de um estado salvo.
         */
        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
            arguments?.let {
                param1 = it.getString(ARG_PARAM1)
                param2 = it.getString(ARG_PARAM2)
            }
        }

        /**
         * Chamado para que o fragmento instancie sua hierarquia de visualização.
         *
         * Infla o layout `fragment_notificacao_item.xml` para este fragmento.
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
            // Inflate the layout for this fragment
            return inflater.inflate(R.layout.fragment_notificacao_item, container, false)
        }

        /**
         * Use este método de fábrica para criar uma nova instância de
         * este fragmento usando os parâmetros fornecidos.
         *
         * @param param1 Parâmetro 1.
         * @param param2 Parâmetro 2.
         * @return Uma nova instância de fragment [NotificacaoItemFragment].
         */
        companion object {
            /**
             * @param param1 Parametero 1.
             * @param param2 Parametero 2.
             * @return Uma nova instância de NotificacaoItemFragment.
             */
            // TODO: Rename and change types and number of parameters
            @JvmStatic
            fun newInstance(param1: String, param2: String) =
                NotificacaoItemFragment().apply {
                    arguments = Bundle().apply {
                        putString(ARG_PARAM1, param1)
                        putString(ARG_PARAM2, param2)
                    }
                }
        }
    }