package agregator.immo.orpi

import agregator.core.AgregatorListener
import agregator.core.AgregatorEvent
import agregator.core.CartridgeEvent.ResultEvent
import agregator.core.AgregatorEvent.AgregatorCartridgeEvent

public class TestListener implements AgregatorListener {

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

