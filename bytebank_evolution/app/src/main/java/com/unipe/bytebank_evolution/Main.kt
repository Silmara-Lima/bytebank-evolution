// Pacote alinhado com o do projeto Android (com.unipe.bytebank_evolution),
// para que MainActivity.kt (usada só para visualizar a saída no Android
// Studio) consiga enxergar executarSimulacaoBancaria() e as classes
// abaixo. Se você for rodar este arquivo isoladamente (Kotlin Playground,
// kotlinc na linha de comando, etc.), pode apagar esta linha sem problema.
package com.unipe.bytebank_evolution

/**
 * ============================================================
 * SISTEMA BANCÁRIO BYTEBANK EVOLUTION
 * ============================================================
 * Projeto acadêmico de Programação Orientada a Objetos (POO) em Kotlin puro,
 * executado 100% via console, sem interface gráfica e sem entrada de dados
 * pelo teclado (readln()). Todas as contas e operações são criadas
 * diretamente no código, dentro da função main(), conforme exigido.
 *
 * Conceitos de POO aplicados (ver PASSO_A_PASSO.md para detalhes):
 * - ABSTRAÇÃO:      ContaBancaria é uma classe abstrata que define o
 *                    "contrato" que toda conta deve cumprir, escondendo
 *                    os detalhes de implementação de cada tipo específico.
 * - ENCAPSULAMENTO: saldo, cliente e histórico são privados/protegidos e só
 *                    podem ser alterados através de métodos que aplicam as
 *                    regras de negócio (nunca diretamente de fora da classe).
 * - HERANÇA:        ContaCorrente e ContaPoupanca herdam de ContaBancaria,
 *                    reaproveitando depósito, saque padrão, transferência e
 *                    histórico, evitando código repetido.
 * - POLIMORFISMO:   aplicarTaxaMensal() e exibirDados() têm implementações
 *                    diferentes em cada subclasse, mas são chamadas de forma
 *                    genérica através de uma referência do tipo ContaBancaria.
 * ============================================================
 */

// ------------------------------------------------------------
// Exceções customizadas — tornam as regras de negócio explícitas
// e permitem tratamento de erro sem derrubar o programa.
// ------------------------------------------------------------
class SaldoInsuficienteException(mensagem: String) : Exception(mensagem)
class ValorInvalidoException(mensagem: String) : Exception(mensagem)

// ------------------------------------------------------------
// Cliente — representa o titular da conta.
// ------------------------------------------------------------
data class Cliente(
    val nome: String,
    val cpf: String
)

// ------------------------------------------------------------
// Historico — registro simples das operações realizadas em uma conta
// (requisito: "histórico simples de operações realizadas").
// ------------------------------------------------------------
class Historico {
    private val operacoes = mutableListOf<String>()

    fun registrar(descricao: String) {
        operacoes.add(descricao)
    }

    fun exibir() {
        println("====================================")
        println("HISTÓRICO DE OPERAÇÕES")
        println("====================================")
        if (operacoes.isEmpty()) {
            println("Nenhuma operação registrada.")
        } else {
            operacoes.forEach { println(it) }
        }
    }
}

// ------------------------------------------------------------
// ContaBancaria — classe abstrata que centraliza tudo que é comum
// a QUALQUER conta do banco (Abstração + Encapsulamento).
// ------------------------------------------------------------
abstract class ContaBancaria(
    val numero: Int,
    protected val cliente: Cliente,
    saldoInicial: Double
) {
    // ENCAPSULAMENTO: saldo é 'private' — nenhuma classe externa consegue
    // atribuir um valor diretamente (ex: conta.saldo = -500 não compila).
    // A única forma de alterá-lo é através dos métodos de negócio abaixo.
    private var saldo: Double = saldoInicial

    // 'protected' porque as subclasses precisam registrar operações
    // específicas (ex.: taxa mensal, rendimento), mas o mundo externo não
    // deve manipular o histórico diretamente.
    protected val historico = Historico()

    /** Consulta de saldo (requisito do PDF). Somente leitura. */
    fun consultarSaldo(): Double = saldo

    // Métodos internos de manipulação de saldo — 'protected' para que só
    // a própria classe e suas subclasses possam usá-los.
    protected fun creditar(valor: Double) {
        saldo += valor
    }

    protected fun debitar(valor: Double) {
        saldo -= valor
    }

    /**
     * Depósito — comportamento comum a todas as contas, por isso já vem
     * implementado aqui na superclasse (reaproveitamento de código).
     * Regra: "depósitos inválidos não sejam aceitos".
     */
    open fun depositar(valor: Double) {
        if (valor <= 0) {
            println("Depósito inválido! O valor deve ser maior que zero.")
            throw ValorInvalidoException("Valor de depósito inválido: R$$valor")
        }
        creditar(valor)
        historico.registrar("Depósito de R$${formatar(valor)} realizado.")
        println("Depósito realizado com sucesso!")
    }

    /**
     * Saque — regra geral: nunca permitir saldo negativo.
     * Marcado como 'open' pois contas específicas podem sobrescrever
     * este comportamento (ex.: ContaCorrente com cheque especial).
     */
    open fun sacar(valor: Double) {
        if (valor <= 0) {
            println("Valor de saque inválido!")
            throw ValorInvalidoException("Valor de saque inválido: R$$valor")
        }
        if (valor > saldo) {
            println("Saldo insuficiente para realizar esta operação!")
            throw SaldoInsuficienteException("Saldo insuficiente para saque de R$$valor")
        }
        debitar(valor)
        historico.registrar("Saque de R$${formatar(valor)} realizado.")
        println("Saque realizado com sucesso!")
    }

    /**
     * Transferência entre contas — reaproveita sacar() e um método interno
     * de crédito, evitando duplicar a validação de saldo.
     */
    fun transferir(valor: Double, destino: ContaBancaria) {
        try {
            this.sacar(valor)
            destino.creditar(valor)
            historico.registrar("Transferência de R$${formatar(valor)} enviada.")
            destino.historico.registrar(
                "Transferência de R$${formatar(valor)} recebida da conta ${this.numero}."
            )
            println("Transferência realizada com sucesso!")
        } catch (e: SaldoInsuficienteException) {
            println("Transferência não realizada: ${e.message}")
        } catch (e: ValorInvalidoException) {
            println("Transferência não realizada: ${e.message}")
        }
    }

    /**
     * Aplicação de taxas/rendimentos mensais.
     * ABSTRATO de propósito: cada tipo de conta TEM que decidir sozinho
     * o que significa "taxa mensal" para ela (polimorfismo obrigatório).
     */
    abstract fun aplicarTaxaMensal()

    /**
     * Exibição dos dados da conta. A parte comum fica aqui; cada subclasse
     * chama super.exibirDados() e complementa com suas particularidades
     * (polimorfismo por sobrescrita).
     */
    open fun exibirDados() {
        println("Cliente: ${cliente.nome}")
        println("Conta: $numero")
        println("Saldo Atual: R$${formatar(consultarSaldo())}")
    }

    fun exibirHistorico() = historico.exibir()

    /** Formata valores monetários sem casas decimais desnecessárias. */
    protected fun formatar(valor: Double): String =
        if (valor == valor.toLong().toDouble()) valor.toLong().toString() else valor.toString()
}

// ------------------------------------------------------------
// ContaCorrente — cobra taxa mensal fixa e oferece cheque especial.
// (HERANÇA de ContaBancaria + comportamento especializado)
// ------------------------------------------------------------
class ContaCorrente(
    numero: Int,
    cliente: Cliente,
    saldoInicial: Double,
    private val limiteChequeEspecial: Double = 0.0
) : ContaBancaria(numero, cliente, saldoInicial) {

    private val taxaMensal = 30.0

    // POLIMORFISMO: sobrescreve sacar() para considerar o limite de
    // cheque especial, algo que a conta poupança não possui.
    override fun sacar(valor: Double) {
        if (valor <= 0) {
            println("Valor de saque inválido!")
            throw ValorInvalidoException("Valor de saque inválido: R$$valor")
        }
        val saldoDisponivel = consultarSaldo() + limiteChequeEspecial
        if (valor > saldoDisponivel) {
            println("Saldo insuficiente para realizar esta operação!")
            throw SaldoInsuficienteException("Saldo insuficiente para saque de R$$valor")
        }
        debitar(valor)
        historico.registrar("Saque de R$${formatar(valor)} realizado.")
        println("Saque realizado com sucesso!")
    }

    // POLIMORFISMO: taxa mensal fixa cobrada da conta corrente.
    override fun aplicarTaxaMensal() {
        println("Aplicando taxa mensal...")
        debitar(taxaMensal)
        historico.registrar("Taxa mensal de R$${formatar(taxaMensal)} aplicada.")
        println("Novo saldo: R$${formatar(consultarSaldo())}")
    }

    override fun exibirDados() {
        super.exibirDados()
        println("Tipo: Conta Corrente")
        println("Limite de cheque especial: R$${formatar(limiteChequeEspecial)}")
    }
}

// ------------------------------------------------------------
// ContaPoupanca — não cobra taxa mensal; em vez disso, rende juros.
// (HERANÇA de ContaBancaria + comportamento especializado distinto)
// ------------------------------------------------------------
class ContaPoupanca(
    numero: Int,
    cliente: Cliente,
    saldoInicial: Double,
    private val taxaRendimento: Double = 0.005 // 0,5% ao mês
) : ContaBancaria(numero, cliente, saldoInicial) {

    // POLIMORFISMO: aqui "taxa mensal" significa rendimento, não cobrança.
    override fun aplicarTaxaMensal() {
        println("Aplicando rendimento mensal...")
        val rendimento = consultarSaldo() * taxaRendimento
        creditar(rendimento)
        historico.registrar("Rendimento de R$${formatar(rendimento)} aplicado.")
        println("Novo saldo: R$${formatar(consultarSaldo())}")
    }

    override fun exibirDados() {
        super.exibirDados()
        println("Tipo: Conta Poupança")
        println("Taxa de rendimento mensal: ${taxaRendimento * 100}%")
    }
}

// ------------------------------------------------------------
// FUNÇÃO main() — toda a simulação exigida pelo enunciado é criada e
// executada diretamente aqui dentro, sem nenhuma entrada de dados pelo
// teclado, conforme pedido no PDF.
//
// Observação: fun main() é uma função Kotlin comum, então nada impede que
// ela também seja chamada de outro lugar (ex.: MainActivity.kt a chama
// para exibir a mesma saída dentro de um app Android, só para fins de
// visualização no Android Studio — isso não muda em nada o fato de que
// tudo está implementado dentro da main()).
// ------------------------------------------------------------
fun main() {
    // Garante que acentos e caracteres especiais sejam exibidos corretamente
    // no console, independentemente da codificação padrão do sistema
    // operacional/terminal de quem executar o programa.
    System.setOut(java.io.PrintStream(System.out, true, "UTF-8"))

    println("====================================")
    println("BANCO BYTEBANK EVOLUTION")
    println("====================================")
    println()

    // Criação dos clientes
    val maria = Cliente("Maria Silva", "111.111.111-11")
    val joao = Cliente("João Souza", "222.222.222-22")

    // Criação de múltiplas contas de tipos diferentes
    val contaMaria = ContaCorrente(
        numero = 1001,
        cliente = maria,
        saldoInicial = 2500.0,
        limiteChequeEspecial = 500.0
    )
    val contaJoao = ContaPoupanca(
        numero = 2002,
        cliente = joao,
        saldoInicial = 1000.0
    )

    // Exibição inicial dos dados da conta (conforme exemplo do PDF)
    contaMaria.exibirDados()
    println()

    // Depósito válido
    contaMaria.depositar(500.0)
    println()

    // Saque válido
    contaMaria.sacar(200.0)
    println()

    // Saque inválido (acima do saldo + limite disponível) -> exceção tratada
    try {
        contaMaria.sacar(999_999.0)
    } catch (e: SaldoInsuficienteException) {
        // A mensagem amigável já foi exibida dentro de sacar();
        // a exceção é capturada aqui só para o programa continuar normalmente.
    }
    println()

    // Tentativa de depósito inválido (valor negativo) -> exceção tratada
    try {
        contaMaria.depositar(-50.0)
    } catch (e: ValorInvalidoException) {
        // Regra de negócio garantida: depósitos inválidos não são aceitos.
    }
    println()

    // Aplicação de taxa mensal (comportamento polimórfico da Conta Corrente)
    contaMaria.aplicarTaxaMensal()
    println()

    // Transferência entre contas de tipos diferentes
    contaMaria.transferir(100.0, contaJoao)
    println()

    // Histórico simples de operações da conta de Maria
    contaMaria.exibirHistorico()
    println()

    // Demonstrando o comportamento diferente da Conta Poupança
    println("------------------------------------")
    contaJoao.exibirDados()
    println()
    contaJoao.aplicarTaxaMensal() // aqui significa RENDIMENTO, não taxa
    println()
    contaJoao.exibirHistorico()
}