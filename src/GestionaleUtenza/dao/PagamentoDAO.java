package GestionaleUtenza.dao;

import GestionaleUtenza.db.DatabaseConnection;
import GestionaleUtenza.model.Pagamento;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PagamentoDAO {

    private Connection getConn() {
        return DatabaseConnection.getInstance().getConnection();
    }

    public int insert(Pagamento p) {
        try {
            PreparedStatement ps = getConn().prepareStatement(
                    "INSERT INTO Pagamenti (fk_ordine, importo, metodo, stato) VALUES (?,?,?,?)",
                    Statement.RETURN_GENERATED_KEYS);
            ps.setInt   (1, p.getFkOrdine());
            ps.setDouble(2, p.getImporto());
            ps.setString(3, p.getMetodo());
            ps.setString(4, p.getStato());
            ps.executeUpdate();
            ResultSet gen = ps.getGeneratedKeys();
            if (gen.next()) return gen.getInt(1);
        } catch (SQLException e) { e.printStackTrace(); }
        return -1;
    }

    public void aggiornaStato(int idPagamento, String stato) {
        try {
            PreparedStatement ps = getConn().prepareStatement(
                    "UPDATE Pagamenti SET stato=? WHERE id_pagamento=?");
            ps.setString(1, stato);
            ps.setInt   (2, idPagamento);
            ps.executeUpdate();
        } catch (SQLException e) { e.printStackTrace(); }
    }

    public List<Pagamento> findAll() {
        List<Pagamento> lista = new ArrayList<>();
        try {
            Statement st = getConn().createStatement();
            ResultSet rs = st.executeQuery("SELECT * FROM Pagamenti ORDER BY data DESC");
            while (rs.next()) {
                Pagamento p = new Pagamento();
                p.setIdPagamento(rs.getInt("id_pagamento"));
                p.setFkOrdine   (rs.getInt("fk_ordine"));
                p.setImporto    (rs.getDouble("importo"));
                p.setMetodo     (rs.getString("metodo"));
                p.setStato      (rs.getString("stato"));
                Timestamp ts = rs.getTimestamp("data");
                if (ts != null) p.setData(ts.toLocalDateTime());
                lista.add(p);
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return lista;
    }
}