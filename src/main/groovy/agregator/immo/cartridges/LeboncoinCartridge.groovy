package agregator.immo.cartridges


import agregator.core.Cartridge
import agregator.immo.ImmoCriteria
import agregator.immo.ImmoResult
import agregator.immo.ImmoCriteria.Demand
import agregator.immo.ImmoCriteria.Type

import agregator.util.Logger
import com.gargoylesoftware.htmlunit.WebClient
import static agregator.ui.Util.*

public class LeboncoinCartridge extends Cartridge<ImmoCriteria,ImmoResult> {

  private static final Logger logger = Logger.getLogger(LeboncoinCartridge.class)

  private static final String ROOT_SITE = 'http://www.leboncoin.fr'

  def LeboncoinCartridge(agregator) {
    super("www.leboncoin.fr", agregator);
  }

  private def VENTE_PRICE_TABLE = {
    def res = new HashMap()
    for (int i=0;i<15;i++) {
      int amount = i * 25000
      res[amount] = i
    }
    res[400000] = 15
    res[450000] = 16
    res[500000] = 17
    res[600000] = 18
    res[700000] = 19
    res[1000000] = 20
    res[1500000] = 21
    return res  
  }()
    
  private def LOYER_PRICE_TABLE = {
    def res = new HashMap()
    for (int i=0;i<11;i++) {
      Integer amount = i * 100
      res.put(amount, amount)
    }
    res[1200] = 1200
    res[1600] = 1600
    res[2000] = 2000
    return res  
  }()

  private def SURFACE_TABLE = {
    def res = new HashMap()
    res[0] = 0
    for (int i=2;i<9;i++) {
      Integer amount = i* 10
      res.put(amount, i-1)
    }
    res[100] = 8
    res[120] = 9
    res[150] = 10
    res[300] = 11
    res[500] = 12
    return res
  }()

  private def NB_ROOMS_TABLE = {
    def res = new HashMap()
    for (int i=1;i<9;i++) {
      res.put(i, i)
    }
    return res
  }()

  private def findValueInSelect(value, map, maxKey, maxVal) {
    def p = value
    while (p<=maxKey && map[p]==null) {
      p++
    }
    def r = map[p]
    return r==null ? maxVal : r
  }

  protected void doAgregate() {

    logger.debug("Building URL")
    def url = new StringBuilder()
    url << ROOT_SITE
    if (criteria.demand==Demand.RENT) {
      url << "/locations/offres/provence_alpes_cote_d_azur/occasions/?f=a&th=1"
    } else {
      url << "/ventes_immobilieres/offres/provence_alpes_cote_d_azur/occasions/?f=a&th=1"
    }
    if (criteria.priceMin) {
      def v = findValueInSelect(
              criteria.priceMin,
              criteria.demand==Demand.RENT ? LOYER_PRICE_TABLE : VENTE_PRICE_TABLE,
              criteria.demand==Demand.RENT ? 2000 : 1500000,
              criteria.demand==Demand.RENT ? 2000 : 21
      )
      if (criteria.demand==Demand.RENT) {
        url << "&mrs=$v"
      } else {
        url << "&ps=$v"
      }
    }
    if (criteria.priceMax) {
      def v = findValueInSelect(
              criteria.priceMax,
              criteria.demand==Demand.RENT ? LOYER_PRICE_TABLE : VENTE_PRICE_TABLE,
              criteria.demand==Demand.RENT ? 999999 : 1500000,
              criteria.demand==Demand.RENT ? 999999 : 22
      )
      if (criteria.demand==Demand.RENT) {
        url << "&mre=$v"
      } else {
        url << "&pe=$v"
      }
    }
    if (criteria.surfaceMin) {
      def v = findValueInSelect(
              criteria.surfaceMin,
              SURFACE_TABLE,
              500,
              12
      )
      url << "&sqs=$v"
    }
    if (criteria.surfaceMax) {
      def v = findValueInSelect(
              criteria.surfaceMax,
              SURFACE_TABLE,
              501,
              13
      )
      url << "&sqe=$v"
    }
    if (criteria.nbRoomsMin) {
      def v = findValueInSelect(
              criteria.nbRoomsMin,
              NB_ROOMS_TABLE,
              8,
              8
      )
      url << "&ros=$v"
    }
    if (criteria.nbRoomsMax) {
      def v = findValueInSelect(
              criteria.nbRoomsMax,
              NB_ROOMS_TABLE,
              9,
              999999
      )
      url << "&roe=$v"
    }
    
    if (criteria.type==Type.APPT) {
      url << "&ret=2"
    } else {
      url << "&ret=1"
    }

    if (criteria.postCode) {
      url << "&zz=$criteria.postCode"
    }

    logger.debug("Sending request : " + url);

    WebClient webClient = new WebClient()
    webClient.setJavaScriptEnabled(false)
    def p = webClient.getPage(url.toString())

    def spanNbAnnonces = p.getByXPath("//li[@class='tab_all']/strong")[1]
    if (spanNbAnnonces==null) {
      throw new IllegalStateException("Could not find //li[@class='tab_all']/strong in page $p")
    }
    Integer nbAnnonces = extractInteger(spanNbAnnonces.textContent)
    Integer nbPages = 1
    if (nbAnnonces>0) {
      nbPages = nbAnnonces / 50 + 1
    }
    logger.debug("Nb pages : $nbPages")

    int totalAdded = 0

    for (int pageNum=1 ; pageNum<=nbPages && !isKilled(); pageNum++) {
      logger.debug("Handling page $pageNum")
      if (pageNum>1) {
        sleepRandomTime()

        // http://www.leboncoin.fr/locations/offres/provence_alpes_cote_d_azur/occasions/?f=a&th=1&mrs=200&mre=1600&sqs=2&zz=06000
        // http://www.leboncoin.fr/locations/offres/provence_alpes_cote_d_azur/occasions/?o=2&mrs=200&mre=1600&sqs=2&zz=06000&th=1


        String u = url.toString().replaceAll(/f=a&/, "o=$pageNum&")
        logger.debug("Getting page $pageNum, url=$u")
        p = webClient.getPage(u)
      }
      int nbAdded = 0
      def listItems = p.getByXPath("//table[@id='hl']/tbody/tr")
      listItems.each { item ->
        try {
          def aEl = item.getByXPath("td[2]/table/tbody/tr[2]/td[2]/a")[0]
          // TODO date
          // TODO desc
          def u = null,
            title = null,
            imgUrl = null,
            price = null,
            description = null,
            date = null
          if (aEl==null) {
            // no image, different markup
            aEl = item.getByXPath("td[3]/a")[0]
            title = trim(aEl.textContent)
          } else {
            def imgEl = item.getByXPath("td[2]/table/tbody/tr[2]/td[2]/a/img")[0]
            imgUrl = imgEl.getAttribute('src')            
            title = trim(aEl.getByXPath("img")[0].getAttribute('alt'))
          }
          u = aEl.getAttribute('href')
          def priceEl = item.getByXPath("td[3]/text()[2]")[0]
          price = extractInteger(priceEl.textContent)
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
