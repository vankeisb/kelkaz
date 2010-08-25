package agregator.search

import agregator.core.AgregatorEvent
import agregator.core.AgregatorListener

public class DummyAgregatorListener implements AgregatorListener {

    def events = []

    public void onEvent(AgregatorEvent e) {
        println e
        events << e
    }

}