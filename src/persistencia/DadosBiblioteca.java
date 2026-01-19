package persistencia;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import modelo.Emprestimo;
import modelo.Livro;
import modelo.Pessoa;

// Objeto simples para guardar os dados da biblioteca em memória (para salvar/carregar)
public class DadosBiblioteca implements Serializable {
    private static final long serialVersionUID = 1L;

    private final List<Livro> livros;
    private final List<Pessoa> pessoas;
    private final List<Emprestimo> emprestimos;

    public DadosBiblioteca(List<Livro> livros, List<Pessoa> pessoas, List<Emprestimo> emprestimos) {
        this.livros = (livros == null) ? new ArrayList<>() : new ArrayList<>(livros);
        this.pessoas = (pessoas == null) ? new ArrayList<>() : new ArrayList<>(pessoas);
        this.emprestimos = (emprestimos == null) ? new ArrayList<>() : new ArrayList<>(emprestimos);
    }

    public List<Livro> getLivros() {
        return livros;
    }

    public List<Pessoa> getPessoas() {
        return pessoas;
    }

    public List<Emprestimo> getEmprestimos() {
        return emprestimos;
    }
}
