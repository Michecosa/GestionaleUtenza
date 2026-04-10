package GestionaleUtenza.model;

public class Utente {
    private int idUtente;
    private String nome;
    private String email;
    private String passwordHash;
    private String tipoAccount; // Normal/Pro
    private int fkRuolo;
    private boolean attivo;
    private Ruolo ruolo;

    public Utente() {}

    public int getIdUtente() {return idUtente;}
    public String getNome() {return nome;}
    public String getEmail() {return email;}
    public String getPasswordHash() {return passwordHash;}
    public String getTipoAccount() {return tipoAccount;}
    public int getFkRuolo() {return fkRuolo;}
    public boolean isAttivo() {return attivo;}
    public Ruolo getRuolo() {return ruolo;}

    public void setIdUtente(int v) {this.idUtente = v;}
    public void setNome(String v) {this.nome = v;}
    public void setEmail(String v) {this.email = v;}
    public void setPasswordHash(String v) {this.passwordHash = v;}
    public void setTipoAccount(String v) {this.tipoAccount = v;}
    public void setFkRuolo(int v) {this.fkRuolo = v;}
    public void setAttivo(boolean v) {this.attivo = v;}
    public void setRuolo(Ruolo v) {this.ruolo = v;}

    public int getLivelloRuolo() {
        return ruolo != null ? ruolo.getLivello() : 0;
    }

    @Override
    public String toString() {
        String r = ruolo != null ? ruolo.getNomeRuolo() : "?";
        return "[" + idUtente + "] " + nome + " <" + email + ">  " + tipoAccount + "  " + r;
    }
}