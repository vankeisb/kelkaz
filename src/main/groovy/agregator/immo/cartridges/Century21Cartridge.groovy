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
import agregator.core.Criteria

/**
 * Created by IntelliJ IDEA.
 * User: server
 * Date: Dec 7, 2010
 * Time: 10:30:37 PM
 */
class Century21Cartridge extends Cartridge {

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

  protected void doAgregate(Criteria criteria) {
    def url
    if (criteria.demand == Demand.RENT){
      url = URL_RENT
    }else{
      url = URL_SELL
    }

    // Set type
    if (criteria.type == Type.MAISON){
      url += '-maison'
    }
    if (criteria.type == Type.APPT){
      url += '-appartement'
    }

    // Set post code
    url += getSeparator(criteria)+'cp-' + criteria.postCode

    // Set surface
    if(criteria.surfaceMin != null){
      url += getSeparator(criteria)+'s-'+ criteria.surfaceMin+'-'
    }else{
      url += getSeparator(criteria)+'s-0-'
    }
    if (criteria.surfaceMax != null){
      url += criteria.surfaceMax
    }

    // Add this tricks for 'surface terrain'
    url += getSeparator(criteria)+'st-0-'

    // Set price
    if (criteria.priceMin != null){
      url += getSeparator(criteria)+'b-' + criteria.priceMin + '-'
    }else{
      url += getSeparator(criteria)+'b-0-'
    }
    if (criteria.priceMax != null){
      url +=criteria.priceMax
    }

    // Set room number
    if (criteria.nbRoomsMin != null && criteria.nbRoomsMax != null){
      url += getSeparator(criteria)+'p-' + criteria.nbRoomsMin + '-' + criteria.nbRoomsMax
    }else if (criteria.nbRoomsMin != null){
      url += getSeparator(criteria)+'p-'+criteria.nbRoomsMin
    }else if (criteria.nbRoomsMax != null){
      url += getSeparator(criteria)+'p-'+criteria.nbRoomsMax
    }

    url += getSeparator(criteria)+'page-'

    logger.debug "sending request to url = " + url

    // Get page and results
    webClient.setJavaScriptEnabled(false)
    def page = webClient.getPage(url.toString()+'1')

    def ulNbPage = page.getByXPath("//div[@class='enteteBloc']/div[@class='blocPage']/ul/li")
    if (ulNbPage == null){
      logger.debug('No results')
      return
    }

    def nbPages = ulNbPage.size -1

    for (int pageNum=1 ; pageNum<=nbPages && !isKilled() ; pageNum++ ){

      if (pageNum>1) {
        sleepRandomTime()
        page = webClient.getPage(url+pageNum)
      }

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
        def imgUrl = imgPhoto.getAttribute('src')

        // Get description
        def pDescription = it.getByXPath("div[@class='descPrincip']/p")[0]
        def description = trim(pDescription.textContent)

        // get url
        def resultUrl = URL_ROOT + aTitle.getAttribute('href')

        fireResultEvent(new ImmoResult(this, title, resultUrl, description, price, new Date(), imgUrl))

      }
    }







  }

  private String getSeparator(Criteria criteria){
    if (criteria.demand == Demand.RENT){
      return RENT_SEPARATOR
    }else{
      return SELL_SEPARATOR
    }

  }

}
