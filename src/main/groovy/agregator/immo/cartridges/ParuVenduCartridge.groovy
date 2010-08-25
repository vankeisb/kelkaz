package agregator.immo.cartridges

import agregator.core.Cartridge
import agregator.core.Agregator
import com.gargoylesoftware.htmlunit.WebClient

import agregator.immo.ImmoCriteria.Type
import agregator.immo.ImmoCriteria.Demand
import agregator.immo.ImmoCriteria
import agregator.immo.ImmoResult
import agregator.util.Logger

public class ParuVenduCartridge extends Cartridge<ImmoCriteria,ImmoResult> {

  private static final Logger logger = Logger.getLogger(ParuVenduCartridge.class)

  private Iterator<ImmoResult> resultsIterator = null

  private static final String ROOT_SITE = 'http://www.paruvendu.fr'

  def ParuVenduCartridge(Agregator agregator) {
    super("ParuVendu", agregator);
  }


  def checkTheBox(String name, def form) {
    def cb = form.getInputByName(name)
    cb.click()
  }

  def setInput(String name, def value, def form) {
    if (value!=null) {
      def tf = form.getInputByName(name)
      tf.setValueAttribute(value.toString())
    }
  }

  private void init() {

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
    webClient.setJavaScriptEnabled(false)
    def p = webClient.getPage(url.toString())

    Util.sleepRandomTime()

    def spanNbAnnonces = p.getByXPath('/html/body/div[4]/div/div[3]/div/div[2]/div/div/h1/div/span')[0]
    Integer nbAnnonces = Util.extractInteger(spanNbAnnonces.textContent)
    Integer nbPages = 1
    if (nbAnnonces>0) {
      nbPages = nbAnnonces / 10 + 1
    }
    logger.debug("Nb pages : $nbPages")

    List<ImmoResult> results = new ArrayList<ImmoResult>()

    int totalAdded = 0

    for (int pageNum=1 ; pageNum<=nbPages ; pageNum++) {
      logger.debug("Handling page $pageNum")
      if (pageNum>1) {
        String u = url.toString() + "&p=$pageNum"
        logger.debug("Getting page $pageNum, url=$u")
        p = webClient.getPage(u)
        Util.sleepRandomTime()        
      }
      int nbAdded = 0
      def listItems = p.getByXPath("//div[@class='au_boxListe_C']")
      listItems.each { item ->
        def lnk = item.getByXPath("div/div[3]/div[1]/div[2]/div[1]/div[1]/a")[0]
        if (lnk) {
          def title = lnk.textContent.trim()
          def u = ROOT_SITE + lnk.getAttribute('href')
          lnk = item.getByXPath('div/div[3]/div[1]/div[2]/div[4]/div[1]/a')[0]
          def description = lnk.textContent.trim()
          def div = item.getByXPath('div/div[1]/div[1]')[0]
          def price = Util.extractInteger(div.getAttribute('title').trim())
          div = item.getByXPath('div/div[3]/div[1]/div[2]/div[2]/div[2]/a')[0]
          def date = Util.extractDate(div.textContent)

          results << new ImmoResult(this, title, u, description, price, date)
          nbAdded++
          totalAdded++
          logger.debug("Added result title $title, url $u" + ", desc $description, date $date")
        }
      }

      logger.debug("added $nbAdded results for page $pageNum, total added $totalAdded")      
    }

    logger.debug("total added : $totalAdded")

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