package GestionaleUtenza.observer;

import GestionaleUtenza.model.Ordine;

/** Soggetto del pattern Observer */
public interface OrdineEventSource {
    void aggiungiObserver(OrdineObserver o);

    void rimuoviObserver(OrdineObserver o);

    void notificaObserver(Ordine ordine, String nuovoStato);
}
