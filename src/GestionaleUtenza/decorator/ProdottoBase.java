package GestionaleUtenza.decorator;

import GestionaleUtenza.model.Prodotto;

/** ConcreteComponent: wrappa un Prodotto del model */
public class ProdottoBase implements ProdottoConfigurabile {

    private final Prodotto prodotto;

    public ProdottoBase(Prodotto prodotto) {
        this.prodotto = prodotto;
    }

    @Override
    public String getNome() {
        return prodotto.getNome();
    }

    @Override
    public double getPrezzo() {
        return prodotto.getPrezzoBase();
    }

    @Override
    public String getDescrizione() {
        return prodotto.getNome();
    }
}
