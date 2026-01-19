package modelo;

public class Professor extends Pessoa{
    protected  String matriculaFuncional;
    public static final int LIMITE_LIVROS_PROFESSOR = 10; // LIMITE FIXO: 10 LIVROS

    public Professor(String nomePessoa, String cpf, String matriculaFuncional) {
        super(nomePessoa, cpf);
        this.matriculaFuncional = matriculaFuncional;
    }

    //GETTERS & SETTERS

    public String getMatriculaFuncional() {
        return matriculaFuncional;
    }

    public void setMatriculaFuncional(String matriculaFuncional) {
        this.matriculaFuncional = matriculaFuncional;
    }

    //FIM DOS GETTERS & SETTERS


    @Override
    public String getTipoPessoa() {
        return "Professor";
    }
}
