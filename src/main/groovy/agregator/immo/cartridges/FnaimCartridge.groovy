package agregator.immo.cartridges

import agregator.core.Agregator
import agregator.core.Cartridge
import agregator.immo.ImmoCriteria
import agregator.immo.ImmoCriteria.Demand
import agregator.immo.ImmoCriteria.Type
import agregator.immo.ImmoResult
import agregator.util.Logger
import com.gargoylesoftware.htmlunit.WebClient

public class FnaimCartridge extends Cartridge<ImmoCriteria,ImmoResult> {

  private static final Logger logger = Logger.getLogger(FnaimCartridge.class)

  private static final String ROOT_SITE = 'http://www.fnaim.fr'

  def FnaimCartridge(Agregator agregator) {
    super("www.fnaim.fr", agregator);
  }

  protected void doAgregate() {

    // http://www.fnaim.fr/?call=resultslittle_biens&category=location&type=maison&FNAIM_Little_Input_Ville=&FNAIM_Little_Input_CP=06000&prix_little_min=500&prix_little_max=2000&surface_little_min=50&surface_little_max=150&resultats.x=72&resultats.y=12

    logger.debug("Building URL")
    def url = new StringBuilder()
    url << ROOT_SITE
    url << "/?recherche.html&call=results_biens"
    if (criteria.demand==Demand.RENT) {
      url << "&category=location"
    } else {
      url << "&category=vente"
    }
    if (criteria.type==Type.APPT) {
      url << "&type_appartement=1&type_maison=0"
      if (criteria.priceMin) {
        url << "&appartement_prix_min=$criteria.priceMin"
      }
      if (criteria.priceMax) {
        url << "&appartement_prix_max=$criteria.priceMax"
      }
      if (criteria.nbRoomsMin) {
        url << "&appartement_pieces_min=$criteria.nbRoomsMin"
      }
      if (criteria.nbRoomsMax) {
        url << "&appartement_pieces_max=$criteria.nbRoomsMax"
      }
      if (criteria.surfaceMin) {
        url << "&appartement_surface_min=$criteria.surfaceMin"
      }
      if (criteria.surfaceMax) {
        url << "&appartement_surface_max=$criteria.surfaceMax"
      }
    } else {
      url << "&type_maison=1&type_appartement=0"
      if (criteria.priceMin) {
        url << "&maison_prix_min=$criteria.priceMin"
      }
      if (criteria.priceMax) {
        url << "&maison_prix_max=$criteria.priceMax"
      }
      if (criteria.nbRoomsMin) {
        url << "&maison_pieces_min=$criteria.nbRoomsMin"
      }
      if (criteria.nbRoomsMax) {
        url << "&maison_pieces_max=$criteria.nbRoomsMax"
      }
      if (criteria.surfaceMin) {
        url << "&maison_surface_min=$criteria.surfaceMin"
      }
      if (criteria.surfaceMax) {
        url << "&maison_surface_max=$criteria.surfaceMax"
      }
    }

    if (criteria.postCode) {
      url << "&FNAIM_Input_CP=$criteria.postCode&FNAIM_Input_Ville="
    }

    logger.debug("Sending request : " + url);

    WebClient webClient = new WebClient()
    webClient.setJavaScriptEnabled(false)
    def p = webClient.getPage(url.toString())

    def spanNbAnnonces = p.getByXPath("/html/body/div/div[@id='content']/div[@id='content-left']/div[@id='FNAIM_Header']/div[@id='FNAIM_Header_Content_Right']/strong")[0]
    Integer nbAnnonces = agregator.ui.Util.extractInteger(spanNbAnnonces.textContent)
    Integer nbPages = 1
    if (nbAnnonces>0) {
      nbPages = nbAnnonces / 10 + 1
    }
    logger.debug("Nb pages : $nbPages")

    int totalAdded = 0

    for (int pageNum=1 ; pageNum<=nbPages && !isKilled(); pageNum++) {
      logger.debug("Handling page $pageNum")
      if (pageNum>1) {
        sleepRandomTime()
        String u = url.toString() + "&page=$pageNum"
        logger.debug("Getting page $pageNum, url=$u")
        p = webClient.getPage(u)
      }
      int nbAdded = 0
      def listItems = p.getByXPath("//div[@class='FNAIM_Detaille_Item']")
      listItems.each { item ->

        try {
          def title = agregator.ui.Util.trim(item.getByXPath("div[1]/div[1]/strong/a")[0].textContent)
          def price = agregator.ui.Util.extractInteger(item.getByXPath("div[1]/div[2]/strong")[0].textContent)
          def descHolderElem = item.getByXPath("div[@class='FNAIM_Detaille_Content']/div[2]/div[2]")[0]
          def description = null
          if (descHolderElem) {
            description = agregator.ui.Util.trim(descHolderElem.textContent).replaceAll(/\[-\]/, '')
          }

          def u = ROOT_SITE + item.getByXPath("div[1]/div[1]/strong/a")[0].getAttribute('href')

          def imgUrlEl = item.getByXPath("div[@class='FNAIM_Detaille_Content']/div[1]/div[1]/a/img")[0]
          def imgUrl = imgUrlEl ? imgUrlEl.getAttribute('src') : null

          def date = null //Util.extractDate(dateEl.textContent)


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