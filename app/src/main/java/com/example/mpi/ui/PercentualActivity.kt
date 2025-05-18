package com.example.mpi.ui

import android.content.Intent
import android.os.Bundle
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.mpi.R
import com.example.mpi.data.Calendario
import com.example.mpi.databinding.ActivityPercentualBinding
import com.example.mpi.repository.PilarRepository
import com.example.mpi.ui.pilar.Pilar

class PercentualActivity : AppCompatActivity() {
    private lateinit var binding: ActivityPercentualBinding

    val USUARIO_ANALISTA = "ANALISTA"
    val USUARIO_COORDENADOR = "COORDENADOR"
    val USUARIO_GESTOR = "GESTOR"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityPercentualBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val intentExtra = intent
        val idUsuario = intentExtra.getIntExtra("idUsuario", 999999)
        val nomeUsuario =
            intentExtra.getStringExtra("nomeUsuario") ?: "Nome de usuário desconhecido"
        val tipoUsuario =
            intentExtra.getStringExtra("tipoUsuario") ?: "Tipo de usuário desconhecido"

        carregarDados()

        binding.textviewPercentualAtivJan.setOnClickListener {
            atualizarPercentual(binding.textviewPercentualAtivJan)
        }

        binding.textviewPercentualAtivFev.setOnClickListener {
            atualizarPercentual(binding.textviewPercentualAtivFev)
        }

        binding.textviewPercentualAtivMar.setOnClickListener {
            atualizarPercentual(binding.textviewPercentualAtivMar)
        }

        binding.textviewPercentualAtivAbr.setOnClickListener {
            atualizarPercentual(binding.textviewPercentualAtivAbr)
        }

        binding.textviewPercentualAtivMai.setOnClickListener {
            atualizarPercentual(binding.textviewPercentualAtivMai)
        }

        binding.textviewPercentualAtivJun.setOnClickListener {
            atualizarPercentual(binding.textviewPercentualAtivJun)
        }

        binding.textviewPercentualAtivJul.setOnClickListener {
            atualizarPercentual(binding.textviewPercentualAtivJul)
        }

        binding.textviewPercentualAtivAgo.setOnClickListener {
            atualizarPercentual(binding.textviewPercentualAtivAgo)
        }

        binding.textviewPercentualAtivSet.setOnClickListener {
            atualizarPercentual(binding.textviewPercentualAtivSet)
        }

        binding.textviewPercentualAtivOut.setOnClickListener {
            atualizarPercentual(binding.textviewPercentualAtivOut)
        }

        binding.textviewPercentualAtivNov.setOnClickListener {
            atualizarPercentual(binding.textviewPercentualAtivNov)
        }

        binding.textviewPercentualAtivDez.setOnClickListener {
            atualizarPercentual(binding.textviewPercentualAtivDez)
        }

        val openMenuPrincipal: ImageView = findViewById(R.id.viewVoltarMenuPrincipal)
        openMenuPrincipal.setOnClickListener {
            val extra = Intent(this, MenuActivity::class.java)
            extra.putExtra("idUsuario", idUsuario)
            extra.putExtra("nomeUsuario", nomeUsuario)
            extra.putExtra("tipoUsuario", tipoUsuario)
            startActivity(extra)
        }

        binding.buttonSalvarAlteracoes.setOnClickListener {
            salvarAlteracoes()
        }

    }

    private fun carregarDados() {

        val cal = Calendario(1, 2025)

        val pilarRepository = PilarRepository(this)
        val pilar: Pilar? = pilarRepository.obterPilar(cal, 1)

        binding.textviewNomePilar.text = pilar?.nome

        binding.textviewNomeSubpilar.text = "aaa"

        binding.textviewNomeAcao.text = "aaa"
        binding.textviewProgressoGeralAcao.text = "aaa"
        binding.textviewAcaoAtribuida.text = "Daniel Gois"

        binding.textviewNomeAtividade.text = "aaa"
        binding.textviewProgressoGeralAtividade.text = "aaa"
        binding.textviewAtividadeAtribuida.text = "Daniel Gois"
        binding.textviewOrcamentoAtividade.text = "aaa"
        binding.textviewInicioAtividade.text = "aaa"
        binding.textviewTerminoAtividade.text = "aaa"
    }

    private fun atualizarPercentual(inputTextView: TextView) {
        val editText = EditText(this)
        editText.hint = "Digite apenas o valor sem o '%'"
        AlertDialog.Builder(this)
            .setTitle("Insira o novo percentual")
            .setView(editText)
            .setPositiveButton("OK") { dialog, _ ->
                var validacaoOk = true
                val input = editText.text.toString()

                if (!validarEditTextComoNumero(input)) {
                    validacaoOk = false
                    Toast.makeText(
                        this, "Digite apenas números no percentual", Toast.LENGTH_LONG
                    )
                        .show()
                    dialog.dismiss()
                }

                if (validacaoOk) {
                    if (!validarFaixaDeValoresValidos(input)) {
                        validacaoOk = false
                        Toast.makeText(
                            this, "Valores devem ser entre 0 e 100", Toast.LENGTH_LONG
                        )
                            .show()
                        dialog.dismiss()
                    }
                }

                if (validacaoOk) {
                    val retorno = "${input.trim()}%"
                    inputTextView.text = retorno
                    dialog.dismiss()
                }
            }

            .setNegativeButton("Cancelar") { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }

    private fun validarEditTextComoNumero(input: String): Boolean {
        if (input.toDoubleOrNull() == null) {
            return false
        } else {
            return true
        }
    }

    private fun validarFaixaDeValoresValidos(input: String): Boolean {
        val inputNumber = input.toDouble()
        if (inputNumber < 0 || inputNumber > 100) {
            return false
        } else {
            return true
        }
    }

    private fun salvarAlteracoes() {
        binding.textviewNomePilar.text = "eueueueueueu"
    }

}