package GestionaleUtenza.model;

import java.time.LocalDateTime;

public class Spedizione {
    private int idSpedizione;
    private int fkOrdine;
    private String codiceTracking;
    // In Preparazione / Spedito / In Transito / Consegnato / Reso
    private String stato;
    private LocalDateTime dataPartenza;
    private LocalDateTime dataConsegna;

    public Spedizione() {}

    public int getIdSpedizione() { return idSpedizione; }
    public int getFkOrdine() { return fkOrdine; }
    public String getCodiceTracking() { return codiceTracking;}
    public String getStato() { return stato; }
    public LocalDateTime getDataPartenza() { return dataPartenza; }
    public LocalDateTime getDataConsegna() { return dataConsegna; }

    public void setIdSpedizione(int v) { this.idSpedizione = v; }
    public void setFkOrdine(int v) { this.fkOrdine = v; }
    public void setCodiceTracking(String v) { this.codiceTracking = v; }
    public void setStato(String v) { this.stato = v; }
    public void setDataPartenza(LocalDateTime v){ this.dataPartenza = v; }
    public void setDataConsegna(LocalDateTime v){ this.dataConsegna = v; }

    @Override public String toString() {
        return String.format("[%d] ordine:%d  tracking:%s  stato:%s", idSpedizione, fkOrdine, codiceTracking != null ? codiceTracking : " - ", stato);
    }
}
