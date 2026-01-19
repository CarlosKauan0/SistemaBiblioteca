package persistencia;

import java.io.IOException;

import controle.Biblioteca;

// Interface de persistência: define salvar e carregar dados
public interface Persistencia {

    // Salva os dados da biblioteca (livros e pessoas) em algum lugar (ex.: arquivo)
    void salvar(Biblioteca biblioteca) throws IOException;

    // Carrega os dados e devolve uma biblioteca pronta
    Biblioteca carregar() throws IOException, ClassNotFoundException;
}
