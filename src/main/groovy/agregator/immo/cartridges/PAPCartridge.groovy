package agregator.immo.cartridges

import agregator.immo.ImmoCriteria
import agregator.immo.ImmoResult
import agregator.core.Cartridge
import agregator.core.Agregator
import com.gargoylesoftware.htmlunit.WebClient
import agregator.immo.ImmoCriteria.Demand
import com.gargoylesoftware.htmlunit.BrowserVersion
import agregator.immo.ImmoCriteria.Type

/**
 * Created by IntelliJ IDEA.
 * User: alex
 * Date: 11 oct. 2009
 * Time: 18:28:42
 * To change this template use File | Settings | File Templates.
 */

public class PAPCartridge extends Cartridge<ImmoCriteria,ImmoResult> {

  private Iterator<ImmoResult> resultsIterator = null

  private static String URL_CARTRIDGE = "http://www.pap.fr"

  protected void doAgregate() {
    //To change body of implemented methods use File | Settings | File Templates.
  }



  def PAPCartridge(Agregator agregator){
    super("pap", agregator)
  }

  private void init(){
    def results = new ArrayList<ImmoResult>()

    def url = URL_CARTRIDGE

    url += '/immobilier/?mode=recherche&userid=&alerteid=&envoiid='

    if (criteria.demand == Demand.RENT){
      url += '&produit=location'
    } else{
      url += '&produit=vente'
    }

    // Add PAP specific stuff
    url += '&rubrique=&section=offre&lieu=france' 

    // Add type
    if (criteria.type == Type.APPT){
      for (int i=criteria.nbRoomsMin ; i <= criteria.nbRoomsMax ; i++){
        if (i == 5){
          url += '&typebien[]=appartement-5-pieces'
          break
        }
        url += '&typebien[]=appartement-' + i + '-pieces'
      }
    }
    
    if (criteria.type == Type.MAISON){
      url += '&typebien[]=maison'
    }
    if (criteria.type == Type.GARAGE){
      url += '&typebien[]=garage-parking'
    }

    // Localisation
    if (criteria.city)
      url += '&champs_libres[1]=' + criteria.city
    else
      url += '&champs_libres[1]='

    // Add PAP specific stuff
    url += '&champs_libres[2]=&champs_libres[3]=&champs_libres[4]=&metro='

    // price min
    if (criteria.priceMin)
      url += '&prix_min=' + criteria.priceMin
    else
      url += '&price_min='

    // price max
    if (criteria.priceMin)
      url += '&prix_max=' + criteria.priceMax
    else
      url += '&price_max='

    // surface min
    if (criteria.surfaceMin)
      url += '&surface_min=' + criteria.surfaceMin
    else
      url +=  '&surface_min='

    // surface max
    if (criteria.surfaceMax)
      url += '&surface_max=' + criteria.surfaceMax
    else
    url += '&surface_max='

    // Add PAP specific stuff
    url += '&action=recherche&x=86&y=5'

    println 'URL = ' + url

    // Get the page
    WebClient webClient = new WebClient(BrowserVersion.FIREFOX_2)
    def page = webClient.getPage(url)

    // Scrap results
    def divResults = page.getByXPath("//div[@class='annonce_resume']")
    divResults.each{
      def h2 =  it.getHtmlElementsByTagName('h2')[0]
      def a = h2.getHtmlElementsByTagName('a')[0]
      def link = URL_CARTRIDGE + a.getAttribute('href')
      def title = it.getHtmlElementsByTagName('p')[1].textContent

      results << new ImmoResult(this, title, link, null)
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