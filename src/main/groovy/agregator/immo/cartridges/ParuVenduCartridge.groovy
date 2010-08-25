package agregator.immo.cartridges

import agregator.core.Cartridge
import agregator.core.Agregator
import com.gargoylesoftware.htmlunit.WebClient
import agregator.immo.ImmoCriteria.Demand
import agregator.immo.ImmoCriteria.Type
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
    url << "/immobilier/annoncefo/liste/listeAnnonces?dt=0&tt=5"
    if (criteria.type==Type.APPT) {
      url << "&tb1=1&tbApp=1&tbDup=1&tbChb=1&tbLof=1&tbAtl=1"
    } else {
      // TODO maison
      url << "&tb2=1"
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

    def locationValue = null
    if (criteria.postCode!=null) {
      locationValue = criteria.postCode
    } else {
      locationValue = criteria.city
    }
    if (locationValue) {
      url << "&lo=$locationValue"
    }

    // appt
    // http://www.paruvendu.fr/immobilier/annoncefo/liste/listeAnnonces?dt=0&tt=5&tb1=1&tbApp=1&tbDup=1&tbChb=1&tbLof=1&tbAtl=1&nbp0=30&nbp1=30&sur0=50&sur1=150&px0=1400&px1=1500&pa=FR&lo=06000
    // maison
    // http://www.paruvendu.fr/immobilier/annoncefo/liste/listeAnnonces?dt=0&tt=5&tb2=1&tbMai=1&tbVil=1&tbCha=1&tbPro=1&tbHot=1&nbp0=30&nbp1=30&sur0=50&sur1=150&px0=1400&px1=1500&pa=FR&lo=06000

    // http://www.paruvendu.fr/immobilier/annoncefo/liste/listeAnnonces?dt=0&tb1=1&tbApp=1&tbDup=1&tbChb=1&tbLof=1&tbAtl=1&nbp0=30&nbp1=30&sur0=50&sur1=150&px0=1400&px1=1500&pa=FR&lo=06000



    logger.debug("Sending request : " + url);

    WebClient webClient = new WebClient()
    webClient.setJavaScriptEnabled(false)
    def p = webClient.getPage(url.toString())

    List<ImmoResult> results = new ArrayList<ImmoResult>()
    def listItems = p.getByXPath("//div[@class='au_cdr_photo']")
    listItems.each { item ->
      def title = item.getByXPath("//div[@class='au_cdr_listdet']//div[@class='flol b']/a")[0].textContent.trim()
      def description = item.getByXPath("//div[@class='au_listdet_cntL']/a")[0].textContent
      def u = ROOT_SITE + item.getByXPath("//div[@class='au_listdet_cntL']/a")[0].getAttribute('href')
      def r = new ImmoResult(this, title, u, description)
      logger.debug("Added result title $title, url $u")
      results << r
    }

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