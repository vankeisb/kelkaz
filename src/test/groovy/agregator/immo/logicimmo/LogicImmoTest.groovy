package agregator.immo.logicimmo

import agregator.MyTestListener
import agregator.immo.ImmoCriteria
import agregator.immo.ImmoCriteria.Demand
import agregator.immo.ImmoCriteria.Type

/**
 * Created by IntelliJ IDEA.
 * User: alex
 * Date: 12 oct. 2009
 * Time: 20:29:57
 * To change this template use File | Settings | File Templates.
 */

public class LogicImmoTest extends GroovyTestCase{

  public void testLoc() {
    ImmoCriteria crit = new ImmoCriteria()
    crit.demand = Demand.RENT
    crit.type = Type.APPT
    crit.nbRoomsMin = 2
    crit.surfaceMin = 30
    crit.priceMin = 300
    crit.priceMax = 3333
    crit.postCode = '06000'
    LogicImmoAgregator a = new LogicImmoAgregator()
    def listener = new MyTestListener()
    a.addListener(listener)
    a.agregate(crit);
  }

  public void testAchat() {
    ImmoCriteria crit = new ImmoCriteria()
    crit.demand = Demand.SELL
    crit.type = Type.APPT
    crit.nbRoomsMin = 3
//    crit.nbRoomsMax = 5
    crit.surfaceMin = 80
//    crit.surfaceMax = 80
//    crit.priceMin = 50000
    crit.priceMax = 300000
    crit.postCode = '06000'
    LogicImmoAgregator a = new LogicImmoAgregator()
    def listener = new MyTestListener()
    a.addListener(listener)
    a.agregate(crit);

    int nbResults = listener.getResults().size()
    assert nbResults == 68
  }

}