package exception;

// Exceção lançada quando já existe uma matrícula cadastrada no sistema
public class MatriculaExistenteException extends CadastroInvalidoException {

    public MatriculaExistenteException(String message) {
        super(message);
    }
}
