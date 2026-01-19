package tela;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JList;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JComponent;
import java.awt.Component;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.DefaultListModel;

import controle.Biblioteca;
import exception.*;
import modelo.*;

// Tela para realizar empréstimos
public class TelaEmprestimoLivro extends JFrame {

    private final Biblioteca biblioteca;

    private final JTextField campoPesquisaLivro;
    private final DefaultListModel<Livro> modeloLivros;
    private final JList<Livro> listaLivros;
    private final JLabel labelAvisoLivro;

    private final JTextField campoPesquisaPessoa;
    private final DefaultListModel<Pessoa> modeloPessoas;
    private final JList<Pessoa> listaPessoas;
    private final JLabel labelAvisoPessoa;

    private final JButton botaoEmprestar;

    private final JTextField campoPrazoDias;

    public TelaEmprestimoLivro(Biblioteca biblioteca) {
        this.biblioteca = biblioteca;

        this.campoPesquisaLivro = new JTextField();
        this.modeloLivros = new DefaultListModel<>();
        this.listaLivros = new JList<>(modeloLivros);
        this.labelAvisoLivro = new JLabel(" ");

        this.campoPesquisaPessoa = new JTextField();
        this.modeloPessoas = new DefaultListModel<>();
        this.listaPessoas = new JList<>(modeloPessoas);
        this.labelAvisoPessoa = new JLabel(" ");

        this.botaoEmprestar = new JButton("Emprestar");

        this.campoPrazoDias = new JTextField("7");

        setTitle("Empréstimo de Livro");
        setSize(720, 420);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        adicionarComponentes();
        configurarEventos();
        atualizarListas();
    }

    private void adicionarComponentes() {
        setLayout(new BorderLayout(10, 10));

        JPanel painelNorte = new JPanel(new BorderLayout(6, 6));
        JLabel titulo = new JLabel("Empréstimo de Livro", JLabel.CENTER);
        painelNorte.add(titulo, BorderLayout.NORTH);

        JPanel painelPrazo = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 0));
        painelPrazo.add(new JLabel("Prazo (dias):"));
        campoPrazoDias.setColumns(6);
        painelPrazo.add(campoPrazoDias);

        painelNorte.add(painelPrazo, BorderLayout.SOUTH);
        add(painelNorte, BorderLayout.NORTH);

        JPanel painelCentro = new JPanel(new GridLayout(1, 2, 10, 10));

        // Painel de livros
        JPanel painelLivros = new JPanel(new BorderLayout(8, 8));
        JPanel topoLivros = new JPanel(new GridLayout(2, 1, 4, 4));
        topoLivros.add(new JLabel("Pesquisar livro (mostra todos):"));
        topoLivros.add(campoPesquisaLivro);
        painelLivros.add(topoLivros, BorderLayout.NORTH);

        listaLivros.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        listaLivros.setCellRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected,
                    boolean cellHasFocus) {
                JComponent c = (JComponent) super.getListCellRendererComponent(list, value, index, isSelected,
                        cellHasFocus);

                if (value instanceof Livro) {
                    Livro l = (Livro) value;
                    String status = l.isDisponivel() ? "Disponível" : "Indisponível";
                    int disponiveis = l.getCopiasDisponiveis();
                    int total = l.getTotalCopias();
                    setText(l.getTitulo() + " - " + l.getAutor() + " (" + status + ") - Estoque: " + disponiveis + "/" + total);
                }

                return c;
            }
        });
        painelLivros.add(new JScrollPane(listaLivros), BorderLayout.CENTER);
        painelLivros.add(labelAvisoLivro, BorderLayout.SOUTH);

        // Painel de pessoas
        JPanel painelPessoas = new JPanel(new BorderLayout(8, 8));
        JPanel topoPessoas = new JPanel(new GridLayout(2, 1, 4, 4));
        topoPessoas.add(new JLabel("Pesquisar pessoa:"));
        topoPessoas.add(campoPesquisaPessoa);
        painelPessoas.add(topoPessoas, BorderLayout.NORTH);

        listaPessoas.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        painelPessoas.add(new JScrollPane(listaPessoas), BorderLayout.CENTER);
        painelPessoas.add(labelAvisoPessoa, BorderLayout.SOUTH);

        painelCentro.add(painelLivros);
        painelCentro.add(painelPessoas);

        add(painelCentro, BorderLayout.CENTER);

        JPanel painelBotoes = new JPanel(new GridLayout(1, 2, 8, 8));

        botaoEmprestar.addActionListener(e -> realizarEmprestimo());

        JButton botaoFechar = new JButton("Fechar");
        botaoFechar.addActionListener(e -> dispose());

        painelBotoes.add(botaoEmprestar);
        painelBotoes.add(botaoFechar);

        add(painelBotoes, BorderLayout.SOUTH);
    }

    private void configurarEventos() {
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

        listaLivros.addListSelectionListener(e -> atualizarBotoes());
        listaPessoas.addListSelectionListener(e -> atualizarBotoes());
    }

    private void atualizarListas() {
        atualizarListaLivros();
        atualizarListaPessoas();
        atualizarBotoes();
    }

    // Lista de livros disponíveis, filtrada pela pesquisa
    private void atualizarListaLivros() {
        String pesquisa = campoPesquisaLivro.getText();
        if (pesquisa == null) {
            pesquisa = "";
        }
        pesquisa = pesquisa.trim().toLowerCase();

        modeloLivros.clear();

        for (Livro livro : biblioteca.getLivros()) {
            String textoLivro = (livro.getTitulo() + " " + livro.getAutor()).toLowerCase();
            if (pesquisa.isEmpty() || textoLivro.contains(pesquisa)) {
                modeloLivros.addElement(livro);
            }
        }

        if (biblioteca.getLivros().isEmpty()) {
            labelAvisoLivro.setText("Nenhum livro cadastrado.");
        } else if (modeloLivros.isEmpty()) {
            labelAvisoLivro.setText("Nenhum livro encontrado para essa pesquisa.");
        } else {
            labelAvisoLivro.setText(" ");
        }

        atualizarBotoes();
    }

    // Lista de pessoas cadastradas, filtrada pela pesquisa
    private void atualizarListaPessoas() {
        String pesquisa = campoPesquisaPessoa.getText();
        if (pesquisa == null) {
            pesquisa = "";
        }
        pesquisa = pesquisa.trim().toLowerCase();

        modeloPessoas.clear();

        for (Pessoa pessoa : biblioteca.getPessoas()) {
            String textoPessoa = (pessoa.getNomePessoa() + " " + pessoa.getCpf() + " " + pessoa.getTipoPessoa()).toLowerCase();
            if (pesquisa.isEmpty() || textoPessoa.contains(pesquisa)) {
                modeloPessoas.addElement(pessoa);
            }
        }

        if (biblioteca.getPessoas().isEmpty()) {
            labelAvisoPessoa.setText("Nenhuma pessoa cadastrada.");
        } else if (modeloPessoas.isEmpty()) {
            labelAvisoPessoa.setText("Nenhuma pessoa encontrada para essa pesquisa.");
        } else {
            labelAvisoPessoa.setText(" ");
        }

        atualizarBotoes();
    }

    private void atualizarBotoes() {
        boolean temLivroSelecionado = listaLivros.getSelectedValue() != null;
        boolean temPessoaSelecionada = listaPessoas.getSelectedValue() != null;

        Livro livroSelecionado = listaLivros.getSelectedValue();
        boolean livroDisponivel = livroSelecionado != null && livroSelecionado.isDisponivel();

        botaoEmprestar.setEnabled(temLivroSelecionado && temPessoaSelecionada && livroDisponivel);
    }

    private void realizarEmprestimo() {
        try {
            Livro livro = listaLivros.getSelectedValue();
            if (livro == null) {
                throw new LivroInexistenteException("Selecione um livro");
            }

            Pessoa pessoa = listaPessoas.getSelectedValue();
            if (pessoa == null) {
                throw new PessoaNaoCadastradaException("Selecione uma pessoa");
            }

            int prazoDias;
            try {
                prazoDias = Integer.parseInt(campoPrazoDias.getText().trim());
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Prazo inválido. Use um número inteiro (ex: 7).", "Erro",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (prazoDias <= 0) {
                JOptionPane.showMessageDialog(this, "Prazo inválido. Use um número maior que 0.", "Erro",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }

            biblioteca.emprestarLivro(livro, pessoa, prazoDias);
            JOptionPane.showMessageDialog(this, "Empréstimo realizado com sucesso!");

            atualizarListas();
        } catch (LivroInexistenteException | PessoaNaoCadastradaException | LivroIndisponivelException
            | LimiteEmprestimosException | LivroJaEmprestadoParaPessoaException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

}
