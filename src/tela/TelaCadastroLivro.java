package tela;

import java.awt.GridLayout;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

import controle.Biblioteca;
import exception.CadastroInvalidoException;
import modelo.Livro;

// Tela para cadastro de livros
public class TelaCadastroLivro extends JFrame {

    private final Biblioteca biblioteca;

    public TelaCadastroLivro(Biblioteca biblioteca) {
        this.biblioteca = biblioteca;

        setTitle("Cadastro de Livros");
        setSize(420, 220);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        adicionarComponentes();
    }

    private void adicionarComponentes() {
        JPanel painel = new JPanel(new GridLayout(4, 2, 8, 8));

        JLabel labelTitulo = new JLabel("Título:");
        JTextField campoTitulo = new JTextField();

        JLabel labelAutor = new JLabel("Autor:");
        JTextField campoAutor = new JTextField();

        JLabel labelCopias = new JLabel("Cópias:");
        JTextField campoCopias = new JTextField("1");

        JButton botaoCadastrar = new JButton("Cadastrar");
        botaoCadastrar.addActionListener(e -> {
            String titulo = campoTitulo.getText();
            String autor = campoAutor.getText();

            int copias;
            try {
                copias = Integer.parseInt(campoCopias.getText().trim());
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Cópias inválidas. Use um número inteiro (ex: 1).", "Erro",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (copias <= 0) {
                JOptionPane.showMessageDialog(this, "Cópias inválidas. Use um número maior que 0.", "Erro",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }

            try {
                biblioteca.cadastrarLivro(new Livro(titulo, autor, copias));
                JOptionPane.showMessageDialog(this, "Livro cadastrado com sucesso!");
                campoTitulo.setText("");
                campoAutor.setText("");
                campoCopias.setText("1");
            } catch (CadastroInvalidoException ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
            }
        });

        JButton botaoFechar = new JButton("Fechar");
        botaoFechar.addActionListener(e -> dispose());

        painel.add(labelTitulo);
        painel.add(campoTitulo);
        painel.add(labelAutor);
        painel.add(campoAutor);
        painel.add(labelCopias);
        painel.add(campoCopias);
        painel.add(botaoCadastrar);
        painel.add(botaoFechar);

        add(painel);
    }
}
