package agregator.immo.cartridges

import agregator.immo.ImmoCriteria
import agregator.immo.ImmoResult
import agregator.core.Cartridge
import agregator.core.Agregator
import com.gargoylesoftware.htmlunit.WebClient
import agregator.immo.ImmoCriteria.Demand
import agregator.immo.ImmoCriteria.Type
import agregator.util.Logger
import static agregator.ui.Util.*
import java.text.SimpleDateFormat

/**
 * Created by IntelliJ IDEA.
 * User: alex
 * Date: 11 oct. 2009
 * Time: 18:28:42
 */

public class PAPCartridge extends Cartridge<ImmoCriteria,ImmoResult> {

  private static final Logger logger = Logger.getLogger(PAPCartridge.class)

  private Iterator<ImmoResult> resultsIterator = null

  private static String URL_ROOT = "http://www.pap.fr"
  private static String URL_CARTRIDGE_RENT = URL_ROOT+"/annonce/locations"
  private static String URL_CARTRIDGE_SELL = URL_ROOT+"/annonce/vente-immobiliere"

  private static Map<Type, String> PAPTYPE_FROMKKTYPE = new HashMap<Type, String>();
  static{
    PAPTYPE_FROMKKTYPE.put(Type.APPT, "appartement");
    PAPTYPE_FROMKKTYPE.put(Type.MAISON, "maison")
  }

  private final WebClient webClient = new WebClient()

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

    // Construct the url : url-appartement-antibes-06600-g8853-du-3-pieces-au-5-pieces-entre-500-et-1000-euros-entre-50-et-100-m2

    // Set Type
    url += '-'+PAPTYPE_FROMKKTYPE.get(criteria.type)

    // Localisation
    def postCodeUrl = URL_ROOT + '/ajax/localisation.php?q=' + criteria.postCode + '&limit=21'
    logger.debug("Sending post code request : " + postCodeUrl)
    webClient = new WebClient()
    webClient.setJavaScriptEnabled(false)
    def p = webClient.getPage(postCodeUrl)
    def postCodeResult = p.webResponse.contentAsString
    url += '-' + getLocaliteFromPostCode(postCodeResult)

    // Set room number
    url += '-du-' + criteria.nbRoomsMin + '-pieces-au-' + criteria.nbRoomsMax + '-pieces'

    // Set price
    url += '-entre-' + criteria.priceMin + '-et-' + criteria.priceMax + '-euros'

    // Set surface
    url += '-entre-' + criteria.surfaceMin + '-et-' + criteria.surfaceMax + '-m2'

    logger.debug("Sending request : " + url);
    def page = webClient.getPage(url.toString())
    logger.debug "form submitted, new page = " + page

    // Get the results
    def divResults = page.getByXPath("//div[@class='annonce annonce-resume']")
    def i = 0
    logger.debug divResults
    divResults.each{


      // Get title and price
      def aTitle = it.getHtmlElementsByTagName('a')[0]
      def title = formatTitle(aTitle.textContent)
      def price = extractPriceFromTitle(aTitle.textContent)

      // Get description
      def resultDescription = it.getByXPath("div/table/tbody/tr/td[@class='annonce-resume-description']/p[@class='annonce-resume-texte']")[0]
      def description = formatDescription(resultDescription.textContent)

      // Get Date
      def resultDate = it.getByXPath("div/table/tbody/tr/td[@class='annonce-resume-description']/p[@class='date-publication']")[0]
      def date = formatDate(resultDate.textContent)

      // Get image and Url
      def tdPhoto = it.getByXPath("div/table/tbody/tr/td[@class='annonce-resume-photo']")[0]
      def urlDetail
      def imgUrl
      if (tdPhoto != null){
        def aPhoto = tdPhoto.getHtmlElementsByTagName('a')[0]
        urlDetail = URL_ROOT + "/" + aPhoto.getAttribute('href')
        def imgPhoto = aPhoto.getHtmlElementsByTagName('img')[0]
        imgUrl = imgPhoto.getAttribute('src')
      }else{
        imgUrl = null
        urlDetail = null
        def liUrlDetail = it.getByXPath("div[@class='fonctionnalites']/table/tbody/tr")[0]
        def aUrlDetail = liUrlDetail.getHtmlElementsByTagName('a')[0]
        urlDetail = URL_ROOT + "/" + aUrlDetail.getAttribute('href')
      }


      fireResultEvent(new ImmoResult(this, title, urlDetail, description, price, date, imgUrl))

      i++
    }
  }

  private String getLocaliteFromPostCode(String postCode){
    def items = postCode.split('\\|');
    return 'g'+ trim(items[1])
  }

  private String formatTitle(String title){
    def items = title.split('\n')
    return trim(items[2])
  }

  private int extractPriceFromTitle(String title){
    def items = title.split('\n')
    def ret = items[0]
    ret = ret.replaceAll('\\.', '')
    ret = ret.replaceAll('€', '')
    return extractInteger(ret)
  }

  private String formatDescription(String description){
    def ret = trim(description)
    ret = ret.replaceAll('\n', '')
    ret = ret.replaceAll('\t', '')

    return ret

  }

  private Date formatDate(String date){
    def ret = date.replaceAll('Annonce mise à jour le  ', '')
    SimpleDateFormat format = new SimpleDateFormat("dd MMMM yyyy");
    return format.parse(ret)
  }
}