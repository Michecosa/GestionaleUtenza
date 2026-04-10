package GestionaleUtenza.model;

public class TipoSpedizione {
    private int idTipoSped;
    private String nome;
    private String corriere;
    private double costo;
    private int giorniStima;

    public TipoSpedizione() {}

    public int getIdTipoSped() {return idTipoSped;}
    public String getNome() {return nome;}
    public String getCorriere() {return corriere;}
    public double getCosto() {return costo;}
    public int getGiorniStima() {return giorniStima;}

    public void setIdTipoSped(int v) {this.idTipoSped = v;}
    public void setNome(String v) {this.nome = v;}
    public void setCorriere(String v) {this.corriere = v;}
    public void setCosto(double v) {this.costo = v;}
    public void setGiorniStima(int v) {this.giorniStima = v;}

    @Override
    public String toString() {
        String c = corriere != null ? corriere : "—";
        return String.format("[%d] %-22s €%5.2f  %d gg  %s", idTipoSped, nome, costo, giorniStima, c);
    }
}