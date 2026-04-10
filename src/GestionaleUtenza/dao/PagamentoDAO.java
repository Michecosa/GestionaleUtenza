package GestionaleUtenza.dao;

import GestionaleUtenza.db.DatabaseConnection;
import GestionaleUtenza.model.Pagamento;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PagamentoDAO {

    private Connection conn() throws SQLException {
        return DatabaseConnection.getInstance().getConnection();
    }

    public int insert(Pagamento p) throws SQLException {
        String sql = """
                INSERT INTO Pagamenti (fk_ordine, importo, metodo, stato)
                VALUES (?, ?, ?, ?)""";
        try (PreparedStatement ps = conn().prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt   (1, p.getFkOrdine());
            ps.setDouble(2, p.getImporto());
            ps.setString(3, p.getMetodo());
            ps.setString(4, p.getStato());
            ps.executeUpdate();
            try (ResultSet gen = ps.getGeneratedKeys()) {
                if (gen.next()) return gen.getInt(1);
            }
        }
        return -1;
    }

    public void aggiornaStato(int idPagamento, String stato) throws SQLException {
        try (PreparedStatement ps = conn().prepareStatement(
                "UPDATE Pagamenti SET stato=? WHERE id_pagamento=?")) {
            ps.setString(1, stato);
            ps.setInt   (2, idPagamento);
            ps.executeUpdate();
        }
    }

    public List<Pagamento> findByOrdine(int idOrdine) throws SQLException {
        List<Pagamento> lista = new ArrayList<>();
        try (PreparedStatement ps = conn().prepareStatement(
                "SELECT * FROM Pagamenti WHERE fk_ordine=? ORDER BY data")) {
            ps.setInt(1, idOrdine);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) lista.add(mapRow(rs));
            }
        }
        return lista;
    }

    public List<Pagamento> findAll() throws SQLException {
        List<Pagamento> lista = new ArrayList<>();
        try (Statement st = conn().createStatement();
             ResultSet rs = st.executeQuery(
                     "SELECT * FROM Pagamenti ORDER BY data DESC")) {
            while (rs.next()) lista.add(mapRow(rs));
        }
        return lista;
    }

    private Pagamento mapRow(ResultSet rs) throws SQLException {
        Pagamento p = new Pagamento();
        p.setIdPagamento(rs.getInt("id_pagamento"));
        p.setFkOrdine (rs.getInt("fk_ordine"));
        p.setImporto (rs.getDouble("importo"));
        p.setMetodo (rs.getString("metodo"));
        p.setStato (rs.getString("stato"));
        Timestamp ts = rs.getTimestamp("data");
        if (ts != null) p.setData(ts.toLocalDateTime());
        return p;
    }
}
