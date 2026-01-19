package controle;

import java.util.ArrayList;
import java.util.List;

import exception.*;
import modelo.*;

// Cadastro e gerenciamento de pessoas (alunos e professores)
public class CadastroDePessoas {

    // Lista de pessoas cadastradas
    private final List<Pessoa> pessoas;

    public CadastroDePessoas() {
        this.pessoas = new ArrayList<>();
    }

    // Cadastro de pessoas (validações básicas)
    public void cadastrarPessoa(Pessoa pessoa) throws CadastroInvalidoException {
        if (pessoa == null) {
            throw new CadastroInvalidoException("Pessoa não pode ser nula.");
        }

        if (pessoa.getNomePessoa() == null || pessoa.getNomePessoa().trim().isEmpty()) {
            throw new CadastroInvalidoException("A pessoa precisa ter um nome.");
        }

        if (pessoa.getCpf() == null || pessoa.getCpf().replaceAll("[^0-9]", "").length() != 11) {
            throw new CadastroInvalidoException("O cpf precisa ter 11 digitos");
        }

        String nomeLimpo = pessoa.getNomePessoa().trim();
        String cpfNovo = pessoa.getCpf().replaceAll("[^0-9]", "");

        for (Pessoa p : pessoas) {
            if (p.getCpf() != null && p.getCpf().replaceAll("[^0-9]", "").equals(cpfNovo)) {
                throw new CadastroInvalidoException("Este cpf já está cadastrado na biblioteca!");
            }
        }

        if (pessoa instanceof Aluno) {
            Aluno aluno = (Aluno) pessoa;

            if (aluno.getMatricula() == null || aluno.getMatricula().trim().isEmpty()) {
                throw new CadastroInvalidoException("Aluno precisa ter matrícula.");
            }

            String matriculaNova = aluno.getMatricula().trim();

            for (Pessoa p : pessoas) {
                if (p instanceof Aluno) {
                    Aluno alunoCadastrado = (Aluno) p;
                    if (alunoCadastrado.getMatricula() != null
                            && alunoCadastrado.getMatricula().trim().equalsIgnoreCase(matriculaNova)) {
                        throw new MatriculaExistenteException("Esta matrícula já está cadastrada no sistema!");
                    }
                }
            }
            aluno.setMatricula(matriculaNova);

        } else if (pessoa instanceof Professor) {
            Professor professor = (Professor) pessoa;

            if (professor.getMatriculaFuncional() == null || professor.getMatriculaFuncional().trim().isEmpty()) {
                throw new CadastroInvalidoException("Professor precisa ter matrícula funcional.");
            }

            String matriculaFuncionalNova = professor.getMatriculaFuncional().trim();

            for (Pessoa p : pessoas) {
                if (p instanceof Professor) {
                    Professor professorCadastrado = (Professor) p;
                    if (professorCadastrado.getMatriculaFuncional() != null
                            && professorCadastrado.getMatriculaFuncional().trim().equalsIgnoreCase(matriculaFuncionalNova)) {
                        throw new MatriculaExistenteException("Esta matrícula funcional já está cadastrada no sistema!");
                    }
                }
            }
            professor.setMatriculaFuncional(matriculaFuncionalNova);
        }

        pessoa.setNomePessoa(nomeLimpo);
        pessoa.setCpf(cpfNovo);
        pessoas.add(pessoa);
    }

    // Busca de pessoa pelo CPF
    public Pessoa buscarPessoaPorCpf(String cpf) throws PessoaNaoCadastradaException {
        if (cpf == null) {
            throw new PessoaNaoCadastradaException("CPF não pode ser nulo");
        }

        String cpfBuscado = cpf.replaceAll("[^0-9]", "");
        if (cpfBuscado.isEmpty()) {
            throw new PessoaNaoCadastradaException("CPF não pode ser vazio");
        }

        for (Pessoa pessoa : pessoas) {
            if (pessoa.getCpf() != null && pessoa.getCpf().replaceAll("[^0-9]", "").equals(cpfBuscado)) {
                return pessoa;
            }
        }

        throw new PessoaNaoCadastradaException("Esta pessoa não está cadastrada na biblioteca");
    }

    // Listagem de todas as pessoas
    public String listarTodasAsPessoas() {
        if (pessoas.isEmpty()) {
            return "Nenhuma pessoa cadastrada.";
        }

        String resultado = "";
        int contador = 1;

        for (Pessoa pessoa : pessoas) {
            resultado += contador + " - " + pessoa.getNomePessoa() + "\n";
            contador++;
        }

        return resultado;
    }

    // Getter da lista de pessoas
    public List<Pessoa> getPessoas() {
        return pessoas;
    }

    // Substitui as pessoas cadastradas (usado na persistência)
    public void definirPessoas(List<Pessoa> pessoas) {
        this.pessoas.clear();
        if (pessoas != null) {
            this.pessoas.addAll(pessoas);
        }
    }

    public void editarPessoa(Pessoa pessoa, String novoNome, String novoCpf, String novaMatricula)
            throws CadastroInvalidoException {
        if (pessoa == null) {
            throw new CadastroInvalidoException("Pessoa não pode ser nula.");
        }

        if (!pessoas.contains(pessoa)) {
            throw new CadastroInvalidoException("Pessoa não está cadastrada.");
        }

        if (novoNome == null || novoNome.trim().isEmpty()) {
            throw new CadastroInvalidoException("A pessoa precisa ter um nome.");
        }

        if (novoCpf == null || novoCpf.replaceAll("[^0-9]", "").length() != 11) {
            throw new CadastroInvalidoException("O cpf precisa ter 11 digitos");
        }

        String nomeLimpo = novoNome.trim();
        String cpfNovo = novoCpf.replaceAll("[^0-9]", "");

        for (Pessoa p : pessoas) {
            if (p == pessoa) {
                continue;
            }

            if (p.getCpf() != null && p.getCpf().replaceAll("[^0-9]", "").equals(cpfNovo)) {
                throw new CadastroInvalidoException("Este cpf já está cadastrado na biblioteca!");
            }
        }

        if (pessoa instanceof Aluno) {
            if (novaMatricula == null || novaMatricula.trim().isEmpty()) {
                throw new CadastroInvalidoException("Aluno precisa ter matrícula.");
            }

            String matriculaNova = novaMatricula.trim();
            for (Pessoa p : pessoas) {
                if (p == pessoa) {
                    continue;
                }

                if (p instanceof Aluno) {
                    Aluno alunoCadastrado = (Aluno) p;
                    if (alunoCadastrado.getMatricula() != null
                            && alunoCadastrado.getMatricula().trim().equalsIgnoreCase(matriculaNova)) {
                        throw new MatriculaExistenteException("Esta matrícula já está cadastrada no sistema!");
                    }
                }
            }

            ((Aluno) pessoa).setMatricula(matriculaNova);
        } else if (pessoa instanceof Professor) {
            if (novaMatricula == null || novaMatricula.trim().isEmpty()) {
                throw new CadastroInvalidoException("Professor precisa ter matrícula funcional.");
            }

            String matriculaFuncionalNova = novaMatricula.trim();
            for (Pessoa p : pessoas) {
                if (p == pessoa) {
                    continue;
                }

                if (p instanceof Professor) {
                    Professor professorCadastrado = (Professor) p;
                    if (professorCadastrado.getMatriculaFuncional() != null
                            && professorCadastrado.getMatriculaFuncional().trim().equalsIgnoreCase(matriculaFuncionalNova)) {
                        throw new MatriculaExistenteException("Esta matrícula funcional já está cadastrada no sistema!");
                    }
                }
            }

            ((Professor) pessoa).setMatriculaFuncional(matriculaFuncionalNova);
        }

        pessoa.setNomePessoa(nomeLimpo);
        pessoa.setCpf(cpfNovo);
    }

    public void removerPessoa(Pessoa pessoa) throws CadastroInvalidoException {
        if (pessoa == null) {
            throw new CadastroInvalidoException("Pessoa não pode ser nula.");
        }

        if (!pessoas.contains(pessoa)) {
            throw new CadastroInvalidoException("Pessoa não está cadastrada.");
        }

        if (!pessoa.getLivrosPegos().isEmpty()) {
            throw new CadastroInvalidoException("Não é possível excluir: a pessoa tem empréstimos ativos.");
        }

        pessoas.remove(pessoa);
    }
}
