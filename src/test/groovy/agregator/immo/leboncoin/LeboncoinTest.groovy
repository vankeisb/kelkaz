package agregator.immo.leboncoin

import agregator.immo.ImmoCriteria
import agregator.immo.ImmoCriteria.Demand
import agregator.immo.ImmoCriteria.Type
import agregator.core.Cartridge

/**
 * Created by IntelliJ IDEA.
 * User: alex
 * Date: 12 oct. 2009
 * Time: 20:29:57
 * To change this template use File | Settings | File Templates.
 */

public class LeboncoinTest extends GroovyTestCase{

  public void testLeboncoin() {
    ImmoCriteria crit = new ImmoCriteria()
    crit.demand = Demand.RENT
    crit.type = Type.APPT
    crit.nbRoomsMin = 2
    crit.nbRoomsMax = 12
    crit.surfaceMin = 30
    crit.surfaceMax = 150
    crit.priceMin = 200000
    crit.priceMax = 250000
    crit.city = 'Nice'
    LeboncoinAgregator a = new LeboncoinAgregator()
    def listener = new LeboncoinListener()
    a.addListener(listener)
    a.agregate(crit);
  }
}