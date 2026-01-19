package tela;

import java.awt.BorderLayout;
import java.awt.GridLayout;

import javax.swing.JButton;
import javax.swing.DefaultListModel;
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
import exception.LivroInexistenteException;
import exception.PessoaNaoCadastradaException;
import modelo.Livro;
import modelo.Pessoa;

// Tela para realizar devolução
// (Observação: no Java o nome do arquivo/classe não pode ter "ç", por isso é Devolucao)
public class TelaDevolucaoLivro extends JFrame {

    private final Biblioteca biblioteca;

    private final JTextField campoPesquisaPessoa;
    private final DefaultListModel<Pessoa> modeloPessoas;
    private final JList<Pessoa> listaPessoas;
    private final JLabel labelAvisoPessoa;

    private final JTextField campoPesquisaLivro;
    private final DefaultListModel<Livro> modeloLivros;
    private final JList<Livro> listaLivros;
    private final JLabel labelAvisoLivro;

    private final JButton botaoDevolver;

    public TelaDevolucaoLivro(Biblioteca biblioteca) {
        this.biblioteca = biblioteca;

        this.campoPesquisaPessoa = new JTextField();
        this.modeloPessoas = new DefaultListModel<>();
        this.listaPessoas = new JList<>(modeloPessoas);
        this.labelAvisoPessoa = new JLabel(" ");

        this.campoPesquisaLivro = new JTextField();
        this.modeloLivros = new DefaultListModel<>();
        this.listaLivros = new JList<>(modeloLivros);
        this.labelAvisoLivro = new JLabel(" ");

        this.botaoDevolver = new JButton("Devolver");

        setTitle("Devolução de Livro");
        setSize(720, 420);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        adicionarComponentes();
        configurarEventos();
        atualizarListas();
    }

    private void adicionarComponentes() {
        setLayout(new BorderLayout(10, 10));

        JLabel titulo = new JLabel("Devolução de Livro", JLabel.CENTER);
        add(titulo, BorderLayout.NORTH);

        JPanel painelCentro = new JPanel(new GridLayout(1, 2, 10, 10));

        // Painel de pessoas (somente com empréstimo ativo)
        JPanel painelPessoas = new JPanel(new BorderLayout(8, 8));
        JPanel topoPessoas = new JPanel(new GridLayout(2, 1, 4, 4));
        topoPessoas.add(new JLabel("Pesquisar pessoa (somente com empréstimo ativo):"));
        topoPessoas.add(campoPesquisaPessoa);
        painelPessoas.add(topoPessoas, BorderLayout.NORTH);

        listaPessoas.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        painelPessoas.add(new JScrollPane(listaPessoas), BorderLayout.CENTER);
        painelPessoas.add(labelAvisoPessoa, BorderLayout.SOUTH);

        // Painel de livros (da pessoa selecionada)
        JPanel painelLivros = new JPanel(new BorderLayout(8, 8));
        JPanel topoLivros = new JPanel(new GridLayout(2, 1, 4, 4));
        topoLivros.add(new JLabel("Pesquisar livro (da pessoa selecionada):"));
        topoLivros.add(campoPesquisaLivro);
        painelLivros.add(topoLivros, BorderLayout.NORTH);

        listaLivros.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        painelLivros.add(new JScrollPane(listaLivros), BorderLayout.CENTER);
        painelLivros.add(labelAvisoLivro, BorderLayout.SOUTH);

        painelCentro.add(painelPessoas);
        painelCentro.add(painelLivros);

        add(painelCentro, BorderLayout.CENTER);

        JPanel painelBotoes = new JPanel(new GridLayout(1, 2, 8, 8));

        botaoDevolver.addActionListener(e -> realizarDevolucao());

        JButton botaoFechar = new JButton("Fechar");
        botaoFechar.addActionListener(e -> dispose());

        painelBotoes.add(botaoDevolver);
        painelBotoes.add(botaoFechar);

        add(painelBotoes, BorderLayout.SOUTH);
    }

    private void configurarEventos() {
        campoPesquisaPessoa.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                atualizarListaPessoas();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                atualizarListaPessoas();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                atualizarListaPessoas();
            }
        });

        campoPesquisaLivro.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                atualizarListaLivros();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                atualizarListaLivros();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                atualizarListaLivros();
            }
        });

        listaPessoas.addListSelectionListener(e -> {
            atualizarListaLivros();
            atualizarBotoes();
        });
        listaLivros.addListSelectionListener(e -> atualizarBotoes());
    }

    private void atualizarListas() {
        atualizarListaPessoas();
        atualizarListaLivros();
        atualizarBotoes();
    }

    // Lista pessoas com empréstimo ativo, filtrada
    private void atualizarListaPessoas() {
        String pesquisa = campoPesquisaPessoa.getText();
        if (pesquisa == null) {
            pesquisa = "";
        }
        pesquisa = pesquisa.trim().toLowerCase();

        modeloPessoas.clear();

        for (Pessoa pessoa : biblioteca.getPessoas()) {
            if (pessoa.getLivrosPegos().isEmpty()) {
                continue;
            }

            String textoPessoa = (pessoa.getNomePessoa() + " " + pessoa.getCpf() + " " + pessoa.getTipoPessoa()).toLowerCase();
            if (pesquisa.isEmpty() || textoPessoa.contains(pesquisa)) {
                modeloPessoas.addElement(pessoa);
            }
        }

        if (modeloPessoas.isEmpty()) {
            labelAvisoPessoa.setText("Nenhuma pessoa com empréstimo ativo.");
        } else {
            labelAvisoPessoa.setText(" ");
        }

        atualizarListaLivros();
        atualizarBotoes();
    }

    // Lista livros da pessoa selecionada, filtrada
    private void atualizarListaLivros() {
        String pesquisa = campoPesquisaLivro.getText();
        if (pesquisa == null) {
            pesquisa = "";
        }
        pesquisa = pesquisa.trim().toLowerCase();

        modeloLivros.clear();

        Pessoa pessoa = listaPessoas.getSelectedValue();
        if (pessoa != null) {
            for (Livro livro : pessoa.getLivrosPegos()) {
                String textoLivro = (livro.getTitulo() + " " + livro.getAutor()).toLowerCase();
                if (pesquisa.isEmpty() || textoLivro.contains(pesquisa)) {
                    modeloLivros.addElement(livro);
                }
            }
        }

        if (pessoa == null) {
            labelAvisoLivro.setText("Selecione uma pessoa para ver os livros.");
        } else if (modeloLivros.isEmpty()) {
            labelAvisoLivro.setText("Nenhum livro encontrado para essa pesquisa.");
        } else {
            labelAvisoLivro.setText(" ");
        }

        atualizarBotoes();
    }

    private void atualizarBotoes() {
        boolean temPessoaSelecionada = listaPessoas.getSelectedValue() != null;
        boolean temLivroSelecionado = listaLivros.getSelectedValue() != null;

        botaoDevolver.setEnabled(temPessoaSelecionada && temLivroSelecionado);
    }

    private void realizarDevolucao() {
        try {
            Pessoa pessoa = listaPessoas.getSelectedValue();
            if (pessoa == null) {
                throw new PessoaNaoCadastradaException("Selecione uma pessoa");
            }

            Livro livro = listaLivros.getSelectedValue();
            if (livro == null) {
                throw new LivroInexistenteException("Selecione um livro");
            }

            double multa = biblioteca.devolverLivro(livro, pessoa);
            if (multa > 0) {
                JOptionPane.showMessageDialog(this,
                        "Devolução realizada com sucesso!\nMulta por atraso: R$ " + String.format("%.2f", multa));
            } else {
                JOptionPane.showMessageDialog(this, "Devolução realizada com sucesso!");
            }

            atualizarListas();
        } catch (LivroInexistenteException | PessoaNaoCadastradaException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

}
