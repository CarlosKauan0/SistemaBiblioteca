package controle;

import java.util.ArrayList;
import java.util.List;

import exception.*;
import modelo.*;

// Fachada do sistema da biblioteca: centraliza e delega para outras classes
public class Biblioteca {

    // Componentes internos (cada um com uma responsabilidade)
    private final Acervo acervo;
    private final CadastroDePessoas cadastroDePessoas;
    private final GerenciadorDeEmprestimos gerenciadorDeEmprestimos;
    private final GerenciadorDeDevolucoes gerenciadorDeDevolucoes;

    // Lista de empréstimos (ativos e devolvidos) com data/prazo
    private final List<Emprestimo> emprestimos;

    public Biblioteca() {
        this.acervo = new Acervo();
        this.cadastroDePessoas = new CadastroDePessoas();
        this.emprestimos = new ArrayList<>();
        this.gerenciadorDeEmprestimos = new GerenciadorDeEmprestimos(acervo, cadastroDePessoas, emprestimos);
        this.gerenciadorDeDevolucoes = new GerenciadorDeDevolucoes(acervo, cadastroDePessoas, emprestimos);
    }

    // Cadastro de livros
    public void cadastrarLivro(Livro livro) throws CadastroInvalidoException {
        acervo.cadastrarLivro(livro);
    }

    public void adicionarCopiasLivro(Livro livro, int quantidade) throws CadastroInvalidoException {
        acervo.adicionarCopias(livro, quantidade);
    }

    public void editarLivro(Livro livro, String novoTitulo, String novoAutor) throws CadastroInvalidoException {
        acervo.editarLivro(livro, novoTitulo, novoAutor, livro.getTotalCopias());
    }

    public void editarLivro(Livro livro, String novoTitulo, String novoAutor, int novoTotalCopias)
            throws CadastroInvalidoException {
        acervo.editarLivro(livro, novoTitulo, novoAutor, novoTotalCopias);
    }

    public void removerLivro(Livro livro) throws CadastroInvalidoException {
        acervo.removerLivro(livro);
        // remove histórico de empréstimos desse livro (para não aparecer em listagens)
        emprestimos.removeIf(e -> e != null && e.getLivro() == livro);
        // garante que não fique em listas de pessoas
        for (Pessoa p : getPessoas()) {
            p.getLivrosPegos().removeIf(l -> l == livro);
        }
    }

    // Cadastro de pessoas
    public void cadastrarPessoa(Pessoa pessoa) throws CadastroInvalidoException {
        cadastroDePessoas.cadastrarPessoa(pessoa);
    }

    public void editarPessoa(Pessoa pessoa, String novoNome, String novoCpf, String novaMatricula)
            throws CadastroInvalidoException {
        cadastroDePessoas.editarPessoa(pessoa, novoNome, novoCpf, novaMatricula);
    }

    public void removerPessoa(Pessoa pessoa) throws CadastroInvalidoException {
        cadastroDePessoas.removerPessoa(pessoa);
        emprestimos.removeIf(e -> e != null && e.getPessoa() == pessoa);
    }

    // Empréstimo de livros
    public void emprestarLivro(Livro livro, Pessoa pessoa)
            throws LivroIndisponivelException, LimiteEmprestimosException, LivroInexistenteException,
            PessoaNaoCadastradaException, LivroJaEmprestadoParaPessoaException {
        gerenciadorDeEmprestimos.emprestarLivro(livro, pessoa, 7);
    }

    // Empréstimo com prazo (em dias)
    public void emprestarLivro(Livro livro, Pessoa pessoa, int prazoDias)
            throws LivroIndisponivelException, LimiteEmprestimosException, LivroInexistenteException,
            PessoaNaoCadastradaException, LivroJaEmprestadoParaPessoaException {
        gerenciadorDeEmprestimos.emprestarLivro(livro, pessoa, prazoDias);
    }

    // Devolução de livros
    public double devolverLivro(Livro livro, Pessoa pessoa) throws LivroInexistenteException, PessoaNaoCadastradaException {
        return gerenciadorDeDevolucoes.devolverLivro(livro, pessoa);
    }

    // Listagens
    public String listarTodosOsLivros() {
        return acervo.listarTodosOsLivros();
    }

    public String listarTodasAsPessoas() {
        return cadastroDePessoas.listarTodasAsPessoas();
    }

    // Lista detalhada de livros (com autor e status)
    public String listarLivrosDetalhado() {
        if (getLivros().isEmpty()) {
            return "Nenhum livro cadastrado.";
        }

        String resultado = "Total de livros: " + getLivros().size() + "\n\n";
        int contador = 1;

        for (Livro livro : getLivros()) {
            String status = livro.isDisponivel() ? "Disponível" : "Indisponível";
            resultado += contador + " - " + livro.getTitulo()
                    + " | Autor: " + livro.getAutor()
                + " | Status: " + status
                + " | Cópias: " + livro.getCopiasDisponiveis() + "/" + livro.getTotalCopias() + "\n";
            contador++;
        }

        return resultado;
    }

    // Lista detalhada de pessoas (com CPF, tipo e quantidade de livros)
    public String listarPessoasDetalhado() {
        if (getPessoas().isEmpty()) {
            return "Nenhuma pessoa cadastrada.";
        }

        String resultado = "Total de pessoas: " + getPessoas().size() + "\n\n";
        int contador = 1;

        for (Pessoa pessoa : getPessoas()) {
            resultado += contador + " - " + pessoa.getNomePessoa()
                    + " | CPF: " + pessoa.getCpf()
                    + " | Tipo: " + pessoa.getTipoPessoa()
                    + " | Livros pegos: " + pessoa.getLivrosPegos().size() + "\n";
            contador++;
        }

        return resultado;
    }

    // Lista detalhada de empréstimos (mostra quem está com quais livros)
    public String listarEmprestimosDetalhado() {
        boolean existeEmprestimo = false;

        String resultado = "EMPRÉSTIMOS ATIVOS\n\n";

        for (Pessoa pessoa : getPessoas()) {
            if (pessoa.getLivrosPegos().isEmpty()) {
                continue;
            }

            existeEmprestimo = true;
            resultado += "Pessoa: " + pessoa.getNomePessoa()
                    + " | CPF: " + pessoa.getCpf()
                    + " | Tipo: " + pessoa.getTipoPessoa() + "\n";

            int contador = 1;
            for (Livro livro : pessoa.getLivrosPegos()) {
                resultado += "  " + contador + ") " + livro.getTitulo() + " | Autor: " + livro.getAutor() + "\n";
                contador++;
            }

            resultado += "\n";
        }

        if (!existeEmprestimo) {
            return "Nenhum empréstimo ativo.";
        }

        return resultado;
    }

    // Getters (úteis para testes)
    public List<Livro> getLivros() {
        return acervo.getLivros();
    }

    public List<Pessoa> getPessoas() {
        return cadastroDePessoas.getPessoas();
    }

    public List<Emprestimo> getEmprestimos() {
        return emprestimos;
    }

    // Métodos usados pela persistência (carregar dados)
    public void definirLivros(List<Livro> livros) {
        acervo.definirLivros(livros);
    }

    public void definirPessoas(List<Pessoa> pessoas) {
        cadastroDePessoas.definirPessoas(pessoas);
    }

    public void definirEmprestimos(List<Emprestimo> emprestimos) {
        this.emprestimos.clear();
        if (emprestimos != null) {
            this.emprestimos.addAll(emprestimos);
        }
    }
}
