package GestionaleUtenza.observer;

import GestionaleUtenza.dao.ProdottoDAO;
import GestionaleUtenza.model.DettaglioOrdine;
import GestionaleUtenza.model.Ordine;

import java.sql.SQLException;
import java.util.List;

/**
 * Quando un ordine diventa "Pagato" decrementa lo stock dei prodotti
 * presenti nei dettagli
 */
public class AggiornaStockObserver implements OrdineObserver {

    private final ProdottoDAO prodottoDAO;

    public AggiornaStockObserver(ProdottoDAO prodottoDAO) {
        this.prodottoDAO = prodottoDAO;
    }

    @Override
    public void aggiorna(Ordine ordine, String nuovoStato) {
        if (!"Pagato".equals(nuovoStato))
            return;

        List<DettaglioOrdine> dettagli = ordine.getDettagli();
        if (dettagli == null || dettagli.isEmpty())
            return;

        for (DettaglioOrdine d : dettagli) {
            if (d.getFkProdotto() == null)
                continue;
            try {
                prodottoDAO.decrementaStock(d.getFkProdotto(), d.getQuantita());
                System.out.printf("[STOCK] Prodotto #%d: -%-3d unità%n", d.getFkProdotto(), d.getQuantita());
            } catch (SQLException e) {
                System.err.printf("[STOCK] Attenzione: %s%n", e.getMessage());
            }
        }
    }
}
