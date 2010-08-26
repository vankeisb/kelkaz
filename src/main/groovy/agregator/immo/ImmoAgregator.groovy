package agregator.immo

import agregator.core.Agregator
import agregator.core.Cartridge
import agregator.immo.cartridges.ParuVenduCartridge
import agregator.immo.cartridges.LeboncoinCartridge
import agregator.immo.cartridges.SeLogerCartridge
import agregator.immo.cartridges.OrpiCartridge
import agregator.immo.cartridges.PAPCartridge
import agregator.immo.cartridges.MockImmoCartridgeForUITests

public class ImmoAgregator extends Agregator {

  protected void createCartridges(List cartridges) {
    cartridges << new ParuVenduCartridge(this)
    cartridges << new MockImmoCartridgeForUITests(this)
//    cartridges << new LeboncoinCartridge(this)
//    cartridges << new SeLogerCartridge(this)
//    cartridges << new OrpiCartridge(this)
//    cartridges << new PAPCartridge(this)

  }


}