# 📚 Sistema de Gerenciamento de Biblioteca

**Programação Orientada a Objetos – Java**

## 📌 Visão Geral

Este projeto consiste no desenvolvimento de um **Sistema de Gerenciamento de Biblioteca**, implementado em **Java**, utilizando de forma consistente os conceitos de **Programação Orientada a Objetos (POO)** e **interface gráfica com Java Swing**.

O sistema foi projetado para atender bibliotecas de pequeno porte, permitindo o **cadastro e gerenciamento de livros e usuários**, bem como o **controle completo de empréstimos, devoluções, multas e persistência de dados**, tudo por meio de uma interface gráfica intuitiva.

---

## 🎓 Informações Acadêmicas

* **Universidade:** Universidade Federal do Ceará (UFC)
* **Centro:** Centro de Tecnologia
* **Curso:** Engenharia de Computação
* **Disciplina:** Programação Orientada a Objetos
* **Professor:** Fernando Antonio Mota Trinta

### 👥 Integrantes do Projeto

* Laécio Gabriel Brito dos Santos – 579665
* João Francisco do Nascimento Rocha – 582354
* João Pedro Alencar Lucas – 582416
* Luiz Eduardo Sousa – 580692
* **Carlos Kauan Cavalcante de Oliveira – 585741**

---

## 🧩 Funcionalidades do Sistema

* 📖 Cadastro de livros (título, autor, código identificador e controle de cópias)
* 👤 Cadastro de usuários (Alunos e Professores)
* 🔄 Empréstimo de livros disponíveis
* ↩️ Devolução de livros emprestados
* 📋 Listagem detalhada de livros, usuários e empréstimos
* 💾 Persistência de dados em arquivos
* ⚠️ Tratamento completo de erros e exceções
* 🖥️ Interface gráfica amigável (Java Swing)

---

## ✅ Requisitos de Programação Orientada a Objetos Atendidos

* **Encapsulamento:** atributos privados com getters e setters
* **Herança:** `Aluno` e `Professor` herdam de `Pessoa`
* **Polimorfismo:** sobrescrita de métodos e uso de referências genéricas
* **Classes Abstratas:** generalização de comportamentos comuns
* **Interfaces:** definição de contratos para persistência
* **Tratamento de Exceções:** exceções personalizadas (checked)
* **Persistência em Arquivos:** serialização de dados
* **Interface Gráfica:** Java Swing

---

## 🏗️ Arquitetura do Sistema

O projeto segue uma **arquitetura em camadas**, promovendo separação de responsabilidades, organização e facilidade de manutenção.

---

### 📦 Camada de Modelo (Domain Layer)

#### 🔹 Classe Abstrata `Pessoa`

**Atributos:**

* `nomePessoa : String`
* `cpf : String` (11 dígitos)
* `livrosPegos : List<Livro>`

**Método abstrato:**

```java
String getTipoPessoa();
```

---

#### 🔹 Classe `Aluno` (herda de `Pessoa`)

* `matricula : String`
* Limite de empréstimos: **5 livros**

---

#### 🔹 Classe `Professor` (herda de `Pessoa`)

* `matriculaFuncional : String`
* Limite de empréstimos: **10 livros**

---

#### 🔹 Classe `Livro`

**Atributos principais:**

* `titulo : String`
* `autor : String`
* `totalCopias : int`
* `copiasDisponiveis : int`

**Controle de estoque:**

* `emprestarUmaCopia()`
* `devolverUmaCopia()`
* `adicionarCopias()`
* `atualizarTotalCopias()`

---

#### 🔹 Classe `Emprestimo`

**Atributos:**

* `livro : Livro`
* `pessoa : Pessoa`
* `dataEmprestimo : LocalDateTime`
* `prazoDevolucao : LocalDateTime`
* `dataDevolucao : LocalDateTime`

**Regras:**

* Multa de **R$ 2,00 por dia de atraso**
* Cálculo automático de atraso
* Controle de status (ativo/devolvido)

---

## 🧠 Camada de Controle (Business Layer)

### 🔹 Classe `Biblioteca` (Fachada Principal)

Responsável por coordenar todas as operações do sistema.

**Principais métodos:**

* `cadastrarLivro()`
* `cadastrarPessoa()`
* `emprestarLivro()`
* `devolverLivro()`
* `listarLivrosDetalhado()`
* `listarPessoasDetalhado()`
* `listarEmprestimosDetalhado()`

---

### 🔹 Classe `Acervo`

* Gerenciamento do catálogo de livros
* Controle de duplicidade (título + autor)
* Validações de edição e remoção

---

### 🔹 Classe `CadastroDePessoas`

* Validação de CPF (11 dígitos)
* Impede CPF ou matrícula duplicados
* Não permite remoção de pessoas com empréstimos ativos

---

### 🔹 Classe `GerenciadorDeEmprestimos`

* Verifica disponibilidade de cópias
* Aplica limites por tipo de usuário
* Impede empréstimos duplicados

---

### 🔹 Classe `GerenciadorDeDevolucoes`

* Localiza empréstimos ativos
* Calcula multas automaticamente
* Atualiza estoque e histórico

---

## 💾 Camada de Persistência (Data Layer)

### 🔹 Interface `Persistencia`

```java
public interface Persistencia {
    void salvar(Biblioteca biblioteca) throws IOException;
    Biblioteca carregar() throws IOException, ClassNotFoundException;
}
```

---

### 🔹 Classe `ArquivoPersistencia`

* Persistência via serialização Java
* Arquivo: `biblioteca.dat`
* Recuperação automática em caso de arquivo inexistente

---

### 🔹 Classe `DadosBiblioteca` (DTO)

* Isola a estrutura de persistência do modelo
* Armazena listas de livros, pessoas e empréstimos

---

## ⚠️ Camada de Exceções

Exceções personalizadas (todas estendem `Exception`):

* `CadastroInvalidoException`
* `LivroInexistenteException`
* `PessoaNaoCadastradaException`
* `LivroIndisponivelException`
* `LimiteEmprestimosException`
* `LivroJaEmprestadoParaPessoaException`
* `MatriculaExistenteException`

---

## 🖥️ Camada de Apresentação (Interface Gráfica)

### 🔹 Classe `Main`

* Ponto de entrada do sistema
* Inicializa persistência, carrega dados e inicia a interface gráfica

---

### 🔹 Telas do Sistema

1. **TelaPrincipal (Menu Central)**

   * Navegação principal
   * Salvamento automático ao fechar

2. **TelaListaPessoas**

   * Pesquisa em tempo real
   * Edição e exclusão com validações

3. **TelaListaLivros**

   * Gerenciamento completo de livros e cópias

4. **TelaListaEmprestimos**

   * Histórico completo com cálculo de multas

5. **TelaEmprestimoLivro**

   * Empréstimos com seleção visual

6. **TelaDevolucaoLivro**

   * Devoluções com fluxo guiado

7. **TelaCadastroUsuario**

   * Cadastro de Alunos e Professores

---

## 🔄 Fluxo de Dados (Exemplo: Empréstimo)

```
Usuário → Interface Gráfica
→ Biblioteca.emprestarLivro()
→ GerenciadorDeEmprestimos
→ Atualização do Modelo
→ Persistência Automática
→ biblioteca.dat
```

---

## ⭐ Características de Destaque

* 📊 Controle avançado de múltiplas cópias
* 🔒 Validações completas de integridade
* 💰 Sistema automático de multas
* 🔄 Compatibilidade com versões anteriores
* 🧱 Arquitetura em camadas bem definida

---

## 🚀 Conclusão

O sistema desenvolvido atende plenamente aos requisitos propostos, aplicando de forma prática e organizada os principais conceitos de **Programação Orientada a Objetos**, além de oferecer uma **interface gráfica funcional, robusta e intuitiva**.

Este projeto demonstra domínio técnico, organização arquitetural e boas práticas de desenvolvimento em Java.
