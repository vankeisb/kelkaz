package agregator.immo.paruvendu

import agregator.immo.ImmoCriteria
import agregator.immo.ImmoCriteria.Demand
import agregator.immo.ImmoCriteria.Type
import com.gargoylesoftware.htmlunit.WebClient
import com.gargoylesoftware.htmlunit.html.HtmlPage

import agregator.immo.ImmoResult

import agregator.immo.ImmoTestBase
import agregator.core.Agregator

public class ParuVenduTest extends ImmoTestBase {

  protected Agregator getAgregator() {
    return new ParuVenduAgregator()
  }

  private String removeSpaces(String s) {
    int i=0;
    StringBuffer res = new StringBuffer()
    while (i<s.length()) {
      char c = s.charAt(i++)
      if (!c.isWhitespace()) {
        res.append(c)
      }
    }
    return res.toString()
  }

  private void assertFirstResultInPage(ImmoResult result) {
    // on rechope la page et on check si le titre est le bon
    println "Sending comparison request to $result.url"
    WebClient c = new WebClient()
    c.setJavaScriptEnabled(false)
    HtmlPage page = c.getPage(result.url)
    def titleInPage = page.getByXPath("//div[@class='im_de_ann_L b']")[0].textContent.trim()
    println "title (page) : $titleInPage"
    println "title (resu) : $result.title"
    assert removeSpaces(result.title) == removeSpaces(titleInPage)
  }

  public void testPagination() {
    searchAndAssertResultCountAndPhotos(new ImmoCriteria([
      demand: Demand.RENT,
      type: Type.APPT,
      nbRoomsMin: 2,
      postCode: '06000'
    ]), 130)
  }

  public void testLocAppt() {
    searchAndAssertResultCountAndPhotos(new ImmoCriteria([
      demand: Demand.RENT,
      type: Type.APPT,
      nbRoomsMin: 3,
      nbRoomsMax: 3,
      surfaceMin: 50,
      surfaceMax: 150,
      priceMin: 1400,
      priceMax: 1500,
      postCode: '06000'
    ]), 2)
  }

  public void testLocMaison() {
    searchAndAssertResultCountAndPhotos(new ImmoCriteria([
      demand: Demand.RENT,
      type: Type.MAISON,
      nbRoomsMin: 3,
      nbRoomsMax: 5,
      surfaceMin: 50,
      surfaceMax: 150,
      priceMin: 1300,
      priceMax: 1500,
      postCode: '06000'
    ]), 3)
  }

  public void testBug18() {
    searchAndAssertResultCountAndPhotos(new ImmoCriteria([
      demand: Demand.SELL,
      type: Type.APPT,
      nbRoomsMin: 3,
      surfaceMin: 80,
      surfaceMax: 150,
      priceMax: 300000,
      postCode: '06000'
    ]), 24)
  }

  
}