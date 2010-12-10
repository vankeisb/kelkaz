package agregator.immo.agregators.paruvendu

import agregator.core.Agregator
import agregator.immo.cartridges.ParuVenduCartridge

public class ParuVenduAgregator extends Agregator {

  protected void createCartridges(List cartridges) {
    cartridges << new ParuVenduCartridge(this)
  }

}