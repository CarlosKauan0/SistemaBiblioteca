package modelo;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;

public class Emprestimo implements Serializable {

    private static final long serialVersionUID = 1L;

    private static final double MULTA_POR_DIA = 2.0;
    private static final DateTimeFormatter FORMATO_DATA_HORA = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    private final Livro livro;
    private final Pessoa pessoa;
    private final LocalDateTime dataEmprestimo;
    private final LocalDateTime prazoDevolucao;

    private LocalDateTime dataDevolucao;

    public Emprestimo(Livro livro, Pessoa pessoa, LocalDateTime dataEmprestimo, LocalDateTime prazoDevolucao) {
        this.livro = livro;
        this.pessoa = pessoa;
        this.dataEmprestimo = dataEmprestimo;
        this.prazoDevolucao = prazoDevolucao;
    }

    public Livro getLivro() {
        return livro;
    }

    public Pessoa getPessoa() {
        return pessoa;
    }

    public LocalDateTime getDataEmprestimo() {
        return dataEmprestimo;
    }

    public LocalDateTime getPrazoDevolucao() {
        return prazoDevolucao;
    }

    public LocalDateTime getDataDevolucao() {
        return dataDevolucao;
    }

    public boolean isDevolvido() {
        return dataDevolucao != null;
    }

    public void marcarDevolvido(LocalDateTime dataDevolucao) {
        this.dataDevolucao = dataDevolucao;
    }

    public int calcularDiasAtraso(LocalDateTime referencia) {
        if (prazoDevolucao == null || referencia == null) {
            return 0;
        }

        LocalDate dataPrazo = prazoDevolucao.toLocalDate();
        LocalDate dataRef = referencia.toLocalDate();

        if (dataRef.isAfter(dataPrazo)) {
            return (int) ChronoUnit.DAYS.between(dataPrazo, dataRef);
        }

        return 0;
    }

    public double calcularMulta(LocalDateTime referencia) {
        int dias = calcularDiasAtraso(referencia);
        return dias * MULTA_POR_DIA;
    }

    public String getResumo() {
        String status = isDevolvido() ? "Devolvido" : "Ativo";
        return livro.getTitulo() + " - " + pessoa.getNomePessoa() + " | Status: " + status;
    }

    @Override
    public String toString() {
        String status = isDevolvido() ? "Devolvido" : "Ativo";

        String dataE = (dataEmprestimo == null) ? "-" : dataEmprestimo.format(FORMATO_DATA_HORA);
        String prazo = (prazoDevolucao == null) ? "-" : prazoDevolucao.format(FORMATO_DATA_HORA);

        String texto = livro.getTitulo() + " - " + pessoa.getNomePessoa()
                + " | Emprestado: " + dataE
                + " | Prazo: " + prazo
                + " | " + status;

        if (!isDevolvido()) {
            double multa = calcularMulta(LocalDateTime.now());
            if (multa > 0) {
                texto += " | Multa: R$ " + String.format("%.2f", multa);
            }
        }

        return texto;
    }
}
