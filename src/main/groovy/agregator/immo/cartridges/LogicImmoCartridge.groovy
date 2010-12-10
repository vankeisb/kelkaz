package agregator.immo.cartridges

import agregator.core.Agregator
import agregator.core.Cartridge
import agregator.immo.ImmoCriteria
import agregator.immo.ImmoCriteria.Demand
import agregator.immo.ImmoCriteria.Type
import agregator.immo.ImmoResult
import agregator.util.Logger
import static agregator.ui.Util.*
import com.gargoylesoftware.htmlunit.WebClient
import org.json.JSONArray
import org.json.JSONObject
import agregator.ui.EmptyRefreshHandler
import agregator.core.Criteria

public class LogicImmoCartridge extends Cartridge {

  private static final Logger logger = Logger.getLogger(LogicImmoCartridge.class)

  private static final String ROOT_SITE = 'http://www.logic-immo.com'

  private final WebClient webClient = new WebClient()

  def LogicImmoCartridge(Agregator agregator) {
    super("www.logic-immo.com", agregator);
    webClient.setJavaScriptEnabled(false)
    webClient.setRefreshHandler(new EmptyRefreshHandler())
  }

  private String computePostCode(Criteria criteria) {
    int cpLen = criteria.postCode.toString().length()
    def postCodeUrl = ROOT_SITE + '/ajax/t9/fr/' + cpLen + '/' + criteria.postCode.charAt(0) + '/' + criteria.postCode + '.txt'
    logger.debug("Sending post code request : " + postCodeUrl)
    def p = webClient.getPage(postCodeUrl)
    def jsonResult = p.webResponse.contentAsString
    logger.debug("post code responded : $jsonResult")
    JSONArray a = new JSONArray(jsonResult)
    JSONObject loc = null
    if (a.length()>=2) {
      loc = a.getJSONObject(1)
    } else if (a.length()>=1) {
      loc = a.getJSONObject(0)
    }
    if (loc==null) {
      throw new RuntimeException("Could not get location for post code $criteria.postCode")
    }
    def lctId = loc.getString("lct_id")
    def lctName = loc.getString("lct_name").toLowerCase().replaceAll(/ /, '-')
    def lctPostCode = loc.getString("lct_post_code")
    def lctLevel = loc.getString("lct_level")
    StringBuilder sb = new StringBuilder()
    sb << '-'
    sb << lctName
    sb << '-'
    sb << lctPostCode
    sb << '-'
    sb << lctId
    sb << '_'
    sb << lctLevel
    return sb.toString()
  }

  private String buildUrl(Criteria criteria, int pageNum, String postCode) {
    logger.debug("Building URL")
    
    def url = new StringBuilder()
    url << ROOT_SITE
    if (criteria.demand==Demand.RENT) {
      url << "/location-immobilier"
    } else {
      url << "/vente-immobilier"
    }
    // send request to obtain post code
    url << postCode

    if (criteria.type==Type.MAISON) {
      url << "-430f000000-$pageNum"
    } else {
      url << "-8000000000-$pageNum"
    }

    url << '-a-'

    url << (criteria.priceMin !=null ? criteria.priceMin : 0)
    url << '-'
    url << (criteria.priceMax !=null ? criteria.priceMax : 0)

    url << '-a-'

    url << (criteria.surfaceMin != null ? criteria.surfaceMin : 0)
    url << '-0-00-3-0-0.htm'

    return url.toString()
  }

  protected void doAgregate(Criteria criteria) {

    // achat appt
    // http://www.logic-immo.com/vente-immobilier-nice-06000-22514_2-8000000000-1-a-222222-333333-a-22-0-0c-3-0-0.htm
    // http://www.logic-immo.com/vente-immobilier-nice-06000-22514_2-430f000000-1-a-222222-333333-a-22-0-0c-3-0-0.htm
    // achat maison
    // http://www.logic-immo.com/vente-immobilier-nice-06000-22514_2-430f000000-1-a-222222-333333-a-22-0-0c-3-0-0.htm

    // loc appt
    // http://www.logic-immo.com/location-immobilier-nice-06000-22514_2-8000000000-1-a-1111-2222-a-22-0-0c-3-0-0.htm
    // http://www.logic-immo.com/location-immobilier-nice-06000-22514_2-8000000000-1-a-1111-2222-a-22-0-08-3-0-0.htm

    def postCode = computePostCode(criteria)

    def url = buildUrl(criteria, 1, postCode)
    logger.debug("Sending request : " + url);

    webClient.addRequestHeader("User-Agent","Mozilla/5.0 (X11; U; Linux i686; fr; rv:1.9.2.9) Gecko/20100825 Ubuntu/10.04 (lucid) Firefox/3.6.9")
    webClient.addRequestHeader("Accept","text/html, application/xhtml+xml, application/xml;q=0.9, */*;q=0.8")
    webClient.addRequestHeader("Accept-Charset","ISO-8859-1,utf-8;q=0.7,*;q=0.7")
    webClient.addRequestHeader("Accept-Encoding","gzip,deflate")
    webClient.addRequestHeader("Accept-Language","fr,fr-fr;q=0.8,en-us;q=0.5,en;q=0.3")

    def p = webClient.getPage(url)

    def spanNbAnnonces = p.getByXPath("/html/body/div[3]/div/div[3]/div")[0]
    if (!spanNbAnnonces) {
      spanNbAnnonces = p.getByXPath("/html/body/div[@id='li-content-global']/div[@id='li-content-left']/div[@id='pagination_v6']/div[1]/strong[1]")[0]
    }
    if (!spanNbAnnonces) {
      logger.debug('No results')
      return
    }
    def nbAnnoncesStr = spanNbAnnonces.textContent
    Integer nbAnnonces = extractInteger(nbAnnoncesStr)
    Integer nbPages = 1
    if (nbAnnonces>0) {
      nbPages = nbAnnonces / 8 + 1
    }
    logger.debug("Nb pages : $nbPages")

    int totalAdded = 0

    for (int pageNum=1 ; pageNum<=nbPages && !isKilled(); pageNum++) {
      logger.debug("Handling page $pageNum")
      if (pageNum>1) {
        sleepRandomTime()
        String u = buildUrl(criteria, pageNum, postCode)
        logger.debug("Getting page $pageNum, url=$u")
        p = webClient.getPage(u)
      }
      int nbAdded = 0
      def listItems = p.getByXPath("""//div[@class='ad-content']""")
      listItems.each { item ->
        try {
          def title = trim(item.getByXPath("div[2]/div[3]/a/strong")[0].textContent)
          def price = extractInteger(item.getByXPath("div[2]/div[1]/span[1]")[0].textContent)
          def description = trim(item.getByXPath("div[2]/div[3]/span")[0].textContent)
          def u = item.getByXPath("div[2]/div[3]/a")[0].getAttribute('href')
          def imgUrl = null // TODO
          def date = null // TODO

          fireResultEvent(new ImmoResult(this, title, u, description, price, date, imgUrl))
          nbAdded++
          totalAdded++
          logger.debug("Added result title $title, url $u" + ", desc $description, date $date")
        } catch(Throwable t) {
          logger.error("Exception caught while processing item : $item", t)
        }

      }

      logger.debug("added $nbAdded results for page $pageNum, total added $totalAdded")
    }

    logger.debug("total added : $totalAdded")

  }
}