package controle;

import java.util.ArrayList;
import java.util.List;

import exception.*;
import modelo.*;

// Cadastro e gerenciamento do acervo de livros (adicionar, buscar e listar)
public class Acervo {

    // Lista de livros cadastrados
    private final List<Livro> livros;

    public Acervo() {
        this.livros = new ArrayList<>();
    }

    // Cadastro de livros
    public void cadastrarLivro(Livro livro) throws CadastroInvalidoException {
        if (livro == null) {
            throw new CadastroInvalidoException("Livro não pode ser nulo.");
        }

        if (livro.getTitulo() == null || livro.getTitulo().trim().isEmpty()) {
            throw new CadastroInvalidoException("O livro precisa ter um título.");
        }

        if (livro.getAutor() == null || livro.getAutor().trim().isEmpty()) {
            throw new CadastroInvalidoException("O livro precisa ter um Autor.");
        }

        String tituloNovo = livro.getTitulo().trim();
        String autorNovo = livro.getAutor().trim();

        // Se já existir (mesmo título e autor), não permite cadastrar duplicado
        for (Livro l : livros) {
            if (l.getTitulo() != null && l.getAutor() != null
                    && l.getTitulo().trim().equalsIgnoreCase(tituloNovo)
                    && l.getAutor().trim().equalsIgnoreCase(autorNovo)) {
                throw new CadastroInvalidoException("Já existe um livro cadastrado com esse título e autor.");
            }
        }

        livro.setTitulo(tituloNovo);
        livro.setAutor(autorNovo);
        livros.add(livro);
    }

    // Busca de livro pelo título
    public Livro buscarLivroPorTitulo(String titulo) throws LivroInexistenteException {
        if (titulo == null) {
            throw new LivroInexistenteException("Título do livro não pode ser nulo");
        }

        String tituloBuscado = titulo.trim();
        if (tituloBuscado.isEmpty()) {
            throw new LivroInexistenteException("Título do livro não pode ser vazio");
        }

        for (Livro livro : livros) {
            if (livro.getTitulo() != null && livro.getTitulo().trim().equalsIgnoreCase(tituloBuscado)) {
                return livro;
            }
        }

        throw new LivroInexistenteException("Não existe nenhum livro com esse titulo cadastrado na biblioteca");
    }

    // Busca de livro por título e autor (mais preciso)
    public Livro buscarLivroPorTituloEAutor(String titulo, String autor) throws LivroInexistenteException {
        if (titulo == null || autor == null) {
            throw new LivroInexistenteException("Título e autor não podem ser nulos");
        }

        String t = titulo.trim();
        String a = autor.trim();

        if (t.isEmpty() || a.isEmpty()) {
            throw new LivroInexistenteException("Título e autor não podem ser vazios");
        }

        for (Livro livro : livros) {
            if (livro.getTitulo() != null && livro.getAutor() != null
                    && livro.getTitulo().trim().equalsIgnoreCase(t)
                    && livro.getAutor().trim().equalsIgnoreCase(a)) {
                return livro;
            }
        }

        throw new LivroInexistenteException("Não existe nenhum livro com esse título e autor cadastrado na biblioteca");
    }

    public void adicionarCopias(Livro livro, int quantidade) throws CadastroInvalidoException {
        if (livro == null) {
            throw new CadastroInvalidoException("Livro não pode ser nulo.");
        }

        if (!livros.contains(livro)) {
            throw new CadastroInvalidoException("Livro não está cadastrado no acervo.");
        }

        if (quantidade <= 0) {
            throw new CadastroInvalidoException("Quantidade de cópias inválida.");
        }

        livro.adicionarCopias(quantidade);
    }

    public void editarLivro(Livro livro, String novoTitulo, String novoAutor, int novoTotalCopias)
            throws CadastroInvalidoException {
        if (livro == null) {
            throw new CadastroInvalidoException("Livro não pode ser nulo.");
        }

        if (!livros.contains(livro)) {
            throw new CadastroInvalidoException("Livro não está cadastrado no acervo.");
        }

        if (novoTitulo == null || novoTitulo.trim().isEmpty()) {
            throw new CadastroInvalidoException("O livro precisa ter um título.");
        }

        if (novoAutor == null || novoAutor.trim().isEmpty()) {
            throw new CadastroInvalidoException("O livro precisa ter um Autor.");
        }

        if (novoTotalCopias <= 0) {
            throw new CadastroInvalidoException("Quantidade total de cópias inválida.");
        }

        String t = novoTitulo.trim();
        String a = novoAutor.trim();

        for (Livro l : livros) {
            if (l == livro) {
                continue;
            }

            if (l.getTitulo() != null && l.getAutor() != null
                    && l.getTitulo().trim().equalsIgnoreCase(t)
                    && l.getAutor().trim().equalsIgnoreCase(a)) {
                throw new CadastroInvalidoException("Já existe um livro cadastrado com esse título e autor.");
            }
        }

        livro.setTitulo(t);
        livro.setAutor(a);

        // Ajusta o total de cópias sem perder o controle das cópias emprestadas
        try {
            livro.atualizarTotalCopias(novoTotalCopias);
        } catch (IllegalArgumentException ex) {
            throw new CadastroInvalidoException(ex.getMessage());
        }
    }

    public void removerLivro(Livro livro) throws CadastroInvalidoException {
        if (livro == null) {
            throw new CadastroInvalidoException("Livro não pode ser nulo.");
        }

        if (!livros.contains(livro)) {
            throw new CadastroInvalidoException("Livro não está cadastrado no acervo.");
        }

        // Só remove se não houver cópias emprestadas
        if (livro.getCopiasDisponiveis() < livro.getTotalCopias()) {
            throw new CadastroInvalidoException("Não é possível excluir: existem cópias emprestadas.");
        }

        livros.remove(livro);
    }

    // Listagem de todos os livros
    public String listarTodosOsLivros() {
        if (livros.isEmpty()) {
            return "Nenhum livro cadastrado.";
        }

        String resultado = "";
        int contador = 1;

        for (Livro livro : livros) {
            resultado += contador + " - " + livro.getTitulo() + "\n";
            contador++;
        }

        return resultado;
    }

    // Getter da lista de livros
    public List<Livro> getLivros() {
        return livros;
    }

    // Substitui os livros do acervo (usado na persistência)
    public void definirLivros(List<Livro> livros) {
        this.livros.clear();
        if (livros != null) {
            this.livros.addAll(livros);
        }
    }
}
