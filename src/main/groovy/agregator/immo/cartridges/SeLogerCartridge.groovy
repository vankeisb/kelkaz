package agregator.immo.cartridges

import agregator.immo.ImmoCriteria
import agregator.immo.ImmoResult
import agregator.core.Cartridge
import agregator.core.Agregator
import com.gargoylesoftware.htmlunit.WebClient
import agregator.immo.ImmoCriteria.Demand
import com.gargoylesoftware.htmlunit.html.HtmlInput
import com.gargoylesoftware.htmlunit.BrowserVersion
import com.gargoylesoftware.htmlunit.html.HtmlSubmitInput

public class SeLogerCartridge extends Cartridge<ImmoCriteria,ImmoResult> {

  // TODO: BUG COMPLET ...

  private Iterator<ImmoResult> resultsIterator = null

  private static String URL_CARTRIDGE = "http://www.leboncoin.com"

  def SeLogerCartridge(Agregator agregator){
    super("leboncoin", agregator)
  }

  private void init(){
    def results = new ArrayList<ImmoResult>()


    // http://www.seloger.com/recherche.htm?ci=60088&idqfix=1&idtt=2&idtypebien=1,2&pxbtw=NAN%2f250000&surfacebtw=50%2fNAN
    // http://www.seloger.com/recherche.htm?ci=60004&idqfix=1&idtt=2&idtypebien=1,2&pxbtw=NAN%2f250000&surfacebtw=50%2fNAN

    // Get the page to scrap
    WebClient webClient = new WebClient()
    def page = webClient.getPage(URL_CARTRIDGE)

    // Choose the demand type
    if (criteria.demand == Demand.RENT){
      page.getElementById('idtt_1').click()
    }else{
      def radioSell = page.getElementsByName('typerecherche')
      println 'radioSell = '+radioSell
      radioSell[2].click()
      println 'radioSell = '+radioSell
    }



    // Fill the localisation field
    def recherche = page.getElementById('ville_p')
    recherche.setValueAttribute(criteria.city+' ('+criteria.postCode+')')

    // Fill the surface min
    def surfaceMin = page.getElementById('surfacemin_p')
    surfaceMin.setValueAttribute('50')

    // Fill the price max
    def priceMax = page.getElementById('pricemax_p')
    priceMax.setValueAttribute('500000')

    println 'recherche = ' + recherche.text

    // Submit the form
    HtmlSubmitInput submit = page.getElementById('btn_engine_p');
    def page2 = submit.click()

    println("page = " + page.titleText)
    println("page2 = " + page2.titleText)

    // Get the results
    def divResult = page.getHtmlElementById('results_container')
    divResult.childElements.each{
      def a = it.getHtmlElementsByTagName('a')[0]
      def title = a.getName()
      // Add the cartridde url before the hit url
      def url = URL_CARTRIDGE + a.getAttribute('href')
      results << new ImmoResult(this, title, url, null)
    }

    resultsIterator = results.iterator()
  }

  
  protected synchronized boolean hasMoreResults() {
    if (resultsIterator==null) {
      init()
    }
    return resultsIterator.hasNext()
  }

  protected synchronized ImmoResult nextResult() {
    if (resultsIterator==null) {
      init()
    }
    return resultsIterator.next()
  }

}