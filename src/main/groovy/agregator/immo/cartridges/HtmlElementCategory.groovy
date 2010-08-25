package agregator.immo.cartridges

import com.gargoylesoftware.htmlunit.html.HtmlElement

class HtmlElementCategory {

  static def eachChildRecurse(HtmlElement elem, Closure c) {
    c.call(elem)
    for (HtmlElement child : elem.childElements) {
      eachChildRecurse(child, c)
    }
  }

}
