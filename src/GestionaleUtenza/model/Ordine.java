package GestionaleUtenza.model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Ordine {
    private int idOrdine;
    private String dataCreazione;
    private String stato;
    private double prezzoTotale;
    private double scontoApplicato;
    private String indirizzoSpedizione;
    private int fkUtente;
    private Integer fkTipoSpedizione;

    private TipoSpedizione tipoSpedizione;
    private List<DettaglioOrdine> dettagli = new ArrayList<>();

    public Ordine() {}

    public int getIdOrdine() { return idOrdine; }
    public String getDataCreazione() { return dataCreazione; }
    public String getStato() { return stato; }
    public double getPrezzoTotale() { return prezzoTotale; }
    public double getScontoApplicato() { return scontoApplicato; }
    public String getIndirizzoSpedizione() { return indirizzoSpedizione; }
    public int getFkUtente() { return fkUtente; }
    public Integer getFkTipoSpedizione() { return fkTipoSpedizione; }
    public TipoSpedizione getTipoSpedizione(){ return tipoSpedizione;  }
    public List<DettaglioOrdine> getDettagli(){ return dettagli; }

    public void setIdOrdine(int v) { this.idOrdine = v; }
    public void setDataCreazione(LocalDateTime v) { this.dataCreazione = v; }
    public void setStato(String v) { this.stato = v; }
    public void setPrezzoTotale(double v) { this.prezzoTotale = v; }
    public void setScontoApplicato(double v) { this.scontoApplicato = v; }
    public void setIndirizzoSpedizione(String v) { this.indirizzoSpedizione = v; }
    public void setFkUtente(int v) { this.fkUtente = v; }
    public void setFkTipoSpedizione(Integer v) { this.fkTipoSpedizione = v; }
    public void setTipoSpedizione(TipoSpedizione v){ this.tipoSpedizione = v; }
    public void setDettagli(List<DettaglioOrdine> v){ this.dettagli = v; }

    @Override
    public String toString() {
        return String.format("[%d] %s  €%.2f  sconto:€%.2f  stato:%s", idOrdine, dataCreazione, prezzoTotale, scontoApplicato, stato);
    }
}