package agregator.immo.cartridges

import agregator.immo.ImmoCriteria
import agregator.immo.ImmoResult
import agregator.core.Cartridge
import agregator.core.Agregator
import com.gargoylesoftware.htmlunit.WebClient
import com.gargoylesoftware.htmlunit.BrowserVersion

public class OrpiCartridge extends Cartridge<ImmoCriteria,ImmoResult> {

  private Iterator<ImmoResult> resultsIterator = null

  private static String URL_CARTRIDGE = "http://www.orpi.com"

  protected void doAgregate() {
    //To change body of implemented methods use File | Settings | File Templates.
  }



  def OrpiCartridge(Agregator agregator){
    super("orpi", agregator)
  }

  private void init(){
    def results = new ArrayList<ImmoResult>()

    // Get the page to scrap
    WebClient webClient = new WebClient(BrowserVersion.FIREFOX_2)
    def page = webClient.getPage(URL_CARTRIDGE)

    // Get the search form
    def form = page.getElementById('achHomeRechercheRapide')

    // Choose sell or rent
    def radio = form.getInputByName('recIdTypeTransaction')
    radio.click()

    // Set the price min
    def priceMinField = form.getInputByName('recPrixMin')
    priceMinField.setValueAttribute(criteria.priceMin.toString())

    // Set the price max
    def priceMaxField = form.getInputByName('recPrixMax')
    priceMaxField.setValueAttribute(criteria.priceMax.toString())

    // Set the localisation field
    def localisationField = form.getInputByName('recVille')
    localisationField.setValueAttribute(criteria.city)


    println "radio sell = " + radio
    println "price min = " + priceMinField.text
    println "price max = " + priceMaxField.text
    println "localisation field = " + localisationField.text


    // Submit the form
    page = form.submit()

    // Get the results
    def divResults = page.getByXPath("//dl[@class='annonceResult']")
    def i = 0
    divResults.each{
      println 'it = ' + it
      def main = it.getByXPath("//dt[@class='annonceResultTitle']")[i]
      def a = main.getHtmlElementsByTagName('a')[0]
      def url = a.getAttribute('href')

      // Sometimes the URL doesn't start with http://... add it
      if (!url.startsWith("http://"))
        url = URL_CARTRIDGE +'/'+ url

      // Create the title from the url link
      def title = url.substring(34, url.length()-16)
      title = title.replaceAll('-', ' ')

      results << new ImmoResult(this, title, url, null)
      i++
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