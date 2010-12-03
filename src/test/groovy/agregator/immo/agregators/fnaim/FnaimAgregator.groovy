package agregator.immo.agregators.fnaim

import agregator.core.Agregator
import agregator.immo.cartridges.FnaimCartridge

class FnaimAgregator extends Agregator {

  protected void createCartridges(List cartridges) {
    cartridges << new FnaimCartridge(this)
  }


}
