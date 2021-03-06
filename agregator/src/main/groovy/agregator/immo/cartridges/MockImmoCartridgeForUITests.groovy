package agregator.immo.cartridges

import agregator.core.Cartridge
import agregator.immo.ImmoCriteria
import agregator.immo.ImmoResult
import agregator.core.Agregator
import agregator.core.Criteria

class MockImmoCartridgeForUITests extends Cartridge {

  def MockImmoCartridgeForUITests(Agregator agregator) {
    super("MockImmoCartridgeForUITests", agregator);
  }

  def MockImmoCartridgeForUITests(String name, Agregator agregator) {
    super(name, agregator);
  }

  protected void doAgregate(Criteria criteria) {
    for (i in 1..50) {
      String title = "The title of result $i"
      String url = "http://dummy.org/$i"
      String description = "this is the description for $i it can be a quite long bunch of text blah blah blah blah and foo bar and baz again if you want !\nsome other line"
      Integer price = Random.newInstance().nextInt(2000)
      Date date = new Date()
      String photoUrl = "http://media.xircles.codehaus.org/_projects/groovy/_logos/medium.png"
      ImmoResult r = new ImmoResult(this, title, url, description, price, date, photoUrl)
      fireResultEvent(r)
      if (i%2==0) {
        System.out.println("Sleeping...")
        Thread.sleep(1000)
      }
      if (isKilled()) {
        break
      }
    }
  }


}
