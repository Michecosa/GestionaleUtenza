package GestionaleUtenza.strategy;

/** Sconto percentuale configurabile */
public class ScontoPercentuale implements ScontoStrategy {

    private final double percentuale; // es. 15.0 -> 15%

    public ScontoPercentuale(double percentuale) {
        if (percentuale < 0 || percentuale > 100)
            throw new IllegalArgumentException("Percentuale non valida: " + percentuale);
        this.percentuale = percentuale;
    }

    @Override
    public double calcola(double prezzoLordo) {
        return prezzoLordo * percentuale / 100.0;
    }

    @Override
    public String descrizione() {
        return String.format("Sconto %.0f%%", percentuale);
    }
}
