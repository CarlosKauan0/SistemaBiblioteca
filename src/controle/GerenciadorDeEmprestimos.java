package controle;

import java.time.LocalDateTime;
import java.util.List;

import exception.*;
import modelo.*;

// Gerencia empréstimos: valida regras e registra o livro com a pessoa
public class GerenciadorDeEmprestimos {

    // Dependências: acervo (livros) e cadastro de pessoas
    private final Acervo acervo;
    private final CadastroDePessoas cadastroDePessoas;
    private final List<Emprestimo> emprestimos;

    public GerenciadorDeEmprestimos(Acervo acervo, CadastroDePessoas cadastroDePessoas, List<Emprestimo> emprestimos) {
        this.acervo = acervo;
        this.cadastroDePessoas = cadastroDePessoas;
        this.emprestimos = emprestimos;
    }

    // Empréstimo de livro para uma pessoa (com validações)
    public void emprestarLivro(Livro livro, Pessoa pessoa, int prazoDias)
            throws LivroIndisponivelException, LimiteEmprestimosException, LivroInexistenteException,
            PessoaNaoCadastradaException, LivroJaEmprestadoParaPessoaException {

        if (livro == null) {
            throw new LivroInexistenteException("Livro não pode ser nulo");
        }

        if (pessoa == null) {
            throw new PessoaNaoCadastradaException("Pessoa não pode ser nula");
        }

        if (prazoDias <= 0) {
            throw new LimiteEmprestimosException("Prazo inválido. Use um número de dias maior que 0.");
        }

        Livro livroDoAcervo = acervo.buscarLivroPorTituloEAutor(livro.getTitulo(), livro.getAutor());
        Pessoa pessoaCadastrada = cadastroDePessoas.buscarPessoaPorCpf(pessoa.getCpf());

        // Regra: a mesma pessoa não pode pegar mais de 1 cópia do mesmo livro ao mesmo tempo
        String cpfPessoa = pessoaCadastrada.getCpf() == null ? "" : pessoaCadastrada.getCpf().replaceAll("[^0-9]", "");
        String tituloLivro = livroDoAcervo.getTitulo() == null ? "" : livroDoAcervo.getTitulo().trim();
        String autorLivro = livroDoAcervo.getAutor() == null ? "" : livroDoAcervo.getAutor().trim();

        for (Emprestimo e : emprestimos) {
            if (e == null || e.isDevolvido()) {
                continue;
            }

            String cpfE = e.getPessoa().getCpf() == null ? "" : e.getPessoa().getCpf().replaceAll("[^0-9]", "");
            String tituloE = e.getLivro().getTitulo() == null ? "" : e.getLivro().getTitulo().trim();
            String autorE = e.getLivro().getAutor() == null ? "" : e.getLivro().getAutor().trim();

            if (cpfE.equals(cpfPessoa) && tituloE.equalsIgnoreCase(tituloLivro) && autorE.equalsIgnoreCase(autorLivro)) {
                throw new LivroJaEmprestadoParaPessoaException("Essa pessoa já possui um empréstimo ativo deste livro.");
            }
        }

        // Regra: precisa ter pelo menos 1 cópia disponível
        if (!livroDoAcervo.isDisponivel()) {
            throw new LivroIndisponivelException("Livro indisponível para empréstimo.");
        }

        // Regra: respeitar limite de empréstimos por tipo de pessoa
        if (pessoaCadastrada instanceof Aluno) {
            Aluno aluno = (Aluno) pessoaCadastrada;

            if (aluno.getLivrosPegos().size() >= Aluno.LIMITE_LIVROS_ALUNO) {
                throw new LimiteEmprestimosException("Aluno já atingiu o limite de 5 livros.");
            }

        } else if (pessoaCadastrada instanceof Professor) {
            Professor professor = (Professor) pessoaCadastrada;

            if (professor.getLivrosPegos().size() >= Professor.LIMITE_LIVROS_PROFESSOR) {
                throw new LimiteEmprestimosException("Professor já atingiu o limite de 10 livros.");
            }
        }

        // Ação: registrar o empréstimo
        livroDoAcervo.emprestarUmaCopia();
        pessoaCadastrada.getLivrosPegos().add(livroDoAcervo);

        LocalDateTime agora = LocalDateTime.now();
        LocalDateTime prazo = agora.plusDays(prazoDias);
        emprestimos.add(new Emprestimo(livroDoAcervo, pessoaCadastrada, agora, prazo));
    }
}
