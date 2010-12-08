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


public class OrpiCartridge extends Cartridge<ImmoCriteria,ImmoResult> {

  private static final Logger logger = Logger.getLogger(OrpiCartridge.class)

  private static String URL_CARTRIDGE = "http://www.orpi.com"

  def OrpiCartridge(Agregator agregator){
    super("www.orpi.com", agregator)
  }

  protected void doAgregate() {
    def results = new ArrayList<ImmoResult>()

    // Get the page to scrap
    WebClient webClient = new WebClient()
    webClient.setJavaScriptEnabled(false)
    def page = webClient.getPage(URL_CARTRIDGE)

    logger.debug "page loaded : " + page

    // Get the search form
    def form = page.getElementById('achHomeRechercheRapide')

    // Choose sell or rent
    def radio
    if (criteria.demand == Demand.RENT) {
      radio = form.getElementById('achRadioButtonLouer');
    }else{
      radio = form.getElementById('achRadioButtonAcheter')
    }
    radio.click()

    // set room number
    def nbRoomLimit = 4
    if (criteria.nbRoomsMin == null){
      criteria.nbRoomsMin = 1
    }

    if (criteria.nbRoomsMin > nbRoomLimit){
      def nbRoomsRadio = form.getElementById('ach_cb_recNbPieces5')
      nbRoomsRadio.click()
    }else{
      if (criteria.nbRoomsMax > nbRoomLimit || criteria.nbRoomsMax == null ){
        criteria.nbRoomsMax = 4
        def nbRoomsRadio = form.getElementById('ach_cb_recNbPieces5')
        nbRoomsRadio.click()
      }

      for (int i = criteria.nbRoomsMin ; i<=criteria.nbRoomsMax ; i++){
        def nbRoomsRadio = form.getElementById('ach_cb_recNbPieces'+i)
        nbRoomsRadio.click()
      }
    }

    // Set type
    if (criteria.getType() == Type.APPT){
      def typeRadio = form.getInputByName('recIdTypeBien2')
      typeRadio.click()
    }
    if (criteria.getType() == Type.MAISON){
      def typeRadio = form.getInputByName('recIdTypeBien1')
      typeRadio.click()
    }

    // Set the price min
    if (criteria.priceMin != null){
      def priceMinField = form.getInputByName('recPrixMin')
      priceMinField.setValueAttribute(criteria.priceMin.toString())
    }

    // Set the price max
    if (criteria.priceMax != null){
      def priceMaxField = form.getInputByName('recPrixMax')
      priceMaxField.setValueAttribute(criteria.priceMax.toString())
    }

    // Set the localisation field
    def localisationField = form.getInputByName('recVille')
    localisationField.setValueAttribute(criteria.postCode)

    // Submit the form
    page = form.submit()

    // Get the results
    def divResults = page.getByXPath("//dl[@class='annonceResult']")
    def i = 0
    divResults.each{
      println 'it = ' + it
      def resultTitle = it.getByXPath("//dt[@class='annonceResultTitle']")[i]
      def aTitle = resultTitle.getHtmlElementsByTagName('a')[0]
      def url = aTitle.getAttribute('href')

      // Sometimes the URL doesn't start with http://... add it
      if (!url.startsWith("http://"))
        url = URL_CARTRIDGE +'/'+ url

      // Create the title from the url link
      def title = url.substring(36, url.length()-16)
      title = title.replaceAll('-', ' ')

      // Get the description
      def resultDescription = it.getByXPath("//dd[@class='annonceResultDesc']")[i]
      def aDescription = resultDescription.getHtmlElementsByTagName('a')[0]
      def description = aDescription.textContent

      // Get the surface in description
      def surface = getSurfaceFromDescription(description)

      // get image
      def resultImage = it.getByXPath("//dt[@class='annonceResultImg']")[i]
      def aImage = resultImage.getHtmlElementsByTagName('a')[0]
      def imgImage = aImage.getHtmlElementsByTagName('img')[0]
      def urlImage = imgImage.getAttribute('src')

      // get price
      def resultPrice = it.getByXPath("//dd[@class='annonceResultPrice']")[i]
      def aPrice = resultPrice.getHtmlElementsByTagName('a')[0]
      def emPrice = aPrice.getHtmlElementsByTagName('em')[0]
      def price = extractInteger(emPrice.textContent)

      if (criteria.surfaceMin != null){
        if (criteria.surfaceMax != null){
          if (criteria.surfaceMin <= surface && criteria.surfaceMax >= surface){
            fireResultEvent(new ImmoResult(this, title, url, description, price, null, urlImage))
          }
        } else{
          if (criteria.surfaceMin <= surface){
            fireResultEvent(new ImmoResult(this, title, url, description, price, null, urlImage))
          }
        }
      } else{
        if (criteria.surfaceMax != null){
          if (criteria.surfaceMax >= surface){
            fireResultEvent(new ImmoResult(this, title, url, description, price, null, urlImage))
          }
        }else{
          fireResultEvent(new ImmoResult(this, title, url, description, price, null, urlImage))
        }
      }
      i++
    }
  }


  private int getSurfaceFromDescription(String description){
    def items = description.split(',')
    return extractInteger(items[2].replaceAll('m²', ''))
  }
}