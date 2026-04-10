package GestionaleUtenza.model;

public class Prodotto {
    private int idProdotto;
    private String nome;
    private String descrizione;
    private double prezzoBase;
    private int stock;
    private Integer fkCategoria;
    private boolean attivo;
    private Categoria categoria;

    public Prodotto() {
    }

    public int getIdProdotto() {
        return idProdotto;
    }

    public String getNome() {
        return nome;
    }

    public String getDescrizione() {
        return descrizione;
    }

    public double getPrezzoBase() {
        return prezzoBase;
    }

    public int getStock() {
        return stock;
    }

    public Integer getFkCategoria() {
        return fkCategoria;
    }

    public boolean isAttivo() {
        return attivo;
    }

    public Categoria getCategoria() {
        return categoria;
    }

    public void setIdProdotto(int v) {
        this.idProdotto = v;
    }

    public void setNome(String v) {
        this.nome = v;
    }

    public void setDescrizione(String v) {
        this.descrizione = v;
    }

    public void setPrezzoBase(double v) {
        this.prezzoBase = v;
    }

    public void setStock(int v) {
        this.stock = v;
    }

    public void setFkCategoria(Integer v) {
        this.fkCategoria = v;
    }

    public void setAttivo(boolean v) {
        this.attivo = v;
    }

    public void setCategoria(Categoria v) {
        this.categoria = v;
    }

    @Override
    public String toString() {
        String cat = categoria != null ? categoria.getNome() : "—";
        return String.format("[%d] %-30s  EUR%6.2f  stock:%-4d  cat:%s", idProdotto, nome, prezzoBase, stock, cat);
    }
}