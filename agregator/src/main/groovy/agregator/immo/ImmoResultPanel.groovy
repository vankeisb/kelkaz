package agregator.immo

import javax.swing.JComponent
import agregator.ui.HyperLink
import javax.swing.SwingUtilities
import java.awt.Color
import groovy.swing.SwingBuilder
import javax.swing.BorderFactory
import java.awt.Dimension
import java.awt.BorderLayout
import javax.swing.ImageIcon
import java.awt.Image
import java.awt.Font
import javax.swing.JPanel
import javax.swing.BoxLayout
import javax.swing.JLabel
import javax.swing.Box
import agregator.core.Exclusions
import java.text.SimpleDateFormat
import java.text.DateFormat
import agregator.ui.Util
import java.awt.Container
import java.awt.Component

class ImmoResultPanel {

  private static final ImageIcon NO_PHOTO = new ImageIcon(ImmoResultsPanel.class.getResource("/no-photo.gif"))
  private static final DateFormat DATE_FORMAT = new SimpleDateFormat('dd/MM/yyyy')

  def cmp
  ImmoResultsPanel resultsPanel
  HyperLink exclusionLink
  ImmoResult r
  def coloredPanes = []

  public ImmoResultPanel(ImmoResultsPanel resultsPanel, ImmoResult r) {
    this.resultsPanel = resultsPanel
    this.r = r

    exclusionLink = new HyperLink("", {
      Exclusions exclusions = resultsPanel.exclusions
      boolean isExcluded = exclusions.isExcluded(r)
      if (!isExcluded) {
        exclusions.addExclusion(r)
      } else {
        exclusions.removeExclusion(r)
      }
      updateExclusionStatus()
    })

    def bgColor = Color.WHITE
    cmp = new SwingBuilder().panel(
            layout: new BorderLayout(),
            border: BorderFactory.createEmptyBorder(0,0,4,0),
            maximumSize: new Dimension(2000, 120),
            minimumSize: new Dimension(100, 120)) {
      coloredPanes << panel(constraints: BorderLayout.CENTER, layout: new BorderLayout(), background: bgColor) {
        def photoLabel = label(constraints: BorderLayout.WEST, border: BorderFactory.createEmptyBorder(10,10,10,10))
        ImageIcon icon = null
        try {
          if (r.photoUrl!=null) {
            icon = new ImageIcon(new URL(r.photoUrl))
            Image img = icon.getImage()
            Image newImg = img.getScaledInstance(120, 80,  Image.SCALE_SMOOTH)
            icon = new ImageIcon(newImg)
          } else {
            icon = NO_PHOTO
          }
          photoLabel.setIcon(icon)
        } catch(Exception e) {
          photoLabel.setIcon(NO_PHOTO)
        }
        coloredPanes << panel(constraints: BorderLayout.CENTER, layout: new BorderLayout(), background: bgColor) {
          def titleLabel = label(text: r.title, constraints: BorderLayout.NORTH)
          def font = titleLabel.font
          titleLabel.font = new Font(font.name, font.style | Font.BOLD, font.size)
          coloredPanes << editorPane(
                    background: bgColor,
                    constraints: BorderLayout.CENTER,
                    text: r.description,
                    editable: false,
                    border: BorderFactory.createEmptyBorder(),
                    minimumSize: new Dimension(100, 30),
                    maximumSize: new Dimension(200, 30),
          )
          def bottomPane = new JPanel(background: bgColor)
          coloredPanes << bottomPane
          bottomPane.setLayout(new BoxLayout(bottomPane, BoxLayout.LINE_AXIS))
          if (r.date!=null) {
            def dateLabel = new JLabel(r.date==null ? "" : DATE_FORMAT.format(r.date))
            bottomPane.add(dateLabel)
            bottomPane.add(Box.createRigidArea(new Dimension(5,0)));
            bottomPane.add(new JLabel("-"))
          }
          bottomPane.add(Box.createRigidArea(new Dimension(5,0)));
          def priceLabel = new JLabel(r.price==null ? "" : Integer.toString(r.price))
          bottomPane.add(priceLabel)
          bottomPane.add(Box.createRigidArea(new Dimension(3, 0)));
          def euroSign = new JLabel(
                  text: Util.getMessage("currency.euro"),
                  horizontalAlignment: JLabel.LEFT)
          bottomPane.add(euroSign)
          widget(widget: bottomPane, constraints: BorderLayout.SOUTH)
        }
      }
      coloredPanes << panel(constraints: BorderLayout.SOUTH, background: bgColor, border: BorderFactory.createEmptyBorder(2,5,4,2)) {
        boxLayout(axis:BoxLayout.X_AXIS)
        widget(new HyperLink("ouvrir", r.cartridge.icon, JLabel.LEFT, {
          resultsPanel.fireResultSelected r
        }))
        label(' | ')
        widget(exclusionLink)
      }
    }
    updateExclusionStatus()
  }

  JComponent getComponent() {
    return cmp
  }

  void updateExclusionStatus() {
    Exclusions exclusions = resultsPanel.exclusions
    boolean isExcluded = exclusions.isExcluded(r)
    String exclusionLinkText = isExcluded ? "ne pas exclure" : "exclure"
    exclusionLink.setText exclusionLinkText
    cmp.visible = resultsPanel.showExclusions || !isExcluded
    setBackground(isExcluded ? null : Color.WHITE)
  }

  private void setBackground(Color bgColor) {
    coloredPanes.each { p->
      p.background = bgColor
    }
  }


}
