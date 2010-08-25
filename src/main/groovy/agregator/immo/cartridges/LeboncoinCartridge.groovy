package agregator.immo.cartridges

import java.io.FileReader

import agregator.core.Cartridge
import agregator.core.Agregator
import agregator.immo.ImmoCriteria
import com.gargoylesoftware.htmlunit.WebClient
import agregator.immo.ImmoResult
import agregator.immo.ImmoCriteria.Demand

import au.com.bytecode.opencsv.CSVReader
import com.gargoylesoftware.htmlunit.BrowserVersion

public class LeboncoinCartridge extends Cartridge<ImmoCriteria,ImmoResult> {

  private Iterator<ImmoResult> resultsIterator = null

  private static String URL_CARTRIDGE = "http://www.leboncoin.fr" 

  def LeboncoinCartridge(Agregator agregator){
    super("leboncoin", agregator)
  }

  protected void doAgregate() {
    //To change body of implemented methods use File | Settings | File Templates.
  }

  /**
   * Return the ares'a code (as used in 'leboncoin.fr' in function of the post code
   * Use here to get the rigth research page
   */
  def getArea(String postCode){
    // Get the dept code from the post code
    def depNb = postCode.substring(0, 2)
    try {
      // Create the CSVReader which contains the Areas name en code values
      FileReader fileReader = new FileReader("src/main/resources/FR_areas.csv");
      char separator = ';'
      CSVReader reader = new CSVReader(fileReader, separator);
      String [] nextLine;
      while ((nextLine = reader.readNext()) != null) {
        if (nextLine[0].equals(depNb))
          return nextLine[2]
      }
    }catch(IOException e){
      e.printStackTrace();
    }
  }

  private void init(){
    def results = new ArrayList<ImmoResult>()

    // Get the area. In case of no informations on the localisation -> Use 'Paris'.
    def areaCode
    if (criteria.postCode)
      areaCode = getArea(criteria.postCode)
    else
      areaCode = getArea('75')

    // Get the page in function of the area's code
    WebClient webClient = new WebClient(BrowserVersion.FIREFOX_2)
    def page = webClient.getPage(URL_CARTRIDGE+'/li?ca='+areaCode+'_s')

    // Get the query form
    def form = page.getFormByName('f')

    // The price fields change in function of the demand tye choose
    // Sell : Price max/min
    // Rent : Loyer max/min
    def selectDemandType = form.getSelectByName('c')
    def priceMinFieldName
    def priceMaxFieldName

    if (criteria.demand == Demand.RENT){
      selectDemandType.setSelectedAttribute('10', true)
      priceMinFieldName = 'mrs'
      priceMaxFieldName = 'mre'
    }else{
      selectDemandType.setSelectedAttribute('9', true)
      priceMinFieldName = 'ps'
      priceMaxFieldName = 'pe'
    }

    // Set the price min field in the form
    // If no min price value : field will be not setted
    def selectPriceMin = form.getSelectByName(priceMinFieldName)
    if (criteria.priceMin != null){
      def priceMinValue = ''
      for (option in selectPriceMin.options){
        if (!option.valueAttribute.equals('')){
          if (criteria.priceMin > Integer.valueOf(option.text.replaceAll(' ', '')) ){
            priceMinValue = option.valueAttribute
          }
        }
      }
      selectPriceMin.setSelectedAttribute(priceMinValue, true)
    }else{
      selectPriceMin.setSelectedAttribute('', true)
    }

    // Set the price max field in the form
    // If no max price value : field will be not setted
    def selectPriceMax = form.getSelectByName(priceMaxFieldName)
    if (criteria.priceMax != null){
      def priceMaxValue = ''
      for (option in selectPriceMax.options){
        if (!option.valueAttribute.equals('')){
          // Be careful the max price bind a String. Catch this case
          if (option.text.equals('Plus de 1 500 000')){
            priceMaxValue = '16'
          }else{
            if (criteria.priceMax < Integer.valueOf(option.text.replaceAll(' ', '')) ){
              priceMaxValue = option.valueAttribute
              break
            }
          }
        }
      }
      selectPriceMax.setSelectedAttribute(priceMaxValue, true)
    }else{
      selectPriceMax.setSelectedAttribute('', true)
    }

    // Set the surface min field in the form
    // If no min surface value : field will be not setted
    def selectSurfaceMin = form.getSelectByName('sqs')
    if (criteria.surfaceMin != null){
      def surfaceMinValue = ''
      for (option in selectSurfaceMin.options){
        if (!option.valueAttribute.equals('')){
          if (criteria.surfaceMin > Integer.valueOf(option.text.replaceAll(' ', '')) ){
            surfaceMinValue = option.valueAttribute
          }
        }
      }
      selectSurfaceMin.setSelectedAttribute(surfaceMinValue, true)
    }else{
      selectSurfaceMin.setSelectedAttribute('', true)
    }

    // Set the surface max field in the form
    // If no max surface value : field will be not setted
    def selectSurfaceMax = form.getSelectByName('sqe')
    if (criteria.surfaceMax != null){
      def surfaceMaxValue = ''
      for (option in selectSurfaceMax.options){
        if (!option.valueAttribute.equals('')){
          // Be careful the max surface bind a String. Catch this case
          if (option.text.equals('Plus de 500')){
            surfaceMaxValue = '8'
          }else{
            if (criteria.surfaceMax < Integer.valueOf(option.text.replaceAll(' ', '')) ){
              surfaceMaxValue = option.valueAttribute
              break
            }
          }
        }
      }
      selectSurfaceMax.setSelectedAttribute(surfaceMaxValue, true)
    }else{
      selectSurfaceMax.setSelectedAttribute('', true)
    }

    // Set the rooms min field in the form
    // If no min rooms value : field will be not setted
    def selectNbRoomsMin = form.getSelectByName('ros')
    if (criteria.nbRoomsMin != null){
      selectNbRoomsMin.setSelectedAttribute(criteria.nbRoomsMin.toString(), true)
    }else{
      selectNbRoomsMin.setSelectedAttribute('', true)
    }

    // Set the surface max field in the form
    // If no max surface value : field will be not setted
    def selectNbRoomsMax = form.getSelectByName('roe')
    if (criteria.nbRoomsMax != null){
      def nbRoomsMaxValue
      // Be careful the max rooms not coresponding to the criteria max rooms but a strange value '999999'. Catch this case
      if (criteria.nbRoomsMax == 9)
        nbRoomsMaxValue = '999999'
      else
        nbRoomsMaxValue = criteria.nbRoomsMax.toString()

      selectNbRoomsMax.setSelectedAttribute(nbRoomsMaxValue, true)
    }else{
      selectNbRoomsMax.setSelectedAttribute('', true)
    }

    // Print for tests
    selectPriceMin.selectedOptions.each{println('price min = \''+it.text+'\'')}
    selectPriceMax.selectedOptions.each{println('price max = \''+it.text+'\'')}
    selectSurfaceMin.selectedOptions.each{println('surface min = \''+it.text+'\'')}
    selectSurfaceMax.selectedOptions.each{println('surface max = \''+it.text+'\'')}
    selectNbRoomsMin.selectedOptions.each{println('rooms min = \''+it.text+'\'')}
    selectNbRoomsMax.selectedOptions.each{println('rooms max = \''+it.text+'\'')}

    // submit form
    page = form.submit()

    // get Immo results (Generics are in da place)
    def tableResult = page.getHtmlElementById('hl')
    tableResult.rows.each{
      def main = it.getCells().get(2)
      def a = main.getHtmlElementsByTagName('a')[0]
      def title = a.getTextContent()
      // Add the cartridde url before the hit url
      def url = URL_CARTRIDGE + a.getAttribute('href')
      results << new ImmoResult(this, title, url, null)
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
