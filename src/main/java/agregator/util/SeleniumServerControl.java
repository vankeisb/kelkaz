package agregator.util;


import org.openqa.selenium.server.SeleniumServer;

public class SeleniumServerControl {
  private static final SeleniumServerControl instance = new SeleniumServerControl();
  public static SeleniumServerControl getInstance() {
    return instance;
  }
  private SeleniumServer server = null;
  protected SeleniumServerControl() {
  }
  public void startSeleniumServer() {
    if (server == null) {
      try {
        server = new SeleniumServer();
        System.out.println(" selenium server " + server.toString());
      } catch (Exception e) {
        throw new RuntimeException("Could not create Selenium Server because of: "
            + e.getMessage(), e);
      }
    }
    try {
      server.start();
    } catch (Exception e) {
        throw new RuntimeException("Could not create Selenium Server because of: "
            + e.getMessage(), e);
    }
  }
  public void stopSeleniumServer() {
    if (server != null) {
      try {
        server.stop();
        server = null;
      } catch (Exception e) {
        throw new RuntimeException("Could not stop Selenium Server because of: "
            + e.getMessage(), e);
      }
    }
  }
}