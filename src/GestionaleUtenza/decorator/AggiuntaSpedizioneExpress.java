package GestionaleUtenza.decorator;

/** Aggiunge spedizione express prioritaria (+ EUR8.00). */
public class AggiuntaSpedizioneExpress extends ProdottoDecoratorBase {

    private static final double COSTO = 8.00;

    public AggiuntaSpedizioneExpress(ProdottoConfigurabile wrapped) {
        super(wrapped);
    }

    @Override
    public double getPrezzo() {
        return wrapped.getPrezzo() + COSTO;
    }

    @Override
    public String getDescrizione() {
        return wrapped.getDescrizione() + " + Spedizione Express";
    }
}
