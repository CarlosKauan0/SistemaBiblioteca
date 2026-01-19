package modelo;

public class Aluno extends Pessoa{
    protected String matricula;
    public static final int LIMITE_LIVROS_ALUNO = 5; // LIMITE FIXO: 5 LIVROS

    public Aluno(String nomePessoa, String cpf, String matricula) {
       super(nomePessoa, cpf);
       this.matricula = matricula;

    }
    //GETTERS & SETTERS

    public String getMatricula() {
        return matricula;
    }

    public void setMatricula(String matricula) {
        this.matricula = matricula;
    }

    //FIM DOS GETTERS & SETTERS


    @Override
    public String getTipoPessoa() {
        return "Aluno";
    }
}
