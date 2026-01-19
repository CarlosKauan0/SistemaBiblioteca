package controle;

import java.time.LocalDateTime;
import java.util.List;

import exception.*;
import modelo.*;

// Gerencia devoluções: valida dados e registra a devolução do livro
public class GerenciadorDeDevolucoes {

    // Dependências: acervo (livros) e cadastro de pessoas
    private final Acervo acervo;
    private final CadastroDePessoas cadastroDePessoas;
    private final List<Emprestimo> emprestimos;

    public GerenciadorDeDevolucoes(Acervo acervo, CadastroDePessoas cadastroDePessoas, List<Emprestimo> emprestimos) {
        this.acervo = acervo;
        this.cadastroDePessoas = cadastroDePessoas;
        this.emprestimos = emprestimos;
    }

    // Devolução de livro por uma pessoa
    public double devolverLivro(Livro livro, Pessoa pessoa) throws LivroInexistenteException, PessoaNaoCadastradaException {
        if (livro == null) {
            throw new LivroInexistenteException("Livro não pode ser nulo");
        }

        if (pessoa == null) {
            throw new PessoaNaoCadastradaException("Pessoa não pode ser nula");
        }

        Livro livroDoAcervo = acervo.buscarLivroPorTituloEAutor(livro.getTitulo(), livro.getAutor());
        Pessoa pessoaCadastrada = cadastroDePessoas.buscarPessoaPorCpf(pessoa.getCpf());

        LocalDateTime agora = LocalDateTime.now();
        double multa = 0;

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
                multa = e.calcularMulta(agora);
                e.marcarDevolvido(agora);
                break;
            }
        }

        // Ação: registrar a devolução (1 cópia)
        livroDoAcervo.devolverUmaCopia();

        for (int i = 0; i < pessoaCadastrada.getLivrosPegos().size(); i++) {
            Livro l = pessoaCadastrada.getLivrosPegos().get(i);
            if (l != null && l.getTitulo() != null && l.getAutor() != null
                    && livroDoAcervo.getTitulo() != null && livroDoAcervo.getAutor() != null
                    && l.getTitulo().trim().equalsIgnoreCase(livroDoAcervo.getTitulo().trim())
                    && l.getAutor().trim().equalsIgnoreCase(livroDoAcervo.getAutor().trim())) {
                pessoaCadastrada.getLivrosPegos().remove(i);
                break;
            }
        }

        return multa;
    }
}
