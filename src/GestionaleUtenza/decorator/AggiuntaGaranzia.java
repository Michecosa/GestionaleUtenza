package GestionaleUtenza.decorator;

/** Aggiunge garanzia estesa (+ EUR9.99). */
public class AggiuntaGaranzia extends ProdottoDecoratorBase {

    private static final double COSTO = 9.99;

    public AggiuntaGaranzia(ProdottoConfigurabile wrapped) {
        super(wrapped);
    }

    @Override
    public double getPrezzo() {
        return wrapped.getPrezzo() + COSTO;
    }

    @Override
    public String getDescrizione() {
        return wrapped.getDescrizione() + " + Garanzia 2 anni";
    }
}
