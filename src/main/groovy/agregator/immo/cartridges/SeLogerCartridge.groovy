package agregator.immo.cartridges

import agregator.core.Cartridge
import agregator.core.Agregator
import com.gargoylesoftware.htmlunit.WebClient

import agregator.immo.ImmoCriteria.Type
import agregator.immo.ImmoCriteria.Demand
import agregator.immo.ImmoCriteria
import agregator.immo.ImmoResult
import agregator.util.Logger

public class SeLogerCartridge extends Cartridge<ImmoCriteria,ImmoResult> {

  private static final Logger logger = Logger.getLogger(SeLogerCartridge.class)

  private static final String ROOT_SITE = 'http://www.seloger.com'

  def SeLogerCartridge(Agregator agregator) {
    super("www.seloger.com", agregator);
  }

  protected void doAgregate() {

    // http://www.seloger.com/recherche.htm?ci=60088&idtt=2&idtypebien=2&nb_pieces=3,4&org=advanced_search&pxmax=300000&pxmin=10000&surfacemax=111&surfacemin=33

    // maison
    // http://www.seloger.com/recherche.htm?idtt=2&idtypebien=2&nb_pieces=3,4&org=advanced_search&pxmax=300000&pxmin=10000&surfacemax=111&surfacemin=33&cp=06000&ci=&
    // appt
    // http://www.seloger.com/recherche.htm?idtt=2&idtypebien=1&nb_pieces=3,4&org=advanced_search&pxmax=300000&pxmin=10000&surfacemax=111&surfacemin=33&cp=06000&ci=&



    logger.debug("Building URL")
    def url = new StringBuilder()
    url << ROOT_SITE
    if (criteria.demand==Demand.RENT) {
      url << "/recherche.htm?idtt=1"
    } else {
      url << "/recherche.htm?idtt=2"
    }
    if (criteria.type==Type.APPT) {
      url << "&idtypebien=1"
    } else {
      url << "&idtypebien=2"
    }
    def nbPiecesList = [1,2,3,4,5]
    if (criteria.nbRoomsMin) {
      nbPiecesList = nbPiecesList.findAll { it >= criteria.nbRoomsMin }
    }
    if (criteria.nbRoomsMax) {
      nbPiecesList = nbPiecesList.findAll { it <= criteria.nbRoomsMax }
    }
    def nbPiecesStr = new StringBuilder()
    int nbPiecesSize = nbPiecesList.size()
    for (def i = 0 ; i < nbPiecesSize ; i++) {
      def nb = nbPiecesList[i]
      if (nb==5) {
        nbPiecesStr << '%2b4'                
      } else {
        nbPiecesStr << Integer.toString(nb)
      }
      if (i<nbPiecesSize-1) {
        nbPiecesStr << ','
      }
    }
    url << "&nb_pieces="
    url << nbPiecesStr.toString()

    url << "&org=advanced_search"

    if (criteria.priceMax) {
      url << "&pxmax=$criteria.priceMax"
    }
    if (criteria.priceMin) {
      url << "&pxmin=$criteria.priceMin"
    }

    if (criteria.surfaceMin) {
      url << "&surfacemin=$criteria.surfaceMin"
    }
    if (criteria.surfaceMax) {
      url << "&surfacemax=$criteria.surfaceMax"
    }


    if (criteria.postCode!=null) {
      url << "&cp=$criteria.postCode"
    }

    url << '&ci=&' // url ends with this when trying in browser, not sure it's required

    logger.debug("Sending request : " + url);

    WebClient webClient = new WebClient()
    webClient.setJavaScriptEnabled(false)
    def p = webClient.getPage(url.toString())

    def spanNbAnnonces = p.getByXPath("//span[@id='refine_h1']")[0]
    Integer nbAnnonces = agregator.ui.Util.extractInteger(spanNbAnnonces.textContent)
    Integer nbPages = 1
    if (nbAnnonces>0) {
      nbPages = nbAnnonces / 7 + 1
    }
    logger.debug("Nb pages : $nbPages")

    int totalAdded = 0

    for (int pageNum=1 ; pageNum<=nbPages && !isKilled(); pageNum++) {
      logger.debug("Handling page $pageNum")
      if (pageNum>1) {
        agregator.ui.Util.sleepRandomTime()
        String u = url.toString() + "&BCLANNpg=$pageNum"
        logger.debug("Getting page $pageNum, url=$u")
        p = webClient.getPage(u)
      }
      int nbAdded = 0
      def listItems = p.getByXPath("//div[@class='ann_ann']")
      listItems.each { item ->

        try {
          def title = agregator.ui.Util.trim(item.getByXPath("div/div[1]/div[1]/span/a")[0].textContent)
          def price = agregator.ui.Util.extractInteger(item.getByXPath("div/div[1]/div[2]/span[@class='mea2']")[0].textContent)
          def descHolderElem = item.getByXPath("div/div[2]/div[1]/div/div/div[2]")[0]
          StringBuilder desc = new StringBuilder()
          for (def node : descHolderElem.children) {
            if (node instanceof com.gargoylesoftware.htmlunit.html.DomText) {
              desc << node.textContent
            }
          }
          def description = agregator.ui.Util.trim(desc.toString().replace('\n', '').replace('\t', ''))

          def u = ROOT_SITE + item.getByXPath("div/div[1]/div[1]/span/a")[0].getAttribute('href')
          int indexOfQuestionMark = u.indexOf('?')
          if (indexOfQuestionMark>0) {
            u = u.substring(0, indexOfQuestionMark)
          }

          def imgUrlEl = item.getByXPath("div/div[2]/div[1]/div/div/div[1]/a/img")[0]
          def imgUrl = imgUrlEl ? imgUrlEl.getAttribute('src') : null

          def dateEl = item.getByXPath("div/div[2]/div[1]/div/div/div[2]/div[3]")[0]
          if (!dateEl) {
            dateEl = item.getByXPath("div/div[2]/div[1]/div/div/div[1]/div[3]")[0]
          }
          def date = agregator.ui.Util.extractDate(dateEl.textContent)


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