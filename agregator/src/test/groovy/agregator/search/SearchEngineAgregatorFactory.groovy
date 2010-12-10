package agregator.search

import agregator.core.AgregatorFactory
import agregator.core.Agregator
import agregator.search.SearchEngineAgregator

public class SearchEngineAgregatorFactory extends AgregatorFactory {

  def SearchEngineAgregatorFactory() {
    super("SearchAgregator", "Agregates results from various search engines...")
  }

  public Agregator create() {
    return new SearchEngineAgregator()
  }


}