package GestionaleUtenza.model;

public class DettaglioOrdine {
    private int     idDettaglio;
    private int     fkOrdine;
    private Integer fkProdotto;
    private String  descrizioneConfigurata;
    private double  prezzoUnitario;
    private int     quantita;

    // relazione opzionale
    private Prodotto prodotto;

    public DettaglioOrdine() {}

    // getters
    public int getIdDettaglio() {return idDettaglio;}
    public int getFkOrdine() {return fkOrdine;}
    public Integer getFkProdotto() {return fkProdotto;}
    public String getDescrizioneConfigurata() {return descrizioneConfigurata;}
    public double getPrezzoUnitario() {return prezzoUnitario;}
    public int getQuantita() {return quantita;}
    public Prodotto getProdotto() {return prodotto;}

    // setters
    public void setIdDettaglio(int v) {this.idDettaglio = v;}
    public void setFkOrdine(int v) {this.fkOrdine = v;}
    public void setFkProdotto(Integer v) {this.fkProdotto = v;}
    public void setDescrizioneConfigurata(String v) {this.descrizioneConfigurata = v;}
    public void setPrezzoUnitario(double v) {this.prezzoUnitario = v;}
    public void setQuantita(int v) {this.quantita = v;}
    public void setProdotto(Prodotto v) {this.prodotto = v;}

    public double subtotale() { return prezzoUnitario * quantita; }

    @Override public String toString() {
        return String.format("  %-40s x%d  €%.2f  = €%.2f",descrizioneConfigurata, quantita, prezzoUnitario, subtotale());
    }
}
