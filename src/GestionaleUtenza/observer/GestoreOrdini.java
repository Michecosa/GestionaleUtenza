package GestionaleUtenza.observer;

import GestionaleUtenza.dao.OrdineDAO;
import GestionaleUtenza.model.Ordine;

import java.util.ArrayList;
import java.util.List;

/**
 * Soggetto concreto: gestisce la lista di observer e propaga i cambi stato
 * Aggiorna anche il DB tramite OrdineDAO
 */
public class GestoreOrdini implements OrdineEventSource {

    private final List<OrdineObserver> observers = new ArrayList<>();
    private final OrdineDAO ordineDAO;

    public GestoreOrdini(OrdineDAO ordineDAO) {
        this.ordineDAO = ordineDAO;
    }

    // registrazione 
    @Override
    public void aggiungiObserver(OrdineObserver o) {
        observers.add(o);
    }

    @Override
    public void rimuoviObserver(OrdineObserver o) {
        observers.remove(o);
    }

    // notifica 
    @Override
    public void notificaObserver(Ordine ordine, String nuovoStato) {
        for (OrdineObserver obs : observers) {
            obs.aggiorna(ordine, nuovoStato);
        }
    }

    /**
     * Cambia lo stato dell'ordine: persiste su DB e notifica tutti gli observer
     */
    public void cambiaStato(Ordine ordine, String nuovoStato) {
        ordineDAO.aggiornaStato(ordine.getIdOrdine(), nuovoStato);
        ordine.setStato(nuovoStato);
        notificaObserver(ordine, nuovoStato);
    }
}
