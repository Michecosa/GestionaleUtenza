package GestionaleUtenza.dao;

import GestionaleUtenza.db.DatabaseConnection;
import GestionaleUtenza.model.DettaglioOrdine;
import GestionaleUtenza.model.Ordine;
import GestionaleUtenza.model.TipoSpedizione;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class OrdineDAO {

    private Connection conn() throws SQLException {
        return DatabaseConnection.getInstance().getConnection();
    }

    // insertOrdine
    public int insert(Ordine o) throws SQLException {
        String sql = """
                INSERT INTO Ordini
                  (stato, prezzo_totale, sconto_applicato, indirizzo_spedizione,
                   fk_utente, fk_tipo_spedizione)
                VALUES (?, ?, ?, ?, ?, ?)""";
        try (PreparedStatement ps = conn().prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, o.getStato());
            ps.setDouble(2, o.getPrezzoTotale());
            ps.setDouble(3, o.getScontoApplicato());
            ps.setString(4, o.getIndirizzoSpedizione());
            ps.setInt   (5, o.getFkUtente());

            if (o.getFkTipoSpedizione() != null) ps.setInt(6, o.getFkTipoSpedizione());
            else ps.setNull(6, Types.INTEGER);
            
            ps.executeUpdate();
            try (ResultSet gen = ps.getGeneratedKeys()) {
                if (gen.next()) return gen.getInt(1);
            }
        }
        return -1;
    }

    // insertDettaglio
    public int insertDettaglio(DettaglioOrdine d) throws SQLException {
        String sql = """
                INSERT INTO Dettagli_Ordine
                  (fk_ordine, fk_prodotto, descrizione_configurata, prezzo_unitario, quantita)
                VALUES (?, ?, ?, ?, ?)""";
        try (PreparedStatement ps = conn().prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt   (1, d.getFkOrdine());
            if (d.getFkProdotto() != null) ps.setInt(2, d.getFkProdotto());
            else ps.setNull(2, Types.INTEGER);
            ps.setString(3, d.getDescrizioneConfigurata());
            ps.setDouble(4, d.getPrezzoUnitario());
            ps.setInt   (5, d.getQuantita());
            ps.executeUpdate();
            try (ResultSet gen = ps.getGeneratedKeys()) {
                if (gen.next()) return gen.getInt(1);
            }
        }
        return -1;
    }

    // aggiornaStato
    public void aggiornaStato(int idOrdine, String nuovoStato) throws SQLException {
        try (PreparedStatement ps = conn().prepareStatement(
                "UPDATE Ordini SET stato=? WHERE id_ordine=?")) {
            ps.setString(1, nuovoStato);
            ps.setInt   (2, idOrdine);
            ps.executeUpdate();
        }
    }

    // aggiornaPrezzo (dopo calcolo definitivo)
    public void aggiornaTotale(int idOrdine, double totale, double sconto) throws SQLException {
        try (PreparedStatement ps = conn().prepareStatement(
                "UPDATE Ordini SET prezzo_totale=?, sconto_applicato=? WHERE id_ordine=?")) {
            ps.setDouble(1, totale);
            ps.setDouble(2, sconto);
            ps.setInt   (3, idOrdine);
            ps.executeUpdate();
        }
    }

    // findById
    public Optional<Ordine> findById(int id) throws SQLException {
        String sql = """
                SELECT o.*, ts.nome AS ts_nome, ts.corriere, ts.costo, ts.giorni_stima
                FROM Ordini o
                LEFT JOIN Tipi_Spedizione ts ON o.fk_tipo_spedizione = ts.id_tipo_sped
                WHERE o.id_ordine = ?""";
        try (PreparedStatement ps = conn().prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Ordine o = mapRow(rs);
                    o.setDettagli(findDettagliByOrdine(id));
                    return Optional.of(o);
                }
            }
        }
        return Optional.empty();
    }

    // findByUtente
    public List<Ordine> findByUtente(int idUtente) throws SQLException {
        List<Ordine> lista = new ArrayList<>();
        String sql = """
                SELECT o.*, ts.nome AS ts_nome, ts.corriere, ts.costo, ts.giorni_stima
                FROM Ordini o
                LEFT JOIN Tipi_Spedizione ts ON o.fk_tipo_spedizione = ts.id_tipo_sped
                WHERE o.fk_utente = ?
                ORDER BY o.data_creazione DESC""";
        try (PreparedStatement ps = conn().prepareStatement(sql)) {
            ps.setInt(1, idUtente);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) lista.add(mapRow(rs));
            }
        }
        return lista;
    }

    // findAll
    public List<Ordine> findAll() throws SQLException {
        List<Ordine> lista = new ArrayList<>();
        String sql = """
                SELECT o.*, ts.nome AS ts_nome, ts.corriere, ts.costo, ts.giorni_stima
                FROM Ordini o
                LEFT JOIN Tipi_Spedizione ts ON o.fk_tipo_spedizione = ts.id_tipo_sped
                ORDER BY o.data_creazione DESC""";
        try (Statement st = conn().createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) lista.add(mapRow(rs));
        }
        return lista;
    }

    // dettagli
    public List<DettaglioOrdine> findDettagliByOrdine(int idOrdine) throws SQLException {
        List<DettaglioOrdine> lista = new ArrayList<>();
        String sql = """
                SELECT d.*, p.nome AS p_nome
                FROM Dettagli_Ordine d
                LEFT JOIN Prodotti p ON d.fk_prodotto = p.id_prodotto
                WHERE d.fk_ordine = ?""";
        try (PreparedStatement ps = conn().prepareStatement(sql)) {
            ps.setInt(1, idOrdine);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) lista.add(mapDettaglio(rs));
            }
        }
        return lista;
    }

    // tipi spedizione
    public List<TipoSpedizione> findTipiSpedizione() throws SQLException {
        List<TipoSpedizione> lista = new ArrayList<>();
        try (Statement st = conn().createStatement();
             ResultSet rs = st.executeQuery(
                     "SELECT * FROM Tipi_Spedizione ORDER BY id_tipo_sped")) {
            while (rs.next()) lista.add(mapTipoSped(rs));
        }
        return lista;
    }

    // mapping
    private Ordine mapRow(ResultSet rs) throws SQLException {
        Ordine o = new Ordine();
        o.setIdOrdine           (rs.getInt("id_ordine"));
        Timestamp ts = rs.getTimestamp("data_creazione");
        if (ts != null) o.setDataCreazione(ts.toLocalDateTime());
        o.setStato              (rs.getString("stato"));
        o.setPrezzoTotale       (rs.getDouble("prezzo_totale"));
        o.setScontoApplicato    (rs.getDouble("sconto_applicato"));
        o.setIndirizzoSpedizione(rs.getString("indirizzo_spedizione"));
        o.setFkUtente           (rs.getInt("fk_utente"));
        int fkTs = rs.getInt("fk_tipo_spedizione");
        if (!rs.wasNull()) {
            o.setFkTipoSpedizione(fkTs);
            TipoSpedizione tipo = new TipoSpedizione();
            tipo.setIdTipoSped (fkTs);
            tipo.setNome       (rs.getString("ts_nome"));
            tipo.setCorriere   (rs.getString("corriere"));
            tipo.setCosto      (rs.getDouble("costo"));
            tipo.setGiorniStima(rs.getInt("giorni_stima"));
            o.setTipoSpedizione(tipo);
        }
        return o;
    }

    private DettaglioOrdine mapDettaglio(ResultSet rs) throws SQLException {
        DettaglioOrdine d = new DettaglioOrdine();
        d.setIdDettaglio           (rs.getInt("id_dettaglio"));
        d.setFkOrdine              (rs.getInt("fk_ordine"));
        int fkP = rs.getInt("fk_prodotto");
        if (!rs.wasNull()) d.setFkProdotto(fkP);
        d.setDescrizioneConfigurata(rs.getString("descrizione_configurata"));
        d.setPrezzoUnitario        (rs.getDouble("prezzo_unitario"));
        d.setQuantita              (rs.getInt("quantita"));
        return d;
    }

    private TipoSpedizione mapTipoSped(ResultSet rs) throws SQLException {
        TipoSpedizione t = new TipoSpedizione();
        t.setIdTipoSped (rs.getInt("id_tipo_sped"));
        t.setNome       (rs.getString("nome"));
        t.setCorriere   (rs.getString("corriere"));
        t.setCosto      (rs.getDouble("costo"));
        t.setGiorniStima(rs.getInt("giorni_stima"));
        return t;
    }
}
