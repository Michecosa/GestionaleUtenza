package GestionaleUtenza.model;

import java.time.LocalDateTime;

public class Pagamento {
    private int idPagamento;
    private int fkOrdine;
    private double importo;
    private String metodo;
    // In Attesa / Completato / Fallito / Rimborsato
    private String stato;
    private LocalDateTime data;

    public Pagamento() {
    }

    public int getIdPagamento() {
        return idPagamento;
    }

    public int getFkOrdine() {
        return fkOrdine;
    }

    public double getImporto() {
        return importo;
    }

    public String getMetodo() {
        return metodo;
    }

    public String getStato() {
        return stato;
    }

    public LocalDateTime getData() {
        return data;
    }

    public void setIdPagamento(int v) {
        this.idPagamento = v;
    }

    public void setFkOrdine(int v) {
        this.fkOrdine = v;
    }

    public void setImporto(double v) {
        this.importo = v;
    }

    public void setMetodo(String v) {
        this.metodo = v;
    }

    public void setStato(String v) {
        this.stato = v;
    }

    public void setData(LocalDateTime v) {
        this.data = v;
    }

    @Override
    public String toString() {
        return String.format("[%d] ordine:%d   EUR%.2f  %s  %s  %s", idPagamento, fkOrdine, importo, metodo, stato,
                data);
    }
}
