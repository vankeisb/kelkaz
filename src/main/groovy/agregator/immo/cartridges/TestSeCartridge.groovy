package agregator.immo.cartridges

import agregator.core.Cartridge
import agregator.immo.ImmoCriteria
import agregator.immo.ImmoResult
import com.thoughtworks.selenium.Selenium
import com.thoughtworks.selenium.DefaultSelenium
import agregator.core.Agregator
import agregator.util.SeleniumServerControl


public class TestSeCartridge extends Cartridge<ImmoCriteria,ImmoResult> {

  private Selenium selenium;

  private volatile Iterator<ImmoResult> resultsIterator = null

  def TestSeCartridge(Agregator agregator) {
    super("ParuVenduSelenium", agregator);
  }


  private synchronized void init() {
    try {
      SeleniumServerControl.getInstance().startSeleniumServer();

      // init selenium
      selenium = new DefaultSelenium("localhost", 4444, "*firefox", 'http://www.paruvendu.fr');

      // perform query
      selenium.open("/");
      selenium.click("//ul[@id='hd09_menu_onglets']/li[3]/a/span");
      selenium.waitForPageToLoad("30000");
      selenium.click("link=Location immobilier");
      selenium.waitForPageToLoad("30000");
      selenium.click("tb1");
      selenium.click("tb2");
      selenium.select("nbp0", "label=3");
      selenium.type("sur0", "60");
      selenium.type("px0", "800");
      selenium.type("px1", "1300");
      selenium.type("lo", "06000");
      selenium.click("//form[@id='moteur']/div[2]/div[8]/div[2]/a/span");
      selenium.waitForPageToLoad("30000");

      // javascript for results
      StringWriter w = new StringWriter()
      InputStreamReader r = new InputStreamReader(getClass().getResourceAsStream('/SeInclude.js'));
      w << r
      String script = w.toString()
      String jsonResults = selenium.getEval(script);

      println jsonResults

    } finally {
    
      SeleniumServerControl.getInstance().stopSeleniumServer();

    }


  }

  protected boolean hasMoreResults() {
    if (resultsIterator==null) {
      init()
    }
    return false;  //To change body of implemented methods use File | Settings | File Templates.
  }

  protected ImmoResult nextResult() {
    if (resultsIterator==null) {
      init()
    }
    return null;  //To change body of implemented methods use File | Settings | File Templates.
  }


}