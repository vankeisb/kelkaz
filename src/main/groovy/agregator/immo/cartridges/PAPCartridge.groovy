package agregator.immo.cartridges

import agregator.immo.ImmoCriteria
import agregator.immo.ImmoResult
import agregator.core.Cartridge
import agregator.core.Agregator
import com.gargoylesoftware.htmlunit.WebClient
import agregator.immo.ImmoCriteria.Demand
import agregator.immo.ImmoCriteria.Type
import agregator.util.Logger

/**
 * Created by IntelliJ IDEA.
 * User: alex
 * Date: 11 oct. 2009
 * Time: 18:28:42
 */

public class PAPCartridge extends Cartridge<ImmoCriteria,ImmoResult> {

  private static final Logger logger = Logger.getLogger(PAPCartridge.class)

  private Iterator<ImmoResult> resultsIterator = null

  private static String URL_CARTRIDGE_RENT = "http://www.pap.fr/annonce/locations"
  private static String URL_CARTRIDGE_SELL = "http://www.pap.fr/annonce/vente-immobiliere"

  def PAPCartridge(Agregator agregator){
    super("www.pap.fr", agregator)
  }

  protected void doAgregate() {
    def results = new ArrayList<ImmoResult>()

    def url
    if (criteria.demand == Demand.RENT){
      url = URL_CARTRIDGE_RENT
    }else{
      url = URL_CARTRIDGE_SELL
    }

    // Get the page to scrap
    WebClient webClient = new WebClient()
    webClient.setJavaScriptEnabled(false)
    def page = webClient.getPage(url)

    logger.debug "page loaded : " + page

    // Get the search form
    def form = page.getElementById('form_recherche_accueil')

    // Set type
    if (criteria.getType() == Type.APPT){
      def typeRadio = form.getElementById('typesbien_appartement')
      typeRadio.click()
    }
    if (criteria.getType() == Type.MAISON){
      def typeRadio = form.getElementById('typesbien_maison')
      typeRadio.click()
    }

    // set room number
    def roomMinField = form.getElementById('nb_pieces_min')
    roomMinField.setValueAttribute(criteria.nbRoomsMin.toString())

    def roomMaxField = form.getElementById('nb_pieces_max')
    roomMaxField.setValueAttribute(criteria.nbRoomsMax.toString())

    // Set location
    def locationField = form.getElementById('geoobjet_autocomplete')
    locationField.setValueAttribute(criteria.postCode)

    def locationButton = form.getElementById('geoobjet_ajouter')
    locationButton.click()


    // Set min/max price
    def minPriceField = form.getElementById('prix_min')
    minPriceField.setValueAttribute(criteria.priceMin.toString())

    def maxPriceField = form.getElementById('prix_max')
    maxPriceField.setValueAttribute(criteria.priceMax.toString())

    // Set min/max surface
    def minSurfaceField = form.getElementById('surface_min')
    minSurfaceField.setValueAttribute(criteria.surfaceMin.toString())

    def maxSurfaceField = form.getElementById('surface_max')
    maxSurfaceField.setValueAttribute(criteria.surfaceMax.toString())

    // Submit form
    page = form.submit()
    logger.debug "form submitted, new page = " + page

    // Get the results
    def divResults = page.getByXPath("//div[@class='annonce annonce-resume']")
    def i = 0
    divResults.each{
      println 'it = ' + it

      // Get title
      def aTitle = it.getHtmlElementsByTagName('a')[0]
      def title = aTitle.textContent

      // Get description
      def resultDescription = it.getByXPath("//p[@class='annonce-resume-texte']")
      def description = resultDescription.textContent


      fireResultEvent(new ImmoResult(this, title, url, "description", 210000, null, null))
    }
  }
}