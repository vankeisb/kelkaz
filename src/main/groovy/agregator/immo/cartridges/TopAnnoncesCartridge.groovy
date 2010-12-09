package agregator.immo.cartridges

import agregator.util.Logger
import agregator.immo.ImmoResult
import agregator.immo.ImmoCriteria
import agregator.immo.ImmoCriteria.Demand
import agregator.immo.ImmoCriteria.Type
import agregator.core.Cartridge
import com.gargoylesoftware.htmlunit.WebClient
import agregator.core.Agregator

import static agregator.ui.Util.*
import java.text.SimpleDateFormat

/**
 * Created by IntelliJ IDEA.
 * User: alex
 * Date: 9 déc. 2010
 * Time: 09:43:31
 * To change this template use File | Settings | File Templates.
 */
class TopAnnoncesCartridge extends Cartridge<ImmoCriteria, ImmoResult> {

  private static final Logger logger = Logger.getLogger(TopAnnoncesCartridge.class)

  private static final String URL_ROOT = "http://www.topannonces.fr"
  private static final String URL_RENT = URL_ROOT + '/petites-annonces--locations-immobilieres.html'
  private static final String URL_SELL = URL_ROOT + '/petites-annonces--ventes-immobilieres.html'

  private final WebClient webClient = new WebClient();

  def TopAnnoncesCartridge(Agregator agregator){
    super("www.topannonces.fr", agregator)
  }

  protected void doAgregate() {
    webClient.setJavaScriptEnabled(false)

    /** Get results by form **/

    // Get the page to scrap
    def page
    if (criteria.demand == Demand.RENT){
      page = webClient.getPage(URL_RENT)
    }else{
      page = webClient.getPage(URL_SELL)
    }

    //http://old.nabble.com/Make-HtmlUnit-not-output-warnings-to-stdout-td22584822.html

    logger.debug "page loaded : " + page

    // Get the search form
    def form = page.getElementById('advSearchForm')

    // Set post code
    def inputCP = form.getElementById('sbZipCode')
    inputCP.setValueAttribute(criteria.postCode)

    def divSup = page.getElementById('scenarioInfo')
    println "divSup = " + divSup

    // Set surface
//    if (criteria.surfaceMin != null){
//      def inputSurfaceMin = divSup.getByXPath("div[0]")[0]
//      logger.debug " Input surface = " + inputSurfaceMin
//      inputSurfaceMin.setValueAttribute(criteria.surfaceMin)
//    }
//    if (criteria.surfaceMax != null){
//      def inputSurfaceMax = form.getElementById('li_surface_max')
//      inputSurfaceMax.setValueAttribute(criteria.surfaceMax)
//    }
//
//    // Set price
//    if (criteria.priceMin != null){
//      def inputPriceMin = form.getElementById(getPriceMinMarkupId())
//      inputPriceMin.setValueAttribute(criteria.priceMin)
//    }
//    if (criteria.priceMax != null){
//      def inputPriceMax = form.getElementById(getPriceMaxMarkupId())
//      inputPriceMax.setValueAttribute(criteria.priceMax)
//    }

    // Submit form
    page = form.submit()

    // Get the results
    def divResults = page.getByXPath("//div[@class='innerAdsBlock']")
    divResults.each{

      // Get Title
      def aTitle = it.getByXPath("div[@class='adsCenter']/table/tbody/tr/td[@class='adsTabTitle']/a")[0]
      def title = aTitle.textContent
      def url = URL_ROOT+'/'+aTitle.getAttribute('href')

      // Get price
      def tdPrice = it.getByXPath("div[@class='adsCenter']/table/tbody/tr/td[@class='adsTabPrice']")[0]
      def price = extractInteger(tdPrice.textContent)

      // Get photo
      def imgPhoto = it.getByXPath("div[@class='adsPhoto']/a/img")[0]
      def photo = null
      if (imgPhoto != null){
        photo = URL_ROOT+'/'+imgPhoto.getAttribute('src')
      }

      // Get description
      def description = getDescription(url)

      // Get date
      def tdDate = it.getByXPath("div[@class='adsCenter']/table/tbody/tr/td[@class='adsTabDate']")[0]
      logger.debug "tdDate = " + tdDate
      def date = formatDate(tdDate.textContent)

      fireResultEvent(new ImmoResult(this, title, url, description, price, date, photo))
    }


  }

  private Date formatDate(String date){
    SimpleDateFormat format = new SimpleDateFormat("dd MMMM yyyy");
    return format.parse(date)
  }

  private String getDescription(String url){
    WebClient webClient = new WebClient()
    webClient.setJavaScriptEnabled(false)
    def p = webClient.getPage(url)

    def dElm = p.getByXPath("//div[@class='ta_details2_description']")[0]
    return trim(dElm.textContent)
  }

  private String getPriceMinMarkupId(){
    if (criteria.demand == Demand.RENT){
      return 'li_loyer_mensuel_min'
    } else {
      return 'vi_price_min'
    }
  }

  private String getPriceMaxMarkupId(){
    if (criteria.demand == Demand.RENT){
      return 'li_loyer_mensuel_max'
    } else {
      return 'vi_price_max'
    }
  }
}