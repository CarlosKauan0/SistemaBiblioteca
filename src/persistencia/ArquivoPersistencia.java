package persistencia;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import controle.Biblioteca;

// Implementação de persistência em arquivos (usando serialização)
public class ArquivoPersistencia implements Persistencia {

    private final String caminhoArquivo;

    public ArquivoPersistencia(String caminhoArquivo) {
        this.caminhoArquivo = caminhoArquivo;
    }

    @Override
    public void salvar(Biblioteca biblioteca) throws IOException {
        if (biblioteca == null) {
            throw new IOException("Biblioteca não pode ser nula");
        }

        DadosBiblioteca dados = new DadosBiblioteca(biblioteca.getLivros(), biblioteca.getPessoas(), biblioteca.getEmprestimos());

        try (ObjectOutputStream saida = new ObjectOutputStream(new FileOutputStream(caminhoArquivo))) {
            saida.writeObject(dados);
        }
    }

    public Biblioteca carregar() throws IOException, ClassNotFoundException {
        File arquivo = new File(caminhoArquivo);
        Biblioteca biblioteca = new Biblioteca();

        // Se ainda não existe arquivo salvo, devolve uma biblioteca vazia
        if (!arquivo.exists()) {
            return biblioteca;
        }

        try (ObjectInputStream entrada = new ObjectInputStream(new FileInputStream(arquivo))) {
            Object objeto = entrada.readObject();

            if (!(objeto instanceof DadosBiblioteca)) {
                throw new IOException("Arquivo inválido: dados não são da biblioteca");
            }

            DadosBiblioteca dados = (DadosBiblioteca) objeto;
            biblioteca.definirLivros(dados.getLivros());
            biblioteca.definirPessoas(dados.getPessoas());
            biblioteca.definirEmprestimos(dados.getEmprestimos());
        }

        return biblioteca;
    }
}
