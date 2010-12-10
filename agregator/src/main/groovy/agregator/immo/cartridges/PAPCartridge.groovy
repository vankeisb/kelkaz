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
import agregator.core.Criteria

/**
 * Created by IntelliJ IDEA.
 * User: alex
 * Date: 11 oct. 2009
 * Time: 18:28:42
 */

public class PAPCartridge extends Cartridge {

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

  protected void doAgregate(Criteria criteria) {
    logger.debug "Launch PAP cartridge with criteria" + criteria
    def results = new ArrayList<ImmoResult>()

    def url
    if (criteria.demand == Demand.RENT){
      url = URL_CARTRIDGE_RENT
    }else{
      url = URL_CARTRIDGE_SELL
    }

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
    if (criteria.nbRoomsMin != null && criteria.nbRoomsMax == null){
      url += '-a-partir-du-' + criteria.nbRoomsMin + '-pieces'
    } else if(criteria.nbRoomsMin == null && criteria.nbRoomsMax != null){
      url += '-jusqu-au-' + criteria.nbRoomsMax + '-pieces'
    }else if (criteria.nbRoomsMin != null && criteria.nbRoomsMax != null){
      url += '-du-' + criteria.nbRoomsMin + '-pieces-au-' + criteria.nbRoomsMax + '-pieces'
    }

    // Set price
    if (criteria.priceMin != null && criteria.priceMax== null){
      url += '-a-partir-de-' + criteria.priceMin+ '-euros'
    } else if(criteria.priceMin == null && criteria.priceMax!= null){
      url += '-jusqu-a-' + criteria.priceMax+ '-euros'
    }else if (criteria.nbRoomsMin != null && criteria.nbRoomsMax != null){
      url += '-entre-' + criteria.priceMin + '-et-' + criteria.priceMax + '-euros'
    }

    // Set surface
    if (criteria.surfaceMin != null && criteria.surfaceMax == null){
      url += '-a-partir-de-' + criteria.surfaceMin+ '-m2'
    } else if(criteria.surfaceMin == null && criteria.surfaceMax!= null){
      url += '-jusqu-a-' + criteria.surfaceMax+ '-m2'
    }else if (criteria.surfaceMin != null && criteria.surfaceMax != null){
      url += '-entre-' + criteria.surfaceMin + '-et-' + criteria.surfaceMax + '-m2'
    }

    // Display 40 results per page
    url += '-40-annonces-par-page'

    logger.debug("Sending request : " + url);

    def hasNextPage = true
    def pageNum = 0
    while(hasNextPage){
      pageNum++
      def page = webClient.getPage(url.toString() +'-'+pageNum)

      // Get the results
      def divResults = page.getByXPath("//div[@class='annonce annonce-resume']")
      def i = 0
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

      def aNextPage = page.getByXPath("//div[@class='pagination']/a")
      hasNextPage = false
      for (def a : aNextPage){
        if (a.textContent.contains('Page suivante') && !isKilled()){
          hasNextPage = true
          sleepRandomTime()
        }
      }
    }
  }

  private String getLocaliteFromPostCode(String postCode){
    //Grasse (06130)|8741
    //Plascassier (06130)|55440
    def cityCode = ''
    def lines = postCode.split('\n')
    for(String line : lines){
    def items = line.split('\\|');
      cityCode += 'g'+ trim(items[1])
    }
    return cityCode
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
    if (date==null) {
      return null
    }
    // find index of first digit
    int i = 0
    int nbChars = date.length()
    boolean found = false
    while (i<nbChars && !found) {
      Character c = date.charAt(i)
      if (c.isDigit()) {
        found = true
      } else {
        i++
      }
    }
    def ret = date.substring(i, nbChars)
    SimpleDateFormat format = new SimpleDateFormat("dd MMMM yyyy");
    return format.parse(ret)
  }

}