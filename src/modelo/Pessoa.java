package modelo;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public abstract class Pessoa implements Serializable {
    private static final long serialVersionUID = 1L;

    protected String nomePessoa;
    protected String cpf;
    protected List<Livro> livrosPegos;

    public Pessoa(String nomePessoa, String cpf) {
        this.nomePessoa = nomePessoa;
        this.cpf = cpf;
        this.livrosPegos = new ArrayList<>();
    }

    //GETTERS & SETTERS
    public String getNomePessoa() {
        return nomePessoa;
    }

    public void setNomePessoa(String nomePessoa) {
        this.nomePessoa = nomePessoa;
    }

    public String getCpf() {
        return cpf;
    }

    public void setCpf(String cpf) {
        this.cpf = cpf;
    }

    public List<Livro> getLivrosPegos() {
        return livrosPegos;
    }

    public void setLivrosPegos(List<Livro> livrosPegos) {
        this.livrosPegos = livrosPegos;
    }

    //FIM DOS GETTERS & SETTERS

    public abstract String getTipoPessoa();

    @Override
    public String toString() {
        return nomePessoa + " - " + cpf + " (" + getTipoPessoa() + ")";
    }




}
