package agregator.immo.agregators.orpi

import agregator.immo.ImmoCriteria
import agregator.immo.ImmoCriteria.Demand
import agregator.immo.ImmoCriteria.Type

public class OrpiTest extends GroovyTestCase {

  public void testOrpiVendu() {
    ImmoCriteria crit = new ImmoCriteria()
    crit.demand = Demand.RENT
    crit.type = Type.APPT
    crit.nbRoomsMin = 2
    crit.nbRoomsMax = 12
    crit.surfaceMin = 30
    crit.surfaceMax = 150
	crit.priceMin = 600
	crit.priceMax = 1000
    crit.city = 'Antibes'
    OrpiAgregator a = new OrpiAgregator()
    a.addListener(new TestListener())
    a.agregate(crit);
	
	def results = listener.results
//	assert results.size() == 1
	def result = results[0]
	
	results.each{
		println it.title
	}
	println result.title
	// on rechope la page et on check si le titre est le bon
	/*println "Sending comparison request"
	WebClient c = new WebClient()
	HtmlPage page = c.getPage('http://www.paruvendu.fr/immobilier/location/appartement/antibes-06600/1146146149A1KILHAP000CX3CI1RG1')
	def titleInPage = page.getByXPath("//div[@class='im_de_ann_L b']")[0].textContent
	println "title (page) : $titleInPage"
	println "title (resu) : $result.title"
	assert removeSpaces(result.title) == removeSpaces(titleInPage)*/
 
  }

}