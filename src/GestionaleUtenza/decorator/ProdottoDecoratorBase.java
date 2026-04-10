package GestionaleUtenza.decorator;

/** Decorator astratto: delega al componente wrapped */
public abstract class ProdottoDecoratorBase implements ProdottoConfigurabile {

    protected final ProdottoConfigurabile wrapped;

    protected ProdottoDecoratorBase(ProdottoConfigurabile wrapped) {
        this.wrapped = wrapped;
    }

    @Override
    public String getNome() {
        return wrapped.getNome();
    }

    @Override
    public double getPrezzo() {
        return wrapped.getPrezzo();
    }

    @Override
    public String getDescrizione() {
        return wrapped.getDescrizione();
    }
}
