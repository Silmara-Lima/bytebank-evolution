package com.unipe.bytebank_evolution

import android.graphics.Typeface
import android.os.Bundle
import android.widget.ScrollView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import java.io.ByteArrayOutputStream
import java.io.PrintStream

/**
 * IMPORTANTE: esta Activity NÃO faz parte do exercício em si — o exercício
 * pede um programa 100% console, sem interface gráfica (ver main.kt, que é
 * o arquivo que deve ser entregue/avaliado).
 *
 * Esta tela existe só para você conseguir visualizar a saída do programa
 * rodando dentro do Android Studio (num emulador ou celular), usando o
 * botão normal "Run 'app'" — sem precisar configurar nenhuma Run
 * Configuration especial de Kotlin/Gradle para o main() do console.
 *
 * O que ela faz, passo a passo:
 * 1. Redireciona temporariamente o System.out para um "buffer" (em vez de
 *    ir para o console, os println() do sistema bancário são capturados
 *    aqui dentro).
 * 2. Chama main() — a MESMA função main.kt, com TODA a simulação criada
 *    dentro dela (nada foi extraído para fora, como pede o enunciado).
 * 3. Restaura o System.out original e pega o texto capturado.
 * 4. Mostra esse texto na tela, dentro de uma caixa com rolagem, para dar
 *    pra ler tudo mesmo sendo um texto longo.
 */
class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val saidaCapturada = capturarSaidaDaSimulacao()

        // Monta a tela por código (sem precisar de arquivo XML de layout):
        // um TextView com fonte monoespaçada dentro de um ScrollView.
        val textView = TextView(this).apply {
            text = saidaCapturada
            typeface = Typeface.MONOSPACE
            textSize = 13f
            setPadding(32, 48, 32, 48)
        }
        val scrollView = ScrollView(this).apply {
            addView(textView)
        }
        setContentView(scrollView)
    }

    /**
     * Executa o main() de main.kt capturando tudo que seria impresso no
     * console, e devolve isso como uma String.
     */
    private fun capturarSaidaDaSimulacao(): String {
        val buffer = ByteArrayOutputStream()
        val outOriginal = System.out
        System.setOut(PrintStream(buffer, true, "UTF-8"))

        try {
            main() // a mesma função main() de main.kt, com tudo dentro dela
        } finally {
            System.setOut(outOriginal) // sempre restaura o console original
        }

        return buffer.toString("UTF-8")
    }
}