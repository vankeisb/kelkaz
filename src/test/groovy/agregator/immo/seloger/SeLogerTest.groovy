package agregator.immo.seloger

import agregator.MyTestListener
import agregator.immo.ImmoCriteria
import agregator.immo.ImmoCriteria.Demand
import agregator.immo.ImmoCriteria.Type

public class SeLogerTest extends GroovyTestCase {

  private void doTest(ImmoCriteria crit, int expectedResultCount) {
    SeLogerAgregator a = new SeLogerAgregator()
    def listener = new MyTestListener()
    a.addListener(listener)
    a.agregate(crit)

    def results = listener.results
    def nbResults = results.size()
    assert nbResults == expectedResultCount
  }

  public void testPagination() {
    def crit = new ImmoCriteria([
      demand: Demand.RENT,
      type: Type.MAISON,
      nbRoomsMin: 1,
      nbRoomsMax: 6,
      surfaceMin: 50,
      surfaceMax: 300,
      priceMin: 500,
      priceMax: 5000,
      postCode: '06000'
    ])

    SeLogerAgregator a = new SeLogerAgregator()
    def listener = new MyTestListener()
    a.addListener(listener)
    a.agregate(crit)

    def results = listener.results
    def nbResults = results.size()
    assert nbResults > 7

  }

  public void testLocMaison() {
    doTest(new ImmoCriteria([
      demand: Demand.RENT,
      type: Type.MAISON,
      nbRoomsMin: 4,
      nbRoomsMax: 6,
      surfaceMin: 50,
      surfaceMax: 150,
      priceMin: 500,
      priceMax: 2000,
      postCode: '06000'
    ]), 5)
  }

}