package agregator.search

import agregator.core.Agregator
import agregator.core.Cartridge
import agregator.search.GoogleCartridge

public class SearchEngineAgregator extends Agregator {

  protected void createCartridges(List cartridges) {
    cartridges << new GoogleCartridge(this)
  }


}