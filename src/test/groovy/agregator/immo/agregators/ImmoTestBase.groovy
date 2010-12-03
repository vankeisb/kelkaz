package agregator.immo.agregators

import agregator.core.Agregator
import agregator.MyTestListener
import agregator.core.AgregatorListener
import agregator.core.AgregatorEvent.AgregatorCartridgeEvent
import agregator.core.CartridgeEvent.ResultEvent
import agregator.immo.ImmoCriteria

abstract class ImmoTestBase extends GroovyTestCase {

  protected abstract Agregator getAgregator()

  protected void doSearch(ImmoCriteria crit, Closure test) {
    def a = getAgregator()
    def listener = new MyTestListener()
    a.addListener(listener)
    a.agregate(crit)

    def results = listener.results
    test.call(results)
  }

  protected void doSearchWithListener(ImmoCriteria crit, Closure resultTest) {
    int nbResults = 0
    Throwable err = null
    Agregator a = getAgregator()
    def listener = { evt ->
      if (evt instanceof AgregatorCartridgeEvent && evt.cartridgeEvent instanceof ResultEvent) {
        nbResults++
        try {
          resultTest.call(evt.cartridgeEvent.result)
        } catch(Throwable e) {
          err = e
          a.kill()
        }
      }
    } as AgregatorListener
    a.addListener(listener)
    a.agregate(crit)
    assert nbResults > 0, "No results for query"
    if (err) {
      throw err
    }
  }

  protected void doSearchAndAssertResultsMatchPrice(ImmoCriteria crit) {
    doSearchWithListener(crit) { result ->
      println "comparing result price $result.price to min $crit.priceMin and max $crit.priceMax"
      if (crit.priceMax) {
        assert result.price <= crit.priceMax, "Result price violates max price ($result.price)"
      }
      if (crit.priceMin) {
        assert result.price >= crit.priceMin, "Result price violates min price ($result.price)"
      }
    }
  }

  protected void searchAndAssertResultCountAndPhotos(ImmoCriteria crit, int expectedResultCount) {
    doSearch(crit) { results ->
      int nbResults = results.size()
      assert nbResults == expectedResultCount

      // check that all photos are different
      def urls = []
      results.each { r->
        if (r.photoUrl) {
          assert !urls.contains(r.photoUrl)
          urls << r.photoUrl
        }
      }
    }



  }


}
