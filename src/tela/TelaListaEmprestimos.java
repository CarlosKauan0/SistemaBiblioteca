package tela;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JList;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import controle.Biblioteca;
import modelo.Emprestimo;

// Tela de listagem de empréstimos com data/hora/prazo e multas
public class TelaListaEmprestimos extends JFrame {

    private final Biblioteca biblioteca;

    private final JTextField campoPesquisa;
    private final DefaultListModel<Emprestimo> modeloEmprestimos;
    private final JList<Emprestimo> listaEmprestimos;
    private final JLabel labelAviso;

    private final JButton botaoMaisInfo;

    private static final DateTimeFormatter FORMATO_DATA_HORA = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    public TelaListaEmprestimos(Biblioteca biblioteca) {
        this.biblioteca = biblioteca;

        this.campoPesquisa = new JTextField();
        this.modeloEmprestimos = new DefaultListModel<>();
        this.listaEmprestimos = new JList<>(modeloEmprestimos);
        this.labelAviso = new JLabel(" ");

        this.botaoMaisInfo = new JButton("Mais informações");

        setTitle("Lista de Empréstimos");
        setSize(920, 460);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        adicionarComponentes();
        configurarEventos();
        atualizarLista();
    }

    private void adicionarComponentes() {
        setLayout(new BorderLayout(10, 10));

        JLabel titulo = new JLabel("Lista de Empréstimos", JLabel.CENTER);
        add(titulo, BorderLayout.NORTH);

        JPanel painelCentro = new JPanel(new BorderLayout(8, 8));

        JPanel painelPesquisa = new JPanel(new GridLayout(2, 1, 4, 4));
        painelPesquisa.add(new JLabel("Pesquisar (nome da pessoa OU nome do livro):"));
        painelPesquisa.add(campoPesquisa);

        painelCentro.add(painelPesquisa, BorderLayout.NORTH);

        listaEmprestimos.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        painelCentro.add(new JScrollPane(listaEmprestimos), BorderLayout.CENTER);
        painelCentro.add(labelAviso, BorderLayout.SOUTH);

        add(painelCentro, BorderLayout.CENTER);

        JPanel painelBotoes = new JPanel(new GridLayout(1, 3, 8, 8));

        JButton botaoAtualizar = new JButton("Atualizar");
        botaoAtualizar.addActionListener(e -> atualizarLista());

        botaoMaisInfo.addActionListener(e -> mostrarMaisInformacoes());

        JButton botaoFechar = new JButton("Fechar");
        botaoFechar.addActionListener(e -> dispose());

        painelBotoes.add(botaoAtualizar);
        painelBotoes.add(botaoMaisInfo);
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

        listaEmprestimos.addListSelectionListener(e -> atualizarBotoes());
        atualizarBotoes();
    }

    private void atualizarBotoes() {
        botaoMaisInfo.setEnabled(listaEmprestimos.getSelectedValue() != null);
    }

    private void atualizarLista() {
        String pesquisa = campoPesquisa.getText();
        if (pesquisa == null) {
            pesquisa = "";
        }
        pesquisa = pesquisa.trim().toLowerCase();

        modeloEmprestimos.clear();

        for (Emprestimo e : biblioteca.getEmprestimos()) {
            if (e == null || e.getLivro() == null || e.getPessoa() == null) {
                continue;
            }

            String texto = (e.getLivro().getTitulo() + " " + e.getPessoa().getNomePessoa()).toLowerCase();
            if (pesquisa.isEmpty() || texto.contains(pesquisa)) {
                modeloEmprestimos.addElement(e);
            }
        }

        if (biblioteca.getEmprestimos().isEmpty()) {
            labelAviso.setText("Nenhum empréstimo registrado.");
        } else if (modeloEmprestimos.isEmpty()) {
            labelAviso.setText("Nenhum empréstimo encontrado para essa pesquisa.");
        } else {
            labelAviso.setText("Total encontrado: " + modeloEmprestimos.size());
        }

        atualizarBotoes();
    }

    private void mostrarMaisInformacoes() {
        Emprestimo e = listaEmprestimos.getSelectedValue();
        if (e == null) {
            return;
        }

        LocalDateTime referencia = e.isDevolvido() ? e.getDataDevolucao() : LocalDateTime.now();
        int diasAtraso = e.calcularDiasAtraso(referencia);
        double multa = e.calcularMulta(referencia);

        String texto = "PESSOA: " + e.getPessoa().getNomePessoa() + "\n"
                + "CPF: " + e.getPessoa().getCpf() + "\n"
                + "LIVRO: " + e.getLivro().getTitulo() + "\n"
                + "AUTOR: " + e.getLivro().getAutor() + "\n"
                + "\nDATA/HORA DO EMPRÉSTIMO: " + (e.getDataEmprestimo() == null ? "-" : e.getDataEmprestimo().format(FORMATO_DATA_HORA)) + "\n"
                + "PRAZO (DATA/HORA): " + (e.getPrazoDevolucao() == null ? "-" : e.getPrazoDevolucao().format(FORMATO_DATA_HORA)) + "\n";

        if (e.isDevolvido()) {
            texto += "DATA/HORA DA DEVOLUÇÃO: "
                    + (e.getDataDevolucao() == null ? "-" : e.getDataDevolucao().format(FORMATO_DATA_HORA)) + "\n";
        } else {
            texto += "STATUS: ATIVO\n";
        }

        texto += "\nDIAS DE ATRASO: " + diasAtraso + "\n";
        texto += "MULTA: R$ " + String.format("%.2f", multa) + "\n";

        new JanelaInformacoes("Informações do Empréstimo", texto).setVisible(true);
    }
}
