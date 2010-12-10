package agregator.search

import agregator.ui.AgregatorFrame
import javax.swing.SwingUtilities
import javax.swing.JFrame
import javax.swing.UIManager

/**
 * Created by IntelliJ IDEA.
 * User: vankeisb
 * Date: 3 oct. 2009
 * Time: 15:03:31
 * To change this template use File | Settings | File Templates.
 */

public class LaunchUI {

  private static final String LAF = "org.jvnet.substance.skin.SubstanceBusinessLookAndFeel";

  public static void main(String[] args) {
    SwingUtilities.invokeLater {
      JFrame.setDefaultLookAndFeelDecorated(true);
      // detect substance look and feel
      boolean substanceAvailable = false;
      try {
        Class.forName(LAF);
        substanceAvailable = true;
      } catch(ClassNotFoundException e) {
        // y'est pas la
      }
      if (substanceAvailable) {
        try {
          UIManager.setLookAndFeel(LAF);
        } catch (Exception e) {
          System.out.println("Substance failed to initialize");
        }
      }
      AgregatorFrame af = new AgregatorFrame(new SearchEngineAgregatorFactory(), new SearchEnginePanel(), new SearchResultsPanel());
      af.setSize(800, 600);
      af.setVisible(true);
    }

  } 

}