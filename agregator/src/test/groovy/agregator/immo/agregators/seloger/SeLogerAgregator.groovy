package agregator.immo.agregators.seloger

import agregator.core.Agregator
import agregator.immo.cartridges.SeLogerCartridge

public class SeLogerAgregator extends Agregator {

  protected void createCartridges(List cartridges) {
    cartridges << new SeLogerCartridge(this)
  }

}