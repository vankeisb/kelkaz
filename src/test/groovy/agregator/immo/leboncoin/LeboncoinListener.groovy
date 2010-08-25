package agregator.immo.leboncoin

import agregator.core.AgregatorListener
import agregator.core.AgregatorEvent
import agregator.core.CartridgeEvent
import agregator.core.CartridgeEvent.ResultEvent


public class LeboncoinListener implements AgregatorListener {

  def events = []

  public void onEvent(AgregatorEvent event) {
    events << event
  }

  def getResults(){
    return events
  }


}