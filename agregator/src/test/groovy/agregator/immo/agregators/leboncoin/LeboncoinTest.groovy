package agregator.immo.agregators.leboncoin

import agregator.immo.ImmoCriteria
import agregator.immo.ImmoCriteria.Demand
import agregator.immo.ImmoCriteria.Type
import agregator.MyTestListener

/**
 * Created by IntelliJ IDEA.
 * User: alex
 * Date: 12 oct. 2009
 * Time: 20:29:57
 * To change this template use File | Settings | File Templates.
 */

public class LeboncoinTest extends GroovyTestCase{

  public void testLeboncoinLoc() {
    ImmoCriteria crit = new ImmoCriteria()
    crit.demand = Demand.RENT
    crit.type = Type.APPT
    crit.nbRoomsMin = 2
    crit.nbRoomsMax = 12
    crit.surfaceMin = 30
    crit.surfaceMax = 150
    crit.priceMin = 1220
    crit.priceMax = 3333
    crit.postCode = '06000'
    LeboncoinAgregator a = new LeboncoinAgregator()
    def listener = new MyTestListener()
    a.addListener(listener)
    a.agregate(crit);
  }

  public void testLeboncoinAchat() {
    ImmoCriteria crit = new ImmoCriteria()
    crit.demand = Demand.SELL
    crit.type = Type.APPT
    crit.nbRoomsMin = 2
    crit.nbRoomsMax = 5
    crit.surfaceMin = 60
    crit.surfaceMax = 80
    crit.priceMin = 50000
    crit.priceMax = 300000
    crit.postCode = '06000'
    LeboncoinAgregator a = new LeboncoinAgregator()
    def listener = new MyTestListener()
    a.addListener(listener)
    a.agregate(crit);
  }

}