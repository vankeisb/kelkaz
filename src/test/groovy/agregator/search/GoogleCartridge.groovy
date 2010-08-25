package agregator.search

import com.gargoylesoftware.htmlunit.WebClient
import agregator.core.Cartridge
import agregator.search.SearchEngineResult
import agregator.core.Agregator

class GoogleCartridge extends Cartridge {

    private def webClient;
    private def resultsIterator = null;

  def GoogleCartridge(Agregator a) {
    super("Google", a);
  }

  protected void doAgregate() {
    webClient = new WebClient()
    def page = webClient.getPage('http://www.google.com')
    def form = page.getFormByName('f')
    def field = form.getInputByName('q')
    field.setValueAttribute(criteria.query)
    def button = form.getInputByName('btnG')
    def result = button.click()

    // grab all li class 'g'
    int nbEvents = 0
    def liNodes = result.getElementsByTagName('li')
    liNodes.each { liNode ->
      if (liNode.getAttribute('class')=~/g/) {
        // search result, convert to S.E.R
        def aNode = liNode.getHtmlElementsByTagName('a')[0]
        String url = aNode.getAttribute('href')
        String title = aNode.getTextContent()
        /*
        def shortTextNode = liNode.getHtmlElementsByTagName('div').find { divNode ->
          return divNode.getAttribute('class') =~ /s/
        }
        */
        SearchEngineResult ser = new SearchEngineResult(this, url, title, "Blah blah")
        fireResultEvent ser
        nbEvents++
        Thread.sleep(500)
      }
    }
    println "fired $nbEvents times"
  }

}
