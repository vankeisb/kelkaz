package agregator.immo.agregators.logicimmo

import agregator.core.Agregator
import agregator.immo.cartridges.LogicImmoCartridge

public class LogicImmoAgregator extends Agregator {

  protected void createCartridges(List cartridges) {
    cartridges << new LogicImmoCartridge(this)
  }

}