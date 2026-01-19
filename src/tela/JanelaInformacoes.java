package tela;

import java.awt.BorderLayout;

import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

// Janela bem simples para mostrar mais informações (texto grande com scroll)
public class JanelaInformacoes extends JFrame {

    public JanelaInformacoes(String titulo, String texto) {
        setTitle(titulo);
        setSize(520, 380);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JTextArea areaTexto = new JTextArea();
        areaTexto.setEditable(false);
        areaTexto.setLineWrap(true);
        areaTexto.setWrapStyleWord(true);
        areaTexto.setText(texto);

        add(new JScrollPane(areaTexto), BorderLayout.CENTER);
    }
}
