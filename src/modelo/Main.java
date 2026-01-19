package modelo;

import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

import controle.Biblioteca;
import persistencia.ArquivoPersistencia;
import persistencia.Persistencia;
import tela.TelaPrincipal;

// Classe principal: inicia a interface gráfica
public class Main {

	public static void main(String[] args) {
		SwingUtilities.invokeLater(() -> {
			Persistencia persistencia = new ArquivoPersistencia("biblioteca.dat");
			Biblioteca biblioteca;

			try {
				biblioteca = persistencia.carregar();
			} catch (Exception e) {
				biblioteca = new Biblioteca();
				JOptionPane.showMessageDialog(null,
						"Não foi possível carregar o arquivo. Iniciando biblioteca vazia.\nErro: " + e.getMessage(),
						"Aviso",
						JOptionPane.WARNING_MESSAGE);
			}

			TelaPrincipal tela = new TelaPrincipal(biblioteca, persistencia);
			tela.setVisible(true);
		});
	}
}
