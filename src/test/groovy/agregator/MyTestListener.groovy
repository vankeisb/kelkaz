package agregator

import agregator.core.CartridgeEvent.ResultEvent
import agregator.core.AgregatorEvent.AgregatorCartridgeEvent
import agregator.core.AgregatorEvent
import agregator.core.AgregatorListener

public class MyTestListener implements AgregatorListener {

  private def events = []

  public void onEvent(AgregatorEvent event) {
    println event
    events << event
  }

  def getResults() {
    def results = []
    events.each{ evt ->
      if (evt instanceof AgregatorCartridgeEvent && evt.cartridgeEvent instanceof ResultEvent) {
        results << evt.cartridgeEvent.result
      }
    }
    return results
  }


}