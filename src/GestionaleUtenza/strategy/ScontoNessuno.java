package GestionaleUtenza.strategy;

/** Nessuno sconto */
public class ScontoNessuno implements ScontoStrategy {
    @Override
    public double calcola(double prezzoLordo) {
        return 0.0;
    }

    @Override
    public String descrizione() {
        return "Nessuno sconto";
    }
}
