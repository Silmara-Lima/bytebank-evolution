# 🏦 Sistema Bancário ByteBank Evolution

Projeto acadêmico em **Kotlin puro**, desenvolvido para o exercício
**"Sistema Bancário ByteBank Evolution"**, aplicando os quatro pilares da
Programação Orientada a Objetos — **Abstração, Encapsulamento, Herança e
Polimorfismo** — para modelar um sistema bancário simples, extensível e
livre de código duplicado.

> 📌 O programa roda inteiramente no console, com todas as contas e
> operações criadas diretamente no código (sem `readln()` / entrada pelo
> teclado), conforme exigido pelo enunciado.

---

## 📋 Sumário

- [Sobre o projeto](#-sobre-o-projeto)
- [Funcionalidades](#-funcionalidades)
- [Arquitetura](#-arquitetura)
- [Como executar](#-como-executar)
- [Exemplo de saída](#-exemplo-de-saída)
- [Visualizando no Android Studio](#-visualizando-no-android-studio)
- [Estrutura do repositório](#-estrutura-do-repositório)
- [Conceitos de POO aplicados](#-conceitos-de-poo-aplicados)
- [Documentação de estudo](#-documentação-de-estudo)

---

## 💡 Sobre o projeto

A empresa fictícia **ByteBank Evolution** sofria com um sistema bancário
mal organizado: código repetido, regras espalhadas pelo projeto e
dificuldade para criar novos tipos de conta. Este projeto resolve esse
problema modelando o domínio bancário com uma hierarquia de classes clara,
onde:

- o que é **comum a todas as contas** vive em uma única classe-base;
- o que é **específico de cada tipo de conta** fica isolado na própria
  subclasse;
- adicionar um novo tipo de conta no futuro exige **criar uma classe**,
  não reescrever o sistema.

## ✅ Funcionalidades

| Requisito do exercício                        | Onde está implementado                          |
|------------------------------------------------|--------------------------------------------------|
| Criação de contas bancárias                     | `ContaCorrente`, `ContaPoupanca`                 |
| Depósitos (com validação)                       | `ContaBancaria.depositar()`                      |
| Saques (com validação de saldo)                 | `ContaBancaria.sacar()` / sobrescrito em `ContaCorrente` |
| Consulta de saldo                               | `ContaBancaria.consultarSaldo()`                 |
| Exibição dos dados da conta                     | `ContaBancaria.exibirDados()`                    |
| Aplicação de taxas bancárias                    | `ContaCorrente.aplicarTaxaMensal()`              |
| Rendimento bancário                             | `ContaPoupanca.aplicarTaxaMensal()`              |
| Transferência entre contas                      | `ContaBancaria.transferir()`                     |
| Histórico simples de operações                  | Classe `Historico`                               |
| Proteção contra saldo negativo                  | Saldo `private` + validações antes de debitar    |
| Tratamento de operações inválidas               | `SaldoInsuficienteException`, `ValorInvalidoException` |

## 🏗️ Arquitetura

```
                    ┌───────────────────────┐
                    │   ContaBancaria       │  (classe abstrata)
                    │───────────────────────│
                    │ - saldo: Double       │  private
                    │ - historico           │  protected
                    │───────────────────────│
                    │ + depositar()         │  comum a todas
                    │ + sacar()             │  pode ser sobrescrito
                    │ + transferir()        │  comum a todas
                    │ + exibirDados()       │  polimórfico
                    │ # aplicarTaxaMensal() │  abstrato
                    └───────────┬───────────┘
                                │ herança
              ┌─────────────────┴─────────────────┐
   ┌──────────────────────┐          ┌──────────────────────┐
   │   ContaCorrente       │          │   ContaPoupanca       │
   │  taxa mensal fixa     │          │  sem taxa mensal      │
   │  + cheque especial    │          │  + rendimento mensal  │
   └──────────────────────┘          └──────────────────────┘
```

O diagrama completo, com a explicação de cada relação de herança e
polimorfismo, está no [`PASSO_A_PASSO.md`](./PASSO_A_PASSO.md).

## 🚀 Como executar

Escolha a opção mais conveniente — todas produzem exatamente a mesma
saída, pois usam o mesmo `main.kt`.

### Opção 1 - Kotlin Playground (zero instalação)

1. Acesse [play.kotlinlang.org](https://play.kotlinlang.org/).
2. Cole o conteúdo de [`main.kt`](./main.kt).
3. Clique em **Run**.

### Opção 2 - Terminal (kotlinc)

```bash
# Compilar
kotlinc main.kt -include-runtime -d bytebank.jar

# Executar
java -jar bytebank.jar
```

### Opção 3 - IntelliJ IDEA / Android Studio

Abra `main.kt` no editor e clique no ícone de "play" ▶️ ao lado de
`fun main()`, ou use `Run 'MainKt'`.

> Em projetos **Android**, ver a seção [Visualizando no Android
> Studio](#-visualizando-no-android-studio) — rodar um `main()` de console
> direto num módulo Android tem uma particularidade que vale a pena
> conhecer.

## 🖥️ Exemplo de saída

Esta é a saída real do programa, gerada a partir do `main.kt` deste
repositório:

```
====================================
BANCO BYTEBANK EVOLUTION
====================================

Cliente: Maria Silva
Conta: 1001
Saldo Atual: R$2500
Tipo: Conta Corrente
Limite de cheque especial: R$500

Depósito realizado com sucesso!

Saque realizado com sucesso!

Saldo insuficiente para realizar esta operação!

Depósito inválido! O valor deve ser maior que zero.

Aplicando taxa mensal...
Novo saldo: R$2770

Saque realizado com sucesso!
Transferência realizada com sucesso!

====================================
HISTÓRICO DE OPERAÇÕES
====================================
Depósito de R$500 realizado.
Saque de R$200 realizado.
Taxa mensal de R$30 aplicada.
Saque de R$100 realizado.
Transferência de R$100 enviada.

------------------------------------
Cliente: João Souza
Conta: 2002
Saldo Atual: R$1100
Tipo: Conta Poupança
Taxa de rendimento mensal: 0.5%

Aplicando rendimento mensal...
Novo saldo: R$1105.5

====================================
HISTÓRICO DE OPERAÇÕES
====================================
Transferência de R$100 recebida da conta 1001.
Rendimento de R$5.5 aplicado.
```

Repare como o **mesmo método** `aplicarTaxaMensal()` produz efeitos
opostos de cobrança na conta corrente e rendimento na poupança, sem
nenhum `if` verificando o tipo da conta. Isso é polimorfismo na prática.

## 📱 Visualizando no Android Studio

Este é um projeto de **console**, sem interface gráfica, conforme solicitação a ser entregue e avaliado (`main.kt`). Ainda assim, o repositório
inclui uma tela Android opcional (`MainActivity.kt`) só para facilitar a
visualização de quem está desenvolvendo dentro do Android Studio, exibindo
a mesma saída do console dentro do app:

<p align="center">
  <img src="./visualizacao-android.svg" alt="Tela do app Android mostrando a saída do sistema bancário" width="280"/>
</p>

<p align="center"><sub>MainActivity.kt chamando a própria <code>main()</code> de <code>main.kt</code> e exibindo a saída capturada dentro de uma tela com rolagem.</sub></p>

**Como usar:**

1. Coloque `main.kt` e `MainActivity.kt` em
   `app/src/main/java/<seu-pacote>/`.
2. Rode o projeto pelo botão verde **"Run 'app'"**, com um emulador ou
   celular selecionado.
3. A tela mostra o texto que apareceria no console.

> ⚠️ Um módulo Android **não** tem a mesma estrutura de um projeto
> Kotlin/JVM puro, então tentar rodar `main()` direto pela seta verde da
> margem do editor dentro de um módulo Android pode falhar com um erro de
> Gradle (`SourceSet 'main' not found`). Usar `Run 'app'` evita esse
> problema por completo, pois builda o projeto do jeito que o Android
> Studio espera.

## 📁 Estrutura do repositório

```
bytebank_evolution/
├── main.kt                    # Código-fonte principal (entregável do exercício)
├── MainActivity.kt            # Opcional — visualização dentro do Android Studio
├── visualizacao-android.svg   # Mockup usado neste README
├── README.md                  # Este arquivo
└── PASSO_A_PASSO.md           # Guia didático + roteiro de defesa/avaliação
```

## 🧠 Conceitos de POO aplicados

| Pilar             | Como aparece no projeto                                                                 |
|--------------------|------------------------------------------------------------------------------------------|
| **Abstração**      | `ContaBancaria` é `abstract` - define o que toda conta faz, sem dizer como cada uma faz |
| **Encapsulamento** | `saldo` é `private`; só é alterado através de métodos que validam as regras de negócio  |
| **Herança**        | `ContaCorrente` e `ContaPoupanca` herdam depósito, transferência, saldo e histórico     |
| **Polimorfismo**   | `aplicarTaxaMensal()` e `exibirDados()` se comportam de forma diferente por subclasse   |

<p align="center"><sub>Projeto acadêmico — Programação Orientada a Objetos em Kotlin.</sub></p>
