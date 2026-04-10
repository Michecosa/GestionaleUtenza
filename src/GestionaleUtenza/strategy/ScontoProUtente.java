package GestionaleUtenza.strategy;

/** 10% fisso riservato agli utenti con account Pro */
public class ScontoProUtente implements ScontoStrategy {

    private static final double PERCENTUALE = 10.0;

    @Override
    public double calcola(double prezzoLordo) {
        return prezzoLordo * PERCENTUALE / 100.0;
    }

    @Override
    public String descrizione() {
        return "Sconto Pro 10%";
    }
}
