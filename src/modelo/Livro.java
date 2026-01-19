package modelo;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;

public class Livro implements Serializable {
    private static final long serialVersionUID = 1L;

    private String titulo;
    private String autor;

    // Campo antigo (mantido para compatibilidade com arquivos salvos antigos)
    private boolean disponivel;

    // Controle de cópias do livro
    private int totalCopias;
    private int copiasDisponiveis;


    public Livro(String titulo, String autor) {
        this(titulo, autor, 1);
    }

    public Livro(String titulo, String autor, int totalCopias) {
        this.titulo = titulo;
        this.autor = autor;

        if (totalCopias <= 0) {
            totalCopias = 1;
        }

        this.totalCopias = totalCopias;
        this.copiasDisponiveis = totalCopias;
        this.disponivel = this.copiasDisponiveis > 0;
    }


    //GETTERS & SETTERS
    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getAutor() {
        return autor;
    }

    public void setAutor(String autor) {
        this.autor = autor;
    }

    public int getTotalCopias() {
        return totalCopias;
    }

    public int getCopiasDisponiveis() {
        return copiasDisponiveis;
    }

    public boolean isDisponivel() {
        return copiasDisponiveis > 0;
    }

    // Mantido por compatibilidade com o restante do projeto
    public void setDisponivel(boolean disponivel) {
        this.disponivel = disponivel;
        if (disponivel) {
            this.copiasDisponiveis = this.totalCopias;
        } else {
            this.copiasDisponiveis = 0;
        }
    }

    public void adicionarCopias(int quantidade) {
        if (quantidade <= 0) {
            return;
        }

        this.totalCopias += quantidade;
        this.copiasDisponiveis += quantidade;
        this.disponivel = this.copiasDisponiveis > 0;
    }

    public void atualizarTotalCopias(int novoTotalCopias) {
        if (novoTotalCopias <= 0) {
            throw new IllegalArgumentException("Quantidade total de cópias inválida");
        }

        int emprestadas = totalCopias - copiasDisponiveis;
        if (novoTotalCopias < emprestadas) {
            throw new IllegalArgumentException("Não é possível reduzir o total abaixo das cópias emprestadas");
        }

        totalCopias = novoTotalCopias;
        copiasDisponiveis = novoTotalCopias - emprestadas;
        disponivel = copiasDisponiveis > 0;
    }

    public void emprestarUmaCopia() {
        if (copiasDisponiveis > 0) {
            copiasDisponiveis--;
        }
        this.disponivel = this.copiasDisponiveis > 0;
    }

    public void devolverUmaCopia() {
        if (copiasDisponiveis < totalCopias) {
            copiasDisponiveis++;
        }
        this.disponivel = this.copiasDisponiveis > 0;
    }

    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        in.defaultReadObject();

        // Se veio de um arquivo antigo, os campos novos podem vir zerados
        if (totalCopias <= 0) {
            totalCopias = 1;
        }

        if (copiasDisponiveis < 0) {
            copiasDisponiveis = 0;
        }

        if (copiasDisponiveis > totalCopias) {
            copiasDisponiveis = totalCopias;
        }

        if (copiasDisponiveis == 0 && disponivel) {
            copiasDisponiveis = totalCopias;
        }
    }
    //FIM DOS GETTERS & SETTERS

    @Override
    public String toString() {
        String status = isDisponivel() ? "Disponível" : "Indisponível";
        return titulo + " - " + autor + " (" + status + " | " + copiasDisponiveis + "/" + totalCopias + ")";
    }
}
