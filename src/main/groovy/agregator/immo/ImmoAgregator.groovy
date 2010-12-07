package agregator.immo

import agregator.core.Agregator
import agregator.immo.cartridges.*

public class ImmoAgregator extends Agregator {

  protected void createCartridges(List cartridges) {
    cartridges << new ParuVenduCartridge(this)
    cartridges << new SeLogerCartridge(this)
    cartridges << new FnaimCartridge(this)
    cartridges << new LeboncoinCartridge(this)
    cartridges << new LogicImmoCartridge(this)

    cartridges << new OrpiCartridge(this)
    cartridges << new PAPCartridge(this)

//    cartridges << new MockImmoCartridgeForUITests("www.foobar.com", this)
//    cartridges << new MockImmoCartridgeForUITests("www.yeah.fr", this)
//    cartridges << new MockImmoCartridgeForUITests("www.shootme.com", this)
  }


}