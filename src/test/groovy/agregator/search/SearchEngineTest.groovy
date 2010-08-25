package agregator.search

import agregator.search.GoogleCartridge
import agregator.search.DummyCartridgeListener
import agregator.search.SearchEngineCriteria
import agregator.search.DummyAgregatorListener
import agregator.search.SearchEngineAgregator

public class SearchEngineTest extends GroovyTestCase {

  void testGoogleCartridge() {
    def c = new GoogleCartridge()
    def l = new DummyCartridgeListener()
    c.addListener(l)
    c.agregate(new SearchEngineCriteria(query: 'Groovy'))
    assert l.events.size() == 12
  }

  void testGoogleCartridgeInAgregator() {
    def l = new DummyAgregatorListener()
    new SearchEngineAgregator().addListener(l).agregate(new SearchEngineCriteria(query:'Groovy'))
    println l.events.size()
    assert l.events.size() == 14
  }

}