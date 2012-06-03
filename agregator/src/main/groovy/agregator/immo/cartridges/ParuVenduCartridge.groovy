package agregator.immo.cartridges

import agregator.core.Cartridge
import agregator.core.Agregator
import com.gargoylesoftware.htmlunit.WebClient

import agregator.immo.ImmoCriteria.Type
import agregator.immo.ImmoCriteria.Demand
import agregator.immo.ImmoCriteria
import agregator.immo.ImmoResult
import agregator.util.Logger
import agregator.core.Criteria

public class ParuVenduCartridge extends Cartridge {

  private static final Logger logger = Logger.getLogger(ParuVenduCartridge.class)

  private static final String ROOT_SITE = 'http://www.paruvendu.fr'

  def ParuVenduCartridge(Agregator agregator) {
    super("www.paru-vendu.fr", agregator);
  }

  protected void doAgregate(Criteria criteria) {
    logger.debug("Building URL")
    def url = new StringBuilder()
    url << ROOT_SITE
    if (criteria.demand==Demand.RENT) {
      url << "/immobilier/annoncefo/liste/listeAnnonces?dt=0&tt=5"
    } else {
      url << "/immobilier/annoncefo/liste/listeAnnonces?dt=0&tt=1"
    }
    if (criteria.type==Type.APPT) {
      url << "&tb1=1&tbApp=1&tbDup=1&tbChb=1&tbLof=1&tbAtl=1"
    } else {
      url << "&tb2=1&tbMai=1&tbVil=1&tbCha=1&tbPro=1&tbHot=1"
      if (criteria.demand==Demand.SELL) {
        url << "&tbMou=1&tbFer=1&at=1"
      }
    }
    if (criteria.nbRoomsMin) {
      def value = criteria.nbRoomsMin * 10
      if (criteria.nbRoomsMin > 5) {
        value = "55"
      }
      url << "&nbp0=$value"
    }
    if (criteria.nbRoomsMax) {
      def value = criteria.nbRoomsMax * 10
      if (criteria.nbRoomsMax > 5) {
        value = "55"
      }
      url << "&nbp1=$value"      
    }
    if (criteria.surfaceMin) {
      url << "&sur0=$criteria.surfaceMin"
    }
    if (criteria.surfaceMax) {
      url << "&sur1=$criteria.surfaceMax"
    }
    if (criteria.priceMin) {
      url << "&px0=$criteria.priceMin"
    }
    if (criteria.priceMax) {
      url << "&px1=$criteria.priceMax"
    }

    // TODO pays
    url << "&pa=FR"

    if (criteria.postCode!=null) {
      url << "&lo=$criteria.postCode"
    } else {
      url << "&lo=$criteria.city"
    }

    // appt
    // http://www.paruvendu.fr/immobilier/annoncefo/liste/listeAnnonces?dt=0&tt=5&tb1=1&tbApp=1&tbDup=1&tbChb=1&tbLof=1&tbAtl=1&nbp0=30&nbp1=30&sur0=50&sur1=150&px0=1400&px1=1500&pa=FR&lo=06000
    // maison
    // http://www.paruvendu.fr/immobilier/annoncefo/liste/listeAnnonces?dt=0&tt=5&tb2=1&tbMai=1&tbVil=1&tbCha=1&tbPro=1&tbHot=1&nbp0=30&nbp1=30&sur0=50&sur1=150&px0=1400&px1=1500&pa=FR&lo=06000

    logger.debug("Sending request : " + url);

    WebClient webClient = new WebClient()
    webClient.setJavaScriptEnabled(true)
    def p = webClient.getPage(url.toString())

    def spanNbAnnonces = p.getByXPath('/html/body/div[8]/div[2]/div/div/div[2]/div/div[2]/div/strong')[0]
    Integer nbAnnonces = agregator.ui.Util.extractInteger(spanNbAnnonces.textContent)
    Integer nbPages = 1
    if (nbAnnonces>0) {
      nbPages = nbAnnonces / 10 + 1
    }
    logger.debug("Nb pages : $nbPages")

    List<ImmoResult> results = new ArrayList<ImmoResult>()

    int totalAdded = 0

    for (int pageNum=1 ; pageNum<=nbPages && !isKilled(); pageNum++) {
      logger.debug("Handling page $pageNum")
      if (pageNum>1) {
        sleepRandomTime()
        String u = url.toString() + "&p=$pageNum"
        logger.debug("Getting page $pageNum, url=$u")
        p = webClient.getPage(u)
      }
      int nbAdded = 0
      def listItems = p.getByXPath("//div[@class='annonce']")
      boolean firstLink = true
      listItems.each { item ->
        def lnk = item.getByXPath("a")[0]
        if (lnk) {
          def title = lnk.textContent.trim()
          def u = ROOT_SITE + lnk.getAttribute('href')
          def descBlock = lnk.getByXPath("span[@class='desc']")[0]
          String description = descBlock.textContent.trim()
          def priceBlock = lnk.getByXPath("span[@class='price']")[0]
          def price = agregator.ui.Util.extractInteger(priceBlock.textContent)
            // TODO date
//          def dateBlock =
          def date = new Date() // agregator.ui.Util.extractDate(div.textContent)
          def imgBlock = lnk.getByXPath("span[@class='img']/img")[0]
          def imgUrl = null
          if (imgBlock!=null) {
              if (firstLink) {
                  imgUrl = imgBlock.getNextSibling().getAttribute("original")
              } else {
                  imgUrl = imgBlock.getAttribute("original")
              }
          }
          fireResultEvent(new ImmoResult(this, title, u, description, price, date, imgUrl))
          nbAdded++
          totalAdded++
          firstLink = false
          logger.debug("Added result title $title, url $u" + ", desc $description, date $date")
        }
      }
    
      logger.debug("added $nbAdded results for page $pageNum, total added $totalAdded")      
    }

    logger.debug("total added : $totalAdded")

  }  
}