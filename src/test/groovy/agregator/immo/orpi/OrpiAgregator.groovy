package agregator.immo.orpi

import agregator.core.Agregator
import agregator.immo.cartridges.OrpiCartridge

public class OrpiAgregator extends Agregator {

  protected void createCartridges(List cartridges) {
    cartridges << new OrpiCartridge(this)
  }
}