package tela;

import java.awt.GridLayout;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

import controle.Biblioteca;
import exception.CadastroInvalidoException;
import modelo.Aluno;
import modelo.Pessoa;
import modelo.Professor;

// Tela para cadastro de usuários (aluno ou professor)
public class TelaCadastroUsuario extends JFrame {

    private final Biblioteca biblioteca;

    public TelaCadastroUsuario(Biblioteca biblioteca) {
        this.biblioteca = biblioteca;

        setTitle("Cadastro de Usuários");
        setSize(460, 240);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        adicionarComponentes();
    }

    private void adicionarComponentes() {
        JPanel painel = new JPanel(new GridLayout(5, 2, 8, 8));

        JLabel labelTipo = new JLabel("Tipo:");
        JComboBox<String> comboTipo = new JComboBox<>(new String[] { "Aluno", "Professor" });

        JLabel labelNome = new JLabel("Nome:");
        JTextField campoNome = new JTextField();

        JLabel labelCpf = new JLabel("CPF:");
        JTextField campoCpf = new JTextField();

        JLabel labelMatricula = new JLabel("Matrícula:");
        JTextField campoMatricula = new JTextField();

        comboTipo.addActionListener(e -> {
            String tipo = (String) comboTipo.getSelectedItem();
            if ("Aluno".equals(tipo)) {
                labelMatricula.setText("Matrícula:");
            } else {
                labelMatricula.setText("Matrícula Funcional:");
            }
        });

        JButton botaoCadastrar = new JButton("Cadastrar");
        botaoCadastrar.addActionListener(e -> {
            String tipo = (String) comboTipo.getSelectedItem();
            String nome = campoNome.getText();
            String cpf = campoCpf.getText();
            String matricula = campoMatricula.getText();

            Pessoa pessoa;
            if ("Aluno".equals(tipo)) {
                pessoa = new Aluno(nome, cpf, matricula);
            } else {
                pessoa = new Professor(nome, cpf, matricula);
            }

            try {
                biblioteca.cadastrarPessoa(pessoa);
                JOptionPane.showMessageDialog(this, "Usuário cadastrado com sucesso!");
                campoNome.setText("");
                campoCpf.setText("");
                campoMatricula.setText("");
            } catch (CadastroInvalidoException ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
            }
        });

        JButton botaoFechar = new JButton("Fechar");
        botaoFechar.addActionListener(e -> dispose());

        painel.add(labelTipo);
        painel.add(comboTipo);
        painel.add(labelNome);
        painel.add(campoNome);
        painel.add(labelCpf);
        painel.add(campoCpf);
        painel.add(labelMatricula);
        painel.add(campoMatricula);
        painel.add(botaoCadastrar);
        painel.add(botaoFechar);

        add(painel);
    }
}
