package GestionaleUtenza.model;

public class Ruolo {
    private int idRuolo;
    private String nomeRuolo;
    private int livello;

    public Ruolo() {}
    public Ruolo(int idRuolo, String nomeRuolo, int livello) {
        this.idRuolo = idRuolo;
        this.nomeRuolo = nomeRuolo;
        this.livello = livello;
    }

    public int getIdRuolo() {return idRuolo;}
    public String getNomeRuolo() {return nomeRuolo;}
    public int getLivello() {return livello;}

    public void setIdRuolo(int v) { this.idRuolo = v;}
    public void setNomeRuolo(String v) { this.nomeRuolo = v;}
    public void setLivello(int v) { this.livello = v;}

    @Override
    public String toString() {
        return "[" + idRuolo + "] " + nomeRuolo + " (livello " + livello + ")";
    }
}