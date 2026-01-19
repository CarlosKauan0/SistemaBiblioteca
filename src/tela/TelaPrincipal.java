package tela;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

import controle.Biblioteca;
import persistencia.Persistencia;

// Tela inicial do sistema
public class TelaPrincipal extends JFrame {

    private final Biblioteca biblioteca;
    private final Persistencia persistencia;

    public TelaPrincipal(Biblioteca biblioteca, Persistencia persistencia) {
        this.biblioteca = biblioteca;
        this.persistencia = persistencia;

        setTitle("Sistema da Biblioteca");
        setSize(420, 260);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);

        adicionarComponentes();
        configurarFechamentoComSalvar();
    }

    private void adicionarComponentes() {
        JLabel titulo = new JLabel("Tela Principal", SwingConstants.CENTER);
        add(titulo, BorderLayout.NORTH);

        JPanel painelBotoes = new JPanel(new GridLayout(7, 1, 8, 8));

        JButton botaoCadastroLivro = new JButton("Cadastro de Livros");
        botaoCadastroLivro.addActionListener(e -> new TelaCadastroLivro(biblioteca).setVisible(true));

        JButton botaoCadastroUsuario = new JButton("Cadastro de Usuários");
        botaoCadastroUsuario.addActionListener(e -> new TelaCadastroUsuario(biblioteca).setVisible(true));

        JButton botaoEmprestimo = new JButton("Empréstimo de Livro");
        botaoEmprestimo.addActionListener(e -> new TelaEmprestimoLivro(biblioteca).setVisible(true));

        JButton botaoDevolucao = new JButton("Devolução de Livro");
        botaoDevolucao.addActionListener(e -> new TelaDevolucaoLivro(biblioteca).setVisible(true));

        JButton botaoListaLivros = new JButton("Lista de Livros");
        botaoListaLivros.addActionListener(e -> {
            new TelaListaLivros(biblioteca).setVisible(true);
        });

        JButton botaoListaEmprestimos = new JButton("Lista de Empréstimos");
        botaoListaEmprestimos.addActionListener(e -> {
            new TelaListaEmprestimos(biblioteca).setVisible(true);
        });

        JButton botaoListaPessoas = new JButton("Lista de Pessoas");
        botaoListaPessoas.addActionListener(e -> {
            new TelaListaPessoas(biblioteca).setVisible(true);
        });

        painelBotoes.add(botaoCadastroLivro);
        painelBotoes.add(botaoCadastroUsuario);
        painelBotoes.add(botaoEmprestimo);
        painelBotoes.add(botaoDevolucao);
        painelBotoes.add(botaoListaLivros);
        painelBotoes.add(botaoListaEmprestimos);
        painelBotoes.add(botaoListaPessoas);

        add(painelBotoes, BorderLayout.CENTER);
    }

    private void configurarFechamentoComSalvar() {
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                int opcao = JOptionPane.showConfirmDialog(
                        TelaPrincipal.this,
                        "Deseja salvar antes de sair?",
                        "Salvar",
                        JOptionPane.YES_NO_CANCEL_OPTION);

                if (opcao == JOptionPane.CANCEL_OPTION) {
                    return;
                }

                if (opcao == JOptionPane.YES_OPTION) {
                    try {
                        persistencia.salvar(biblioteca);
                    } catch (IOException ex) {
                        JOptionPane.showMessageDialog(TelaPrincipal.this,
                                "Erro ao salvar: " + ex.getMessage(),
                                "Erro",
                                JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                }

                dispose();
            }
        });
    }
}
