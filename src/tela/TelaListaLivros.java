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
import modelo.Livro;
import modelo.Pessoa;

// Tela de listagem de livros (com pesquisa e botão de mais informações)
public class TelaListaLivros extends JFrame {

    private final Biblioteca biblioteca;

    private final JTextField campoPesquisa;
    private final DefaultListModel<Livro> modeloLivros;
    private final JList<Livro> listaLivros;
    private final JLabel labelAviso;

    private final JButton botaoMaisInfo;
    private final JButton botaoAdicionarCopias;
    private final JButton botaoEditar;
    private final JButton botaoExcluir;

    public TelaListaLivros(Biblioteca biblioteca) {
        this.biblioteca = biblioteca;

        this.campoPesquisa = new JTextField();
        this.modeloLivros = new DefaultListModel<>();
        this.listaLivros = new JList<>(modeloLivros);
        this.labelAviso = new JLabel(" ");

        this.botaoMaisInfo = new JButton("Mais informações");
        this.botaoAdicionarCopias = new JButton("Adicionar cópias");
        this.botaoEditar = new JButton("Editar");
        this.botaoExcluir = new JButton("Excluir");

        setTitle("Lista de Livros");
        setSize(720, 420);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        adicionarComponentes();
        configurarEventos();
        atualizarLista();
    }

    private void adicionarComponentes() {
        setLayout(new BorderLayout(10, 10));

        JLabel titulo = new JLabel("Lista de Livros", JLabel.CENTER);
        add(titulo, BorderLayout.NORTH);

        JPanel painelCentro = new JPanel(new BorderLayout(8, 8));

        JPanel painelPesquisa = new JPanel(new GridLayout(2, 1, 4, 4));
        painelPesquisa.add(new JLabel("Pesquisar (título/autor):"));
        painelPesquisa.add(campoPesquisa);

        painelCentro.add(painelPesquisa, BorderLayout.NORTH);

        listaLivros.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        painelCentro.add(new JScrollPane(listaLivros), BorderLayout.CENTER);
        painelCentro.add(labelAviso, BorderLayout.SOUTH);

        add(painelCentro, BorderLayout.CENTER);

        JPanel painelBotoes = new JPanel(new GridLayout(2, 3, 8, 8));

        JButton botaoAtualizar = new JButton("Atualizar");
        botaoAtualizar.addActionListener(e -> atualizarLista());

        botaoMaisInfo.addActionListener(e -> mostrarMaisInformacoes());

        botaoAdicionarCopias.addActionListener(e -> adicionarCopias());
        botaoEditar.addActionListener(e -> editarLivro());
        botaoExcluir.addActionListener(e -> excluirLivro());

        JButton botaoFechar = new JButton("Fechar");
        botaoFechar.addActionListener(e -> dispose());

        painelBotoes.add(botaoAtualizar);
        painelBotoes.add(botaoMaisInfo);
        painelBotoes.add(botaoAdicionarCopias);
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

        listaLivros.addListSelectionListener(e -> atualizarBotoes());
        atualizarBotoes();
    }

    private void atualizarBotoes() {
        boolean temSelecao = listaLivros.getSelectedValue() != null;
        botaoMaisInfo.setEnabled(temSelecao);
        botaoAdicionarCopias.setEnabled(temSelecao);
        botaoEditar.setEnabled(temSelecao);
        botaoExcluir.setEnabled(temSelecao);
    }

    private void atualizarLista() {
        String pesquisa = campoPesquisa.getText();
        if (pesquisa == null) {
            pesquisa = "";
        }
        pesquisa = pesquisa.trim().toLowerCase();

        modeloLivros.clear();

        for (Livro livro : biblioteca.getLivros()) {
            String texto = (livro.getTitulo() + " " + livro.getAutor()).toLowerCase();
            if (pesquisa.isEmpty() || texto.contains(pesquisa)) {
                modeloLivros.addElement(livro);
            }
        }

        if (biblioteca.getLivros().isEmpty()) {
            labelAviso.setText("Nenhum livro cadastrado.");
        } else if (modeloLivros.isEmpty()) {
            labelAviso.setText("Nenhum livro encontrado para essa pesquisa.");
        } else {
            labelAviso.setText("Total encontrado: " + modeloLivros.size());
        }

        botaoMaisInfo.setEnabled(listaLivros.getSelectedValue() != null);
        atualizarBotoes();
    }

    private void mostrarMaisInformacoes() {
        Livro livro = listaLivros.getSelectedValue();
        if (livro == null) {
            return;
        }

        String status = livro.isDisponivel() ? "Disponível" : "Indisponível";

        int total = livro.getTotalCopias();
        int disponiveis = livro.getCopiasDisponiveis();
        int emprestadas = total - disponiveis;

        String emprestadoPara = "Ninguém";
        if (emprestadas > 0) {
            String resultado = "";

            for (Pessoa pessoa : biblioteca.getPessoas()) {
                int qtdComPessoa = 0;
                for (Livro l : pessoa.getLivrosPegos()) {
                    if (l != null && l.getTitulo() != null && l.getAutor() != null
                            && livro.getTitulo() != null && livro.getAutor() != null
                            && l.getTitulo().trim().equalsIgnoreCase(livro.getTitulo().trim())
                            && l.getAutor().trim().equalsIgnoreCase(livro.getAutor().trim())) {
                        qtdComPessoa++;
                    }
                }

                if (qtdComPessoa > 0) {
                    resultado += pessoa.getNomePessoa() + " (" + qtdComPessoa + ")" + " - " + pessoa.getCpf()
                            + " (" + pessoa.getTipoPessoa() + ")\n";
                }
            }

            emprestadoPara = resultado.isEmpty() ? "Não encontrado" : resultado;
        }

        String texto = "TÍTULO: " + livro.getTitulo() + "\n"
                + "AUTOR: " + livro.getAutor() + "\n"
                + "STATUS: " + status + "\n"
                + "CÓPIAS DISPONÍVEIS: " + disponiveis + "\n"
                + "CÓPIAS TOTAIS: " + total + "\n"
                + "CÓPIAS EMPRESTADAS: " + emprestadas + "\n"
                + "\nEMPRESTADO PARA:\n" + emprestadoPara + "\n";

        new JanelaInformacoes("Informações do Livro", texto).setVisible(true);
    }

    private void adicionarCopias() {
        Livro livro = listaLivros.getSelectedValue();
        if (livro == null) {
            return;
        }

        String entrada = JOptionPane.showInputDialog(this, "Quantas cópias deseja adicionar?", "1");
        if (entrada == null) {
            return;
        }

        try {
            int qtd = Integer.parseInt(entrada.trim());
            biblioteca.adicionarCopiasLivro(livro, qtd);
            atualizarLista();
            JOptionPane.showMessageDialog(this, "Cópias adicionadas com sucesso!");
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Valor inválido.", "Erro", JOptionPane.ERROR_MESSAGE);
        } catch (CadastroInvalidoException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void editarLivro() {
        Livro livro = listaLivros.getSelectedValue();
        if (livro == null) {
            return;
        }

        String novoTitulo = JOptionPane.showInputDialog(this, "Novo título:", livro.getTitulo());
        if (novoTitulo == null) {
            return;
        }

        String novoAutor = JOptionPane.showInputDialog(this, "Novo autor:", livro.getAutor());
        if (novoAutor == null) {
            return;
        }

        String novoTotalCopiasTexto = JOptionPane.showInputDialog(this, "Total de cópias:", String.valueOf(livro.getTotalCopias()));
        if (novoTotalCopiasTexto == null) {
            return;
        }

        int novoTotalCopias;
        try {
            novoTotalCopias = Integer.parseInt(novoTotalCopiasTexto.trim());
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Total de cópias inválido.", "Erro", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            int emprestadas = livro.getTotalCopias() - livro.getCopiasDisponiveis();
            if (novoTotalCopias < emprestadas) {
                JOptionPane.showMessageDialog(this,
                        "Não é possível reduzir o total de cópias abaixo das emprestadas.\n"
                                + "Cópias emprestadas agora: " + emprestadas,
                        "Erro",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }

            biblioteca.editarLivro(livro, novoTitulo, novoAutor, novoTotalCopias);
            atualizarLista();
            JOptionPane.showMessageDialog(this, "Livro editado com sucesso!");
        } catch (CadastroInvalidoException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void excluirLivro() {
        Livro livro = listaLivros.getSelectedValue();
        if (livro == null) {
            return;
        }

        int opcao = JOptionPane.showConfirmDialog(this,
                "Deseja realmente excluir este livro?\n\n" + livro.toString(),
                "Confirmar exclusão",
                JOptionPane.YES_NO_OPTION);

        if (opcao != JOptionPane.YES_OPTION) {
            return;
        }

        try {
            biblioteca.removerLivro(livro);
            atualizarLista();
            JOptionPane.showMessageDialog(this, "Livro excluído com sucesso!");
        } catch (CadastroInvalidoException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }
}
