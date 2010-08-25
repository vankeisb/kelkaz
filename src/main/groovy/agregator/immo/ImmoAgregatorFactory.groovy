package agregator.immo

import agregator.core.AgregatorFactory
import agregator.core.Agregator

public class ImmoAgregatorFactory extends AgregatorFactory {

  def ImmoAgregatorFactory() {
    super("Immo", "Blah blah");
  }

  public Agregator create() {
    return new ImmoAgregator()
  }


}