package GestionaleUtenza.decorator;

/** Aggiunge confezione regalo (+ EUR4.90). */
public class AggiuntaConfezioneGift extends ProdottoDecoratorBase {

    private static final double COSTO = 4.90;

    public AggiuntaConfezioneGift(ProdottoConfigurabile wrapped) {
        super(wrapped);
    }

    @Override
    public double getPrezzo() {
        return wrapped.getPrezzo() + COSTO;
    }

    @Override
    public String getDescrizione() {
        return wrapped.getDescrizione() + " + Confezione regalo";
    }
}
