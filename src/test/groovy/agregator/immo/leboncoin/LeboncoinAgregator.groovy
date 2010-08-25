package agregator.immo.leboncoin

import agregator.core.Agregator
import agregator.immo.cartridges.LeboncoinCartridge

public class LeboncoinAgregator extends Agregator {

  protected void createCartridges(List cartridges) {
    cartridges << new LeboncoinCartridge(this)
  }

}