package agregator.search

import agregator.core.CartridgeEvent
import agregator.core.CartridgeListener

public class DummyCartridgeListener implements CartridgeListener {

  def events = []

  public void onEvent(CartridgeEvent e) {
    events << e
    println "evt : $e"
  }


}