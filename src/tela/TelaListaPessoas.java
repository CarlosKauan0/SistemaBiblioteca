package tela;

import java.awt.BorderLayout;
import java.awt.GridLayout;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JList;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import controle.Biblioteca;
import exception.CadastroInvalidoException;
import modelo.Aluno;
import modelo.Livro;
import modelo.Pessoa;
import modelo.Professor;

// Tela de listagem de pessoas (com pesquisa e botão de mais informações)
public class TelaListaPessoas extends JFrame {

    private final Biblioteca biblioteca;

    private final JTextField campoPesquisa;
    private final DefaultListModel<Pessoa> modeloPessoas;
    private final JList<Pessoa> listaPessoas;
    private final JLabel labelAviso;

    private final JButton botaoMaisInfo;
    private final JButton botaoEditar;
    private final JButton botaoExcluir;

    public TelaListaPessoas(Biblioteca biblioteca) {
        this.biblioteca = biblioteca;

        this.campoPesquisa = new JTextField();
        this.modeloPessoas = new DefaultListModel<>();
        this.listaPessoas = new JList<>(modeloPessoas);
        this.labelAviso = new JLabel(" ");

        this.botaoMaisInfo = new JButton("Mais informações");
        this.botaoEditar = new JButton("Editar");
        this.botaoExcluir = new JButton("Excluir");

        setTitle("Lista de Pessoas");
        setSize(720, 420);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        adicionarComponentes();
        configurarEventos();
        atualizarLista();
    }

    private void adicionarComponentes() {
        setLayout(new BorderLayout(10, 10));

        JLabel titulo = new JLabel("Lista de Pessoas", JLabel.CENTER);
        add(titulo, BorderLayout.NORTH);

        JPanel painelCentro = new JPanel(new BorderLayout(8, 8));

        JPanel painelPesquisa = new JPanel(new GridLayout(2, 1, 4, 4));
        painelPesquisa.add(new JLabel("Pesquisar (nome/CPF/tipo):"));
        painelPesquisa.add(campoPesquisa);

        painelCentro.add(painelPesquisa, BorderLayout.NORTH);

        listaPessoas.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        painelCentro.add(new JScrollPane(listaPessoas), BorderLayout.CENTER);
        painelCentro.add(labelAviso, BorderLayout.SOUTH);

        add(painelCentro, BorderLayout.CENTER);

        JPanel painelBotoes = new JPanel(new GridLayout(1, 5, 8, 8));

        JButton botaoAtualizar = new JButton("Atualizar");
        botaoAtualizar.addActionListener(e -> atualizarLista());

        botaoMaisInfo.addActionListener(e -> mostrarMaisInformacoes());

        botaoEditar.addActionListener(e -> editarPessoa());
        botaoExcluir.addActionListener(e -> excluirPessoa());

        JButton botaoFechar = new JButton("Fechar");
        botaoFechar.addActionListener(e -> dispose());

        painelBotoes.add(botaoAtualizar);
        painelBotoes.add(botaoMaisInfo);
        painelBotoes.add(botaoEditar);
        painelBotoes.add(botaoExcluir);
        painelBotoes.add(botaoFechar);

        add(painelBotoes, BorderLayout.SOUTH);
    }

    private void configurarEventos() {
        campoPesquisa.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                atualizarLista();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                atualizarLista();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                atualizarLista();
            }
        });

        listaPessoas.addListSelectionListener(e -> atualizarBotoes());
        atualizarBotoes();
    }

    private void atualizarBotoes() {
        boolean temSelecao = listaPessoas.getSelectedValue() != null;
        botaoMaisInfo.setEnabled(temSelecao);
        botaoEditar.setEnabled(temSelecao);
        botaoExcluir.setEnabled(temSelecao);
    }

    private void atualizarLista() {
        String pesquisa = campoPesquisa.getText();
        if (pesquisa == null) {
            pesquisa = "";
        }
        pesquisa = pesquisa.trim().toLowerCase();

        modeloPessoas.clear();

        for (Pessoa pessoa : biblioteca.getPessoas()) {
            String texto = (pessoa.getNomePessoa() + " " + pessoa.getCpf() + " " + pessoa.getTipoPessoa()).toLowerCase();
            if (pesquisa.isEmpty() || texto.contains(pesquisa)) {
                modeloPessoas.addElement(pessoa);
            }
        }

        if (biblioteca.getPessoas().isEmpty()) {
            labelAviso.setText("Nenhuma pessoa cadastrada.");
        } else if (modeloPessoas.isEmpty()) {
            labelAviso.setText("Nenhuma pessoa encontrada para essa pesquisa.");
        } else {
            labelAviso.setText("Total encontrado: " + modeloPessoas.size());
        }

        botaoMaisInfo.setEnabled(listaPessoas.getSelectedValue() != null);
        atualizarBotoes();
    }

    private void mostrarMaisInformacoes() {
        Pessoa pessoa = listaPessoas.getSelectedValue();
        if (pessoa == null) {
            return;
        }

        String texto = "NOME: " + pessoa.getNomePessoa() + "\n"
                + "CPF: " + pessoa.getCpf() + "\n"
                + "TIPO: " + pessoa.getTipoPessoa() + "\n";

        if (pessoa instanceof Aluno) {
            Aluno aluno = (Aluno) pessoa;
            texto += "MATRÍCULA: " + aluno.getMatricula() + "\n";
        } else if (pessoa instanceof Professor) {
            Professor professor = (Professor) pessoa;
            texto += "MATRÍCULA FUNCIONAL: " + professor.getMatriculaFuncional() + "\n";
        }

        texto += "\nLIVROS PEGOS (" + pessoa.getLivrosPegos().size() + "):\n";
        if (pessoa.getLivrosPegos().isEmpty()) {
            texto += "Nenhum\n";
        } else {
            int contador = 1;
            for (Livro livro : pessoa.getLivrosPegos()) {
                texto += contador + " - " + livro.getTitulo() + " | Autor: " + livro.getAutor() + "\n";
                contador++;
            }
        }

        new JanelaInformacoes("Informações da Pessoa", texto).setVisible(true);
    }

    private void editarPessoa() {
        Pessoa pessoa = listaPessoas.getSelectedValue();
        if (pessoa == null) {
            return;
        }

        String novoNome = JOptionPane.showInputDialog(this, "Novo nome:", pessoa.getNomePessoa());
        if (novoNome == null) {
            return;
        }

        String novoCpf = JOptionPane.showInputDialog(this, "Novo CPF (apenas números ou com pontuação):", pessoa.getCpf());
        if (novoCpf == null) {
            return;
        }

        String novaMatricula;
        if (pessoa instanceof Aluno) {
            novaMatricula = JOptionPane.showInputDialog(this, "Nova matrícula:", ((Aluno) pessoa).getMatricula());
        } else if (pessoa instanceof Professor) {
            novaMatricula = JOptionPane.showInputDialog(this, "Nova matrícula funcional:", ((Professor) pessoa).getMatriculaFuncional());
        } else {
            novaMatricula = JOptionPane.showInputDialog(this, "Nova matrícula:", "");
        }

        if (novaMatricula == null) {
            return;
        }

        try {
            biblioteca.editarPessoa(pessoa, novoNome, novoCpf, novaMatricula);
            atualizarLista();
            JOptionPane.showMessageDialog(this, "Pessoa editada com sucesso!");
        } catch (CadastroInvalidoException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void excluirPessoa() {
        Pessoa pessoa = listaPessoas.getSelectedValue();
        if (pessoa == null) {
            return;
        }

        int opcao = JOptionPane.showConfirmDialog(this,
                "Deseja realmente excluir esta pessoa?\n\n" + pessoa.toString(),
                "Confirmar exclusão",
                JOptionPane.YES_NO_OPTION);

        if (opcao != JOptionPane.YES_OPTION) {
            return;
        }

        try {
            biblioteca.removerPessoa(pessoa);
            atualizarLista();
            JOptionPane.showMessageDialog(this, "Pessoa excluída com sucesso!");
        } catch (CadastroInvalidoException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }
}
