package agregator.immo.cartridges

import agregator.core.Agregator
import agregator.core.Cartridge
import agregator.immo.ImmoCriteria
import agregator.immo.ImmoCriteria.Demand
import agregator.immo.ImmoCriteria.Type
import agregator.immo.ImmoResult
import agregator.util.Logger
import com.gargoylesoftware.htmlunit.WebClient
import static agregator.ui.Util.*

/**
 * Created by IntelliJ IDEA.
 * User: server
 * Date: Dec 7, 2010
 * Time: 10:30:37 PM
 */
class Century21Cartridge extends Cartridge<ImmoCriteria, ImmoResult> {

  private static final Logger logger = Logger.getLogger(Century21Cartridge.class)

  private static final String URL_ROOT = "http://www.century21.fr"
  private static final String URL_RENT = URL_ROOT + '/annonces-immobilieres/location-appartement/location'
  private static final String URL_SELL = URL_ROOT + '/annonces/achat'

  private static final String SELL_SEPARATOR = "/"
  private static final String RENT_SEPARATOR = ","

  private final WebClient webClient = new WebClient()

  def Century21Cartridge(Agregator agregator){
    super("www.century21.fr", agregator)
  }

  protected void doAgregate() {
    def url
    if (criteria.demand == Demand.RENT){
      url = URL_RENT
    }else{
      url = URL_SELL
    }

    // http://www.century21.fr/annonces/achat-maison-appartement/cp-06600/s-20-300/st-0-/b-100000-700000/page-1/
    // Set type
    if (criteria.type == Type.MAISON){
      url += '-maison'
    }
    if (criteria.type == Type.APPT){
      url += '-appartement'
    }

    // Set post code
    url += getSeparator()+'cp-' + criteria.postCode

    // Set surface
    url += getSeparator()+'s-'+ criteria.surfaceMin+'-'+criteria.surfaceMax

    // Add this tricks for 'surface terrain'
    url += getSeparator()+'st-0-'

    // Set price
    url += getSeparator()+'b-' + criteria.priceMin + '-' + criteria.priceMax

    // Set room number
    url += getSeparator()+'p-' + criteria.nbRoomsMin + '-' + criteria.nbRoomsMax

    // trick display pge 1
    url += getSeparator()+'page-1'

    logger.debug "sending request to url = " + url

    // Get page and results
    webClient.setJavaScriptEnabled(false)
    def page = webClient.getPage(url.toString())
    def divResults = page.getByXPath("//div[@class='blocAnnonce']")
    logger.debug divResults
    divResults.each{
      // Get title
      def aTitle = it.getHtmlElementsByTagName('a')[0]
      def title = aTitle.getAttribute('title')

      // Get price
      def spanPrice = it.getHtmlElementsByTagName('span')[0]
      def price = extractInteger(spanPrice.textContent)

      // Get photo
      def imgPhoto = it.getByXPath("div[@class='infosPrincip']/div[@class='image']/span/img")[0] // /a")[0]
      println "#########################################"
      println "imgPhoto" + imgPhoto
      println "#########################################"
      def imgUrl = imgPhoto.getAttribute('src')

      // Get description
      def pDescription = it.getByXPath("div[@class='descPrincip']/p")[0]
      def description = trim(pDescription.textContent)

      fireResultEvent(new ImmoResult(this, title, null, description, price, new Date(), imgUrl))


    }







    }

    private String getSeparator(){
      if (criteria.demand == Demand.RENT){
        return RENT_SEPARATOR
      }else{
        return SELL_SEPARATOR
      }

    }

  }
