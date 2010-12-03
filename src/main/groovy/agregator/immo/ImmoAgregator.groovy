package agregator.immo

import agregator.core.Agregator
import agregator.core.Cartridge
import agregator.immo.cartridges.ParuVenduCartridge
import agregator.immo.cartridges.LeboncoinCartridge
import agregator.immo.cartridges.SeLogerCartridge
import agregator.immo.cartridges.OrpiCartridge
import agregator.immo.cartridges.PAPCartridge
import agregator.immo.cartridges.MockImmoCartridgeForUITests
import agregator.immo.cartridges.FnaimCartridge
import agregator.immo.cartridges.LogicImmoCartridge

public class ImmoAgregator extends Agregator {

  protected void createCartridges(List cartridges) {
//    cartridges << new ParuVenduCartridge(this)
//    cartridges << new SeLogerCartridge(this)
//    cartridges << new FnaimCartridge(this)
//    cartridges << new LeboncoinCartridge(this)
//    cartridges << new LogicImmoCartridge(this)
    cartridges << new MockImmoCartridgeForUITests("www.foobar.com", this)
//    cartridges << new MockImmoCartridgeForUITests("www.yeah.fr", this)
//    cartridges << new MockImmoCartridgeForUITests("www.shootme.com", this)
  }


}