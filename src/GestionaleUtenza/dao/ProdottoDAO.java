package GestionaleUtenza.dao;

import GestionaleUtenza.db.DatabaseConnection;
import GestionaleUtenza.model.Categoria;
import GestionaleUtenza.model.Prodotto;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ProdottoDAO {

    private Connection conn() throws SQLException {
        return DatabaseConnection.getInstance().getConnection();
    }

    // findAll attivi
    public List<Prodotto> findAllAttivi() throws SQLException {
        return eseguiQuery("""
                SELECT p.*, c.nome AS cat_nome
                FROM Prodotti p
                LEFT JOIN Categorie_Prodotto c ON p.fk_categoria = c.id_categoria
                WHERE p.attivo = TRUE
                ORDER BY p.id_prodotto""");
    }

    public List<Prodotto> findAll() throws SQLException {
        return eseguiQuery("""
                SELECT p.*, c.nome AS cat_nome
                FROM Prodotti p
                LEFT JOIN Categorie_Prodotto c ON p.fk_categoria = c.id_categoria
                ORDER BY p.id_prodotto""");
    }

    // findById
    public Optional<Prodotto> findById(int id) throws SQLException {
        String sql = """
                SELECT p.*, c.nome AS cat_nome
                FROM Prodotti p
                LEFT JOIN Categorie_Prodotto c ON p.fk_categoria = c.id_categoria
                WHERE p.id_prodotto = ?""";
        try (PreparedStatement ps = conn().prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return Optional.of(mapRow(rs));
            }
        }
        return Optional.empty();
    }

    // findByCategoria
    public List<Prodotto> findByCategoria(int idCategoria) throws SQLException {
        String sql = """
                SELECT p.*, c.nome AS cat_nome
                FROM Prodotti p
                LEFT JOIN Categorie_Prodotto c ON p.fk_categoria = c.id_categoria
                WHERE p.fk_categoria = ? AND p.attivo = TRUE
                ORDER BY p.id_prodotto""";
        List<Prodotto> lista = new ArrayList<>();
        try (PreparedStatement ps = conn().prepareStatement(sql)) {
            ps.setInt(1, idCategoria);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) lista.add(mapRow(rs));
            }
        }
        return lista;
    }

    // insert
    public int insert(Prodotto p) throws SQLException {
        String sql = """
                INSERT INTO Prodotti (nome, descrizione, prezzo_base, stock, fk_categoria, attivo)
                VALUES (?, ?, ?, ?, ?, ?)""";
        try (PreparedStatement ps = conn().prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, p.getNome());
            ps.setString(2, p.getDescrizione());
            ps.setDouble(3, p.getPrezzoBase());
            ps.setInt   (4, p.getStock());
            if (p.getFkCategoria() != null)
                ps.setInt(5, p.getFkCategoria());
            else
                ps.setNull(5, Types.INTEGER);
            ps.setBoolean(6, p.isAttivo());
            ps.executeUpdate();
            try (ResultSet gen = ps.getGeneratedKeys()) {
                if (gen.next()) return gen.getInt(1);
            }
        }
        return -1;
    }

    // update
    public void update(Prodotto p) throws SQLException {
        String sql = """
                UPDATE Prodotti
                SET nome=?, descrizione=?, prezzo_base=?, stock=?, fk_categoria=?, attivo=?
                WHERE id_prodotto=?""";
        try (PreparedStatement ps = conn().prepareStatement(sql)) {
            ps.setString(1, p.getNome());
            ps.setString(2, p.getDescrizione());
            ps.setDouble(3, p.getPrezzoBase());
            ps.setInt   (4, p.getStock());
            if (p.getFkCategoria() != null)
                ps.setInt(5, p.getFkCategoria());
            else
                ps.setNull(5, Types.INTEGER);
            ps.setBoolean(6, p.isAttivo());
            ps.setInt   (7, p.getIdProdotto());
            ps.executeUpdate();
        }
    }

    // decrementaStock
    public void decrementaStock(int idProdotto, int quantita) throws SQLException {
        String sql = "UPDATE Prodotti SET stock = stock - ? WHERE id_prodotto = ? AND stock >= ?";
        try (PreparedStatement ps = conn().prepareStatement(sql)) {
            ps.setInt(1, quantita);
            ps.setInt(2, idProdotto);
            ps.setInt(3, quantita);
            int righe = ps.executeUpdate();
            if (righe == 0)
                throw new SQLException("Stock insufficiente per prodotto #" + idProdotto);
        }
    }

    // categorie
    public List<Categoria> findAllCategorie() throws SQLException {
        List<Categoria> lista = new ArrayList<>();
        try (Statement st = conn().createStatement();
             ResultSet rs = st.executeQuery(
                     "SELECT * FROM Categorie_Prodotto ORDER BY id_categoria")) {
            while (rs.next()) {
                lista.add(new Categoria(
                        rs.getInt("id_categoria"),
                        rs.getString("nome")));
            }
        }
        return lista;
    }

    public int insertCategoria(String nome) throws SQLException {
        try (PreparedStatement ps = conn().prepareStatement(
                "INSERT INTO Categorie_Prodotto (nome) VALUES (?)",
                Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, nome);
            ps.executeUpdate();
            try (ResultSet gen = ps.getGeneratedKeys()) {
                if (gen.next()) return gen.getInt(1);
            }
        }
        return -1;
    }

    // helper
    private List<Prodotto> eseguiQuery(String sql) throws SQLException {
        List<Prodotto> lista = new ArrayList<>();
        try (Statement st = conn().createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) lista.add(mapRow(rs));
        }
        return lista;
    }

    private Prodotto mapRow(ResultSet rs) throws SQLException {
        Prodotto p = new Prodotto();
        p.setIdProdotto (rs.getInt("id_prodotto"));
        p.setNome (rs.getString("nome"));
        p.setDescrizione (rs.getString("descrizione"));
        p.setPrezzoBase (rs.getDouble("prezzo_base"));
        p.setStock (rs.getInt("stock"));
        p.setAttivo (rs.getBoolean("attivo"));
        int fkCat = rs.getInt("fk_categoria");
        if (!rs.wasNull()) {
            p.setFkCategoria(fkCat);
            String catNome = rs.getString("cat_nome");
            if (catNome != null) p.setCategoria(new Categoria(fkCat, catNome));
        }
        return p;
    }
}
