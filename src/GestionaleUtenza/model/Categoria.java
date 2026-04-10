package GestionaleUtenza.model;

public class Categoria {
    private int idCategoria;
    private String nome;

    public Categoria() {}
    public Categoria(int idCategoria, String nome) {
        this.idCategoria = idCategoria;
        this.nome = nome;
    }

    public int getIdCategoria() {return idCategoria;}
    public String getNome() {return nome;}
    public void setIdCategoria(int v) {this.idCategoria = v;}
    public void setNome(String v) {this.nome = v;}

    @Override
    public String toString() { return "[" + idCategoria + "] " + nome; }
}