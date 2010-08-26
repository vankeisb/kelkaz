package agregator.immo

import agregator.ui.AgregatorFrame

import javax.swing.SwingUtilities
import javax.swing.JFrame
import javax.swing.UIManager

/**
 * Created by IntelliJ IDEA.
 * User: vankeisb
 * Date: 6 oct. 2009
 * Time: 00:16:07
 * To change this template use File | Settings | File Templates.
 */

public class LaunchImmo {

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
      def f = new AgregatorFrame(new ImmoAgregatorFactory(), new ImmoSearchPanel(), new ImmoResultsPanel())
      f.setSize(1000, 800);
      f.setVisible(true);
    }
  }

}