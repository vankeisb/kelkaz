package agregator.immo.paruvendu

import agregator.immo.ImmoCriteria
import agregator.immo.ImmoCriteria.Demand
import agregator.immo.ImmoCriteria.Type
import com.gargoylesoftware.htmlunit.WebClient
import com.gargoylesoftware.htmlunit.html.HtmlPage
import com.gargoylesoftware.htmlunit.BrowserVersion

public class ParuVenduTest extends GroovyTestCase {

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

  public void testParuVendu() {
    println "Sending criteria"
    ImmoCriteria crit = new ImmoCriteria()
    crit.demand = Demand.RENT
    crit.type = Type.APPT
    crit.nbRoomsMin = 3
    crit.nbRoomsMax = 3
    crit.surfaceMin = 50
    crit.surfaceMax = 150
    crit.priceMin = 1400
    crit.priceMax = 1500
    crit.postCode = '06000'
//    crit.city = 'Nice'
    ParuVenduAgregator a = new ParuVenduAgregator()
    def listener = new ParuVenduTestListener()
    a.addListener(listener)
    a.agregate(crit)

    def results = listener.results
    assert results.size() == 2
    def result = results[0]

    // on rechope la page et on check si le titre est le bon
    println "Sending comparison request to $result.url"
    WebClient c = new WebClient()
    HtmlPage page = c.getPage(result.url)
    def titleInPage = page.getByXPath("//div[@class='im_de_ann_L b']")[0].textContent.trim()
    println "title (page) : $titleInPage"
    println "title (resu) : $result.title"
    assert removeSpaces(result.title) == removeSpaces(titleInPage)
  }

}