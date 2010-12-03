package agregator.immo

import agregator.core.Exclusions
import groovy.xml.MarkupBuilder
import agregator.core.Cartridge
import agregator.core.Result

class ImmoExclusions extends Exclusions {

  public ImmoExclusions(List<Cartridge<?, ?>> cartridges) {
    this(null, cartridges)
  }

  public ImmoExclusions(String path, List<Cartridge<?,?>> cartridges) {
    super(path, cartridges)
  }

  protected Result deserializeResult(InputStream is) {
    def doc = new XmlSlurper().parse(is)
    return new ImmoResult(
            getCartridge(doc.cartridge.text()),
            stringFromNode(doc.title),
            stringFromNode(doc.url),
            stringFromNode(doc.description),
            intFromNode(doc.price),
            dateFromNode(doc.date),
            stringFromNode(doc.photoUrl))
  }

  private String stringFromNode(def n) {
    if (n==null) {
      return null
    }
    return n.text()
  }

  private Date dateFromNode(def n) {
    if (n==null) {
      return null
    }
    try {
      return new Date(Long.parseLong(n.text()))
    } catch(Exception e) {
      return null
    }
  }

  private Integer intFromNode(def n) {
    if (n==null) {
      return null
    }
    try {
      return Integer.parseInt(n.text())
    } catch(Exception e) {
      return null
    }
  }

  protected boolean serializeResult(Result result, OutputStream os) {
    PrintWriter out = new PrintWriter(os)
    try {
      def xml = new MarkupBuilder(out)
      xml.exclusion {
        cartridge(result.cartridge.name)
        url(result.url)
        title(result.title)
        description(result.description)
        price(stringFromInt(result.price))
        date(stringFromDate(result.date))
        photoUrl(result.photoUrl)
      }
      return true
    } catch(Exception e) {
      e.printStackTrace()
      return false
    }
  }


}
