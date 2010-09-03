package agregator.immo

import agregator.ui.AgregatorFrame

import javax.swing.SwingUtilities
import javax.swing.JFrame
import javax.swing.UIManager
import agregator.core.ExcludedResults

/**
 * Created by IntelliJ IDEA.
 * User: vankeisb
 * Date: 6 oct. 2009
 * Time: 00:16:07
 * To change this template use File | Settings | File Templates.
 */

public class LaunchImmo {

//  private static final String LAF = "org.jvnet.substance.skin.SubstanceBusinessLookAndFeel";
  private static final String LAF = "com.sun.java.swing.plaf.windows.WindowsLookAndFeel"
//  private static final String LAF = "com.seaglasslookandfeel.SeaGlassLookAndFeel"

  public static void main(String[] args) {
    System.setProperty("com.apple.mrj.application.apple.menu.about.name", "TrouvToo Immobilier");    
    SwingUtilities.invokeLater {
      JFrame.setDefaultLookAndFeelDecorated(true);
      // detect substance look and feel
      boolean lafAvailable = false;
      try {
        Class.forName(LAF);
        lafAvailable = true;
      } catch(ClassNotFoundException e) {
        // y'est pas la
      }
      if (lafAvailable) {
        try {
          UIManager.setLookAndFeel(LAF);
        } catch (Exception e) {
          System.out.println("Look and Feel failed to initialize");
        }
      }
      def f = new AgregatorFrame(
              new ImmoAgregatorFactory().create(),
              new ImmoSearchPanel(),
              new ImmoResultsPanel(new ExcludedResults()))
      f.setSize(1000, 800);
      f.setVisible(true);
    }
  }

}